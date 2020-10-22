package com.changgou.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.content.feign.ContentFeign;
import com.changgou.content.pojo.Content;
import com.changgou.entity.Result;
import com.changgou.item.feign.PageFeign;
import com.xpand.starter.canal.annotation.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

//事件监听的注解，监听数据库
@CanalEventListener
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class MyEventListener {

   /* *//**
     * 监听数据库的增加事件
     * @param EventType   CanalEntry.EventType eventType  监听到的操作的类型  INSERT  UPDATE ,DELETE ,CREATE INDEX ,GRAND
     * @param rowData   CanalEntry.RowData rowData 被修改的数据()
     *//*
    @InsertListenPoint
    public void onEventInsert(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        for (CanalEntry.Column column: afterColumnsList) {
            System.out.println("新增："+column.getName()+"::"+column.getValue());
        }
    }

    *//**
     * 监听数据库的修改事件
     * @param rowData
     *//*
    @UpdateListenPoint
    public void onEventUpdate(CanalEntry.RowData rowData){
        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        for (CanalEntry.Column column : beforeColumnsList){
            System.out.println("修改前:" + column.getName()+"::"+column.getValue());
        }
        for (CanalEntry.Column column : afterColumnsList){
            System.out.println("修改后:"+column.getName() + "::"+column.getValue());
        }
    }

    *//**
     * 监听数据库的删除事件
     * @param rowData
     *//*
    @DeleteListenPoint
    public void onEventDelete(CanalEntry.RowData rowData){
        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        beforeColumnsList.forEach(column -> {
            System.out.println("删除前："+column.getName()+"::"+column.getValue());
        });
    }

    *//**
     * 自定义监听
     * @param rowData
     *//*
    @ListenPoint(destination = "example",schema = {"changgou_content"},table = {"tb_content","tb_content_category"},eventType = CanalEntry.EventType.UPDATE)
    public void onEventCustmUpdate(CanalEntry.RowData rowData){
        System.out.println("广告内容修改前：");
        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        beforeColumnsList.forEach(column -> {
            System.out.println(column.getName()+"::"+column.getValue());
        });
        System.out.println("广告内容修改后：");
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        afterColumnsList.forEach(column -> {
            System.out.println(column.getName()+"::"+column.getValue());
        });
    }*/

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ContentFeign contentFeign;

    @Autowired
    private PageFeign pageFeign;

    @ListenPoint(
            destination = "example",
            schema = {"changgou_content"},
            table = {"tb_content","tb_content_category"},
            eventType = {
                    CanalEntry.EventType.UPDATE,
                    CanalEntry.EventType.DELETE,
                    CanalEntry.EventType.INSERT
            }
    )
    public void onEventCustomContent(CanalEntry.EventType eventType ,CanalEntry.RowData rowData){
        String columnValue = getColumnValue(eventType, rowData);
        Result<List<Content>> byCategory = contentFeign.findByCategory(Long.valueOf(columnValue));
        List<Content> data = byCategory.getData();
        stringRedisTemplate.boundValueOps("content_"+columnValue).set(JSON.toJSONString(data));
    }

    public String getColumnValue(CanalEntry.EventType eventType , CanalEntry.RowData rowData){
        String categoryId = "";
        if(eventType == CanalEntry.EventType.DELETE){
            List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
            for (CanalEntry.Column column : beforeColumnsList){
                if("category_id".equalsIgnoreCase(column.getName())){
                    categoryId = column.getValue();
                    return categoryId;
                }
            }
        }else {
            List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
            for (CanalEntry.Column column : afterColumnsList){
                if("category_id".equalsIgnoreCase(column.getName())){
                    categoryId = column.getValue();
                    return categoryId;
                }
            }
        }
        return categoryId;
    }

    @ListenPoint(
            destination = "example",
            schema = "changgou_goods",
            table = {"tb_spu"},
            eventType = {CanalEntry.EventType.UPDATE, CanalEntry.EventType.INSERT, CanalEntry.EventType.DELETE}
    )
    public void onEventCustomSpu(CanalEntry.EventType eventType ,CanalEntry.RowData rowData){
        String spuId = "";
        if(CanalEntry.EventType.DELETE == eventType){
            List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
            for (CanalEntry.Column column : beforeColumnsList){
                if("id".equals(column.getName())){
                    spuId = column.getName();
                    break;
                }
            }
        }else {
            List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
            for (CanalEntry.Column column :afterColumnsList){
                if("id".equals(column.getName())){
                    spuId = column.getValue();
                    break;
                }
            }
        }
        pageFeign.createPageHtml(Long.parseLong(spuId));
    }

//    @Test
//    public void test01(){
//        String abc = stringRedisTemplate.opsForValue().get("abc");
//        System.out.println(abc);
//    }
}
