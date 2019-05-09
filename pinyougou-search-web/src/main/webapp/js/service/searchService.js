app.service("searchService",function($http){

    this.search=function(searchMap) {
        $http.get("itemSearch/serarch.do",searchMap)
    }
})