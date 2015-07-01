package ebayUKItem;

import java.util.ArrayList;
import java.util.List;

import org.epiclouds.handlers.AbstractNettyCrawlerHandler;
import org.epiclouds.spiders.dbstorage.condition.impl.EqualCondition;
import org.epiclouds.spiders.dbstorage.manager.abstracts.StorageBean;
import org.epiclouds.spiders.dbstorage.manager.abstracts.StorageBean.OperationType;
import org.epiclouds.spiders.spiderobject.abstracts.AbstractSpiderObject;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import terapeak_spider.test.DataBean;

public class EbayCategoryItemHandler extends AbstractNettyCrawlerHandler{

	private ItemBean itembean;
	
	public EbayCategoryItemHandler(String host, String url,
			AbstractSpiderObject spider, String itemId, String categoryId) {
		super(host, url, spider);
		itembean = new ItemBean();
		itembean.setItemId(itemId);
		itembean.setCategoryId(categoryId);
		itembean.setItemViewUrl(host+url+itemId);		
	}

	public void handle(String content) throws Exception {
		if(content.equals("")){
			throw new Exception("return content is null!");
		}
		Document doc=Jsoup.parse(content);
		if(doc==null){
			throw new Exception("return content is null!");
		}
		Elements els;
		String seller_name="";
		els=doc.select("a[href][aria-label]");
		for(int i=0;i<els.size();i++){
			seller_name=els.get(i).attr("href");
			if(seller_name.startsWith("http://www.ebay.co.uk/usr/")){
				int start="http://www.ebay.co.uk/usr/".length();
				seller_name=seller_name.substring(start);
				int end;
				for(int j=0;j<seller_name.length();j++){
					if(seller_name.charAt(j)=='?'){
						end=j;
						seller_name=seller_name.substring(0, end);
						break;
					}
				}
				break;
			}
		}
		if(seller_name.equals("")){
			throw new Exception("can't get the seller name");
		}
		//System.out.println(seller_name);
//		if(doc.select("span.mbg-nw").size()>0){
//			seller_name=doc.select("span.mbg-nw").first().ownText();
//			itembean.setSellerName(seller_name);
//		}
		String item_name=doc.select("h1#itemTitle").first().ownText();
		if(item_name.equals(""))
			throw new Exception("can't get item_name");
		String item_location="";
		String shipTo_location="";
		String shipping_type=doc.select("span[id=fShippingSvc]").text();
		String shipping_price=doc.select("span[id=fshippingCost]").text();
		if(doc.select("#mapPrcDChkUrl")!=null&&doc.select("#mapPrcDChkUrl").size()>0){
			stop();
			return;
		}
		{
			Element el=doc.select("#shSummary").first();
			if(el!=null&&el.text().contains("In-store pickup only")){
				stop();
				return;
			}
		}
		Elements price_list=doc.select("span[class=notranslate]");
		String item_price="";
		
		if(price_list.size()==1){
			item_price=price_list.first().text();
		}
		else{
			item_price=price_list.select("#mm-saleDscPrc").text();
			if(item_price.equals("")){
				item_price=price_list.select("#prcIsum").text();
			}
		}
		Element es;
		if(!item_price.startsWith("US")){
			es=doc.select("span#convbidPrice").first();
			if(es!=null){
				item_price=es.ownText();
			}
		}
		
		if(item_price.equals("")){
			if(doc.select("#finalPrc").size()>0){
				item_price=doc.select("#finalPrc").first().text();
			}
		}
		if(item_price.equals("")){
			if(doc.select("meta[name=twitter:text:price]").size()>0){
				item_price=doc.select("meta[name=twitter:text:price]").first().attr("content");
			}
		}
		if(item_price.equals("")){
			item_price=doc.select("div.pd-dt.pd-ip-pd").text();
			//System.out.println(item_price);
			if(item_price.equals("")||item_price.split(" ").length<2){
				this.itembean.setSoldNumbers(null);
				stop();
				return;
			}
			item_price=item_price.split(" ")[3]+item_price.split(" ")[4];
		}
		//System.out.println(item_price);
		if(item_price.equals("")){
			throw new Exception("can't get the item_price");
		}
			
		
		String sold_number=doc.select("[href^=http://offer.ebay.co.uk/ws/eBayISAPI.dll?ViewBidsLogin]").text();
		sold_number = sold_number.replace(" sold", "");

		els=doc.select("div.iti-eu-bld-gry");
		if(els.size()>0)
			item_location=els.get(0).ownText();
		if(els.size()>1){
			shipTo_location=els.get(1).text();
			String suf=els.get(1).select("a[href=#shpCntId").text();
			if(!"".equals(suf)){
				shipTo_location=shipTo_location.substring(0, shipTo_location.length()-suf.length());
			}
		}
		
		if(item_location.equals("")){
			Elements locations=doc.select("div.u-flL.lable:contains(Item location:)");
			if(locations.size()>0){
				item_location=locations.get(0).nextElementSibling().text();
			}
		}
		
		if(item_location.equals(""))
			throw new Exception("can't get the item_location");
		if(shipTo_location.equals(""))
			throw new Exception("can't get the shipTo_location");
		Elements pic_url_els=doc.select("div#vi_main_img_fs").select("table.img");
		String pic_url="";

		List<String> pic_url_list=new ArrayList<String>();
		if(pic_url_els.size()==0){
			Elements pic_url_big=doc.select("img#icImg");
			if(pic_url_big.size()>0){
				pic_url_list.add(picUrlAmplifier(pic_url_big.get(0).attr("src")));
				//System.out.println("the big picture Url is"+pic_url_big.get(0).attr("src"));
			}
			
		}
		else{
		//System.out.println(els.size());
			for(int i=0;i<pic_url_els.size();i++){
				try{

					pic_url=pic_url_els.get(i).select("img").attr("src");
					if(pic_url=="")
						throw new Exception("the pic ur is null");
					if(pic_url.startsWith("http://i.ebayimg.com"))
						pic_url_list.add(picUrlAmplifier(pic_url));
					else
						pic_url_list.add(pic_url);
				}catch(Exception e){
					System.out.println("exist null pic url in item "+itembean.getItemViewUrl());
				}
			}
		}

		els=doc.select("div[class=bsi-c1]");
		String sellerRealName = els.get(0).text();
		String sellerAddress = null;
		for(int i=1;i<els.size();i++){
		sellerAddress += els.get(i).text() + "#";
		}
		
		els=doc.select("div[class=bsi-c2]");
		String sellerPhone = els.get(1).text();
		String sellerEmail = els.get(3).text();
		
		//System.out.println(els.html());
		itembean.setSellerName(seller_name);
		itembean.setItemLocation(item_location);
		itembean.setItemName(item_name);
		itembean.setItemPrice(item_price);
		itembean.setShippingPrice(shipping_price);
		itembean.setShippingType(shipping_type);
		itembean.setShipToLocation(shipTo_location);
		itembean.setItemPicUrls(pic_url_list);
		itembean.setSellerAddr(sellerAddress);
		itembean.setSellerEmail(sellerEmail);
		itembean.setSellerRealName(sellerRealName);
		itembean.setSellerPhone(sellerPhone);
		
			
		if(pic_url_list.size()==0)
		{
			throw new Exception("can't get picture url");
		}
		if(itembean.getSoldNumbers()!=null&&itembean.getSoldNumbers().size()>0){
			SoldNumberBean bean=itembean.getSoldNumbers().get
					(itembean.getSoldNumbers().size()-1);
	        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");  
	        //鏃堕棿瑙ｆ瀽    
			if(DateTime.parse(bean.getTime(), format).toString("yyyy-MM-dd").equals((new DateTime().toString("yyyy-MM-dd")))){
				itembean.getSoldNumbers().remove(itembean.getSoldNumbers().size()-1);
			}
		}
		
		itembean.getSoldNumbers().add(new SoldNumberBean(sold_number));
		stop();		
	}
	
