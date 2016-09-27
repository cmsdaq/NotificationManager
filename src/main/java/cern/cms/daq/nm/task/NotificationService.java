package cern.cms.daq.nm.task;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import cern.cms.daq.nm.NotificationException;

public class NotificationService {
	
	private static final Logger logger = Logger.getLogger(NotificationService.class);
	
	public static void main(String[] args){
		NotificationService s = new NotificationService();
		s.send("maciej.gladki@gmail.com", "exaaample");
	}

	//final List<String> recipients = Arrays.asList("maciej.gladki@gmail.com");
	//final List<String> recipients = Arrays.asList("+41754116032@mail2sms.cern.ch");

	final String smtpHost = "cernmx.cern.ch";
	final String fromAddress = "daq_notificationmanager@cmsusr.cern.ch"; // you probably can
															// even change the
															// domain here

	
	public void send(String recipient, String body){
		this.send(Arrays.asList(recipient), body);
	}
	
	public void send(List<String> recipients, String body) {

		Properties props = System.getProperties();
		// see
		// https://javamail.java.net/nonav/docs/api/com/sun/mail/smtp/package-summary.html
		props.put("mail.smtp.user", fromAddress);
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", "25");
		props.put("mail.smtp.auth", "false");
		props.put("mail.smtp.ehlo", "false"); // really needed to work without
												// authentication

		// props.put("mail.debug", "true");

		Session session = Session.getInstance(props);
		MimeMessage message = new MimeMessage(session);

		try {
			message.setFrom(new InternetAddress(fromAddress));
			InternetAddress[] toAddress = new InternetAddress[recipients.size()];

			// To get the array of addresses
			for (int i = 0; i < recipients.size(); i++)
				toAddress[i] = new InternetAddress(recipients.get(i));

			for (int i = 0; i < toAddress.length; i++)
				message.addRecipient(Message.RecipientType.TO, toAddress[i]);

			message.setSubject("DAQ notification");
			message.setText(body);

			Transport transport = session.getTransport("smtp");
			transport.connect(smtpHost, fromAddress);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();

		} catch (MessagingException ex) {
			logger.warn("Problem sending e-mail: " + ex.getMessage());
			throw new NotificationException(ex.getMessage());
		}
	}

}
