package com.virtusa;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class App {

    public static void sendEmailWithAttachments(String host, String port,
                                                final String userName, final String password, String toAddress,
                                                String subject, String message, List<String> attachFiles)
            throws AddressException, MessagingException {
        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.user", userName);
        properties.put("mail.password", password);

        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
        Session session = Session.getInstance(properties, auth);

        // creates a new e-mail message
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(userName));
        InternetAddress[] toAddresses = {new InternetAddress(toAddress)};
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new Date());

        // creates message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(message, "text/html");

        // creates multi-part
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // adds attachments
        for (String filePath : attachFiles) {
            MimeBodyPart attachPart = new MimeBodyPart();
            try {
                attachPart.attachFile(filePath);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            multipart.addBodyPart(attachPart);
        }

        // sets the multi-part as e-mail's content
        msg.setContent(multipart);

        // sends the e-mail
        Transport.send(msg);
    }

    /**
     * Test sending e-mail with attachments
     * modified from http://www.codejava.net/java-ee/javamail/send-e-mail-with-attachment-in-java
     */
    public static void main(String[] args) {

        try {
            //load a properties file from class path
            Properties props = new Properties();
            InputStream input = null;
            String filename = "attachmentmailer.properties";
            input = App.class.getClassLoader().getResourceAsStream(filename);
            props.load(input);

            // SMTP info
            String host = props.getProperty("host");
            String port = props.getProperty("port");
            String mailFrom = props.getProperty("mailFrom");
            String password = props.getProperty("password");

            // message info
            String mailTo = "pgleghorn@virtusapolaris.com";
            String subject = "Subject here";
            String message = "Message body here";

            // attachments
            List<String> attachFiles = Collections.singletonList("C:/test.pdf");


            sendEmailWithAttachments(host, port, mailFrom, password, mailTo,
                    subject, message, attachFiles);
            System.out.println("Email sent.");
        } catch (Exception ex) {
            System.out.println("Could not send email.");
            ex.printStackTrace();
        }
    }
}
