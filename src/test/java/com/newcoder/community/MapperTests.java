package com.newcoder.community;

import com.newcoder.community.dao.AlphaDao;
import com.newcoder.community.dao.DiscussPostMapper;
import com.newcoder.community.dao.UserMapper;
import com.newcoder.community.entity.DiscussPost;
import com.newcoder.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("https://www.newcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateUser() {
        int rows = userMapper.updateStatus(1924665351, 1);
        System.out.println(rows);
        rows = userMapper.updateHeader(1924665351,"http://images.nowcoder.com/head/120t.png");
        System.out.println(rows);
        rows = userMapper.updatePassword(1924665351,"123456");
        System.out.println(rows);
    }

    @Test
    public void testDeleteUser() {
        System.out.println(userMapper.deleteByName("test"));
    }

    @Test
    public void testSelectPosts() {
        List<DiscussPost> discussPostList = discussPostMapper.selectDiscussPosts(0, 0, 20);
        for(DiscussPost post : discussPostList) {
            System.out.println(post);
        }
        int rows = discussPostMapper.selectDiscussPostsRows(0);
        System.out.println(rows);
    }
}
