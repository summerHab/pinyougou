package com.pinyougou.sellergoods.service.impl;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	
	
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	
	
	@Autowired
	private TbItemMapper tbItemMapper;
	
	@Autowired
	private TbBrandMapper brandMapper;
	
	
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Autowired
	private TbSellerMapper sellerMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		goods.getGoods().setAuditStatus("0");//设置未申请状态
		goodsMapper.insert(goods.getGoods());
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());//设置 ID
		goodsDescMapper.insert(goods.getGoodsDesc());//插入商品扩展数据
		saveItemList(goods);
		
	}
	/**
	 * 提取add和update中保存的公共代码
	 * @param goods
	 */
	private void saveItemList(Goods goods){
		//如果启用规格
				if("1".equals(goods.getGoods().getIsEnableSpec())){
					List<TbItem> itemList = goods.getItemList();
					//sku
					for(TbItem item:itemList){
						//sku标题
						String title = goods.getGoods().getGoodsName();
						
						String spec = item.getSpec();//获取spcd，转化为map集合
						Map<String, Object> specMap = com.alibaba.fastjson.JSON.parseObject(spec);
						Set<String> keySet = specMap.keySet();
						for(String key:keySet){
							
							title+=""+specMap.get(key);//添加网络，内存到标题上
							
						}
						item.setTitle(title);//设置标题内容
						
						item.setSellPoint(goods.getGoodsDesc().getIntroduction());//设置sellPoint
						
//						item.setPrice(goods.getGoods().getPrice());//设置price
						
						item.setGoodsId(goods.getGoods().getId());//商品 SPU 编号
						item.setSellerId(goods.getGoods().getSellerId());//商家编号
						item.setCategoryid(goods.getGoods().getCategory3Id());//商品分类编号（3 级）
						item.setCreateTime(new Date());//创建日期
						item.setUpdateTime(new Date());//修改日期
						//品牌名称
						TbBrand brand =brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
						item.setBrand(brand.getName());
						//分类名称
						TbItemCat itemCat =itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
						item.setCategory(itemCat.getName());
						//商家名称
						TbSeller seller =sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
						item.setSeller(seller.getNickName());
						
//						JSON.parseArray(goods.getGoodsDesc().getItemImages())
						List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(),Map.class);
//						Object object = imageList.get(0).get("url");
						if(imageList.size()>0){
							item.setImage ((String)imageList.get(0).get("url"));
						}
						tbItemMapper.insert(item);
					}
				}else{
					
					TbItem item=new TbItem();
					item.setTitle(goods.getGoods().getGoodsName());//设置标题内容
					item.setSellPoint(goods.getGoodsDesc().getIntroduction());//设置sellPoint
					
					item.setPrice(goods.getGoods().getPrice());//设置price
					
					item.setGoodsId(goods.getGoods().getId());//商品 SPU 编号
					item.setSellerId(goods.getGoods().getSellerId());//商家编号
					item.setCategoryid(goods.getGoods().getCategory3Id());//商品分类编号（3 级）
					item.setCreateTime(new Date());//创建日期
					item.setUpdateTime(new Date());//修改日期
					item.setStatus("1");//状态
					item.setIsDefault("1");//是否默认
					item.setNum(99999);//库存数量
					item.setSpec("{}");
					//品牌名称
					TbBrand brand =brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
					item.setBrand(brand.getName());
					//分类名称
					TbItemCat itemCat =itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
					item.setCategory(itemCat.getName());
					//商家名称
					TbSeller seller =sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
					item.setSeller(seller.getNickName());
					
//					JSON.parseArray(goods.getGoodsDesc().getItemImages())
					List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(),Map.class);
//					Object object = imageList.get(0).get("url");
					if(imageList.size()>0){
						item.setImage ((String)imageList.get(0).get("url"));
					}
					tbItemMapper.insert(item);
				}
				
	}
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		goods.getGoods().setAuditStatus("0");//设置未申请状态:如果是经过修改的商品，需要重新设置状态
		
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		//移除原有的sku的spcd队列
		
		TbItemExample example=new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		tbItemMapper.deleteByExample(example);
		
		//插入此进去
		
		saveItemList(goods);
			
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods=new Goods();
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbGoods);
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(tbGoodsDesc);
		//读取sku
		TbItemExample example=new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		
		List<TbItem> list = tbItemMapper.selectByExample(example);
		goods.setItemList(list);
		
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("0");
			tbGoods.setAuditStatus("4");
			goodsMapper.updateByPrimaryKey(tbGoods);
			
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
//				criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
				criteria.andSellerIdEqualTo(goods.getSellerId());	
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
		
		/**
		 * 更新状态
		 */
		@Override
		public void updateStatus(Long[] ids, String status) {
			for(Long id:ids){
				TbGoods goods = goodsMapper.selectByPrimaryKey(id);
				goods.setAuditStatus(status);
				goodsMapper.updateByPrimaryKey(goods);
			}
		}

	@Override
	public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status){

		TbItemExample example=new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdIn(Arrays.asList(goodsIds));
		criteria.andStatusEqualTo(status);
		return tbItemMapper.selectByExample(example);
	}


}
