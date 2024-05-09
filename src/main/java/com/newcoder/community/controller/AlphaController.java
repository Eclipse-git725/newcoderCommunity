package com.newcoder.community.controller;

import com.newcoder.community.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {
    // Controller调用Service的方式
    @Autowired
    private AlphaService alphaService;
    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "hello, spring boot";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData() {
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        // 获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames(); // 中间若干行
        while(enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ": " + value);
        }
        System.out.println(request.getParameter("code"));

        // 获取返回相应数据
        response.setContentType("text/html;charset=utf-8");
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.write("<h1>牛客网</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 只有GET方法能请求到
    // /students?current=1&limit=20
    @RequestMapping(value = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    // /student/123, 获取参数的方式
    @RequestMapping(value = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable(name = "id")int id) {
        System.out.println(id);
        return "a student";
    }

    // POST请求
    @RequestMapping(value = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    // 响应HTML数据
    @RequestMapping(value = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "gxy");
        mav.addObject("age", "22");
        mav.setViewName("/demo/view");
        return mav;
    }

    @RequestMapping(value = "/school", method = RequestMethod.GET)
    public String getSchool(Model model) {
        model.addAttribute("name", "ouc");
        model.addAttribute("age", "99");
        return "/demo/view";
    }

    // 相应JSON数据（一般在异步请求中）
    // 通过JSON字符串，JAVA对象可以转成JS对象
    @RequestMapping(value = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp() {
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "gxy");
        emp.put("age", 23);
        emp.put("salary", "15k");
        return emp;
    }

    @RequestMapping(value = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "gxy");
        emp.put("age", 23);
        emp.put("salary", "15k");
        list.add(emp);

        emp.put("name", "hyy");
        emp.put("age", 19);
        emp.put("salary", "15k");
        list.add(emp);
        return list;
    }
}
