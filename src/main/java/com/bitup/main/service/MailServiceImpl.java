package com.bitup.main.service;

import com.bitup.main.config.MailConfig;
import org.apache.commons.mail.EmailException;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {
    @Override
    public void send(String name, String mail, String subject, String body) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MailConfig.send(name, mail, subject, body);
                } catch (EmailException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
