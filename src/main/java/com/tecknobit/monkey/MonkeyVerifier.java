package com.tecknobit.monkey;

import com.tecknobit.apimanager.apis.ResourcesUtils;
import com.tecknobit.monkey.MonkeyTemplate.MonkeyColorsScheme;
import com.tecknobit.monkey.MonkeyTemplate.MonkeyLogo;
import com.tecknobit.monkey.MonkeyTemplate.MonkeyTemplateTag;
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
import static org.apache.commons.lang3.RandomStringUtils.*;
import static org.simplejavamail.api.mailer.config.TransportStrategy.SMTP;

/**
 * The {@code MonkeyVerifier} class is useful to manage the workflow to send the verification email and check the codes
 * sent
 *
 * @author N7ghtm4r3 - Tecknobit
 */
public class MonkeyVerifier {

    /**
     * {@code KeepEmailValid} list of available times to keep the verification emails valid
     */
    public enum KeepEmailValid {

        /**
         * {@code FIVE_MINUTES} the verification email will be kept valid for five minutes from its sent,
         * then will expire
         */
        FIVE_MINUTES(5 * 60000),

        /**
         * {@code FIFTEEN_MINUTES} the verification email will be kept valid for fifteen minutes from its sent,
         * then will expire
         */
        FIFTEEN_MINUTES(15 * 60000),

        /**
         * {@code THIRTY_MINUTES} the verification email will be kept valid for thirty minutes from its sent,
         * then will expire
         */
        THIRTY_MINUTES(30 * 60000),

        /**
         * {@code ONE_HOUR} the verification email will be kept valid for one hour from its sent,
         * then will expire
         */
        ONE_HOUR(60 * 60000),

        /**
         * {@code ALWAYS_KEEP_VALID} the verification email never expires
         */
        ALWAYS_KEEP_VALID(-1);

        /**
         * {@code time} the time value after consider a verification email expired
         */
        private final long time;

        /**
         * Constructor to init the {@link KeepEmailValid} class
         *
         * @param time:{@code time} the time value after consider a verification email expired
         *
         */
        KeepEmailValid(long time) {
            this.time = time;
        }

        /**
         * Method to get {@link #time} instance <br>
         * No-any params required
         *
         * @return {@link #time} instance as long
         */
        public long getTime() {
            return time;
        }

    }

    /**
     * {@code VerificationCodeType} list of available verification code type to sent with the emails
     */
    public enum VerificationCodeType {

        /**
         * {@code NUMBERS} the verification code must contain only digits e.g. 212230
         */
        NUMBERS,

        /**
         * {@code CHARACTERS} the verification code must contain only characters e.g. glmmpm
         */
        CHARACTERS,

        /**
         * {@code ALPHANUMERIC} the verification code can contain both digits and character e.g. g1l2m3
         */
        ALPHANUMERIC

    }

    /**
     * {@code context} the context from the {@link ResourcesUtils} is used
     */
    private static final Class<MonkeyVerifier> context = MonkeyVerifier.class;

    /**
     * {@code DEFAULT_MONKEY_TEMPLATE} the pathname of the default monkey template to use in the verification emails
     *
     * @apiNote this pathname belongs to the <b>default_monkey_template.html</b> resource file
     */
    private static final String DEFAULT_MONKEY_TEMPLATE = "default_monkey_template.html";

    /**
     * {@code MONKEY_TEMPLATE} the default monkey template content
     */
    private static final String MONKEY_TEMPLATE;

