package com.newcoder.community.dao;

import com.newcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);
    int selectDiscussPostsRows(@Param("userId") int userId);
}
