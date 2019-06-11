app.service("loginService",function($http){
	this.loginName=function(){
		debugger;
		return $http.get("../login/name.do");
	}
})