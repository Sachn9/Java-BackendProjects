package com.instamart.shopping_delivery.service;

import com.instamart.shopping_delivery.models.AppUser;
import com.instamart.shopping_delivery.models.WareHouse;
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

    public void sendCreateWareHouseMail(WareHouse wareHouse,AppUser appUser){

        Context context=new Context();
        context.setVariable("wareHouseName",wareHouse.getName());
        context.setVariable("address",wareHouse.getLocation().getAddress());
        context.setVariable("city",wareHouse.getLocation().getCity());
        context.setVariable("state",wareHouse.getLocation().getState());
        context.setVariable("country",wareHouse.getLocation().getCountry());
        context.setVariable("pinCode",wareHouse.getLocation().getPinCode());
        context.setVariable("createdAt",wareHouse.getLocation().getCreatedAt());

        String htmlContent=templateEngine.process("wareHouse-created-email",context);

        MimeMessage message=javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(message);

        try{
            mimeMessageHelper.setTo(appUser.getEmail());
            mimeMessageHelper.setText(htmlContent,true);
            mimeMessageHelper.setSubject("New wareHouse Created!");
        }catch (Exception e){
            log.error(e.getMessage());
        }

        javaMailSender.send(message);
    }
}

