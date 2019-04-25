package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
	private TbBrandMapper brandDao;
	@Override
	public List<TbBrand> findAll() {
		// TODO Auto-generated method stub
	
		return brandDao.selectByExample(null);
	}
	
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		// TODO Auto-generated method stub
       PageHelper.startPage(pageNum, pageSize);
       Page<TbBrand> page=(Page<TbBrand>) brandDao.selectByExample(null);
		
       
       return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void save(TbBrand tbBrand) {
		// TODO Auto-generated method stub
		brandDao.insert(tbBrand);
	}

	@Override
	public TbBrand findOne(long id) {
		// TODO Auto-generated method stub
		return brandDao.selectByPrimaryKey(id);
	}

	@Override
	public void update(TbBrand tbBrand) {
		// TODO Auto-generated method stub
		brandDao.updateByPrimaryKey(tbBrand);
	}

	@Override
	public void delete(Long [] ids) {
		// TODO Auto-generated method stub
		for (Long id : ids) {
			brandDao.deleteByPrimaryKey(id);
		}
	}

	@Override
	public PageResult findPage(TbBrand tbBrand, int pageNum, int pageSize) {
			
		PageHelper.startPage(pageNum, pageSize);
		
	       TbBrandExample example=new TbBrandExample();
	       
	       Criteria criteria =example.createCriteria();
	       if(tbBrand!=null) {
	    	   if(tbBrand.getName()!=null && tbBrand.getName().length()>0) {
	    		   criteria.andNameLike("%"+tbBrand.getName()+"%");
	    	   }
	    	   if(tbBrand.getFirstChar()!=null && tbBrand.getFirstChar().length()>0) {
	    	   criteria.andFirstCharLike("%"+tbBrand.getFirstChar()+"%");
	    	   }
	       }
	       
	         Page<TbBrand> page=(Page<TbBrand>) brandDao.selectByExample(example);
	         
	       return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> selectOptionList() {
		return brandDao.selectOptionList();
	}

}
