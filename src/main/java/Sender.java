import com.tecknobit.monkey.MonkeyTemplate;
import com.tecknobit.monkey.MonkeyVerifier;
import org.json.JSONObject;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static com.tecknobit.monkey.MonkeyTemplate.MonkeyTemplateTag.PRIMARY_COLOR_TAG;
import static com.tecknobit.monkey.MonkeyTemplate.MonkeyTemplateTag.SECONDARY_COLOR_TAG;

public class Sender {

    /*
     * Steps
     * 1] https://www.apache.org/dyn/closer.lua/james/server/3.8.0/james-server-app-3.8.0-app.zip
     * 2] https://javaworld-abhinav.blogspot.com/2019/04/setting-up-local-mail-server-using.html
     * 3] https://www.courier.com/guides/java-send-email/
     */

    /*
     * .\james-cli.bat ListUsers
     * .\james-cli.bat AddDomain monkey.com
     * .\james-cli.bat AddUser admin@monkey.com monkey
     */

    static AtomicReference<Process> process = new AtomicReference<>();
    static ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void main(String[] args) throws Exception {
        String[] recipients = {"kemepol999@evvgo.com"};
        String fromText = "Prova da monkey";
        String emailSubject = "Monkey Subject";
        MonkeyVerifier monkeyVerifier = new MonkeyVerifier("locahost", "admin@monkey.com", "admin");
        monkeyVerifier.sendPlainVerificationEmail(fromText, emailSubject, "Monkey body plain", recipients);
        MonkeyTemplate.MonkeyColorsScheme colorsScheme = new MonkeyTemplate.MonkeyColorsScheme(
                new JSONObject()
                        .put(PRIMARY_COLOR_TAG.getValue(), "07020d")
                        .put(SECONDARY_COLOR_TAG.getValue(), "f9f6f0")
        );
        /*MonkeyTemplate.MonkeyLogo monkeyLogo = new MonkeyTemplate.MonkeyLogo("https://github.com/N7ghtm4r3/Pandoro",
                "https://raw.githubusercontent.com/N7ghtm4r3/Pandoro/main/images/profiles/defProfilePic.png");
        MonkeyTemplate monkeyTemplate = new MonkeyTemplate(colorsScheme, monkeyLogo, new Random().nextInt());
        monkeyVerifier.sendDefaultTemplateVerificationEmail(fromText, emailSubject, monkeyTemplate, recipients);*/
        //startServer(); Thread.sleep(30000);
        //sendEmail();
        /*Thread.sleep(10000);
        stopServer();*/
    }

    private static void sendEmail() {
        String host = "localhost";
        String user = "admin@prova.com";
        Email email = EmailBuilder.startingBlank()
                .from("From", user)
                .to("2 nd Receiver", "kemepol999@evvgo.com")
                .withSubject("Email Subject")
                .withPlainText("Email Body")
                .buildEmail();
        Mailer mailer = MailerBuilder
                .withSMTPServer(host, 25, user, "admin")
                .withTransportStrategy(TransportStrategy.SMTP).buildMailer();
        mailer.sendMail(email);
    }

    private static void startServer() {
        executor.execute(() -> {
            try {
                process.set(Runtime.getRuntime().exec(
                        new String[]{"cmd", "/C", "run.bat"},
                        null,
                        new File(System.getProperty("user.dir")
                                + "/james-server-spring-app-3.8.0/bin/")
                ));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void stopServer() throws IOException, InterruptedException {
        if(process.get() != null) {
            System.out.println(process.get().pid());
            executor.shutdownNow();
            for (ProcessHandle processHandle : process.get().children().toList()) {
                System.out.println(processHandle.pid());
                System.out.println(new BufferedReader(new InputStreamReader(
                    Runtime.getRuntime().exec(
                            new String[]{"taskkill", "/F", "/PID", String.valueOf(processHandle.pid())},
                            null,
                            new File(System.getProperty("user.dir")
                                    + "/james-server-spring-app-3.8.0/bin/")).getErrorStream())).readLine());
            }
        }
    }

}
