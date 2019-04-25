 app.controller('brandController',function($scope,$controller,brandService){
	       $controller('baseController',{$scope:$scope});
    	   $scope.findAll=function(){
    		   brandService.findAll().success(function(response){
    			   $scope.brands=response;
    		   });
    	   }
    		 
    	 //分页
    	    $scope.findPage=function(page,rows){
    	    	brandService.findPage(page,rows).success(function(response){
    	    		$scope.brands=response.rows;  //显示数据
    	    		$scope.paginationConf.totalItems=response.total; //将总记录数赋值给paginationConf
    	    	});
    	    }
    	 //新增
    	 $scope.save=function(){
    		 var object=brandService.add($scope.b);
    		 if($scope.b.id!=null){
    			 object=brandService.update($scope.b);
    		 }
    		 object.success(function(rp){
    			 if(rp.success){
    				 $scope.reloadList();
    			 }else{
    				 alart(rp.message);
    			 }
    		 });
    	 }
    	
    	 //查找对象
    	 $scope.findOne=function(id){
    		 brandService.findOne(id).success(function(rp){
    			 $scope.b=rp;
    		 });
    	 }
    	  
    	
    	 //删除选中
    	 $scope.dele=function(){
    		 brandService.dele($scope.selectIds).success(function(rp){
    			 if(rp.success){
    				 $scope.reloadList();
    			 }else{
    				 alart(rp.message);
    			 }
    		 });
    	 }
    	 //搜索
    	 $scope.search=function(page,size){
    		    brandService.search(page,size,$scope.s).success(function(response){
 	    		$scope.brands=response.rows;  //显示数据
 	    		$scope.paginationConf.totalItems=response.total; //将总记录数赋值给paginationConf
 	    	});
    	 }
       });