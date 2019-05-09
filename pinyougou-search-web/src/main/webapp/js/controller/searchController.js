app.controller("searchController",function(searchService,$scope){

    $scope.search=function() {
        alert("666");
        searchService.search($scope.searchMap).success(
            function(response) {
                $scope.resultMap=response;
        });
    }
})