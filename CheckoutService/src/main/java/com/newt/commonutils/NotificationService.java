package com.newt.commonutils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

	private JavaMailSender javaMailSender;

	@Autowired
	public NotificationService(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}
	
	public void sendNotification(String Subject,String Message) throws MailException
	{
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setFrom("karthiraj@gamil.com");
		mail.setTo("karthyrj5@gmail.com");
		mail.setSubject(Subject);
		mail.setText(Message);
		
		javaMailSender.send(mail);
		
		
	}
	
}
