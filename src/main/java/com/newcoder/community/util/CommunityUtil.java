package com.newcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {
    // 生成随机字符串（激活码或是图片的随机名）
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    // MD5加密，对密码进行加密
    // 不能解密，但每次加密的结果都是一样的
    // 在密码后面加上随机字符串，key时密码+salt
    public static String md5(String key) {
        if(StringUtils.isBlank(key)) {
            // 传入的是空格或空串
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
