app.controller("indexController",function($scope,loginService){
//	app.controller("baseController",$scope,$scope);
	
//	$scope.name=null;
	$scope.showName=function(){
		loginService.loginName().success(function(rp){
			$scope.loginName=rp.loginName;
		});
	}
});