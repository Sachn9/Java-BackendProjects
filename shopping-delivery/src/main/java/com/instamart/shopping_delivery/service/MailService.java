package com.instamart.shopping_delivery.service;

import com.instamart.shopping_delivery.models.AppUser;
import com.instamart.shopping_delivery.models.Product;
import com.instamart.shopping_delivery.models.WareHouse;
import com.instamart.shopping_delivery.models.WareHouseItem;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.angus.mail.imap.protocol.MailboxInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Callable;

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


    public void sendADDProductionMail(Product product, AppUser user){
        Context context=new Context();
        context.setVariable("productName",product.getProductName());
        context.setVariable("unitPrice",product.getUnitPrice());
        context.setVariable("totalQuantity",product.getTotalQuantity());
        context.setVariable("ownerCompany",product.getOwnerCompany());
        context.setVariable("description",product.getDescription());
        context.setVariable("createdBy",product.getCreateBy());
        context.setVariable("createdAt",product.getCreatedAt());

        String htmlContent=templateEngine.process("Product-add-email",context);

        MimeMessage message=javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);
        try {
            mimeMessageHelper.setTo(user.getEmail());
            mimeMessageHelper.setText(htmlContent, true);
            mimeMessageHelper.setSubject("New Products Add");
        }catch (Exception e){
            log.error(e.getMessage());
        }

        javaMailSender.send(message);


    }

    public void sendAssignWareHouseItemToAppAdmin(WareHouseItem wareHouseItem,AppUser user){
        Context context=new Context();
        context.setVariable("wid",wareHouseItem.getWid());
        context.setVariable("pid",wareHouseItem.getPid());
        context.setVariable("quantity",wareHouseItem.getQuantity());
        context.setVariable("discount",wareHouseItem.getDiscount());
        context.setVariable("createdAt",wareHouseItem.getCreatedAt());
        context.setVariable("updatedAt",wareHouseItem.getUpdatedAt());

        String htmlContent=templateEngine.process("wareHouse-assign-product-sendMailToAppAdmin",context);
        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage);

        try {
            mimeMessageHelper.setTo(user.getEmail());
            mimeMessageHelper.setText(htmlContent, true);
            mimeMessageHelper.setSubject("WareHouse Product Allocate");
        }catch (Exception e){
            log.error(e.getMessage());
        }

        javaMailSender.send(mimeMessage);

    }

//    public void sendMailAssignWareHouseItemToWareHouseAdmin(WareHouseItem wareHouseItem,WareHouse wareHouse){
//        Context context=new Context();
//        context.setVariable("wid",wareHouseItem.getWid());
//        context.setVariable("pid",wareHouseItem.getPid());
//        context.setVariable("quantity",wareHouseItem.getQuantity());
//        context.setVariable("discount",wareHouseItem.getDiscount());
//        context.setVariable("createdAt",wareHouseItem.getCreatedAt());
//        context.setVariable("updatedAt",wareHouseItem.getUpdatedAt());
//
//        String htmlContent=templateEngine.process("wareHouse-assign-product-sendEmailToWareHouseAdmin",context);
//        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
//        MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage);
//
//        try{
//            mimeMessageHelper.setTo(wareHouse.);
//            mimeMessageHelper.setText(htmlContent,true);
//            mimeMessageHelper.setSubject("Assign product in wareHouse!");
//        }catch (Exception e){
//            log.error(e.getMessage());
//        }
//
//        javaMailSender.send(mimeMessage);
//
//    }


    public void sendEmailToWareHouseAdminAssignManager(WareHouse wareHouse,AppUser wareHouseAdmin){
        Context context=new Context();
        context.setVariable("managerName",wareHouseAdmin.getName());
        context.setVariable("warehouseName",wareHouse.getName());
        context.setVariable("locationAddress",wareHouse.getLocation().getAddress());
        context.setVariable("locationCity",wareHouse.getLocation().getCity());
        context.setVariable("locationState",wareHouse.getLocation().getState());
        context.setVariable("locationCountry",wareHouse.getLocation().getCountry());
        context.setVariable("pinCode",wareHouse.getLocation().getPinCode());

        String htmContent=templateEngine.process("assign-to-manager-a wareHouse",context);
        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage);

        try{
            mimeMessageHelper.setTo(wareHouseAdmin.getEmail());
            mimeMessageHelper.setText(htmContent,true);
            mimeMessageHelper.setSubject("Manager Allocate successfully!");
        }catch (Exception e){
            log.error((e.getMessage()));
        }

        javaMailSender.send(mimeMessage);
    }

    public void sendMailToWareHouseAdminRegardingTODeliveryPartnerRegistration(AppUser deliveryPartner,
                                                                               String wareHouseAdminEmail){
        Context context=new Context();
        context.setVariable("name",deliveryPartner.getName());
        context.setVariable("email",deliveryPartner.getEmail());
        context.setVariable("phoneNumber",deliveryPartner.getPhoneNumber());;
        context.setVariable("userType",deliveryPartner.getUserType());
        context.setVariable("status",deliveryPartner.getStatus());
        context.setVariable("acceptLink","http://localhost:8080/api/v1/user/deliverpartner/registration/accept" + deliveryPartner.getId().toString());

        String htmlContent=templateEngine.process("delivery-partner-registration",context);

        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage);

        try{
            mimeMessageHelper.setTo(wareHouseAdminEmail);
            mimeMessageHelper.setText(htmlContent,true);
            mimeMessageHelper.setSubject("Delivery Partner Registration");
        }catch (Exception e){
            log.error(e.getMessage());
        }

        javaMailSender.send(mimeMessage);
    }


    public void sendToMailCustomer(AppUser customer, List<WareHouseItem> wareHouseItem,List<Product> products){
        if (customer == null || products.isEmpty() || wareHouseItem.isEmpty()) return;

        Context context=new Context();
        context.setVariable("productName",products.get(0).getProductName());
        context.setVariable("price",products.get(0).getUnitPrice());
        context.setVariable("discount",wareHouseItem.get(0).getDiscount());
        context.setVariable("quantity",wareHouseItem.get(0).getQuantity());
        context.setVariable("isAvailable",wareHouseItem.get(0).getQuantity() > 0 ? "Yes" : "No");
        String htmlTemplate=templateEngine.process("send-to customer-email",context);

        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage);

        try{
            mimeMessageHelper.setTo(customer.getEmail());
            mimeMessageHelper.setText(htmlTemplate,true);
            mimeMessageHelper.setSubject("Product Search for customer");
        }catch (Exception e){
            log.error(e.getMessage());
        }

        javaMailSender.send(mimeMessage);
    }

}



