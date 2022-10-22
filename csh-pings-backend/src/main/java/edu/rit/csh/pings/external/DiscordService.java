package edu.rit.csh.pings.external;

import edu.rit.csh.pings.entities.DiscordServiceConfiguration;
import edu.rit.csh.pings.entities.Route;
import edu.rit.csh.pings.entities.VerificationRequest;
import edu.rit.csh.pings.managers.VerificationRequestManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DiscordService implements ExternalService<DiscordServiceConfiguration> {

    private static final Log log = LogFactory.getLog("pings.discord_service");
    private static final Log botLog = LogFactory.getLog("pings.discord_service.bot_stdout");

    @Value("${csh.pings.discord.bot_id}")
    static String DISCORD_TOKEN;

    // Define a process instance variable to run the discord service
    Process p;
    BufferedWriter botStdin;

    private final VerificationRequestManager verificationRequestManager;

    @PostConstruct
    public void setup() {
        // Run the discord service
//                p = new ProcessBuilder("csh-pings-backend/venv/bin/python3", "csh-pings-backend/src/discordBot.py", DISCORD_TOKEN)
//                        .start();
//        log.info("Runtime string: " + Runtime.getRuntime().toString());
        try {
            Runtime r = Runtime.getRuntime();
            if (r == null) {
                log.error("Runtime is null");
            } else {
                log.info("Runtime: " + r);
                // TODO: Don't commit credentials
                p = r.exec(new String[] {"csh-pings-backend/venv/bin/python3", "csh-pings-backend/src/discordBot.py", "MTAzMjQ2NjIxMjgwMzg1MDMyMQ.G14JXl.G9YG8mf9PSMuu7uVtJrVeNI4vxclQeIfDxMioU"});
//                p = r.exec(new String[] {"cat", "/dev/urandom"});
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Started Discord Service");
//             assign the process's stdin to the botStdin variable
        botStdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
        try {
            botStdin.write("send Creeper 3621 Bot Initialized");
            botStdin.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down Discord Service");
        try {
            botStdin.write("send Creeper 3621 Bot Shutting Down");
            botStdin.write("exit");
            botStdin.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.SECONDS)
    public void pingBot() {
        log.debug("Pinging Discord Bot");
        try {
            botStdin.write("ping");
            botStdin.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(fixedRate = 1, initialDelay = 5, timeUnit = TimeUnit.SECONDS)
    public void checkStdOut() {
        // read the process's stdout and log it
        try {
            if (p.getInputStream().available() > 0) {
                byte[] b = new byte[p.getInputStream().available()];
                p.getInputStream().read(b);
                String[] lines = new String(b).split("\n");
                for (String line : lines) {
                    botLog.info(line.strip());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // read the process's stderr and log it
        // The discord.py library logs to stderr even for info messages, and I still want to log them
        try {
            if (p.getErrorStream().available() > 0) {
                byte[] b = new byte[p.getErrorStream().available()];
                p.getErrorStream().read(b);
                String[] lines = new String(b).split("\n");
                for (String line : lines) {
                    botLog.info(line.strip());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPing(Route route, DiscordServiceConfiguration config, String body) {
        sendMessage(config, body);
    }

    @Override
    public void sendVerification(DiscordServiceConfiguration config) {
        final VerificationRequest vr = this.verificationRequestManager.generateVerification(config);
        sendMessage(config, "Please verify your account by");
    }

    private void sendMessage(DiscordServiceConfiguration config, String message) {
        // write the message to the discord service's stdin
        try {
            botStdin.write(String.format("send %s %s %s", config.getDiscordUsername(), config.getDiscordDiscriminator(), message));
            botStdin.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
