app.service("brandService",function($http){
			//查询品牌列表
			this.findAll=function(){
				return $http.get('../brand/findAll.do');
			}
			//查询品牌列表
			this.findPage=function(page,size){
				return $http.get('../brand/findPage.do?page='+page+'&size='+size);
			}
			//增加
			this.add=function(entity){
				return $http.post('../brand/add.do',entity);
			}
			//修改
			this.update=function(entity){
				return $http.post('../brand/update.do',entity);
			}
			//查询一个
			this.findOne=function(id){
				return $http.get('../brand/findOne.do?id='+id);
			}
			//删除
			this.dele=function(ids){
				return $http.get('../brand/delete.do?ids='+ids);
			}
			//多条件查询
			this.search=function(page,size,searchEntity){
				return $http.post('../brand/search.do?page='+page+'&size='+size,searchEntity);
			}
			//下拉列表查询
			this.selectOptionList=function(){
				return $http.get('../brand/selectOptionList.do');
			}
		})