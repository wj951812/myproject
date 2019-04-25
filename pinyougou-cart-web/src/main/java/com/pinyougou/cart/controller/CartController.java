package com.pinyougou.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;

import entity.Result;
import utils.CookieUtil;

@RestController
@RequestMapping("/cart")
public class CartController {

	@Reference
	private CartService cartService;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private HttpServletResponse response;

	/**
	 * 购物车列表
	 * @return
	 */
	@RequestMapping("/findCartList")
	public List<Cart> findCartList(){
		String cartListString  = CookieUtil.getCookieValue(request, "cartList","UTF-8");
	
	    if(cartListString==null || cartListString.equals("")) {
	    	cartListString="[]";
	    }
	    List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
	    return cartList_cookie;
	}
	
	@RequestMapping("/addGoodsToCartList")
	public Result addGoodsToCartList(Long itemId,Integer num) {
		try {
			List<Cart> cartList = findCartList();//获取购物车列表

			cartList = cartService.addGoodsToCartList(cartList, itemId, num);

			CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600 * 24, "UTF-8");
			return new Result(true, "添加成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new Result(false, "添加失败");
		}
	}
}
