import jakarta.mail.*;

import java.io.IOException;
import java.util.Properties;

public class Receiver {

    public static void main(String[] args) throws MessagingException, IOException, InterruptedException {
        String host = "localhost";
        String user = "admin@prova.com";
        //create properties field
        Properties properties = new Properties();

        properties.put("mail.pop3.host", host);
        properties.put("mail.pop3.port", 110);
        //properties.put("mail.pop3.starttls.enable", "true");
        Session emailSession = Session.getDefaultInstance(properties);

        //create the POP3 store object and connect with the pop server
        Store store = emailSession.getStore("pop3");

        store.connect(host, user, "admin");

        //create the folder object and open it
        Folder emailFolder = store.getFolder("INBOX");
        emailFolder.open(Folder.READ_ONLY);

        // retrieve the messages from the folder in an array and print it
        Message[] messages = emailFolder.getMessages();
        System.out.println("messages.length---" + messages.length);

        for (Message message : emailFolder.getMessages()) {
            System.out.println("---------------------------------");
            //System.out.println("Email Number " + (i + 1));
            System.out.println("Subject: " + message.getSubject());
            System.out.println("From: " + message.getFrom()[0]);
            System.out.println("Text: " + message.getContent().toString());
        }

        //close the store and folder objects
        emailFolder.close(false);
        store.close();
    }

}
