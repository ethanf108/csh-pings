package edu.rit.csh.pings.external;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramException;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import edu.rit.csh.pings.entities.Application;
import edu.rit.csh.pings.entities.Route;
import edu.rit.csh.pings.entities.TelegramServiceConfiguration;
import edu.rit.csh.pings.entities.VerificationRequest;
import edu.rit.csh.pings.managers.VerificationRequestManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOError;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.rit.csh.pings.util.Util.readFully;

@Service
@RequiredArgsConstructor
public class TelegramService implements ExternalService<TelegramServiceConfiguration> {

    private final Log log = LogFactory.getLog("pings.telegram_service");

    private final VerificationRequestManager verificationRequestManager;
    private transient final Map<String, Long> usernameToId = new HashMap<>();
    private String pingTemplate;
    private String verificationTemplate;
    @Value("${csh.pings.telegram.bot_id}")
    private String botId;
    private transient TelegramBot bot;

    @Value("${csh.pings.url:https://pings.csh.rit.edu}")
    private String url;

    @PostConstruct
    public void readTemplates() {
        try {
            this.pingTemplate = readFully(new ClassPathResource("telegram_ping_template.txt").getInputStream());
            this.verificationTemplate = readFully(new ClassPathResource("telegram_verification_template.txt").getInputStream());
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    @PostConstruct
    private void setupTelegram() {
        if (this.bot != null) {
            return;
        }
        this.bot = new TelegramBot(this.botId);
        if (!this.bot.execute(new GetMe()).isOk()) {
            this.log.fatal("Telegram error");
            throw new Error("Telegram error");
        }
        this.bot.setUpdatesListener(this::telegramUpdates, this::onTelegramException);
        this.log.info("Set up Bot");
    }

    @PreDestroy
    private void teardownBot() {
        this.bot.shutdown();
        this.bot = null;
        this.log.info("Shutdown Bot");
    }

    private int telegramUpdates(List<Update> updates) {
        for (Update update : updates) {
            try {
                this.usernameToId.put(update.message().from().username().toString(), update.message().from().id());
            } catch (NullPointerException e) {
                //do nothing, for code succinctness
            }
        }
        this.log.debug("Updated from Telegram");
        return UpdatesListener.CONFIRMED_UPDATES_NONE;
    }

    private void onTelegramException(TelegramException e) {
        this.log.warn("Telegram Exception", e);
    }

    @Override
    public void sendPing(Route route, TelegramServiceConfiguration config, String body) {
        this.log.info("Sending Ping to T-@" + config.getTelegramUsername() + ", CSH-@" + config.getUsername());
        final Application app = route.getApplication();
        final String send = this.pingTemplate
                .replace("%%%BODY%%%", body)
                .replace("%%%APPLICATION%%%", app.getName());
        final SendMessage message = new SendMessage(config.getTelegramId(), send.replace("%%%URL%%%", this.url))
                .entities(new MessageEntity(
                        MessageEntity.Type.url,
                        send.indexOf("%%%URL%%%"),
                        this.url.length()
                ));
        final BaseResponse response = this.bot.execute(message);
        if (response.isOk()) {
            this.log.info("Sent ping to T-ID: " + config.getTelegramId() + ", CSH-@" + config.getUsername());
        } else {
            this.log.error("Error sending message. error: " + response.errorCode());
        }
    }

    @Override
    public void sendVerification(TelegramServiceConfiguration config) {
        if (!this.usernameToId.containsKey(config.getTelegramUsername())) {
            throw new IllegalArgumentException("Username not found. Please add the CSH Pings Telegram bot");
        }
        config.setTelegramId(this.usernameToId.get(config.getTelegramUsername()));
        final VerificationRequest vr = this.verificationRequestManager.generateVerification(config);
        this.log.debug("Created VR");
        final String url = this.url + "verify?token=" + vr.getToken();
        final String urlReplacement = "click here";
        final String verificationText = this.verificationTemplate.replace("%%%LINK%%%", urlReplacement).replace("%%%USER%%%", config.getUsername());
        final BaseResponse response = this.bot.execute(
                new SendMessage(
                        config.getTelegramId(),
                        verificationText)
                        .entities(
                                new MessageEntity(
                                        MessageEntity.Type.text_link,
                                        this.verificationTemplate.indexOf("%%%LINK%%%"),
                                        urlReplacement.length()).url(url)));
        if (!response.isOk()) {
            this.log.error("Error sending verification request to " + config.getTelegramId() + ", error code: " + response.errorCode());
        } else {
            this.log.info("Sent Message");
        }
    }
}
