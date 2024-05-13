package com.newcoder.community.controller;

import com.newcoder.community.entity.DiscussPost;
import com.newcoder.community.entity.Page;
import com.newcoder.community.entity.User;
import com.newcoder.community.service.DiscussPostService;
import com.newcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page) {
        // SpringMVC会将Page自动注入到Model，这样Thymeleaf可以直接使用Page中的数据
        // 服务端可以设置的分页参数
        page.setRows(discussPostService.findDiscussPostsRows(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> postList = new ArrayList<>();
        if(list != null) {
            for(DiscussPost post : list) {
                // 将用户名加入列表中
                User user = userService.findUserById(post.getUserId());
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                map.put("user", user);
                postList.add(map);
            }
        }
        model.addAttribute("discussPosts", postList);
        return "/index";
    }
}
