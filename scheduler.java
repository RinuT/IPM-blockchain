package com.example.schedulerdemo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ScheduledTasks {

	private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	/**
	 * schedule method to be executed in fixed time interval (eg:12 am every day)
	 * http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/crontrigger.html
	 * -->use this to understand cron
	 */

	@Scheduled(cron = "* * * ? * *")
	public void scheduleTask() {

		final String uri = "https://gturnquist-quoters.cfapps.io/api/random";

		RestTemplate restTemplate = new RestTemplate();
		String jsonString = restTemplate.getForObject("http://ec2-35-173-231-185.compute-1.amazonaws.com:3000/api/purchaseorder", String.class);

		//String jsonString = "{\"fileName\": [{\"name\": \"Anand\",\"last\": \"Dwivedi\",\"place\": \"Bangalore\"},{\"name\": \"Anand\",\"last\": \"Dwivedi\",\"place\": \"Bangalore\"}]}";

		JSONObject outputN;
		String jsonStringData="";
		try {
			
			String jsonStringNew=jsonString.substring(1, jsonString.length()-1);
			JSONArray output = new JSONArray(jsonString);
	        for (int i = 0; i < output.length(); i++)
	        {
	            JSONObject jsonObj = output.getJSONObject(i);
	            JSONObject jsonObjPurchase = jsonObj.getJSONObject("purchaseorder");
	            JSONArray batch = jsonObjPurchase.getJSONArray("batch");
	            for(int j = 0; j < batch.length(); j++) {
	            	JSONObject batchNew = batch.getJSONObject(j);
	            	JSONObject jsonObjbatch = batchNew.getJSONObject("batch");
	            
		            if(!jsonObjbatch.getString("receiptDate").isEmpty()) {
		            	int quantity=Integer.parseInt(jsonObjbatch.getString("availableQuantity"));
		            	  String date=jsonObjbatch.getString("receiptDate");
				            Date dateBefore = new Date(date);
				 	        Date dateAfter = new Date();
				 	        long difference = dateAfter.getTime() - dateBefore.getTime();
				 	        float daysBetween = (difference / (1000*60*60*24));
				            if(daysBetween>90  & quantity>0) {
				            	if(jsonStringData=="") {
				            		jsonStringData = "[{\"batchCode\":\" "+batchNew.getString("batchCode")+"\",\"PO Number\": \""+jsonObj.getString("poNumber")+"\",\"Material code\": \""+jsonObjbatch.getString("materialCode")+"\",\"Shipped Quantity\": \""+jsonObjbatch.getString("shippedQuantity")+"\",\"Received Quantity\": \""+jsonObjbatch.getString("recievedQuantity")+"\",\" Available Quantity\": \""+jsonObjbatch.getString("availableQuantity")+" \",\"received date\": \" "+jsonObjbatch.getString("receiptDate")+"\",\" OverDue period\": \""+daysBetween+" \",\"Supplier\": \" Flextronics\"}]";
				            		
				            	}
				            	else {
				            	
				            		jsonStringData = jsonStringData.substring(0, jsonStringData.length() - 1)+",{\"batchCode\":\" "+batchNew.getString("batchCode")+"\",\"PO Number\": \""+jsonObj.getString("poNumber")+"\",\"Material code\": \""+jsonObjbatch.getString("materialCode")+"\",\"Shipped Quantity\": \""+jsonObjbatch.getString("shippedQuantity")+"\",\"Received Quantity\":\""+jsonObjbatch.getString("recievedQuantity")+"\",\" Available Quantity\": \""+jsonObjbatch.getString("availableQuantity")+" \",\"received date\": \" "+jsonObjbatch.getString("receiptDate")+"\",\" OverDue period\": \""+daysBetween+" \",\"Supplier\": \" Flextronics \"}]";
				            	}
				            	 //outputN = new JSONObject(jsonStringData);

								JSONArray jsonArray = new JSONArray(jsonStringData);
								//JSONArray jsonArray=outputN.getJSONArray("fileName");
								String fileName = "src/main/resources/notifications.csv";
								FileWriter writer = new FileWriter(fileName);
								String csv = CDL.toString(jsonArray);
								writer.write(csv);
								writer.close();
								sendEmailWithAttachment(csv);
				            	}
		            }
		            
		          
	            }
	            
	       
	        }
	           
	           
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("Fixed Rate Task :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
	}
	
	public void sendEmailWithAttachment(String csv) throws MessagingException, IOException {Properties props = new Properties();
	props.put("mail.smtp.host", "smtp.gmail.com");
	props.put("mail.smtp.socketFactory.port", "465");
	props.put("mail.smtp.socketFactory.class",
			"javax.net.ssl.SSLSocketFactory");
	props.put("mail.smtp.auth", "true");
	props.put("mail.smtp.port", "465");

	Session session = Session.getDefaultInstance(props,
		new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("nichu9445@gmail.com","!1@2#3abc");
			}
		});

	try {

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("nichu9445@gmail.com"));
		message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("rinutyr@gmail.com"));
		message.setSubject("List of batch passed 90 day rule");
		  // Set the email msg text.
        MimeBodyPart messagePart = new MimeBodyPart();
        messagePart.setText("Please find attached");

		// Set the email attachment file
        FileDataSource fileDataSource = new FileDataSource("src/main/resources/notifications.csv");

        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.setDataHandler(new DataHandler(fileDataSource));
        attachmentPart.setFileName(fileDataSource.getName());

        // Create Multipart E-Mail.
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messagePart);
        multipart.addBodyPart(attachmentPart);

        message.setContent(multipart);
		Transport.send(message);

		System.out.println("Done");

	} catch (MessagingException e) {
		throw new RuntimeException(e);
	}
}

	  
}


