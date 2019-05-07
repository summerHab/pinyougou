 //控制层 
app.controller('goodsController',function($scope,$controller,$location,goodsService,uplodeService,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){		
		
	var id=$location.search()['id']//会将静态页面传递的参数封装成
		if(id==null){
			return;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				editor.html($scope.entity.goodsDesc.introduction);
				//解析图片的颜色和url路径
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				//规格
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
				//sku
				for(var i=0;i<$scope.entity.itemList.length;i++){
					$scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
				}
			}
		);				
	}
	//
	$scope.checkedAttrbute=function(spaceName,opactionName){
		
		var object=$scope.selectkey($scope.entity.goodsDesc.specificationItems,'attributeName',spaceName);
		if(object!=null){
			if(object.attributeValue.indexOf(opactionName)>=0){
				return true;
			}
			else{
				return false;
			}
		}else{
			
			
			return false;
		}
	}
	
	
//	//添加
//	$scope.add=function(){	
//		
//		$scope.entity.goodsDesc.introduction=editor.html();
//		goodsService.add($scope.entity).success(
//			function(response){
//				if(response.success){	
//					alert("新增成功");
//		        	$scope.entity={};
//		        	editor.html("");
//				}else{
//					alert(response.message);
//				}
//			}		
//		);				
//	}
	//保存方法
	$scope.save=function(){
		//初始化文本域
		$scope.entity.goodsDesc.introduction=editor.html();
		var objectGoods;
		if($scope.entity.goods.id!=null){
			//如果有id，就是修改
			objectGoods=goodsService.update($scope.entity);	//修改
		}else{
			objectGoods=goodsService.add($scope.entity);//添加
		}
		
		objectGoods.success(
				function(response){
					if(response.success){	
						alert("新增成功");
			        	$scope.entity={};
			        	editor.html("");
			        	location.href="goods.html";//跳转到商品列表页
					}else{
						alert(response.message);
					}
				}		
			);				
		
	}
	
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele($scope.selectIds).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){	
		
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	//上传
	$scope.uplodeFile=function(){
		
		uplodeService.uplodeFile().success(
				
				function(response){
						if(response.success){//上传成功
								$scope.image_entity.url=response.message;//设置文件地址
						}else{
								alert(response.message);
							}
			
		}).error(function(){
			
			alert("上传发生错误");
		});
	}
	//将当前上传的图片传入图片列表
	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]},itemList:{}};
	$scope.add_image_entity=function(){
		
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity)
	}
		//删除图片
	$scope.remove_image_entity=function(index){
		$scope.entity.goodsDesc.itemImages.splice(index,1);
		}
	//一级商品分类表
	$scope.selectitemCatList=function(){
		itemCatService.findByParentId(0).success(
				function(response){
					$scope.itemCatList=response;
		});
	}
	//二级商品分类表
	$scope.$watch('entity.goods.category1Id',function(newValue,oldValue){
			itemCatService.findByParentId(newValue).success(
					function(response){
						$scope.itemCat2List=response;
			});
		
	});
	//三级商品分类
	$scope.$watch('entity.goods.category2Id',function(newValue,oldValue){
			itemCatService.findByParentId(newValue).success(
					function(response){
						$scope.itemCat3List=response;
			});
		
	});
	//更新模板id
	$scope.$watch('entity.goods.category3Id',function(newValue,oldValue){
			itemCatService.findOne(newValue).success(
					function(response){
						$scope.entity.goods.typeTemplateId=response.typeId;
			});
		
	});
	
	//读取模板id后读取品牌列表
	$scope.$watch('entity.goods.typeTemplateId',function(newValue,oldValue){
		typeTemplateService.findOne(newValue).success(
					function(response){
							$scope.typeTemplate=response;//获取类型模板
						$scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);//将其转化为json对象
						if($location.search()['id']==null){
						$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);//将其转化为json对象
						}
			});
		
		
		typeTemplateService.findSpecList(newValue).success(
				function(response){
					$scope.specList=response;
					
				});
	});
	//操作规格选项是否勾选
	$scope.updatesetAttrebute=function($event,name,value){
		
		var object=$scope.selectkey($scope.entity.goodsDesc.specificationItems,'attributeName',name);
		//object相当于{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}这个
		if(object!=null){
			if($event.target.checked){
				object.attributeValue.push(value);
			}else{//取消勾选
				 object.attributeValue.splice(object.attributeValue.indexOf(value),1);//移除
				
				//选项都取消了,移除
				if(object.attributeValue.length==0){
				$scope.entity.goodsDesc.specificationItems.splice(
							$scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}
			}
		}else{
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
		}
	}
	//创建item索引
	$scope.createitem=function(){
		
		$scope.entity.itemList=[{spec:{},price:0,num:9999,status:'0',isDefault:'0'}];//列表初始化
		var items=$scope.entity.goodsDesc.specificationItems;
		for(var i=0;i<items.length;i++){
			$scope.entity.itemList=addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
		}
	}
	
	//添加列值
	addColumn=function(list,columnName,conlumnValues){
		var newList=[];//新的集合
		
		for(var i=0;i<list.length;i++){
			
			var oldRow= list[i];
			
			for(var j=0;j<conlumnValues.length;j++){
				
				var newRow=JSON.parse(JSON.stringify(oldRow) );//深克隆
				
				newRow.spec[columnName]=conlumnValues[j];
				
				newList.push(newRow);
			}
		}
		return newList;
	}
	
	//状态值
	$scope.status=["未审核","已审核","审核为通过","关闭"];
	//修改查询的级别分类
	$scope.temCatcategoryIdList=[];
	$scope.findcategoryId=function(){
		itemCatService.findAll().success(
				function(response){
					
				for(var i=0;i<response.length;i++){
					$scope.temCatcategoryIdList[response[i].id]=response[i].name;	
				}	
			
		})
	}

	
});	
