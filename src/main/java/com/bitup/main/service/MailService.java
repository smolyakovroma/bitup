package com.bitup.main.service;

import org.springframework.stereotype.Service;

@Service
public interface MailService {

    void send(String name, String mail, String subject, String body);
}
