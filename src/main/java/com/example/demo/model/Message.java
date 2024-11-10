package com.example.demo.model;


import com.example.demo.utils.enumeration.MessageType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Message {

       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;

       private String content;

       @ManyToOne
       private User from;

       @ManyToOne
       private User to;

       private MessageType type;

       private Date date;
}
