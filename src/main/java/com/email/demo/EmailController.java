package com.email.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@RequestMapping("/email")
@RestController
public class EmailController {

    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String sendName;
    @Autowired
    private TemplateEngine templateEngine;

    public EmailController(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @GetMapping(value = "/simple")
    public String sendSimpleMsg(@RequestParam(value = "msg",required = false) String msg,
                                @RequestParam(value = "email",required = false)String email) {
        /*if (StringUtils.isEmpty(msg) || StringUtils.isEmpty(email)) {
            return "请输入要发送消息和目标邮箱";
        }*/

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(sendName);
            mail.setTo("3220178335@qq.com");
            mail.setSubject("这是一封简单邮件");
            mail.setText("Hello Email");
            emailSender.send(mail);
            return "发送成功";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "发送失败:" + ex.getMessage();
        }
    }

    /**
     * 发送html邮件，在发送邮件时指定文本
     * @param msg
     * @param email
     * @return
     */
    @PostMapping(value = "html")
    public String sendHtmlMsg(String msg, String email) {
        if (StringUtils.isEmpty(msg) || StringUtils.isEmpty(email)) {
            return "请输入要发送消息和目标邮箱";
        }
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            messageHelper.setFrom(sendName);
            messageHelper.setTo(email);
            messageHelper.setSubject("HTML邮件");
            String html = "<div><h1><a name=\"hello\"></a><span>Hello</span></h1><blockquote><p><span>this is a html email.</span></p></blockquote><p>&nbsp;</p><p><span>"
                    + msg + "</span></p></div>";
            messageHelper.setText(html, true);
            emailSender.send(message);
            return "发送成功";
        } catch (  MessagingException e) {
            e.printStackTrace();
            return "发送失败：" + e.getMessage();
        }
    }

    /**
     * 发送携带附件的邮件 比如投递简历 需要一个文件
     * resources下添加一个PDF文件，然后发送邮件时携带这个文件
     * @param msg
     * @param email
     * @return
     */
    @PostMapping(value = "mime_with_file")
    public String sendWithFile(String msg, String email) {
        if (StringUtils.isEmpty(msg) || StringUtils.isEmpty(email)) {
            return "请输入要发送消息和目标邮箱";
        }

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            messageHelper.setFrom(sendName);
            messageHelper.setTo(email);
            messageHelper.setSubject("一封包含附件的邮件");
            messageHelper.setText(msg);
            // 该文件位于resources目录下
            // 文件路径不能直接写文件名，系统会报错找不到路径，而IDEA却能直接映射过去
            // 文件路径可以写成相对路径src/main/resources/x.pdf，也可以用绝对路径：System.getProperty("user.dir")   "/src/main/resources/x.pdf"
            File file = new File("src/main/resources/SpringBoot日志处理之Logback.pdf");
            //File file = new File(System.getProperty("user.dir")   "/src/main/resources/SpringBoot日志处理之Logback.pdf");
            System.out.println("文件是否存在：" +  file.exists());
            messageHelper.addAttachment(file.getName(), file);
            emailSender.send(message);
            return "发送成功";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "发送失败："  + e.getMessage();
        }
    }

    /**
     * 发送HTML含有图片发送 使用addInline即可
     * @param msg
     * @param email
     * @return
     */
    @PostMapping(value = "html_with_img")
    public String sendHtmlWithImg(String msg, String email) {
        if (StringUtils.isEmpty(msg) || StringUtils.isEmpty(email)) {
            return "请输入要发送消息和目标邮箱";
        }
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            messageHelper.setFrom(sendName);
            messageHelper.setTo(email);
            messageHelper.setSubject("带静态资源图片的HTML邮件");
            String html = "<div><h1><a name=\"hello\"></a><span>Hello</span></h1><blockquote>" +
                    "<p><span>this is a html email.</span></p></blockquote><p>&nbsp;</p><p><span>"+
            msg +  "</span></p><img src='cid:myImg' /></div>";
            messageHelper.setText(html, true);
            File file = new File("src/main/resources/wei.jpg");
            messageHelper.addInline("myImg", file);
            emailSender.send(message);
            return "发送成功";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "发送失败："  + e.getMessage();
        }
    }

}
