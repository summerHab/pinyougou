package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.pojo.TbTypeTemplateExample;
import com.pinyougou.pojo.TbTypeTemplateExample.Criteria;
import com.pinyougou.sellergoods.service.TypeTemplateService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;
	
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;

	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page=   (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
	@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();
		
		if(typeTemplate!=null){			
						if(typeTemplate.getName()!=null && typeTemplate.getName().length()>0){
				criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}
			if(typeTemplate.getSpecIds()!=null && typeTemplate.getSpecIds().length()>0){
				criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}
			if(typeTemplate.getBrandIds()!=null && typeTemplate.getBrandIds().length()>0){
				criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}
			if(typeTemplate.getCustomAttributeItems()!=null && typeTemplate.getCustomAttributeItems().length()>0){
				criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}
	
		}
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)typeTemplateMapper.selectByExample(example);
		saveToRedis();//存入数据到缓存
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> findSpecList(Long id) {
		 TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
		 List<Map> list = JSON.parseArray(tbTypeTemplate.getSpecIds(),Map.class);
		 for(Map map:list){
			 
			 TbSpecificationOptionExample example=new TbSpecificationOptionExample();
			 com.pinyougou.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			 criteria.andSpecIdEqualTo(new Long( (Integer)map.get("id") ));
			 List<TbSpecificationOption> list2 = specificationOptionMapper.selectByExample(example);
			 map.put("opaction",list2);
		 }

		return list;
	}

	/**
	 * 缓存模板品牌和规格
	 */
//	private void saveToRedis(){
//		List<TbTypeTemplate> tbTypeTemplates = findAll();
//		for (TbTypeTemplate tbTypeTemplate : tbTypeTemplates) {
//			//缓存品牌
//			List<Map> brandList  = JSON.parseArray(tbTypeTemplate.getBrandIds(), Map.class);
//			redisTemplate.boundHashOps("brandList").put(tbTypeTemplate.getId(),brandList);
//			//缓存规格
////			List<Map> specidList = JSON.parseArray(tbTypeTemplate.getSpecIds(),Map.class);
////			redisTemplate.boundHashOps("specidList").put(tbTypeTemplate.getId(),specidList);
//			//存储规格列表
//			List<Map> specList = findSpecList(tbTypeTemplate.getId());//根据模板 ID 查询规格列表
//			redisTemplate.boundHashOps("specList").put(tbTypeTemplate.getId(),specList);
//		}
//	}

	private void saveToRedis(){


		//获取模板数据
		List<TbTypeTemplate> typeTemplateList = findAll();
			//循环模板
		for(TbTypeTemplate typeTemplate :typeTemplateList){
		//存储品牌列表
			List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(),
					Map.class);
			redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(),
					brandList);
		//存储规格列表
			List<Map> specList = findSpecList(typeTemplate.getId());//根据模板 ID 查询规格列表
			redisTemplate.boundHashOps("specList").put(typeTemplate.getId(),
					specList);
		}
		System.out.println("缓存模板品牌+规格==============================");
	}
}
