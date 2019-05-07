package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;

/**
 * 品牌列表
 * @author ASUS
 *
 */
public interface BrandService {
	
	/**
	 * 查询所有
	 * @return
	 */
	public List<TbBrand> findAll();
	/**
	 * 品牌下拉框数据
	 * @return
	 */
	public List<Map> selectOptionList();
	/**
	 * 分页
	 * @param pageNum 当前页码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	/**
	 * 增加方法
	 * @param tbBrand
	 */
	public void add(TbBrand tbBrand);
	/**
	 * id查一个
	 * @param id
	 * @return
	 */
	public TbBrand findOne(long id);
	/**
	 * 修改
	 * @param tbBrand
	 */
	public void update(TbBrand tbBrand);
	
	/**
	 * 删除
	 * @param ids
	 */
	public void delete(long[] ids);
	/**
	 * 条件查询
	 * @param tbBrand
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public PageResult findPage(TbBrand tbBrand,int pageNum,int pageSize);
}
