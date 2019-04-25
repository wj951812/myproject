app.service('brandService',function($http){
    	   this.findAll=function(){
    		 return  $http.get("../brand/findAll.do");
    	   }
    	   this.findPage=function(page,size){
    		  return   $http.get("../brand/findPage.do?page="+page+"&size="+size);
    	   }
    	   this.findOne=function(id){
    		  return  $http.get("../brand/findOne.do?id="+id);
    	   }
    	   this.add=function(b){
    		  return $http.post("../brand/addBrand.do",b);
    	   } 
    	   this.update=function(b){
     		  return $http.post("../brand/update.do",b);
     	   }
    	   this.dele=function(ids){
    		   return $http.get("../brand/delete.do?ids="+ids);
    	   }
    	   this.search=function(page,size,s){
    		   return    $http.post("../brand/search.do?page="+page+"&size="+size,s);
    	   }
    	   this.selectOptionList=function(){
    		   return $http.get("../brand/selectOptionList.do");
    	   }
    	});