package com.newcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.newcoder.community.entity.LoginTicket;
import com.newcoder.community.entity.User;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommunityConstant;
import com.newcoder.community.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.jws.WebParam;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private Producer kaptchaProducer;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if(map == null || map.isEmpty()) {
            // 注册成功后，提示，跳转到首页
            model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活！");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        }else {
            // 注册失败，提示
            model.addAttribute("usernameMessage", map.get("usernameMessage"));
            model.addAttribute("passwordMessage", map.get("passwordMessage"));
            model.addAttribute("emailMessage", map.get("emailMessage"));
            return "/site/register";
        }
    }

    // https://localhost:8080/community/activation/101/code
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId")int userId, @PathVariable("code")String code) {
        // 从访问路径中获取参数
        int result = userService.activation(userId, code);
        if(result == ACTIVATION_SUCCESS) {
            // 激活成功后，提示，跳转到首页
            model.addAttribute("msg", "激活成功，您的账号已可以正常使用了！");
            model.addAttribute("target", "/login");
        }else if(result == ACTIVATION_REPEAT) {
            // 重复激活
            model.addAttribute("msg", "无效操作，该账号已激活！");
            model.addAttribute("target", "/index");
        }else {
            // 激活失败
            model.addAttribute("msg", "激活失败，您提供的激活码无效！");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    // 应验证码图片
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        // 将验证码返回给session
        session.setAttribute("kaptcha", text);
        // 设置响应类型
        response.setContentType("image/png");
        // 将图片输出给浏览器(将生成的图片写入响应数据流)
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败" + e.getMessage());
        }
    }

    // 登录
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(Model model, HttpSession session, HttpServletResponse response,
                        String username, String password, String code, boolean rememberMe) {
        // 判断验证码是否正确
        String kaptcha = (String) session.getAttribute("kaptcha");
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !code.equalsIgnoreCase(kaptcha)) {
            model.addAttribute("codeMessage", "验证码不正确!");
            return "/site/login";
        }

        // 根据是否勾选记住我，设置超时时间
        int expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECOND : DEFAULT_EXPIRED_SECOND;

        Map<String, Object> map = userService.login(username, password, expiredSeconds);

        if(map.containsKey("ticket")) {
            // 登录成功，跳转到首页，这里用来重定向，直接跳转到了首页

            // 设置cookie内容
            Cookie cookie = new Cookie("ticket", (String) map.get("ticket"));
            cookie.setMaxAge(expiredSeconds);
            cookie.setPath(contextPath);
            response.addCookie(cookie);

            /**
             * model.addAttribute("msg", "登录成功！");
             * model.addAttribute("target", "/index");
             * return "/site/operate-result";
             */

            return "redirect:/index";
        }else {
            model.addAttribute("usernameMessage", map.get("usernameMessage"));
            model.addAttribute("passwordMessage", map.get("passwordMessage"));
            return "/site/login";
        }
    }

    // 退出登录
    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket")String ticket) {
        userService.logout(ticket);
        return "redirect:/login";
    }
}
