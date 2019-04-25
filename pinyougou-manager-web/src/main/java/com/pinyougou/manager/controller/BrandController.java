package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;
@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;
    
	@RequestMapping("/findAll")
    public List<TbBrand> findAll(){
    return 	brandService.findAll();
    }
	
	
	@RequestMapping("/findPage")
	public PageResult findPage(int page,int size) {
	return brandService.findPage(page, size);
	}
	
	
	@RequestMapping("/addBrand")
	public Result saveBrand(@RequestBody TbBrand tbBrand) {
		try {
			brandService.save(tbBrand);
			return new Result(true,"保存成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return new Result(false, "保存失败");
		}
		
	}
	@RequestMapping("/update")
	public Result update(@RequestBody TbBrand tbBrand) {
		
		try {
			brandService.update(tbBrand);
			return new Result(true,"修改成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return new Result(false, "修改失败");
		}
		
	}
	@RequestMapping("/findOne")
	public TbBrand findOne(long id) {
		return brandService.findOne(id);
	}
	@RequestMapping("/delete")
	public Result delete(Long [] ids) {
		try {
			brandService.delete(ids);
			return new Result(true,"删除成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return new Result(false, "删除失败");
		}
	}
	
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbBrand tbBrand,int page,int size) {
		return brandService.findPage(tbBrand,page,size);

	}
	
	@RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
		return brandService.selectOptionList();
	}	
}
