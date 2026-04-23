package com.granter.utility;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class SendMessageUtil {

	@Autowired
	private JavaMailSender mailSender;

	@Value("${twilio.account.sid}")
	private String accountSid;

	@Value("${twilio.auth.token}")
	private String authToken;

	@Value("${twilio.url}")
	private String url;

	@Value("${twilio.from.number}")
	private String fromNumber;

	public void sendVerificationEmail(String toEmail, String token) {

		String subject = "Verify Your Account";
		String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
		String body = "Dear User,\n\n" + " Your verifcation code is:\n" + encodedToken
				+ "\n\n" + "If you did not register, please ignore this email.\n\n" + "Thanks & Regards,\nGranter Team";

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toEmail);
		message.setSubject(subject);
		message.setText(body);

		mailSender.send(message);
	}

	private final RestTemplate restTemplate = new RestTemplate();

	public String sendSms(String to, String body) {
		try {
			String finalUrl = String.format(url, accountSid);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			
			headers.set("Authorization", "Basic QUM1ZmZhYjc0MmMzMzBkNGM2NTY1YTg5ODc1MWJiZTFlOTpjYTc0NGI3MGI0MmE5MzExZDdmNDU5NjY0MzhkMjlkNg==");

			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			map.add("To", to);
			map.add("From", fromNumber);
			map.add("Body", body);

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

			ResponseEntity<String> response = restTemplate.postForEntity(finalUrl, request, String.class);

			return response.getBody();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}