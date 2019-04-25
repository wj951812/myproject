package com.pinyougou.search.service.impl;

import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
/**
 * 监听：用于删除索引库中记录
 * @author Administrator
 *
 */
@Component
public class ItemSearchListener implements MessageListener{

	@Autowired
	private ItemSearchService itemSearchService;
	
	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		try {
			System.out.println("監聽接收到消息.");
			
			TextMessage textMessage=(TextMessage)message;
			String text;
			text = textMessage.getText();
			List<TbItem> list = JSON.parseArray(text,TbItem.class);
			for(TbItem item:list) {
//				System.out.println(item.getId()+""+item.getTitle());
				Map specMap=JSON.parseObject(item.getSpec());//將spec字段中的json字符串
				item.setSpecMap(specMap);//給帶註解的字段賦值
			}
			itemSearchService.importList(list);//導入
			System.out.println("成功導入到索引庫");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
