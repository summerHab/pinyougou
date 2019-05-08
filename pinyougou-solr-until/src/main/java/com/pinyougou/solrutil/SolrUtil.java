package com.pinyougou.solrutil;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

/**
 * 实现商品数据的查询(已审核商品) 63
 * @author ASUS
 *
 */
@Component
public class SolrUtil {
	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
  	private SolrTemplate  solrTemplate;
	/**
	 * 导入数据
	 */
	public void importItemData(){
		
		TbItemExample example=new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");
		List<TbItem> items = itemMapper.selectByExample(example);
		System.out.println("开始");
		for(TbItem item:items){
			System.out.println(item.getTitle());
		}

		solrTemplate.saveBean(items);
		solrTemplate.commit();
		System.out.println("结束");

		
	}

	public static void main(String[] args) {
		ApplicationContext context=new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
		SolrUtil solrUtil= (SolrUtil)context.getBean("solrUtil");
		solrUtil.importItemData();
	}

}
