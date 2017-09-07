package com.androiddevelopment.mayureshbhojane.todolist.mail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by Mayuresh Bhojane on 8/14/2017.
 */

public class GmailSender extends Authenticator {

    public static final String GMAIL = "@gmail.com";
    private static final String mailhost = "smtp.gmail.com";
    private static final String port = "465";

    private Session session;
    private String emailId, emailPwd;

    static {
        Security.addProvider(new JSSEProvider());
    }

    // constructor
    public GmailSender(String emailId, String emailPwd) {
        this.emailId = emailId;
        this.emailPwd = emailPwd;
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.emailId, this.emailPwd);
    }

    /**
     * This method performs sending email task
     * @param body content of the email
     */
    public synchronized void sendMail(String body) {
        try {
            MimeMessage message = new MimeMessage(session);
            DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
            message.setSender(new InternetAddress(this.emailId));
            message.setSubject("ToDoList");
            message.setDataHandler(handler);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(this.emailId));
            Transport.send(message);
        }
        catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    public class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }

}