	public String picUrlAmplifier(String url)throws Exception{
		if("".equals(url)){
			throw new Exception("the pic ur is null");
		}
		String place_str="12";
		int suf=0;
		int pre=0;
		int l=url.length();
		for(int i=l-1;i>=0;i--){
			if(url.charAt(i)=='.')suf=i;
			if(url.charAt(i)=='_'){
				pre=i;
				break;
			}
		}
		return url.substring(0, pre+1)+place_str+url.substring(suf);

	}


	@Override
	protected void onBefore() {
		StorageBean.Builder builder = StorageBean.Builder.newBuilder(OperationType.FIND, "ebayUK", "page_data");
		EqualCondition<Object> con1 = new EqualCondition<Object>("id", itembean.getCategoryId());
		EqualCondition<Object> con2 = new EqualCondition<Object>("id", itembean.getItemId());
		builder.addConditon(con1);
		builder.addConditon(con2);
		StorageBean sb=builder.build();
		List<DBObject> re = null;
		try {
			re = this.getSpider().getDbmanager().find(sb, DBObject.class);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		itembean.getSoldNumbers().clear();
		if(re.size()>0){
			if(re.get(0).get("soldNumbers")!=null){
				BasicDBList list=(BasicDBList)re.get(0).get("soldNumbers");
				if(list!=null){				
					for(int i=0;i<list.size();i++){
						DBObject db=(DBObject)list.get(i);
						SoldNumberBean bean=new SoldNumberBean();
						bean.setTime((String)db.get("time"));
						bean.setSoldnumber((String)db.get("soldnumber"));
						itembean.getSoldNumbers().add(bean);
					}
					
				}
			}
		}
	}

	@Override
	protected void onNormalFinished() {
		this.getSpider().finish();
		
	}

	@Override
	protected void onDataFinished() {
		int sold_threshold = -1;
		if(itembean.getSoldNumbers()==null||itembean.getSoldNumbers().size()==0){
	//		this.getSpider().finish();
			return;
		}
		int sold_number=soldNumberToInt(itembean.getSoldNumbers().get(itembean.getSoldNumbers().size()-1).getSoldnumber());
		if(sold_number<=sold_threshold){
			//debug
			//System.out.println(sold_number);
	//		this.getSpider().finish();
			return;
		}
		StorageBean.Builder builder = StorageBean.Builder.newBuilder(OperationType.UPORINSERT, "ebayUK", "category_data");		
		
		EqualCondition<Object> con1 = new EqualCondition<Object>("id", itembean.getItemId());
		EqualCondition<Object> con2 = new EqualCondition<Object>("id", itembean.getCategoryId());
		builder.addConditon(con1);		
		builder.addConditon(con1);
		try {
			storage(builder, itembean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public ItemBean getItembean() {
		return itembean;
	}

	public void setItembean(ItemBean itembean) {
		this.itembean = itembean;
	}
	
	private  int soldNumberToInt(String number){
		if(number.equals(""))
			return 0;
		else
		   number=number.split(" ")[0];
		String numbers[]=number.split(",");
		int d=1;
		int integer=0;
		for(int i=numbers.length-1;i>=0;i--){
			integer+=d*Integer.valueOf(numbers[i]);
			d=d*1000;
		}
		return integer;
	}

}
