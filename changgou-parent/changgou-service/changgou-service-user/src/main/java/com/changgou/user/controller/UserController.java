package com.changgou.user.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.BCrypt;
import com.changgou.entity.JwtUtil;
import com.changgou.user.config.TokenDecode;
import com.changgou.user.pojo.User;
import com.changgou.user.service.UserService;
import com.github.pagehelper.PageInfo;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/points/add")
    public Result addUserPoints(@RequestParam(value = "points")Integer points){
        Map<String, String> map = new TokenDecode().dcodeToken();
        String username = map.get("username");
        User user = new User();
        user.setPoints(points);
        user.setUsername(username);
        userService.addUserPoints(user);
        return new Result(true , StatusCode.OK , "积分增加成功");
    }

    /***
     * User分页条件搜索实现
     * @param user
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}")
    public Result<PageInfo> findPage(@RequestBody(required = false) User user, @PathVariable int page, @PathVariable int size) {
        //调用UserService实现分页条件查询User
        PageInfo<User> pageInfo = userService.findPage(user, page, size);
        return new Result(true, StatusCode.OK, "查询成功", pageInfo);
    }

    /***
     * User分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}")
    public Result<PageInfo> findPage(@PathVariable int page, @PathVariable int size) {
        //调用UserService实现分页查询User
        PageInfo<User> pageInfo = userService.findPage(page, size);
        return new Result<PageInfo>(true, StatusCode.OK, "查询成功", pageInfo);
    }

    /***
     * 多条件搜索品牌数据
     * @param user
     * @return
     */
    @PostMapping(value = "/search")
    public Result<List<User>> findList(@RequestBody(required = false) User user) {
        //调用UserService实现条件查询User
        List<User> list = userService.findList(user);
        return new Result<List<User>>(true, StatusCode.OK, "查询成功", list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @PreAuthorize("admin")
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        //调用UserService实现根据主键删除
        userService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /***
     * 修改User数据
     * @param user
     * @param id
     * @return
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody User user, @PathVariable String id) {
        //设置主键值
        user.setUsername(id);
        //调用UserService实现修改User
        userService.update(user);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /***
     * 新增User数据
     * @param user
     * @return
     */
    @PostMapping(value = "/add")
    public Result add(@RequestBody User user) {
        //调用UserService实现添加User
        userService.add(user);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    /***
     * 根据ID查询User数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<User> findById(@PathVariable String id) {
        //调用UserService实现根据主键查询User
        User user = userService.findById(id);
        return new Result<User>(true, StatusCode.OK, "查询成功", user);
    }

    /***
     * 根据ID查询User数据，用于feign测试，加载user的数据
     * @param id
     * @return
     */
    @GetMapping("/load/{id}")
    public Result<User> findByUsername(@PathVariable String id) {
        //调用UserService实现根据主键查询User
        User user = userService.findById(id);
        return new Result<User>(true, StatusCode.OK, "查询成功", user);
    }


    /***
     * 查询User全部数据
     * @return
     */
    @GetMapping
    public Result<List<User>> findAll(HttpServletRequest request) {
        //调用UserService实现查询所有User
        List<User> list = userService.findAll();
        String authorization = request.getHeader("Authorization");
        System.out.println("令牌消息"+authorization);
        return new Result<List<User>>(true, StatusCode.OK, "查询成功", list);
    }

    @RequestMapping("/login")
    public Result login(String username, String password ,HttpServletResponse response){
        //查询用户信息
        User user = userService.findById(username);

        if(user != null && BCrypt.checkpw(password,user.getPassword())){
            //设置令牌信息
            HashMap<String, Object> map = new HashMap<>();
            map.put("role","USER");
            map.put("username",username);
            map.put("success","success");
            String jwt = JwtUtil.createJWT(UUID.randomUUID().toString(), JSON.toJSONString(map), null);
            //加入一个cookie
            Cookie cookie = new Cookie("Authorization",jwt);
            response.addCookie(cookie);
//            response.setHeader("Authorization",jwt);

            return new Result(true,StatusCode.OK,"登录成功",jwt);
        }else {
            return new Result(true,StatusCode.LOGINERROR,"登录失败");
        }

    }
}
