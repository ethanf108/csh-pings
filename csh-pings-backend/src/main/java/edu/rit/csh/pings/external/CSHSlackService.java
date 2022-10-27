package edu.rit.csh.pings.external;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.conversations.ConversationsOpenResponse;
import com.slack.api.methods.response.users.UsersLookupByEmailResponse;
import edu.rit.csh.pings.entities.CSHSlackServiceConfiguration;
import edu.rit.csh.pings.entities.Route;
import edu.rit.csh.pings.entities.VerificationRequest;
import edu.rit.csh.pings.managers.VerificationRequestManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CSHSlackService implements ExternalService<CSHSlackServiceConfiguration> {

    private final VerificationRequestManager verificationRequestManager;

    @Value("${csh.pings.url:https://pings.csh.rit.edu/}")
    private String url;
    @Value("${csh.pings.slack.bot_token}")
    private String SLACK_BOT_TOKEN;
    private MethodsClient slackClient;

    @PostConstruct
    private void setupSlackClient() {
        if (this.SLACK_BOT_TOKEN == null) {
            throw new Error("Slack token missing.");
        }
        this.slackClient = Slack.getInstance().methods();
    }

    @Override
    public void sendPing(Route route, CSHSlackServiceConfiguration config, String body) {
        try {
            final ConversationsOpenResponse openResponse = this.slackClient.conversationsOpen(r -> r.token(this.SLACK_BOT_TOKEN).users(List.of(config.getSlackUID())));
            if (!openResponse.isOk()) {
                throw new RuntimeException("Error opening DM: " + openResponse.getError());
            }
            final ChatPostMessageResponse sendResponse = this.slackClient.chatPostMessage(r -> r
                    .token(this.SLACK_BOT_TOKEN)
                    .channel(openResponse.getChannel().getId())
                    .text("*" + route.getApplication().getName() + "* - *" + route.getName() + "*\n\n" + body)
                    .mrkdwn(true));
            if (!sendResponse.isOk()) {
                throw new RuntimeException("Error sending message: " + sendResponse.getError());
            }
        } catch (SlackApiException e) {
            throw new RuntimeException("Error with the Slack API", e);
        } catch (IOException e) {
            throw new RuntimeException("Error communicating with Slack (IOException)", e);
        }
    }

    @Override
    public void sendVerification(CSHSlackServiceConfiguration config) {
        try {
            final UsersLookupByEmailResponse response = this.slackClient.usersLookupByEmail(r -> r.token(this.SLACK_BOT_TOKEN).email(config.getSlackEmail()));
            if (!response.isOk()) {
                throw new IllegalArgumentException("User not found in CSH Slack: " + response.getError());
            }
            config.setSlackUID(response.getUser().getId());
            final VerificationRequest vr = this.verificationRequestManager.generateVerification(config);
            final ChatPostMessageResponse sendResponse = this.slackClient.chatPostMessage(r -> r
                    .token(this.SLACK_BOT_TOKEN)
                    .channel(config.getSlackUID())
                    .text("<" + this.url + "verify?token=" + vr.getToken() + "|Click here> to verify this Slack account for pings")
                    .mrkdwn(true));
        } catch (SlackApiException e) {
            throw new RuntimeException("Error with the Slack API", e);
        } catch (IOException e) {
            throw new RuntimeException("Error communicating with Slack (IOException)", e);
        }
    }
}
