package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {
	
	@Autowired
	private TbBrandMapper brandmapper;
	/**
	 * 查询所有
	 */
	@Override
	public List<TbBrand> findAll() {
		// TODO Auto-generated method stub
		return brandmapper.selectByExample(null);
	}
	/**
	 * 分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbBrand> page = (Page<TbBrand>) brandmapper.selectByExample(null);
		return new PageResult(page.getTotal(),page.getResult());
	}
	/**
	 * 添加
	 */
	@Override
	public void add(TbBrand tbBrand) {
		brandmapper.insert(tbBrand);
		
	}
	/**
	 * 查询一个
	 */
	@Override
	public TbBrand findOne(long id) {
		
		return brandmapper.selectByPrimaryKey(id);
	}
	/**
	 * 修改
	 */
	@Override
	public void update(TbBrand tbBrand) {
		brandmapper.updateByPrimaryKey(tbBrand);
	}
	/**
	 * 删除
	 */
	@Override
	public void delete(long[] ids) {
		for(long id:ids){
			brandmapper.deleteByPrimaryKey(id);
		}
		
	}
	/**
	 * 条件查询
	 */
	@Override
	public PageResult findPage(TbBrand tbBrand, int pageNum, int pageSize) {
		
		PageHelper.startPage(pageNum, pageSize);
		
		//创建条件
		TbBrandExample example=new TbBrandExample();
		//构建条件
		Criteria criteria = example.createCriteria();
		
		if(tbBrand!=null){
			if(tbBrand.getName()!=null && tbBrand.getName().length()>0){
				criteria.andNameLike("%"+tbBrand.getName()+"%");
			}
			if(tbBrand.getFirstChar()!=null && tbBrand.getFirstChar().length()>0){
				criteria.andFirstCharLike("%"+tbBrand.getFirstChar()+"%");
			}
		}
		
		//条件查询
		Page<TbBrand> page = (Page<TbBrand>) brandmapper.selectByExample(example);
		return new PageResult(page.getTotal(),page.getResult());
	}
	/**
	 * 品牌下拉列表
	 */
	@Override
	public List<Map> selectOptionList() {
		return brandmapper.selectOptionList();
	}
	
	

}
