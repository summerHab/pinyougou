package com.pinyougou.search.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;


import java.util.*;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String, Object> map=new HashMap<>();
        //排除空格搜索的影响
        String keywords =(String)searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ",""));

//        //创建一个solr的查询对象
//        Query query=new SimpleQuery();
//        //创建查询对象
//        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
//        query.addCriteria(criteria);
//        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query,TbItem.class);
//        List<TbItem> items = tbItems.getContent();//获取到对象
//        map.put("rows",items);
//        return map;

        //1.1根据关键字查询列表高亮显示
        map.putAll(searchList(searchMap));

        //1.2根据关键字查询商品分类
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);

        //1.3查询品牌和规格列表
        String categoryName=(String)searchMap.get("category");
        if(!"".equals(categoryName)){//如果有分类名称
            map.putAll(searchBrandAndSpecList(categoryName));
        }else{//如果没有分类名称，按照第一个查询
            if(categoryList.size()>0){
                map.putAll(searchBrandAndSpecList(categoryList.get(0)));
            }
        }


        return map;



    }

    /**
     * 高亮显示
     * @param searchMap
     * @return
     */
    private Map searchList(Map searchMap){
        Map map=new HashMap();
        //高亮显示构建
        HighlightQuery query=new SimpleHighlightQuery();
        HighlightOptions highlightOptions=new HighlightOptions().addField("item_title");//设置高亮的域
        highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前缀
        highlightOptions.setSimplePostfix("</em>");//高亮后缀
        query.setHighlightOptions(highlightOptions);//设置高亮选项
        //1.按照关键字查询
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //1.2 按分类筛选
        if(!"".equals(searchMap.get("category"))){

            Criteria filterCriteria=new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.3 按品牌筛选
        if(!"".equals(searchMap.get("brand"))) {
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.4 过滤规格
        if(searchMap.get("spec")!=null){
            Map<String,String> specMap= (Map) searchMap.get("spec");
            for(String key:specMap.keySet() ){
                Criteria filterCriteria=new Criteria("item_spec_"+key).is( specMap.get(key) );
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //1.5 按价格筛选.....
        if(!"".equals(searchMap.get("price"))) {
            String[] price = ((String) searchMap.get("price")).split("-");
            if (!price[0].equals("0")){//如果区间起点不等于 0
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if (!price[1].equals("*")){//如果区间终点不等于*
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //1.6 分页查询
        Integer pageNo= (Integer) searchMap.get("pageNo");//提取页码
        if(pageNo==null){
            pageNo=1;//默认第一页
        }
        Integer pageSize=(Integer) searchMap.get("pageSize");//每页记录数
        if(pageSize==null){
            pageSize=20;//默认 20
        }
        query.setOffset((pageNo-1)*pageSize);//从第几条记录查询
        query.setRows(pageSize);

        //1.7 排序

        String sortValue= (String)searchMap.get("sort");//升序ASC 降序DESC
        String sortField=  (String)searchMap.get("sortField");//排序字段

        if(sortValue!=null && !sortValue.equals("")){

            if(sortValue.equals("ASC")){
                Sort sort=new Sort(Sort.Direction.ASC, "item_"+sortField);
                query.addSort(sort);
            }
            if(sortValue.equals("DESC")){
                Sort sort=new Sort(Sort.Direction.DESC, "item_"+sortField);
                query.addSort(sort);
            }
        }





        /*************获取高亮结果集*****************/
        //高亮页对象
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query,TbItem.class);
        //高亮入口
        for(HighlightEntry<TbItem> h: page.getHighlighted()){//循环高亮入口集合
            TbItem item = h.getEntity();//获取原实体类
            if(h.getHighlights().size()>0 &&
                    h.getHighlights().get(0).getSnipplets().size()>0){
                item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));//设置高亮的结果
            }
        }
        map.put("rows",page.getContent());
        map.put("totalPages", page.getTotalPages());//返回总页数
        map.put("total", page.getTotalElements());//返回总记录数
        return map;
    }

    /**
     * 根据分组显示
     * @param searchMap
     * @return
     */
    private List searchCategoryList(Map searchMap){
        List<String> list=new ArrayList();

        Query query=new SimpleQuery("*:*");
        //按照关键字查询
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //设置分组选项
        GroupOptions groupOptions=new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        // 分组查询
        //得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);

        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");

        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for(GroupEntry<TbItem> entry:content){
            list.add(entry.getGroupValue());//将分组结果的名称封装到返回值中
        }
        return list;
    }

    /**
     * 查询品牌和规格列表
     * @param category 分类名称
     * @return
     */
    private Map searchBrandAndSpecList(String category){
        Map map=new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);//获取模板 ID
        if(typeId!=null){
            //根据模板 ID 查询品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);//返回值添加品牌列表
            //根据模板 ID 查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", specList);
        }
        return map;

    }
}
