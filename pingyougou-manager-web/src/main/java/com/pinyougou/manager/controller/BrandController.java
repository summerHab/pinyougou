package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/brand")
public class BrandController {
	
	@Reference
	private BrandService brandservice;
	/**
	 * 查询所有
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbBrand> findAll() {
		
		return brandservice.findAll();
	}
	/**
	 * 分页查询
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page,int size) {
		return brandservice.findPage(page, size);
	}
	
	/**
	 * 下拉列表查询
	 * @return
	 */
	@RequestMapping("/selectOptionList")
	public List<Map> selectOptionList(){
		return brandservice.selectOptionList();
	}
	/**
	 * 增加
	 * @param tbBrand
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbBrand tbBrand) {
		try {
			brandservice.add(tbBrand);
			return new Result(true, "新加成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "新加失败");
		}
		
	}
	/**
	 * 通过id查询一个
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbBrand findOne(long id){
		return brandservice.findOne(id);
	}
	/**
	 * 修改
	 * @param tbBrand
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbBrand tbBrand) {
		try {
			brandservice.update(tbBrand);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
		
	}
	/**
	 * 删除
	 * @param ids
	 */
	@RequestMapping("/delete")
	public Result delete(long[] ids) {
		try {
			brandservice.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
		
	}
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbBrand tbBrand,int page,int size) {
		
		return brandservice.findPage(tbBrand, page, size);
	}
}
