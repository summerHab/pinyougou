package com.pinyougou.search.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String, Object> map=new HashMap<>();
        //创建一个solr的查询对象
        Query query=new SimpleQuery();

        Criteria criteria=new Criteria("item_keywords");
        query.addCriteria(criteria);
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query,TbItem.class);
        List<TbItem> items = tbItems.getContent();//获取到对象
        map.put("rows",items);
        return map;

    }
}
