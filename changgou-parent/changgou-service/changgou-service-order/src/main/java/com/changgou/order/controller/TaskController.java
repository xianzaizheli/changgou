package com.changgou.order.controller;

import com.changgou.order.pojo.Task;
import com.changgou.order.service.TaskService;
import com.github.pagehelper.PageInfo;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/task")
@CrossOrigin
public class TaskController {

    @Autowired
    private TaskService taskService;

    /***
     * Task分页条件搜索实现
     * @param task
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@RequestBody(required = false) Task task, @PathVariable  int page, @PathVariable  int size){
        //调用TaskService实现分页条件查询Task
        PageInfo<Task> pageInfo = taskService.findPage(task, page, size);
        return new Result(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * Task分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@PathVariable  int page, @PathVariable  int size){
        //调用TaskService实现分页查询Task
        PageInfo<Task> pageInfo = taskService.findPage(page, size);
        return new Result<PageInfo>(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * 多条件搜索品牌数据
     * @param task
     * @return
     */
    @PostMapping(value = "/search" )
    public Result<List<Task>> findList(@RequestBody(required = false) Task task){
        //调用TaskService实现条件查询Task
        List<Task> list = taskService.findList(task);
        return new Result<List<Task>>(true,StatusCode.OK,"查询成功",list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable Long id){
        //调用TaskService实现根据主键删除
        taskService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 修改Task数据
     * @param task
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody Task task, @PathVariable Long id){
        //设置主键值
        task.setId(id);
        //调用TaskService实现修改Task
        taskService.update(task);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /***
     * 新增Task数据
     * @param task
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Task task){
        //调用TaskService实现添加Task
        taskService.add(task);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /***
     * 根据ID查询Task数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Task> findById(@PathVariable Long id){
        //调用TaskService实现根据主键查询Task
        Task task = taskService.findById(id);
        return new Result<Task>(true,StatusCode.OK,"查询成功",task);
    }

    /***
     * 查询Task全部数据
     * @return
     */
    @GetMapping
    public Result<List<Task>> findAll(){
        //调用TaskService实现查询所有Task
        List<Task> list = taskService.findAll();
        return new Result<List<Task>>(true, StatusCode.OK,"查询成功",list) ;
    }
}
