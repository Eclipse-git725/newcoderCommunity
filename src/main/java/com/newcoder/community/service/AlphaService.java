package com.newcoder.community.service;

import com.newcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
@Scope("prototype")
public class AlphaService {
    // Service依赖于Dao的方式
    @Autowired
    @Qualifier("alphaHibernate")
    private AlphaDao alphaDao;
    //构造器
    public AlphaService() {
        System.out.println("实例化AlphaService");
    }

    // 在构造器后调用
    @PostConstruct
    public void init() {
        System.out.println("Init AlphaService");
    }

    // 在销毁之前调用
    @PreDestroy
    public void destroy() {
        System.out.println("销毁AlphaService");
    }

    public String find() {
        return alphaDao.select();
    }
}
