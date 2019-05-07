app.service('uplodeService',function($http){
	
	this.uplodeFile=function(){
		//创建文件对象
		var formData=new FormData();
		formData.append('file',file.files[0]);//文件上传框的name，file.flies[0]去的是第一个
	return $http({
			method:'post',
			url:"../uplode.do",
			data:formData,
			headers: {'Content-Type':undefined},
			transformRequest:angular.identity
		});
		
	}
})