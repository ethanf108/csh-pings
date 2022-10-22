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
import java.util.*;

import static edu.rit.csh.pings.util.Util.readFully;

@Service
@RequiredArgsConstructor
public class DiscordService implements ExternalService<DiscordServiceConfiguration> {

    private final Log log = LogFactory.getLog("pings.discord_service");

    private transient final Map<String, User> usernameToId = new HashMap<>();

    private String pingTemplate;
    private String verificationTemplate;
    private final VerificationRequestManager verificationRequestManager;

    @Value("${csh.pings.discord.bot_id}")
    private String DISCORD_TOKEN;

    @Value("${csh.pings.url:https://pings.csh.rit.edu/}")
    private String url;


    private final Collection<GatewayIntent> intents = EnumSet.of(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES);
    private JDA jda = JDABuilder.create(DISCORD_TOKEN, intents).build();

    @PostConstruct
    public void readTemplates() {
        try {
            this.pingTemplate = readFully(new ClassPathResource("discord_ping_template.txt").getInputStream());
            this.verificationTemplate = readFully(new ClassPathResource("discord_verification_template.txt").getInputStream());
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    @PostConstruct
    public void setup() {
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // get the guilds the bot is in
        jda.getGuilds().forEach(guild -> {
            // get the users in the guild
            guild.getMembers().forEach(member -> {
                // add the user to the set
                usernameToId.put(member.getUser().getName() + "#" + member.getUser().getDiscriminator(), member.getUser());
            });
        });
        sendMessageTest("Creeper", "3621", "Discord bot is online!");
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        sendMessageTest("Creeper", "3621", "Discord bot is shutting down!");
        jda.shutdown();
        jda.awaitStatus(JDA.Status.SHUTDOWN);
        log.info("Discord bot has shut down successfully");
    }

    public void sendPing(Route route, DiscordServiceConfiguration config, String body) {
        this.log.info("Sending Ping to D-@" + config.getDiscordUsername() + "#" + config.getDiscordDiscriminator() + ", CSH-@" + config.getUsername());
        final Application app = route.getApplication();
        final String send = this.pingTemplate
                .replace("%%%BODY%%%", body)
                .replace("%%%APPLICATION%%%", app.getName());
        sendMessage(config, send);
    }

    @Override
    public void sendVerification(DiscordServiceConfiguration config) {
        final VerificationRequest vr = this.verificationRequestManager.generateVerification(config);
        this.log.debug("Created VR");
        final String url = this.url + (this.url.endsWith("/") ? "" : "/") + "verify?token=" + vr.getToken();
        final String verificationText = this.verificationTemplate.replace("%%%LINK%%%", url).replace("%%%USER%%%", config.getUsername());
        // send the verification message to the user
        sendMessage(config, verificationText);
    }

    public void sendMessage(DiscordServiceConfiguration config, String message) {
        User user = usernameToId.get(config.getDiscordUsername() + "#" + config.getDiscordDiscriminator());
        if (user == null) {
            log.error("Could not find user " + config.getDiscordUsername() + "#" + config.getDiscordDiscriminator());
            return;
        }
        user.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue());
    }

    private void sendMessageTest(String username, String discriminator, String message) {
        User user = usernameToId.get(username + "#" + discriminator);
        if (user == null) {
            log.error("Could not find user " + username + "#" + discriminator);
            return;
        }
        user.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue());
    }
}
