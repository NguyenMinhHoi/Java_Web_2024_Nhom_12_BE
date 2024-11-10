package com.example.demo.utils;

public class Const {
    public static String DEFAULT = "DEFAULT";

    public static String createEmailBase = "CREATE TABLE IF NOT EXISTS email_configurations (\n" +
            "    id BIGINT AUTO_INCREMENT PRIMARY KEY,\n" +
            "    host VARCHAR(255) NOT NULL,\n" +
            "    port INT NOT NULL,\n" +
            "    username VARCHAR(255) NOT NULL,\n" +
            "    password VARCHAR(255) NOT NULL,\n" +
            "    protocol VARCHAR(50) NOT NULL,\n" +
            "    auth BOOLEAN NOT NULL,\n" +
            "    starttls_enable BOOLEAN NOT NULL,\n" +
            "    debug BOOLEAN NOT NULL\n" +
            ");";
}
