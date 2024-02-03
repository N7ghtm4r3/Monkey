import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.email.EmailPopulatingBuilder;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.internal.MailerRegularBuilderImpl;

import java.io.IOException;

import static com.tecknobit.apimanager.apis.ResourcesUtils.getResourceContent;
import static org.simplejavamail.api.mailer.config.TransportStrategy.SMTP;

public class MonkeyVerifier {

    public enum MonkeyTemplateTag {

        PROJECT_TAG("project");

        private final String tag;

        MonkeyTemplateTag(String tag) {
            this.tag = "<" + tag + ">";
        }

    }

    private static final Class<MonkeyVerifier> context = MonkeyVerifier.class;

    private static final String DEFAULT_MONKEY_TEMPLATE = "default_monkey_template.html";

    private static final String MONKEY_TEMPLATE;

    static {
        try {
            MONKEY_TEMPLATE = getResourceContent(DEFAULT_MONKEY_TEMPLATE, context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static final int WELL_KNOWN_SMTP_PORT = 25;

    private final String host;

    private final String from;

    private final Mailer mailer;

    public MonkeyVerifier(String host, String from) {
        this(host, WELL_KNOWN_SMTP_PORT, from);
    }

    public MonkeyVerifier(String host, int smtpPort, String from) {
        this(host, smtpPort, from, null);
    }

    public MonkeyVerifier(String host, String from, String password) {
        this(host, WELL_KNOWN_SMTP_PORT, from, password);
    }

    public MonkeyVerifier(String host, int smtpPort, String from, String password) {
        this.host = host;
        this.from = from;
        MailerRegularBuilderImpl mailerBuilder = MailerBuilder.withTransportStrategy(SMTP);
        if(password != null)
            mailerBuilder.withSMTPServer(host, smtpPort, from, password);
        else
            mailerBuilder.withSMTPServer(host, smtpPort, from);
        mailer = mailerBuilder.buildMailer();
    }

    public void sendPlainVerificationEmail(String fromText, String emailSubject, String emailBody, String ... recipients) {
        EmailPopulatingBuilder emailPopulatingBuilder = initEmailBuilder(fromText, emailSubject, recipients);
        Email email = emailPopulatingBuilder
                .withPlainText(emailBody)
                .buildEmail();
        mailer.sendMail(email);
    }

    public void sendDefaultTemplateVerificationEmail(String fromText, String emailSubject, MonkeyTemplate monkeyTemplate,
                                                     String ... recipients) {
        sendTemplateVerificationEmail(fromText, emailSubject, formatTemplate(MONKEY_TEMPLATE, monkeyTemplate),
                recipients);
    }

    /*public void sendCustomTemplateVerificationEmail(String fromText, String emailSubject, String templatePathname,
                                                    String ... recipients) throws IOException {
        sendTemplateVerificationEmail(fromText, emailSubject, getResourceFileRuntimeCopy(templatePathname, context),
                recipients);
    }

    public void sendCustomTemplateVerificationEmail(String fromText, String emailSubject, File customTemplate,
                                                    String ... recipients) {
        sendTemplateVerificationEmail(fromText, emailSubject, customTemplate, recipients);
    }*/

    private String formatTemplate(String template, MonkeyTemplate monkeyTemplate) {
        return template.replaceAll(MonkeyTemplateTag.PROJECT_TAG.tag, monkeyTemplate.getProject());
    }

    private void sendTemplateVerificationEmail(String fromText, String emailSubject, String template,
                                               String ... recipients) {
        EmailPopulatingBuilder emailPopulatingBuilder = initEmailBuilder(fromText, emailSubject, recipients);
        Email email = emailPopulatingBuilder
                .appendTextHTML(template)
                .buildEmail();
        mailer.sendMail(email);
    }

    private EmailPopulatingBuilder initEmailBuilder(String fromText, String emailSubject, String ... recipients) {
        EmailPopulatingBuilder emailPopulatingBuilder = EmailBuilder.startingBlank();
        for (String recipient : recipients)
            emailPopulatingBuilder.to(recipient);
        return emailPopulatingBuilder
                .from(fromText, from)
                .withSubject(emailSubject);
    }

    public String getHost() {
        return host;
    }

    public String getFrom() {
        return from;
    }

}