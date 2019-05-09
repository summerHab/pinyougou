package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/itemSearch")
public class ItemSearchController {

    @Reference
    private ItemSearchService itemSearchService;

    /**
     * solr搜索查询
     * @param searchMap
     * @return
     */
    @RequestMapping("/serarch")
    public Map<String,Object> findSearch(@RequestBody Map<String,Object> searchMap){

        return itemSearchService.search(searchMap);
    }

//    @RequestMapping("/s")
//    public Map<String,Object> findSearch(){
//        System.out.println("6666");
//        return null;
//    }
}
