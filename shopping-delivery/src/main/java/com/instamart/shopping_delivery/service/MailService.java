package com.instamart.shopping_delivery.service;

import com.instamart.shopping_delivery.models.AppUser;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Properties;

@Service
@Slf4j
public class MailService {

    JavaMailSender javaMailSender;
    TemplateEngine templateEngine;

    @Autowired
    public MailService(JavaMailSender javaMailSender,
                       TemplateEngine templateEngine){
        this.javaMailSender=javaMailSender;
        this.templateEngine=templateEngine;
    }
    public void sendWareHouseInvitationMail(AppUser wareHouseAdmin){

        String platformName="Grocery ShoopIng App";
        String acceptLink="http://localhost:8080/api/v1/user/wareHouse/admin/accept/invite/" + wareHouseAdmin.getId().toString();
        Context context=new Context();
        context.setVariable("platformName",platformName);
        context.setVariable("warehouseAdminName",wareHouseAdmin.getName());
        context.setVariable("acceptLink",acceptLink);

        String htmlContent=templateEngine.process("wareHouse-admin-invite",context);
        //MimeMessage ke ander hum mail ka content set karate h
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        //hum mimeMessage ke ander bhi direct content set nhi kr skate es ke lya chay MimeMessageHelper
        MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage);

        try {
            mimeMessageHelper.setTo(wareHouseAdmin.getEmail());
            mimeMessageHelper.setSubject("Invitation to our Siggy platform as wareHouse Admin");
            mimeMessageHelper.setText(htmlContent,true);
        }catch(Exception e){
                log.error(e.getMessage());
        }
        javaMailSender.send(mimeMessage);
    }
}
