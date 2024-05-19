package com.newcoder.community.service;

import com.newcoder.community.dao.LoginTicketMapper;
import com.newcoder.community.dao.UserMapper;
import com.newcoder.community.entity.LoginTicket;
import com.newcoder.community.entity.User;
import com.newcoder.community.util.CommunityConstant;
import com.newcoder.community.util.CommunityUtil;
import com.newcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    // 注册相关的页面，及新增一个用户，写在这个业务层
    // 需要用到邮箱
    @Autowired
    private MailClient mailClient;
    // 注入模板引擎，因为需要发送一个HTML激活邮件
    @Autowired
    private TemplateEngine templateEngine;
    // 登录时会用到登录凭证
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    // 激活码路径
    @Value("${community.path.domain}")
    private String domain;
    // 项目路径
    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    /**
     * 注册业务，返回注册信息，如用户已存在，传入的是用户
     * @param user
     * @return
     */
    public Map<String, Object> register(User user) {
        // 在业务层进行逻辑校验，确保进入数据库的数据是合法的
        Map<String, Object> map = new HashMap<>();
        // 空值校验
        if(user == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        if(StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMessage", "账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMessage", "密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())) {
            map.put("emailMessage", "邮箱不能为空！");
            return map;
        }
        // 验证用户名
        User u = userMapper.selectByName(user.getUsername());
        if(u != null) {
            map.put("usernameMessage", "用户已存在！");
            return map;
        }
        // 验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null) {
            map.put("emailMessage", "该邮箱已被注册！");
            return map;
        }
        // 注册用户
        // 对密码加密
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        // 对其他属性设置
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 发送激活邮件
        Context context = new Context();
        // 给thymeleaf传参username
        context.setVariable("email", user.getEmail());
        // https://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        System.out.println(url);
        context.setVariable("url", url);
        String text = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", text);
        return map;
    }

    /**
     * 激活
     * @param userId
     * @param code
     * @return
     */
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        }else if(code.equals(user.getActivationCode())) {
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 登录业务，返回登录信息，传入用户名、密码、过期秒数
     * @param username
     * @param password
     * @param expiredSeconds
     * @return
     */
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        // 空值校验
        if(StringUtils.isBlank(username)) {
            map.put("usernameMessage", "账号不能为空！");
            return map;
        }

        if(StringUtils.isBlank(password)) {
            map.put("passwordMessage", "密码不能为空！");
            return map;
        }

        // 验证用户名
        User u = userMapper.selectByName(username);
        if(u == null) {
            map.put("usernameMessage", "用户不存在，请先注册！");
            return map;
        }

        // 账号没有激活
        if(u.getStatus() == 0) {
            map.put("usernameMessage", "账号未激活，请先激活！");
        }

        // 验证密码是否正确
        String salt = u.getSalt();
        password = CommunityUtil.md5(password + salt);
        if(!password.equals(u.getPassword())) {
            map.put("passwordMessage", "密码错误！");
            return map;
        }

        // 登录成功，生成登录凭证传给数据库
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(u.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        // 凭证要返回给客户端
        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    /**
     * 退出业务
     * @param ticket
     */
    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }

    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }
}
