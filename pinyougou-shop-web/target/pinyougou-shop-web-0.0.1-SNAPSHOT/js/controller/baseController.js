    app.controller('baseController',function($scope){
//分页控件配置
    	
    	   $scope.paginationConf = {
    			   currentPage: 1,   //当前页
    			   totalItems: 10,   //总条数
    			   itemsPerPage: 10, //每页显示条数
    			   perPageOptions: [10, 20, 30, 40, 50],  //选择每页显示条数
    			   onChange: function(){           //onchange 页码变更自动触发的方法
    			    $scope.reloadList();//重新加载
    			   }
    	    }; 
    	 //刷新列表
            $scope.reloadList=function(){
            	$scope.search($scope.paginationConf.currentPage , $scope.paginationConf.itemsPerPage);
            };  
            $scope.selectIds=[];//选中项的id集合
       	 //用户勾选复选框
       	 $scope.select=function($event,id){
       		 if($event.target.checked){
       		 $scope.selectIds.push(id);//push 向集合中添加元素
       		 }else{
       			 var index=$scope.selectIds.indexOf(id);//查找元素所在位置
       			 $scope.selectIds.splice(index,1);   //第一个参数 元素所在位置，第二个参数 要移除几个
       		 }
       	 };
       	$scope.s={};
       	$scope.jsonToString=function(jsonString,key){
    	
    		var json= JSON.parse(jsonString);
    		var value="";
    		
    		for(var i=0;i<json.length;i++){
    			if(i>0){
    				value+=",";
    			}			
    			value +=json[i][key];			
    		}
    				
    		return value;
    	}
       	
       	$scope.searchObjectByKey=function(list,key,keyValue){
       		for(var i=0;i<list.length;i++){
       			if(list[i][key]==keyValue){
       				return list[i];
       			}
       		}
       		return null;
       	}
    });  	 