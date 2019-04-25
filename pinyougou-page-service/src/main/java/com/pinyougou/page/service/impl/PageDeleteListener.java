package com.pinyougou.page.service.impl;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;

import com.pinyougou.page.service.ItemPageService;

public class PageDeleteListener implements MessageListener{
@Autowired
private ItemPageService ItemPageService;
	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		ObjectMessage objectMessage=(ObjectMessage) message;
		
		try {
		Long [] goodsIds=(Long [])objectMessage.getObject();
		System.out.println("ItemDeleteListener监听收到的消息..."+goodsIds);
         boolean b = ItemPageService.deleteItemHtml(goodsIds);
         System.out.println("网页删除的结果"+b);
		} catch (Exception e) {
			// TODO: handle exception
		}	}

}
