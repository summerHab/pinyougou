
		app.controller('brandController',function($scope,$http,$controller,brandService){
			
			$controller('baseController',{$scope:$scope})
			//查询品牌列表
			$scope.findAll=function(){
				brandService.findAll().success(
						function(response){
							$scope.list=response;
						}
				);	
			};	
			
			
			//分页
			$scope.findPage=function(page,size){
				brandService.findPage(page,size).success(
						function(response){
							$scope.list=response.rows;//显示当前页数据
							$scope.paginationConf.totalItems=response.total;//总记录数
						}
				);	
			};	
			
			/* //增加
			$scope.save=function(){
				var methord='add';
				if($scope.entity.id!=null){
					methord='update';
				}
				$http.post('../brand/'+methord+'.do',$scope.entity).success(
						function(response){
							if(response.success){
								$scope.reloadList();//重新加载
							}else{
								alert(response.message);//错误提示
							}
						}
				);		
			};	 */
			
			//新增
			$scope.save=function(){
				var object=null;//方法名 
				if($scope.entity.id!=null){
					object=brandService.update($scope.entity);
				}else{
					object=brandService.add($scope.entity);
				}			
				object.success(
					function(response){
						if(response.success){
							$scope.reloadList();//刷新
						}else{
							alert(response.message);
						}				
					}		
				);
			}
				
			 //查询一个
			$scope.findOne=function(id){
				brandService.findOne(id).success(
					function(response){
							$scope.entity=response;
						}
					
				);
			};
			
			
			//删除
			$scope.dele=function(){
				brandService.dele($scope.selectIds).success(
						function(response){
							if(response.success){
								$scope.reloadList();//刷新
							}else{
								alert(response.message);
							}				
						}	
				);
			};
		
			
			
			//条件查询
			$scope.searchEntity={};//定义搜索对象
			$scope.search=function(page,size){
				brandService.search(page,size,$scope.searchEntity).success(
						function(response){
							$scope.list=response.rows;//显示当前页数据
							$scope.paginationConf.totalItems=response.total;//总记录数
						}
				);	
			};
//			//下拉品牌列表
//			$scope.selectOptionList=function(){
//				brandService.selectOptionList.success(
//						function(response){
//						$scope.list=response;
//				});
//			}
		});