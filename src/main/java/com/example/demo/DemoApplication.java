package com.example.demo;

import com.example.demo.model.Role;
import com.example.demo.repository.RoleRepository;
import com.example.demo.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            Role admin = new Role("ROLE_ADMIN");
            Role user = new Role("ROLE_USER");
            roleRepository.save(admin);
            roleRepository.save(user);
        }
        // Ensure Const.createEmailBase is a trusted, hardcoded SQL statement
        if (Const.createEmailBase != null && !Const.createEmailBase.isEmpty()) {
            jdbcTemplate.execute(Const.createEmailBase);
        } else {
            throw new IllegalStateException("Invalid or empty SQL statement for creating email base");
        }
    }

}
