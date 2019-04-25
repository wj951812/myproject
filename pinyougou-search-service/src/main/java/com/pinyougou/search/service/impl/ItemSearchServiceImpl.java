package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.weaver.patterns.IfPointcut.IfFalsePointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

	@Autowired
	private SolrTemplate solrTemplate;

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 关键字查询
	 * 
	 * @param searchMap
	 * @return
	 */
	public Map searchList(Map searchMap) {
		// TODO Auto-generated method stub
		Map map = new HashMap<>();

		HighlightQuery query = new SimpleHighlightQuery();

		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");// 设置高亮域

		highlightOptions.setSimplePrefix("<em style='color:yellow'>");// 高亮前缀

		highlightOptions.setSimplePostfix("</em>");// 高亮后缀

		query.setHighlightOptions(highlightOptions);// 设置高亮选项

		// 关键字查询
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));

		query.addCriteria(criteria);
        //按分类筛选
		if(!"".equals(searchMap.get("category"))) {
			Criteria filterCriteria=new Criteria("item_category").is(searchMap.get("category"));
			FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		
		//按品牌筛选
		if(!"".equals(searchMap.get("brand"))) {
			Criteria filterCriteria=new Criteria("item_brand").is(searchMap.get("brand"));
			FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		
		//按规格过滤
		if(searchMap.get("spec")!=null) {
			Map<String,String> specMap = (Map)searchMap.get("spec");
			for (String key : specMap.keySet()) {
				
				Criteria filterCriteria=new Criteria("item_spec_"+key).is(specMap.get(key));
				FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery);
				
			}
		}
		//按照价格过滤
		if(!"".equals(searchMap.get("price"))) {
			String [] price = ((String) searchMap.get("price")).split("-");
			if(!price[0].equals("0")) {//如果区间起点不等于0
				Criteria filtercriteria=new Criteria("item_price").greaterThanEqual(price[0]);
			    FilterQuery filterQuery=new SimpleFilterQuery(filtercriteria);
			    query.addFilterQuery(filterQuery);
			}
			if(!price[1].equals("*")) {//如果区间终点不等于*
				Criteria filtercriteria=new Criteria("item_price").lessThanEqual(price[1]);
			    FilterQuery filterQuery=new SimpleFilterQuery(filtercriteria);
			    query.addFilterQuery(filterQuery);
			}
			
		}
		//分页查询
		Integer pageNo=(Integer)searchMap.get("pageNo");//提取页码
		
		if(pageNo==null) {
			pageNo=1;//默认第一页
		}
		Integer pageSize=(Integer)searchMap.get("pageSize");//每页记录数
		if(pageSize==null) {
			pageSize=20;//默认每页显示条数
		}
		query.setOffset((pageNo-1)*pageSize);//设置起始查询条数，即从第几页开始查询
		query.setRows(pageSize);
		
		//排序
		String sortValue=(String) searchMap.get("sort");//ASC DESC
		String sortField=(String) searchMap.get("sortField");//排序字段
		
		if(sortValue!=null && !sortValue.equals("")) {
			if(sortValue.equals("ASC")) {
				Sort sort=new Sort(Sort.Direction.ASC,"item_"+sortField);
				query.addSort(sort);
			}
			if(sortValue.equals("DESC")) {
				Sort sort=new Sort(Sort.Direction.DESC,"item_"+sortField);
				query.addSort(sort);
			}
		}
		
		// 高亮页
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

		for (HighlightEntry<TbItem> h : page.getHighlighted()) {// 循环高亮入口集合
			TbItem item = h.getEntity();// 获取原实体类

			if (h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size() > 0) {
				item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));// 设置高亮结果
			}
			;
		}
		map.put("rows", page.getContent());
		map.put("totalPages", page.getTotalPages());//返回总页数
		map.put("total", page.getTotalElements());//返回总记录数
		return map;
	}

	@Override
	public Map<String, Object> search(Map searchMap) {
		// TODO Auto-generated method stub
		//关键字空格处理
		String keywords=(String)searchMap.get("keywords");
		if(keywords.contains("")) {
		searchMap.put("keywords", keywords.replace(" ",""));
		}
		Map<String, Object> map = new HashMap<>();
		// 查询列表高亮显示
		map.putAll(searchList(searchMap));

		// 根据关键字查询商品分类
		List<String> categoryList = searchCategoryList(searchMap);
		map.put("categoryList", categoryList);
		
		//查询品牌及规格列表
		String category=(String)searchMap.get("category");
		if(!category.equals("")) {//如果有分类名称
			map.putAll(searchBrandAndSpecList(category));
		}else {//如果没有分类名称则按照第一个查询
			if(categoryList.size()>0) {
				map.putAll(searchBrandAndSpecList(categoryList.get(0)));
			}
		}
		return map;
	}

	/**
	 * 查询分类列表
	 * 
	 * @param searchMap
	 * @return
	 */
	private List searchCategoryList(Map searchMap) {
		List<String> list = new ArrayList<>();

		Query query = new SimpleQuery();
		// 按照关键字查询
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));

		query.addCriteria(criteria);

		// 设置分组选项
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions);

		// 获取分组页
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);

		// 得到分组结果集
		GroupResult<TbItem> groupResult = page.getGroupResult("item_category");

		// 得到分组结果入口页
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();

		// 得到分组入口集合
		List<GroupEntry<TbItem>> content = groupEntries.getContent();

		for (GroupEntry<TbItem> entry : content) {
			list.add(entry.getGroupValue());// 将分组结果的名称封装到返回值中
		}

		return list;
	}
	
	/**
	 * 查询品牌列表和规格列表
	 * @param category 分类名称
	 * @return
	 */
	private Map searchBrandAndSpecList(String category) {
		
		Map map=new HashMap<>();
		
		//获取模板Id
	    Long typeId=(Long)redisTemplate.boundHashOps("itemCat").get(category);	
	    
	    if(typeId!=null) {
	    	
	    	//根据品牌列表ID查询品牌
	    	List brandList=(List)redisTemplate.boundHashOps("brandList").get(typeId);
	    	
	    	map.put("brandList",brandList);//返回添加品牌列表
	    	
	    	//根据模板Id查询规格列表
	    	List specList=(List)redisTemplate.boundHashOps("specList").get(typeId);
	    	
	    	map.put("specList", specList);
	    }
	    return map;
	}

	@Override
	public void importList(List list) {
		// TODO Auto-generated method stub
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
		System.out.println("方法执行了");
	}

	@Override
	public void deleteByGoodsIds(List goodsIdList) {
		// TODO Auto-generated method stub
		
		Query query=new SimpleQuery();
		Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
		query.addCriteria(criteria);
		solrTemplate.delete(query);
		solrTemplate.commit();
		System.out.println("方法执行了");
	}	
	
	
}
