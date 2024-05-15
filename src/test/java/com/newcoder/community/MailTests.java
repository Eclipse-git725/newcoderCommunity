package com.newcoder.community;

import com.newcoder.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testMail(){
        mailClient.sendMail("2219570448@qq.com", "测试发送邮件", "Hello World!");
    }
    @Test
    public void testHtmlMail(){
        Context context = new Context();
        // 给thymeleaf传参username
        context.setVariable("username", "gxy");
        String text = templateEngine.process("/mail/demo", context);
        System.out.println(text);
        mailClient.sendMail("2219570448@qq.com", "测试发送邮件", text);
    }
}
