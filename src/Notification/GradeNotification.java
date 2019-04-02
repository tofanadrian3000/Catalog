package Notification;

import Model.Catalog;
import Model.Grade;
import Model.Homework;
import Model.Student;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class GradeNotification extends Thread {
    private Student student;
    private Grade grade;
    private Homework homework;

    public GradeNotification(Student student, Grade grade, Homework homework){
        this.student=student;
        this.grade=grade;
        this.homework=homework;
    }

    @Override
    public void run() {
        try{
            String host ="smtp.gmail.com" ;
            String user = "email@mail.com";
            String pass = "password";
            String to = student.getEmail();
            String from = "hypertech.contact@gmail.com";
            String subject = "New Grade Notification";
            String messageText = Catalog.writeStudentGrade(student,homework,grade);
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
