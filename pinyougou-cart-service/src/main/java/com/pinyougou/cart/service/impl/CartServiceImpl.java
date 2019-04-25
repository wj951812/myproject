package com.pinyougou.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.request.CollectionAdminRequest.Create;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
@Service
public class CartServiceImpl implements CartService{

	@Autowired
	private TbItemMapper itemMapper;
	
	@Override
	public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
		// TODO Auto-generated method stub
		//1. 根据SKU ID查询SKU 商品信息
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		if(item==null) {
			throw new RuntimeException("商品不存在");
		}
		if(!item.getStatus().equals("1")) {
			throw new RuntimeException("商品状态无效");
		}
		
		//2.获取商家ID
		String sellerId=item.getSellerId();
		
		//3.根据商家ID判断购物车列表是否存在该商家的购物车
		Cart cart = searchCartBySellerId(cartList, sellerId);
		
		//4.如果购物车列表不存在该商家的购物车
		if(cart==null) {
			
		//4.1新建购物车对象
		cart=new Cart();
		cart.setSellerId(sellerId);
		cart.setSellerName(item.getSeller());
		TbOrderItem orderItem=createOrderItem(item, num);
		List orderItemList=new ArrayList<>();
		orderItemList.add(orderItem);
		cart.setOrderItemList(orderItemList);
		
		//4.2将购物车对象添加到购物车
		cartList.add(cart);
		}else {
			//5.如果购物车已存在该商家的购物车
			//判断该购物车中是否存在该商品
			TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
			
			if(orderItem==null) {//5.1.没有，新增购物车明细
				orderItem=createOrderItem(item, num);
				cart.getOrderItemList().add(orderItem);
			}else {//该购物车存在该商品
				orderItem.setNum(orderItem.getNum()+num);
				orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()));
				//如果数量操作后小于等于0，则移除
				if(orderItem.getNum()<=0) {
					cart.getOrderItemList().remove(orderItem);
				}
				//如果移除后cart的明细数量为0，则将cart移除
				if(cart.getOrderItemList().size()==0) {
					cartList.remove(cart);
					
				}
			}
			
		}
		return cartList;
	}
	
	/**
	 * 根据商家Id查询购物车对象，判断购物车中是否含有该商家
	 * @param cartList
	 * @param sellerId
	 * @return
	 */
	private Cart searchCartBySellerId(List<Cart> cartList,String sellerId) {
           for(Cart cart:cartList) {
        	   if(cart.getSellerId().equals(sellerId)) {
        		   return cart;
        	   }
           }
		return null;
		
	}
    /**
     * 根据商品明细id查询
     * @param orderItemList
     * @param itemId
     * @return
     */
	private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList,Long itemId) {
		for(TbOrderItem orderItem:orderItemList) {
			if(orderItem.getItemId().longValue()==itemId.longValue()) {
				return orderItem;
			}
		}
		return null;
	}
	/**
	 * 创建订单明细
	 * @param item
	 * @param num
	 * @return
	 */
	private TbOrderItem createOrderItem(TbItem item,Integer num) {
		if(num<=0) {
			throw new RuntimeException("数量非法");
		}
		TbOrderItem orderItem=new TbOrderItem();
		
		orderItem.setGoodsId(item.getGoodsId());
		
		orderItem.setItemId(item.getId());
		
		orderItem.setNum(num);
		
		orderItem.setPicPath(item.getImage());
	
		orderItem.setSellerId(item.getSellerId());
		
		orderItem.setPrice(item.getPrice());
		
		orderItem.setTitle(item.getTitle());
		
		orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
		
		return orderItem;
	}
}
