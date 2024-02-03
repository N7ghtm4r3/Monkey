package com.tecknobit.monkey;

import com.tecknobit.apimanager.apis.ResourcesUtils;
import com.tecknobit.monkey.MonkeyTemplate.MonkeyColorsScheme;
import com.tecknobit.monkey.MonkeyTemplate.MonkeyLogo;
import com.tecknobit.monkey.MonkeyTemplate.MonkeyTextTemplate;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.email.EmailPopulatingBuilder;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.internal.MailerRegularBuilderImpl;

import java.io.File;
import java.io.IOException;

import static com.tecknobit.apimanager.apis.ResourcesUtils.getResourceContent;
import static com.tecknobit.monkey.MonkeyTemplate.MonkeyTemplateTag.*;
import static org.simplejavamail.api.mailer.config.TransportStrategy.SMTP;

public class MonkeyVerifier {

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
        sendEmail(emailPopulatingBuilder.withPlainText(emailBody).buildEmail());
    }

    public void sendDefaultTemplateVerificationEmail(String fromText, String emailSubject, MonkeyTemplate monkeyTemplate,
                                                     String ... recipients) {
        sendTemplateVerificationEmail(fromText, emailSubject, formatTemplate(MONKEY_TEMPLATE, monkeyTemplate),
                recipients);
    }

    // TO-DO: WARN USER THAT THE FILE MUST BE IN THE RESOURCES FOLDER
    public void sendCustomTemplateVerificationEmail(String templatePathname, String fromText, String emailSubject,
                                                    int verificationCode, String ... recipients) throws IOException {
        sendCustomTemplateVerificationEmail(templatePathname, fromText, emailSubject, String.valueOf(verificationCode),
                recipients);
    }

    // TO-DO: WARN USER THAT THE FILE MUST BE IN THE RESOURCES FOLDER
    public void sendCustomTemplateVerificationEmail(String templatePathname, String fromText, String emailSubject,
                                                    String verificationCode, String ... recipients) throws IOException {
        sendCustomTemplateVerificationEmail(ResourcesUtils.getResourceFileRuntimeCopy(templatePathname, context),
                fromText, emailSubject, verificationCode, recipients);
    }

    // TO-DO: WARN USER THAT THE FILE MUST BE IN THE RESOURCES FOLDER
    public void sendCustomTemplateVerificationEmail(File customTemplate, String fromText, String emailSubject,
                                                    int verificationCode, String ... recipients) throws IOException {
        sendCustomTemplateVerificationEmail(customTemplate, fromText, emailSubject, String.valueOf(verificationCode),
                recipients);
    }

    // TO-DO: WARN USER THAT THE FILE MUST BE IN THE RESOURCES FOLDER
    public void sendCustomTemplateVerificationEmail(File customTemplate, String fromText, String emailSubject,
                                                    String verificationCode, String ... recipients) throws IOException {
        String customContentTemplate = ResourcesUtils.getResourceContent(customTemplate.getName(), context);
        sendTemplateVerificationEmail(fromText, emailSubject, formatTemplate(customContentTemplate,
                new MonkeyTemplate(verificationCode)), recipients);
    }

    private String formatTemplate(String template, MonkeyTemplate monkeyTemplate) {
        String verificationTag = VERIFICATION_CODE_TAG.getTag();
        if(!template.contains(verificationTag))
            throw new IllegalArgumentException("verification_code tag is missing!");
        MonkeyColorsScheme colorsScheme = monkeyTemplate.getColorsScheme();
        MonkeyTextTemplate monkeyTextTemplate = monkeyTemplate.getMonkeyTextTemplate();
        MonkeyLogo monkeyLogo = monkeyTemplate.getMonkeyLogo();
        template = template.replaceAll(verificationTag, monkeyTemplate.getVerificationCode());
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
            return template.replaceAll(TITLE_TAG.getTag(), monkeyTextTemplate.getTitle())
                    .replaceAll(DESCRIPTION_TAG.getTag(), monkeyTextTemplate.getDescription())
                    .replaceAll(FOOTER_TEXT_TAG.getTag(), monkeyTextTemplate.getFooterText())
                    .replaceAll(REASONS_TEXT_TAG.getTag(), monkeyTextTemplate.getReasonsText());
        }
        return template;
    }

    private void sendTemplateVerificationEmail(String fromText, String emailSubject, String template,
                                               String ... recipients) {
        EmailPopulatingBuilder emailPopulatingBuilder = initEmailBuilder(fromText, emailSubject, recipients);
        sendEmail(emailPopulatingBuilder.appendTextHTML(template).buildEmail());
    }

    private EmailPopulatingBuilder initEmailBuilder(String fromText, String emailSubject, String ... recipients) {
        EmailPopulatingBuilder emailPopulatingBuilder = EmailBuilder.startingBlank();
        for (String recipient : recipients)
            emailPopulatingBuilder.to(recipient);
        return emailPopulatingBuilder
                .from(fromText, from)
                .withSubject(emailSubject);
    }

    private void sendEmail(Email email) {
        mailer.sendMail(email);
        System.out.println(email.getId().split("@")[0].replaceAll("<", ""));
    }

    public void verifyCodeSent(String emailId, MonkeyVerificationActions actions) {

    }

    public String getHost() {
        return host;
    }

    public String getFrom() {
        return from;
    }

}
