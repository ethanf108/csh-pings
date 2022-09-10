package edu.rit.csh.pings.external;

import edu.rit.csh.pings.entities.EmailServiceConfiguration;
import edu.rit.csh.pings.entities.Route;
import edu.rit.csh.pings.entities.VerificationRequest;
import edu.rit.csh.pings.managers.VerificationRequestManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public final class EmailService extends Authenticator implements ExternalService<EmailServiceConfiguration> {

    private final VerificationRequestManager verificationRequestManager;

    private Properties mailProps;

    @Value("${csh.pings.email.username}")
    private String emailUsername;

    @Value("${csh.pings.email.password}")
    private String emailPassword;

    @Value("${csh.pings.email.host}")
    private String emailHost;

    @Value("${csh.pings.email.port}")
    private String emailPort;

    private String pingTemplate;
    private String verificationTemplate;

    private static String readFully(InputStream in) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(1024 * 1024);
        byte[] block = new byte[1024];
        int read;
        while ((read = in.read(block)) == block.length) {
            buf.put(block);
        }
        if (read != -1) {
            buf.put(block, 0, read);
        }
        return new String(buf.array(), 0, buf.position());
    }

    @PostConstruct
    private void setup() {
        this.mailProps = new Properties();
        this.mailProps.put("mail.smtp.host", this.emailHost);
        this.mailProps.put("mail.smtp.port", this.emailPort);
        this.mailProps.put("mail.smtp.auth", "true");
        this.mailProps.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        try {
            this.pingTemplate = readFully(new ClassPathResource("ping_template.html").getInputStream());
            this.verificationTemplate = readFully(new ClassPathResource("verification_template.html").getInputStream());
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    private void sendEmail(String email, String title, String subject, String bodyHTML) {
        if (email.contains(" ") || email.contains(",")) {
            throw new IllegalArgumentException("Invalid email");
        }
        try {
            Session session = Session.getInstance(this.mailProps, this);
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-Type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setFrom(new InternetAddress("pings@csh.rit.edu", title));
            msg.setReplyTo(InternetAddress.parse("DONOTREPLY@csh.rit.edu", false));
            msg.setSubject(subject, "UTF-8");
            msg.setContent(bodyHTML, "text/html");
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
            Transport.send(msg);
        } catch (AddressException e) {
            throw new IllegalArgumentException("Invalid email", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Email failed", e);
        }
    }

    @Override
    public void sendPing(Route route, EmailServiceConfiguration config, String body) {
        final String bodyHTML = this.pingTemplate
                .replace("%%%BODY%%%", body)
                .replace("%%%APPLICATION%%%", route.getApplication().getName())
                .replace("%%%ROUTE%%%", route.getName())
                .replace("%%%DATE%%%", new Date().toString());
        this.sendEmail(config.getToEmail(), "CSH Pings - " + route.getApplication().getName(), "Ping from " + route.getApplication().getName(), bodyHTML);
    }

    @Override
    public void sendVerification(EmailServiceConfiguration config) {
        final VerificationRequest vr = this.verificationRequestManager.generateVerification(config);
        final String bodyHTML = this.verificationTemplate
                .replace("%%%USERNAME%%%", config.getUsername())
                .replace("%%%TOKEN%%%", vr.getToken())
                .replace("%%%DATE%%%", new Date().toString());
        this.sendEmail(config.getToEmail(), "CSH Pings", "Verify your Email Address", bodyHTML);
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(EmailService.this.emailUsername, EmailService.this.emailPassword);
    }
}
