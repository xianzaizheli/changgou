package com.changgou.order.controller;

import com.changgou.order.pojo.TaskHis;
import com.changgou.order.service.TaskHisService;
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
@RequestMapping("/taskHis")
@CrossOrigin
public class TaskHisController {

    @Autowired
    private TaskHisService taskHisService;

    /***
     * TaskHis分页条件搜索实现
     * @param taskHis
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@RequestBody(required = false) TaskHis taskHis, @PathVariable  int page, @PathVariable  int size){
        //调用TaskHisService实现分页条件查询TaskHis
        PageInfo<TaskHis> pageInfo = taskHisService.findPage(taskHis, page, size);
        return new Result(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * TaskHis分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@PathVariable  int page, @PathVariable  int size){
        //调用TaskHisService实现分页查询TaskHis
        PageInfo<TaskHis> pageInfo = taskHisService.findPage(page, size);
        return new Result<PageInfo>(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * 多条件搜索品牌数据
     * @param taskHis
     * @return
     */
    @PostMapping(value = "/search" )
    public Result<List<TaskHis>> findList(@RequestBody(required = false) TaskHis taskHis){
        //调用TaskHisService实现条件查询TaskHis
        List<TaskHis> list = taskHisService.findList(taskHis);
        return new Result<List<TaskHis>>(true,StatusCode.OK,"查询成功",list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable Long id){
        //调用TaskHisService实现根据主键删除
        taskHisService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 修改TaskHis数据
     * @param taskHis
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody TaskHis taskHis, @PathVariable Long id){
        //设置主键值
        taskHis.setId(id);
        //调用TaskHisService实现修改TaskHis
        taskHisService.update(taskHis);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /***
     * 新增TaskHis数据
     * @param taskHis
     * @return
     */
    @PostMapping
    public Result add(@RequestBody TaskHis taskHis){
        //调用TaskHisService实现添加TaskHis
        taskHisService.add(taskHis);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /***
     * 根据ID查询TaskHis数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<TaskHis> findById(@PathVariable Long id){
        //调用TaskHisService实现根据主键查询TaskHis
        TaskHis taskHis = taskHisService.findById(id);
        return new Result<TaskHis>(true,StatusCode.OK,"查询成功",taskHis);
    }

    /***
     * 查询TaskHis全部数据
     * @return
     */
    @GetMapping
    public Result<List<TaskHis>> findAll(){
        //调用TaskHisService实现查询所有TaskHis
        List<TaskHis> list = taskHisService.findAll();
        return new Result<List<TaskHis>>(true, StatusCode.OK,"查询成功",list) ;
    }
}
