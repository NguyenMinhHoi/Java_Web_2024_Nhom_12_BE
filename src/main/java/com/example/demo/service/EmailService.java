package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private EmailConfigurationService emailConfigService;

    public void sendHtmlMessage(String to, String subject, String htmlContent) throws MessagingException {
        JavaMailSender emailSender = emailConfigService.getJavaMailSender();
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        emailSender.send(message);
    }

    public void sendMerchantRegistrationAcceptance(String to, String merchantName) throws MessagingException {
        String subject = "Chào mừng bạn đến với Chương trình Đối tác Kinh doanh của chúng tôi!";
        String htmlContent = String.format(
            "<html>" +
            "<head>" +
            "<style>" +
            "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
            "h1 { color: #4a4a4a; }" +
            ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
            ".header { background-color: #f8f9fa; padding: 20px; text-align: center; }" +
            ".content { padding: 20px; }" +
            ".footer { background-color: #f8f9fa; padding: 20px; text-align: center; font-size: 12px; }" +
            "</style>" +
            "</head>" +
            "<body>" +
            "<div class='container'>" +
            "<div class='header'>" +
            "<h1>Chào mừng đến với Chương trình Đối tác Kinh doanh!</h1>" +
            "</div>" +
            "<div class='content'>" +
            "<h2>Xin chúc mừng, %s!</h2>" +
            "<p>Đơn đăng ký trở thành đối tác kinh doanh của bạn đã được chấp nhận.</p>" +
            "<p>Bây giờ bạn đã có quyền truy cập vào các công cụ dành cho đối tác và có thể bắt đầu bán sản phẩm của mình.</p>" +
            "<p>Nếu bạn có bất kỳ câu hỏi nào, đừng ngần ngại liên hệ với đội ngũ hỗ trợ của chúng tôi.</p>" +
            "</div>" +
            "<div class='footer'>" +
            "<p>Trân trọng,<br>Đội ngũ Thương mại Điện tử của bạn</p>" +
            "</div>" +
            "</div>" +
            "</body></html>",
            merchantName
        );
        sendHtmlMessage(to, subject, htmlContent);
    }

    public void sendRoyalMerchantRegistrationAcceptance(String to, String merchantName) throws MessagingException {
        String subject = "Congratulations on Becoming a Royal Merchant!";
        String htmlContent = String.format(
            "<html><body>" +
            "<h1>Welcome to the Royal Merchant Program, %s!</h1>" +
            "<p>We are thrilled to inform you that your application for Royal Merchant status has been accepted.</p>" +
            "<p>As a Royal Merchant, you now have access to exclusive benefits, including:</p>" +
            "<ul>" +
            "<li>Priority customer support</li>" +
            "<li>Lower transaction fees</li>" +
            "<li>Advanced analytics tools</li>" +
            "<li>Exclusive promotional opportunities</li>" +
            "</ul>" +
            "<p>Our Royal Merchant support team will contact you shortly to guide you through your new privileges.</p>" +
            "<p>Thank you for your outstanding performance and commitment to our platform!</p>" +
            "<p>Best regards,<br>Your E-commerce Executive Team</p>" +
            "</body></html>",
            merchantName
        );
        sendHtmlMessage(to, subject, htmlContent);
    }
}