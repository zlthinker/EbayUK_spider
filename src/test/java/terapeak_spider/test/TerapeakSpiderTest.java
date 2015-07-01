package terapeak_spider.test;

import org.epiclouds.spiders.bootstrap.imp.Bootstrap;
import org.epiclouds.spiders.spiderobject.manager.abstracts.SpiderObjectManagerInterface;

public class TerapeakSpiderTest {

		final private String  addMessage="";
		final private String  deleteMessage="";
		public static SpiderObjectManagerInterface spiderManager;
		
		public static void main(String[] args) throws Exception {
			Bootstrap bt=new Bootstrap();
			bt.setBootSpiderClass(TerapeakManageSpider.class).start();
			TerapeakManageSpider terapeak1=new TerapeakManageSpider(null, 1);
			
			spiderManager=bt.getSingle().getSpiderManager();
			try{
				spiderManager.add(terapeak1);
			}catch(Exception e){
				e.printStackTrace();
			}
		}

}
