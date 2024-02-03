package com.tecknobit.monkey;

import com.tecknobit.apimanager.apis.ResourcesUtils;
import com.tecknobit.monkey.MonkeyTemplate.MonkeyColorsScheme;
import com.tecknobit.monkey.MonkeyTemplate.MonkeyLogo;
import com.tecknobit.monkey.MonkeyTemplate.MonkeyTextTemplate;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.email.EmailPopulatingBuilder;
import org.simplejavamail.api.email.Recipient;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.internal.MailerRegularBuilderImpl;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Properties;

import static com.tecknobit.apimanager.apis.ResourcesUtils.getResourceContent;
import static com.tecknobit.monkey.MonkeyTemplate.MonkeyTemplateTag.*;
import static com.tecknobit.monkey.MonkeyVerifier.KeepEmailValid.ALWAYS_KEEP_VALID;
import static org.simplejavamail.api.mailer.config.TransportStrategy.SMTP;

public class MonkeyVerifier {

    public enum KeepEmailValid {

        FIVE_MINUTES(5 * 60000),

        FIFTEEN_MINUTES(15 * 60000),

        THIRTY_MINUTES(30 * 60000),

        ONE_HOUR(60 * 60000),

        ALWAYS_KEEP_VALID(-1);

        private final long time;

        KeepEmailValid(long time) {
            this.time = time;
        }

        public long getTime() {
            return time;
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

    public static final String VERIFICATION_CODE_KEY = "verification_code";

    public static final int WELL_KNOWN_SMTP_PORT = 25;

    private final Properties emailsSent;

    private final String host;

    private final String from;

    private final Mailer mailer;

    private final KeepEmailValid keepEmailValid;

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
        this(host, smtpPort, ALWAYS_KEEP_VALID, from, password);
    }

    public MonkeyVerifier(String host, KeepEmailValid keepEmailValid, String from) {
        this(host, WELL_KNOWN_SMTP_PORT, keepEmailValid, from);
    }

    public MonkeyVerifier(String host, int smtpPort, KeepEmailValid keepEmailValid, String from) {
        this(host, smtpPort, keepEmailValid, from, null);
    }

    public MonkeyVerifier(String host, KeepEmailValid keepEmailValid, String from, String password) {
        this(host, WELL_KNOWN_SMTP_PORT, keepEmailValid, from, password);
    }

    public MonkeyVerifier(String host, int smtpPort, KeepEmailValid keepEmailValid, String from, String password) {
        this.host = host;
        this.keepEmailValid = keepEmailValid;
        this.from = from;
        MailerRegularBuilderImpl mailerBuilder = MailerBuilder.withTransportStrategy(SMTP);
        if(password != null)
            mailerBuilder.withSMTPServer(host, smtpPort, from, password);
        else
            mailerBuilder.withSMTPServer(host, smtpPort, from);
        mailer = mailerBuilder.buildMailer();
        emailsSent = new Properties();
    }

    public void sendPlainVerificationEmail(String fromText, String emailSubject, String emailBody, String ... recipients) {
        EmailPopulatingBuilder emailPopulatingBuilder = initEmailBuilder(fromText, emailSubject, recipients);
        MonkeyData monkeyData = formatVerificationCode(emailBody);
        sendEmail(emailPopulatingBuilder.withPlainText(monkeyData.content).buildEmail(), monkeyData.verificationCode);
    }

    public void sendDefaultTemplateVerificationEmail(String fromText, String emailSubject, MonkeyTemplate monkeyTemplate,
                                                     String ... recipients) {
        sendTemplateVerificationEmail(fromText, emailSubject, formatTemplate(MONKEY_TEMPLATE, monkeyTemplate),
                recipients);
    }

    // TO-DO: WARN USER THAT THE FILE MUST BE IN THE RESOURCES FOLDER
    public void sendCustomTemplateVerificationEmail(String templatePathname, String fromText, String emailSubject,
                                                    String ... recipients) throws IOException {
        sendCustomTemplateVerificationEmail(ResourcesUtils.getResourceFileRuntimeCopy(templatePathname, context),
                fromText, emailSubject, recipients);
    }

    // TO-DO: WARN USER THAT THE FILE MUST BE IN THE RESOURCES FOLDER
    public void sendCustomTemplateVerificationEmail(File customTemplate, String fromText, String emailSubject,
                                                    String ... recipients) throws IOException {
        String customContentTemplate = ResourcesUtils.getResourceContent(customTemplate.getName(), context);
        sendTemplateVerificationEmail(fromText, emailSubject, formatTemplate(customContentTemplate, null),
                recipients);
    }

