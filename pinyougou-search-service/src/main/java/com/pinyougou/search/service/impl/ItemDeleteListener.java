package com.pinyougou.search.service.impl;

import java.lang.reflect.Array;
import java.util.Arrays;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.search.service.ItemSearchService;
@Component
public class ItemDeleteListener implements MessageListener{

	@Autowired
	private ItemSearchService itemSearchService;
	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method 
		try {
			ObjectMessage objectMessage=(ObjectMessage)message;
			Long[] 	goodsIds = (Long []) objectMessage.getObject();
			System.out.println("ItemDeleteListener监听收到消息。。。"+goodsIds);
			itemSearchService.deleteByGoodsIds(Arrays.asList(goodsIds));
			System.out.println("成功删除索引库中的记录");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
