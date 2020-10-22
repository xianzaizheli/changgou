package com.changgou.web.controller;

import com.changgou.entity.Page;
import com.changgou.entity.Result;
import com.changgou.search.feign.SkuInfoFeign;
import com.changgou.search.pojo.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping(value = "/search")
public class SkuController {

    @Autowired
    SkuInfoFeign skuInfoFeign;

    @GetMapping(value = "/list")
    public String search(@RequestParam(required = false)Map<String,String> map , Model model){
        Map mapResult = skuInfoFeign.search1(map);
        //搜索结果回显
        model.addAttribute("result",mapResult);
        //搜索条件也是需要回显的
        model.addAttribute("searchMap",map);
        //设置搜索条件回显
        String urlCheck = urlCheck(map);
        model.addAttribute("urlCheck",urlCheck);
        Page<SkuInfo> page = new Page<SkuInfo>(
                Long.parseLong(mapResult.get("totalElement")+""),
                Integer.parseInt(mapResult.get("pageNum")+""),
                Integer.parseInt(mapResult.get("pageSize")+"")
                );
        model.addAttribute("page",page);
        return "search";
    }

    private String urlCheck(Map<String,String> map){
        String url = "/search/list";
        if(null != map && map.size() > 0){
            url = url + "?";
            for (Map.Entry<String,String> s:map.entrySet()){
                if("sortField".equalsIgnoreCase(s.getKey()) || "sortRule".equalsIgnoreCase(s.getKey())){
                    continue;
                }
                if("pageNum".equalsIgnoreCase(s.getKey())){
                    continue;
                }
                url = url + s.getKey() + "=" + s.getValue()+"&";
            }
            if(url.lastIndexOf("&") != -1){
                url = url.substring(0,url.length()-1);
            }
        }
        return url;
    }
}
