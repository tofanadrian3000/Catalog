package Notification;

import Model.Homework;
import Model.Student;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class UpdateHomeworkNotification extends Thread {
    private Homework homework;
    private Student student;

    public UpdateHomeworkNotification(Homework homework, Student student){
        this.homework=homework;
        this.student=student;
    }

    @Override
    public void run() {
        try{
            String host ="smtp.gmail.com" ;
            String user = "email@mail.com";
            String pass = "password";
            String to = student.getEmail();
            String from = "hypertech.contact@gmail.com";
            String subject = "Update Grade Notification";
            String messageText = "Buna ziua,\n\n\tTema cu numarul " + homework.getID().toString() +
                    " are un nou termen limita de predare, acesta fiind " + homework.getDeadline().toString() +
                    ".\n\nMult succes!\nO zi buna!";
            boolean sessionDebug = false;

            Properties props = System.getProperties();

            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            Session mailSession = Session.getDefaultInstance(props, null);
            mailSession.setDebug(sessionDebug);
            Message msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(from));
            InternetAddress[] address = {new InternetAddress(to)};
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(subject); msg.setSentDate(new Date());
            msg.setText(messageText);

            Transport transport=mailSession.getTransport("smtp");
            transport.connect(host, user, pass);
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();
            System.out.println("message send successfully");
        }catch(Exception ex)
        {
            System.out.println(ex);
        }

    }

}
