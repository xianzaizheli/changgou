package com.thymeleaf.controller;

import com.thymeleaf.controller.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@Controller
public class ThymeleafConntroller {
    @RequestMapping("/hi")
    public String hello(Model model){
        model.addAttribute("player","玩家1号");

        ArrayList<User> users = new ArrayList<>();
        users.add(new User(1,"1号玩家","上海"));
        users.add(new User(2,"2号玩家","广州"));
        users.add(new User(3,"3号玩家","武汉"));
        model.addAttribute("users",users);

        HashMap<String, Object> map = new HashMap<>();
        map.put("name","地图名称A");
        map.put("time", new Date().toInstant());
        map.put("address","深圳");
        model.addAttribute("maps",map);

        String[] names = {"张三","李四","王五"};
        model.addAttribute("names",names);
        model.addAttribute("now",new Date());

        model.addAttribute("age",20);

        model.addAttribute("hello","hello 重恩");
        model.addAttribute("class1","aaa");
        model.addAttribute("class2","bbb");
        return "hello";
    }

    @RequestMapping("/test/dd")
    public String dd(Model model){
        model.addAttribute("mark","表格");
        return "test";
    }
}