    static {
        try {
            MONKEY_TEMPLATE = getResourceContent(DEFAULT_MONKEY_TEMPLATE, context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@code VERIFICATION_CODE_KEY} the verification code key to use to check if the code sent by the user
     * matches with the correct verification code of the email sent
     */
    public static final String VERIFICATION_CODE_KEY = "verification_code";

    /**
     * {@code WELL_KNOWN_SMTP_PORT} the value of the well known port for the SMTP protocol
     */
    public static final int WELL_KNOWN_SMTP_PORT = 25;

    /**
     * {@code emailsSent} list of the emails sent during the current session
     *
     * @apiNote when the session has been restarted, for example the server is restarted, the list will be empty and
     * the past verification emails sent will be considered expired
     */
    private final Properties emailsSent;

    /**
     * {@code host} the host where the SMTP service is running
     */
    private final String host;

    /**
     * {@code from} the user that will be considered the "From" in the verification email e.g. server@monkey.org
     *
     * @apiNote this must be one of the users added to the <b>James</b> service
     */
    private final String from;

    /**
     * {@code mailer} the manager to send the verification emails
     */
    private final Mailer mailer;

    /**
     * {@code keepEmailValid} value of the {@link KeepEmailValid}
     */
    private final KeepEmailValid keepEmailValid;

    /**
     * {@code verificationCodeType} value of the {@link VerificationCodeType}
     */
    private final VerificationCodeType verificationCodeType;

    /**
     * Constructor to init the {@link MonkeyVerifier} class
     *
     * @param host: the host where the SMTP service is running
     * @param verificationCodeType: value of the {@link VerificationCodeType}
     * @param from: the user that will be considered the "From" in the verification email
     *
     */
    public MonkeyVerifier(String host, VerificationCodeType verificationCodeType, String from) {
        this(host, WELL_KNOWN_SMTP_PORT, verificationCodeType, from);
    }

    /**
     * Constructor to init the {@link MonkeyVerifier} class
     *
     * @param host: the host where the SMTP service is running
     * @param smtpPort: the port where the SMTP service is running
     * @param verificationCodeType: value of the {@link VerificationCodeType}
     * @param from: the user that will be considered the "From" in the verification email
     *            
     */
    public MonkeyVerifier(String host, int smtpPort, VerificationCodeType verificationCodeType, String from) {
        this(host, smtpPort, verificationCodeType, from, null);
    }

    /**
     * Constructor to init the {@link MonkeyVerifier} class
     *
     * @param host: the host where the SMTP service is running
     * @param verificationCodeType: value of the {@link VerificationCodeType}
     * @param from: the user that will be considered the "From" in the verification email
     * @param password: the password of the user, this must be the same used when the {@link #from} has been added
     *
     */
    public MonkeyVerifier(String host, VerificationCodeType verificationCodeType, String from, String password) {
        this(host, WELL_KNOWN_SMTP_PORT, verificationCodeType, from, password);
    }

    /**
     * Constructor to init the {@link MonkeyVerifier} class
     *
     * @param host: the host where the SMTP service is running
     * @param smtpPort: the port where the SMTP service is running
     * @param verificationCodeType: value of the {@link VerificationCodeType}
     * @param from: the user that will be considered the "From" in the verification email
     * @param password: the password of the user, this must be the same used when the {@link #from} has been added
     *
     */
    public MonkeyVerifier(String host, int smtpPort, VerificationCodeType verificationCodeType, String from,
                          String password) {
        this(host, smtpPort, verificationCodeType, ALWAYS_KEEP_VALID, from, password);
    }

    /**
     * Constructor to init the {@link MonkeyVerifier} class
     *
     * @param host: the host where the SMTP service is running
     * @param verificationCodeType: value of the {@link VerificationCodeType}
     * @param keepEmailValid: value of the {@link KeepEmailValid}
     * @param from: the user that will be considered the "From" in the verification email
     *
     */
    public MonkeyVerifier(String host, VerificationCodeType verificationCodeType, KeepEmailValid keepEmailValid,
                          String from) {
        this(host, WELL_KNOWN_SMTP_PORT, verificationCodeType, keepEmailValid, from);
    }

    /**
     * Constructor to init the {@link MonkeyVerifier} class
     *
     * @param host: the host where the SMTP service is running
     * @param smtpPort: the port where the SMTP service is running
     * @param verificationCodeType: value of the {@link VerificationCodeType}
     * @param keepEmailValid: value of the {@link KeepEmailValid}
     * @param from: the user that will be considered the "From" in the verification email
     */
    public MonkeyVerifier(String host, int smtpPort, VerificationCodeType verificationCodeType,
                          KeepEmailValid keepEmailValid, String from) {
        this(host, smtpPort, verificationCodeType, keepEmailValid, from, null);
    }

    /**
     * Constructor to init the {@link MonkeyVerifier} class
     *
     * @param host: the host where the SMTP service is running
     * @param verificationCodeType: value of the {@link VerificationCodeType}
     * @param keepEmailValid: value of the {@link KeepEmailValid}
     * @param from: the user that will be considered the "From" in the verification email 
     * @param password: the password of the user, this must be the same used when the {@link #from} has been added
     *
     */
    public MonkeyVerifier(String host, VerificationCodeType verificationCodeType, KeepEmailValid keepEmailValid,
                          String from, String password) {
        this(host, WELL_KNOWN_SMTP_PORT, verificationCodeType, keepEmailValid, from, password);
    }

    /**
     * Constructor to init the {@link MonkeyVerifier} class
     *
     * @param host: the host where the SMTP service is running
     * @param smtpPort: the port where the SMTP service is running
     * @param verificationCodeType: value of the {@link VerificationCodeType}
     * @param keepEmailValid: value of the {@link KeepEmailValid}
     * @param from: the user that will be considered the "From" in the verification email
     * @param password: the password of the user, this must be the same used when the {@link #from} has been added
     *
     */
    public MonkeyVerifier(String host, int smtpPort, VerificationCodeType verificationCodeType,
                          KeepEmailValid keepEmailValid, String from, String password) {
        this.host = host;
        this.verificationCodeType = verificationCodeType;
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

    /**
     * Method to send a verification email with a plain text body
     * 
     * @param fromText: the text for the "from" section of the email
     * @param emailSubject: the subject for the verification email
     * @param emailBody: the plain body text of the email 
     * @param recipients: the recipients to send the verification email
     *                  
     * @implNote the verification code is unique for each email send in batch 
     * @apiNote e.g. of plain verification email -> Hi, your verification code is 212230
     */
    public void sendPlainVerificationEmail(String fromText, String emailSubject, String emailBody, String ... recipients) {
        for(String recipient : recipients) {
            EmailPopulatingBuilder emailPopulatingBuilder = initEmailBuilder(fromText, emailSubject);
            emailPopulatingBuilder.to(recipient);
            MonkeyData monkeyData = formatVerificationCode(emailBody);
            sendEmail(emailPopulatingBuilder.withPlainText(monkeyData.content).buildEmail(), monkeyData.verificationCode);
        }
    }

    /**
     * Method to send a verification email with the default monkey template
     *
     * @param fromText: the text for the "from" section of the email
     * @param emailSubject: the subject for the verification email
     * @param monkeyTemplate: the template to use in the verification email
     * @param recipients: the recipients to send the verification email
     *
     * @implNote the verification code is unique for each email send in batch
     */
    public void sendDefaultTemplateVerificationEmail(String fromText, String emailSubject, MonkeyTemplate monkeyTemplate,
                                                     String ... recipients) {
        sendTemplateVerificationEmail(fromText, emailSubject, MONKEY_TEMPLATE, monkeyTemplate, recipients);
    }

    /**
     * Method to send a verification email with the default monkey template
     *
     * @param templatePathname: the pathname of the file to use as template for the verification email
     * @param fromText: the text for the "from" section of the email
     * @param emailSubject: the subject for the verification email
     * @param recipients: the recipients to send the verification email
     *
     * @implNote the verification code is unique for each email send in batch
     * @implSpec you need to place your custom template file in the resources folder to works correctly
     * @throws IOException when an error occurred during operations with the template file
     */
    public void sendCustomTemplateVerificationEmail(String templatePathname, String fromText, String emailSubject,
                                                    String ... recipients) throws IOException {
        sendCustomTemplateVerificationEmail(ResourcesUtils.getResourceFileRuntimeCopy(templatePathname, context),
                fromText, emailSubject, recipients);
    }

    /**
     * Method to send a verification email with the default monkey template
     *
     * @param customTemplate: the file to use as template for the verification email
     * @param fromText: the text for the "from" section of the email
     * @param emailSubject: the subject for the verification email
     * @param recipients: the recipients to send the verification email
     *
     * @implNote the verification code is unique for each email send in batch
     * @implSpec you need to place your custom template file in the resources folder to works correctly
     * @throws IOException when an error occurred during operations with the template file
     */
    public void sendCustomTemplateVerificationEmail(File customTemplate, String fromText, String emailSubject,
                                                    String ... recipients) throws IOException {
        String customContentTemplate = getResourceContent(customTemplate.getName(), context);
        sendTemplateVerificationEmail(fromText, emailSubject, customContentTemplate, null, recipients);
    }

    /**
     * Method to send a verification email with the default monkey template
     *
     * @param fromText: the text for the "from" section of the email
     * @param emailSubject: the subject for the verification email
     * @param templateContent: the content of the template to use
     * @param monkeyTemplate: the templateContent to use in the verification email
     * @param recipients: the recipients to send the verification email
     *
     * @implNote the verification code is unique for each email send in batch
     */
    private void sendTemplateVerificationEmail(String fromText, String emailSubject, String templateContent,
                                               MonkeyTemplate monkeyTemplate, String ... recipients) {
        for (String recipient : recipients) {
            EmailPopulatingBuilder emailPopulatingBuilder = initEmailBuilder(fromText, emailSubject);
            emailPopulatingBuilder.to(recipient);
            MonkeyData monkeyData = formatTemplate(templateContent, monkeyTemplate);
            sendEmail(emailPopulatingBuilder.appendTextHTML(monkeyData.content).buildEmail(), monkeyData.verificationCode);
        }
    }

    /**
     * Method to create an {@link EmailPopulatingBuilder}
     *
     * @param fromText: the text for the "from" section of the email
     * @param emailSubject: the subject for the verification email
     * @return an email builder as {@link EmailPopulatingBuilder}
     */
    private EmailPopulatingBuilder initEmailBuilder(String fromText, String emailSubject) {
        return EmailBuilder.startingBlank()
                .from(fromText, from)
                .withSubject(emailSubject);
    }

    /**
     * Method to send a verification email
     *
     * @param email: the email object to send as email
     * @param verificationCode: the verification code attached to the verification email
     */
    private void sendEmail(Email email, String verificationCode) {
        mailer.sendMail(email);
        for (Recipient recipient : email.getToRecipients()) {
            String emailAddress = recipient.getAddress();
            emailsSent.put(emailAddress, new EmailSent(emailAddress, verificationCode));
        }
    }

    /**
     * Method to format the content of a template replacing with the {@link MonkeyTemplate} details
     * @param contentTemplate: the base content template
     * @param monkeyTemplate: the monkey template to create the verification email template
     * @return the template formatted as {@link MonkeyData}
     */
    private MonkeyData formatTemplate(String contentTemplate, MonkeyTemplate monkeyTemplate) {
        MonkeyData monkeyData = formatVerificationCode(contentTemplate);
        contentTemplate = monkeyData.content;
        if(monkeyTemplate != null) {
            MonkeyColorsScheme colorsScheme = monkeyTemplate.getColorsScheme();
            MonkeyTextTemplate monkeyTextTemplate = monkeyTemplate.getMonkeyTextTemplate();
            MonkeyLogo monkeyLogo = monkeyTemplate.getMonkeyLogo();
            if(colorsScheme != null) {
                contentTemplate = contentTemplate.replaceAll(PRIMARY_COLOR_TAG.getTag(), colorsScheme.getPrimaryColor())
                        .replaceAll(SECONDARY_COLOR_TAG.getTag(), colorsScheme.getSecondaryColor())
                        .replaceAll(TERTIARY_COLOR_TAG.getTag(), colorsScheme.getTertiaryColor())
                        .replaceAll(TEXT_COLOR_TAG.getTag(), colorsScheme.getTextsColor());
            }
            if(monkeyLogo != null) {
                contentTemplate = contentTemplate.replaceAll(LOGO_LINK_TAG.getTag(), monkeyLogo.getLogoLink())
                        .replaceAll(LOGO_URL_TAG.getTag(), monkeyLogo.getLogoUrl());
            }
            if(monkeyTextTemplate != null) {
                contentTemplate = contentTemplate.replaceAll(TITLE_TAG.getTag(), monkeyTextTemplate.getTitle())
                        .replaceAll(DESCRIPTION_TAG.getTag(), monkeyTextTemplate.getDescription())
                        .replaceAll(FOOTER_TEXT_TAG.getTag(), monkeyTextTemplate.getFooterText())
                        .replaceAll(REASONS_TEXT_TAG.getTag(), monkeyTextTemplate.getReasonsText());
            }
        }
        monkeyData.setContent(contentTemplate);
        return monkeyData;
    }

    /**
     * Method to format the content of a template replacing the {@link MonkeyTemplateTag#VERIFICATION_CODE_TAG} with
     * a verification code generated
     * @param contentTemplate: the base content template
     *
     * @return the template formatted as {@link MonkeyData}
     * @throws IllegalArgumentException when the {@link MonkeyTemplateTag#VERIFICATION_CODE_TAG} is missing
     */
    private MonkeyData formatVerificationCode(String contentTemplate) {
        String verificationTag = VERIFICATION_CODE_TAG.getTag();
        if(!contentTemplate.contains(verificationTag))
            throw new IllegalArgumentException("verification_code tag is missing!");
        String verificationCode = generateVerificationCode();
        return new MonkeyData(
                contentTemplate.replaceAll(verificationTag, verificationCode),
            verificationCode
        );
    }

    /**
     * Method to generate a verification code using the {@link #verificationCodeType} to create the correct code <br>
     * No-any params required
     * @return the verification code as {@link String}
     */
    private String generateVerificationCode() {
        String verificationCode = "";
        switch (verificationCodeType) {
            case NUMBERS -> verificationCode = randomNumeric(6);
            case CHARACTERS -> verificationCode = randomAlphabetic(6);
            case ALPHANUMERIC -> verificationCode = randomAlphanumeric(6);
        }
        return verificationCode;
    }

    /**
     * Method to verify the code sent by the user with corresponding the verification code of the verification email sent
     * @param userEmail: the email of the user
     * @param userCode: the verification code sent by the user
     * @param actions: the actions to execute after the verification
     */
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

    /**
     * Method to remove from the {@link #emailsSent} the emails which their timestamp is over the {@link #keepEmailValid}
     * threshold chosen <br>
     * No-any params required
     *
     * @apiNote this routine is executed if the {@link #keepEmailValid} is different from {@link KeepEmailValid#ALWAYS_KEEP_VALID}
     */
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

    /**
     * Method to get {@link #host} instance <br>
     * No-any params required
     *
     * @return {@link #host} instance as {@link String}
     */
    public String getHost() {
        return host;
    }

    /**
     * Method to get {@link #from} instance <br>
     * No-any params required
     *
     * @return {@link #from} instance as {@link String}
     */
    public String getFrom() {
        return from;
    }

    /**
     * Method to get {@link #verificationCodeType} instance <br>
     * No-any params required
     *
     * @return {@link #verificationCodeType} instance as {@link VerificationCodeType}
     */
    public VerificationCodeType getVerificationCodeType() {
        return verificationCodeType;
    }

    /**
     * Method to get {@link #keepEmailValid} instance <br>
     * No-any params required
     *
     * @return {@link #keepEmailValid} instance as {@link KeepEmailValid}
     */
    public KeepEmailValid getKeepEmailValid() {
        return keepEmailValid;
    }

    /**
     * The {@code MonkeyData} class is useful to store the data to send in the verification email
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    private static class MonkeyData {

        /**
         * {@code content} the content of the email
         */
        private String content;

        /**
         * {@code verificationCode} the verification code of the email
         */
        private final String verificationCode;

        /**
         * Constructor to init the {@link MonkeyData} class
         *
         * @param content: the content of the email
         * @param verificationCode: the verification code of the email
         *
         */
        public MonkeyData(String content, String verificationCode) {
            this.content = content;
            this.verificationCode = verificationCode;
        }

        /**
         * Method to set the {@link #content} instance
         *
         * @param content: the content of the email
         */
        public void setContent(String content) {
            this.content = content;
        }

    }

    /**
     * The {@code EmailSent} class is useful to store the details of a verification email sent
     *
     * @author N7ghtm4r3 - Tecknobit
     * @see Serializable
     */
    private static final class EmailSent implements Serializable {

        /**
         * {@code email} the email of the recipient user
         */
        private final String email;

        /**
         * {@code timestamp} the timestamp when the email has been sent
         */
        private final long timestamp;

        /**
         * {@code verificationCode} the value of the verification code generated
         */
        private final String verificationCode;

        /**
         * Constructor to init the {@link EmailSent} class
         *
         * @param email: the email of the recipient user
         * @param verificationCode: the value of the verification code generated
         *
         */
        public EmailSent(String email, String verificationCode) {
            this.email = email;
            this.timestamp = System.currentTimeMillis();
            this.verificationCode = verificationCode;
        }

    }

}