    private MonkeyData formatTemplate(String template, MonkeyTemplate monkeyTemplate) {
        MonkeyData monkeyData = formatVerificationCode(template);
        template = monkeyData.content;
        if(monkeyTemplate != null) {
            MonkeyColorsScheme colorsScheme = monkeyTemplate.getColorsScheme();
            MonkeyTextTemplate monkeyTextTemplate = monkeyTemplate.getMonkeyTextTemplate();
            MonkeyLogo monkeyLogo = monkeyTemplate.getMonkeyLogo();
            if(colorsScheme != null) {
                template = template.replaceAll(PRIMARY_COLOR_TAG.getTag(), colorsScheme.getPrimaryColor())
                        .replaceAll(SECONDARY_COLOR_TAG.getTag(), colorsScheme.getSecondaryColor())
                        .replaceAll(TERTIARY_COLOR_TAG.getTag(), colorsScheme.getTertiaryColor())
                        .replaceAll(TEXT_COLOR_TAG.getTag(), colorsScheme.getTextColor());
            }
            if(monkeyLogo != null) {
                template = template.replaceAll(LOGO_LINK_TAG.getTag(), monkeyLogo.getLogoLink())
                        .replaceAll(LOGO_URL_TAG.getTag(), monkeyLogo.getLogoUrl());
            }
            if(monkeyTextTemplate != null) {
                template = template.replaceAll(TITLE_TAG.getTag(), monkeyTextTemplate.getTitle())
                        .replaceAll(DESCRIPTION_TAG.getTag(), monkeyTextTemplate.getDescription())
                        .replaceAll(FOOTER_TEXT_TAG.getTag(), monkeyTextTemplate.getFooterText())
                        .replaceAll(REASONS_TEXT_TAG.getTag(), monkeyTextTemplate.getReasonsText());
            }
        }
        monkeyData.setContent(template);
        return monkeyData;
    }

    private MonkeyData formatVerificationCode(String content) {
        String verificationTag = VERIFICATION_CODE_TAG.getTag();
        if(!content.contains(verificationTag))
            throw new IllegalArgumentException("verification_code tag is missing!");
        // TO-DO: TO change with real workflow
        String verificationCode = "1";//String.valueOf(new Random().nextInt(1));
        return new MonkeyData(
            content.replaceAll(verificationTag, verificationCode),
            verificationCode
        );
    }

    private void sendTemplateVerificationEmail(String fromText, String emailSubject, MonkeyData monkeyData,
                                               String ... recipients) {
        EmailPopulatingBuilder emailPopulatingBuilder = initEmailBuilder(fromText, emailSubject, recipients);
        sendEmail(emailPopulatingBuilder.appendTextHTML(monkeyData.content).buildEmail(), monkeyData.verificationCode);
    }

    private EmailPopulatingBuilder initEmailBuilder(String fromText, String emailSubject, String ... recipients) {
        EmailPopulatingBuilder emailPopulatingBuilder = EmailBuilder.startingBlank();
        for (String recipient : recipients)
            emailPopulatingBuilder.to(recipient);
        return emailPopulatingBuilder
                .from(fromText, from)
                .withSubject(emailSubject);
    }

    private void sendEmail(Email email, String verificationCode) {
        mailer.sendMail(email);
        for (Recipient recipient : email.getToRecipients()) {
            String emailAddress = recipient.getAddress();
            emailsSent.put(emailAddress, new EmailSent(emailAddress, verificationCode));
        }
    }

    public void verifyCodeSent(String userEmail, String userCode, MonkeyVerificationActions actions) {
        removeExpiredVerificationEmails();
        EmailSent emailSent = (EmailSent) emailsSent.get(userEmail);
        if(emailSent != null) {
            if(userCode.equals(emailSent.verificationCode)) {
                emailsSent.remove(userEmail);
                actions.onSuccess();
            } else
                actions.onFailure();
        } else
            actions.onFailure();
    }

    private void removeExpiredVerificationEmails() {
        if(keepEmailValid != ALWAYS_KEEP_VALID) {
            long currentTimestamp = System.currentTimeMillis();
            for (Iterator<Object> iterator = emailsSent.elements().asIterator(); iterator.hasNext(); ) {
                EmailSent emailSent = (EmailSent) iterator.next();
                if((currentTimestamp - emailSent.timestamp) >= keepEmailValid.time)
                    emailsSent.remove(emailSent.email);
            }
        }
    }

    public String getHost() {
        return host;
    }

    public String getFrom() {
        return from;
    }

    public KeepEmailValid getKeepEmailValid() {
        return keepEmailValid;
    }

    private static class MonkeyData {

        private String content;

        private final String verificationCode;

        public MonkeyData(String content, String verificationCode) {
            this.content = content;
            this.verificationCode = verificationCode;
        }

        public void setContent(String content) {
            this.content = content;
        }

    }

    private static final class EmailSent implements Serializable {

        private final String email;

        private final long timestamp;

        private final String verificationCode;

        public EmailSent(String email, String verificationCode) {
            this.email = email;
            this.timestamp = System.currentTimeMillis();
            this.verificationCode = verificationCode;
        }

    }

}
