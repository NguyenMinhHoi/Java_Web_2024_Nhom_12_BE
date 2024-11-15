package com.example.demo.service;

import com.example.demo.entity.ConfigurationParameter;
import com.example.demo.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Properties;


import com.example.demo.repository.ConfigurationRepository;

@Service
public class EmailConfigurationService {

    @Autowired
    private ConfigurationRepository ConfigRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;


    public JavaMailSender getJavaMailSender() {
        HashMap<String,Object> params = new HashMap<>();
        params.put("key_parameter", Const.MAIL);
        String config = jdbcTemplate.queryForObject(Const.VALUE_FROM_KEY, params, (rs,row)->{
            return rs.getString("value_parameter");
        });
        if (config == null) {
            throw new RuntimeException("No email configuration found");
        }

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        String[] accountInfo = config.split(":");
        mailSender.setUsername(accountInfo[0]);
        mailSender.setPassword(accountInfo[1]);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");

        return mailSender;
    }

    public ConfigurationParameter saveConfiguration(ConfigurationParameter config) {
        return ConfigRepository.save(config);
    }
}