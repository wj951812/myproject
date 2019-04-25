package com.pinyougou.sellergoods.service;
/**
 *品牌接口
 * @author Administrator
 *
 */

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;

public interface BrandService {
	
 /**
  * 查询所有
  * @return
  */
	public List<TbBrand> findAll();
 /**
  * 分页
  * @param page
  * @param size
  * @return
  */
 public PageResult findPage(int pageNum,int pageSize);
 /**
  * 添加品牌
  * @param tbBrand
  */
 public void save(TbBrand tbBrand);
 
 /**
  *根据id查询对象
  * @param id
  * @return
  */
 public TbBrand findOne(long id);
 /**
  * 修改
  * @param tbBrand
  */
 public void update(TbBrand tbBrand);
 /**
  * 删除选中
  * @param ids
  */
 public void delete(Long [] ids);
 /**
  * 条件查询+分页
  * @param page
  * @param size
  * @return
  */
 public PageResult findPage(TbBrand tbBrand, int pageNum,int pageSize);
 /**
  * 品牌下拉列表
  * @return
  */
 public List<Map> selectOptionList();
}
