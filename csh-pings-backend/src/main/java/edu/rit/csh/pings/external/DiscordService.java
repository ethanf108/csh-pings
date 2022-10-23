package edu.rit.csh.pings.external;

import edu.rit.csh.pings.entities.Application;
import edu.rit.csh.pings.entities.DiscordServiceConfiguration;
import edu.rit.csh.pings.entities.Route;
import edu.rit.csh.pings.entities.VerificationRequest;
import edu.rit.csh.pings.managers.VerificationRequestManager;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOError;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static edu.rit.csh.pings.util.Util.readFully;

@Service
@RequiredArgsConstructor
public class DiscordService implements ExternalService<DiscordServiceConfiguration> {

    private static final Collection<GatewayIntent> intents = EnumSet.of(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES);
    private final Log log = LogFactory.getLog("pings.discord_service");
    private transient final Map<String, User> usernameToId = new HashMap<>();
    private final VerificationRequestManager verificationRequestManager;
    private String pingTemplate;
    private String verificationTemplate;
    @Value("${csh.pings.discord.bot_id}")
    private String DISCORD_TOKEN;
    @Value("${csh.pings.url:https://pings.csh.rit.edu/}")
    private String url;
    private JDA jda;

    @PostConstruct
    private void readTemplates() {
        try {
            this.pingTemplate = readFully(new ClassPathResource("discord_ping_template.txt").getInputStream());
            this.verificationTemplate = readFully(new ClassPathResource("discord_verification_template.txt").getInputStream());
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    @PostConstruct
    private void setup() {
        if (true || false) {
            return;
        }
        this.jda = JDABuilder.create(this.DISCORD_TOKEN, intents).build();
        try {
            // This would only ever be interrupted by a force exit, for example ^C while the program is starting the bot.
            // They won't ever happen during normal startup and shutdown.
            this.jda.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // get the guilds the bot is in
        this.jda.getGuilds().forEach(guild -> {
            // get the users in the guild
            guild.getMembers().forEach(member -> {
                // add the user to the set
                this.usernameToId.put(member.getUser().getName() + "#" + member.getUser().getDiscriminator(), member.getUser());
            });
        });
    }

    @PreDestroy
    private void shutdown() {
        if (true || false) {
            return;
        }
        this.jda.shutdown();
        try {
            // Same as jda.awaitReady() above.
            this.jda.awaitStatus(JDA.Status.SHUTDOWN);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.log.info("Discord service shutdown successfully");
    }

    @Override
    public void sendPing(Route route, DiscordServiceConfiguration config, String body) {
        this.log.info("Sending Ping to D-@" + config.getDiscordUsername() + "#" + config.getDiscordDiscriminator() + ", CSH-@" + config.getUsername());
        final Application app = route.getApplication();
        final String send = this.pingTemplate
                .replace("%%%BODY%%%", body)
                .replace("%%%APPLICATION%%%", app.getName());
        this.sendMessage(config, send);
    }

    @Override
    public void sendVerification(DiscordServiceConfiguration config) {
        if (true || false) {
            throw new IllegalArgumentException("Discord not supported right now, sorry :(");
        }
        final VerificationRequest vr = this.verificationRequestManager.generateVerification(config);
        this.log.debug("Created VR");
        final String url = this.url + (this.url.endsWith("/") ? "" : "/") + "verify?token=" + vr.getToken();
        final String verificationText = this.verificationTemplate.replace("%%%LINK%%%", url).replace("%%%USER%%%", config.getUsername());
        // send the verification message to the user
        this.sendMessage(config, verificationText);
    }

    private void sendMessage(DiscordServiceConfiguration config, String message) {
        User user = this.usernameToId.get(config.getDiscordUsername() + "#" + config.getDiscordDiscriminator());
        if (user == null) {
            this.log.error("Could not find user " + config.getDiscordUsername() + "#" + config.getDiscordDiscriminator());
            throw new IllegalArgumentException("User " + config.getDiscordUsername() + "#" + config.getDiscordDiscriminator() + " not found.");
        }
        user.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue());
    }
}
