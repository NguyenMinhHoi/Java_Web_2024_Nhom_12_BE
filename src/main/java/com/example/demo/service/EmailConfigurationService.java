package com.example.demo.service;

import com.example.demo.entity.EmailConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;


import com.example.demo.entity.EmailConfiguration;
import com.example.demo.repository.EmailConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailConfigurationService {

    @Autowired
    private EmailConfigurationRepository emailConfigRepository;

    public JavaMailSender getJavaMailSender() {
        EmailConfiguration config = emailConfigRepository.findTopByOrderByIdDesc();
        if (config == null) {
            throw new RuntimeException("No email configuration found");
        }

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.getHost());
        mailSender.setPort(config.getPort());
        mailSender.setUsername(config.getUsername());
        mailSender.setPassword(config.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", config.getProtocol());
        props.put("mail.smtp.auth", String.valueOf(config.isAuth()));
        props.put("mail.smtp.starttls.enable", String.valueOf(config.isStarttlsEnable()));
        props.put("mail.debug", String.valueOf(config.isDebug()));

        return mailSender;
    }

    public EmailConfiguration saveConfiguration(EmailConfiguration config) {
        return emailConfigRepository.save(config);
    }
}