package ebayUKItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.epiclouds.handlers.AbstractNettyCrawlerHandler;
import org.epiclouds.spiders.spiderobject.abstracts.AbstractSpiderObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PageSpiderHandler extends AbstractNettyCrawlerHandler{
	private ConcurrentHashMap<String,Integer> itemMap;
	private Queue<String> itemQueue = new LinkedBlockingQueue<String>();
	private String categoryId;

	public PageSpiderHandler(String host, String url,
			AbstractSpiderObject spider, String categoryId) {
		super(host, url, spider);
		this.categoryId = categoryId;
	}

	public void handle(String content) throws Exception {
		// TODO Auto-generated method stub
		Document doc=Jsoup.parse(content);
//		FileOutputStream fout=new FileOutputStream("html.txt");
//		fout.write(content.getBytes());
		
		if(doc==null||"".equals(content)){
			throw new Exception("return content is null!");
		}
		Elements eles=doc.select("ul#ListViewInner > li");
		String item_id="";
		String seller_name="";
		String hot_red="";
		Element ele;
		//System.out.println(eles.html());
		if(eles.size()==0){
			stop();
			return;
		}
		for(int i=0;i<eles.size();i++){
			//System.out.println(eles.get(i));
			//System.out.println(eles.get(i).select("[iid]").attr("iid"));
			item_id=eles.get(i).select("[iid]").attr("iid");
			ele=eles.get(i).select("div.hotness-signal.red").first();
			if(ele!=null)
				hot_red=ele.ownText();
			else
				hot_red="";
//			Elements c_eles;
//			c_eles=eles.get(i).select("ul.lvdetails left space-zero full-width").select("li");
//			if(c_eles.size()>3)
//			{
//				seller_name=c_eles.get(1).text();
//				if(seller_name.startsWith("Seller:")){
//					seller_name=seller_name.substring(8);
//				}
//			}
			//System.out.println(hot_red);
			String id="";
			//if(!item_id.equals("")&&(hot_red.endsWith("sold")||hot_red.endsWith("watch"))){
			if(!item_id.equals("")&&(hot_red.contains("sold"))){
				id=eles.get(i).select("[iid]").attr("iid");
				//System.out.println(hot_red);
				if(!itemMap.containsKey(id)){
					itemQueue.add(id);
				}
			}
		}
//		if(item_id_que.size()==0)
//			throw new Exception("can't get any itemId");
		stop();
	}

	@Override
	protected void onBefore() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onNormalFinished() {
		String itemId;
		while(!itemQueue.isEmpty()){
			itemId=itemQueue.poll();
			EbayCategoryItemSpider ebayItemSpider=new EbayCategoryItemSpider(this.getSpider(), 
					1, categoryId, itemId);
		

	}
		
	}

	@Override
	protected void onDataFinished() {
		// TODO Auto-generated method stub
		
	}

}
