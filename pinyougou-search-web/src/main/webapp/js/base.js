var app=angular.module('pinyougou',[]);//不分页模板
/*$sce 服务写成过滤器*/
app.filter('trustHtml',['$sce',function($sce){
    return function(data){
        return $sce.trustAsHtml(data);
    }
}]);