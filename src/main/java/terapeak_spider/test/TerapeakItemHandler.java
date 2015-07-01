package terapeak_spider.test;

import io.netty.handler.codec.http.HttpMethod;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.epiclouds.handlers.AbstractNettyCrawlerHandler;
import org.epiclouds.handlers.util.ProxyStateBean;
import org.epiclouds.spiders.spiderobject.abstracts.AbstractSpiderObject;
import org.epiclouds.spiders.dbstorage.condition.impl.EqualCondition;
import org.epiclouds.spiders.dbstorage.data.impl.DBDataEntry;
import org.epiclouds.spiders.dbstorage.manager.abstracts.DBMangerInterface;
import org.epiclouds.spiders.dbstorage.manager.abstracts.StorageBean;
import org.epiclouds.spiders.dbstorage.manager.abstracts.StorageBean.OperationType;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class TerapeakItemHandler extends AbstractNettyCrawlerHandler{
	private volatile int i=1;
	private volatile int days=80;
	private volatile int num=9;
	private volatile DataBean dbb=new DataBean();
	private volatile SearchBean sr;
	private volatile CategoryBean eb;
	public volatile static HashMap<String,String> hs;

	public SearchBean getSr() {
		return sr;
	}
	public CategoryBean getEb() {
		return eb;
	}
	public void setEb(CategoryBean eb) {
		this.eb = eb;
	}

	public int getDays() {
		return days;
	}
	public void setDays(int days) {
		this.days = days;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public void setSr(SearchBean sr) {
		this.sr = sr;
	}
	public int getI() {
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	static{
		hs=new HashMap<String,String>();
		//headers
	    hs.put("Accept", "application/json, text/javascript, */*; q=0.01");
	    hs.put("Content-Type", "application/json");

	    hs.put("Origin","https://sell.terapeak.com");
	    hs.put("Referer","https://sell.terapeak.com/?page=eBayCategoryResearch");
	    hs.put("X-Requested-With", "XMLHttpRequest");
	}
	
	public TerapeakItemHandler(
			String host, String url, HttpMethod md,
			AbstractSpiderObject spider, int days, int num, final SearchBean sb, CategoryBean eb) {
		super(host, url, spider);
		this.setMd(md);
		this.setSchema("https");
		this.getHeaders().putAll(hs);
		this.days=days;
		this.num=num;
		this.sr=sb;
		this.eb=eb;
		dbb.setData(new TerapeakBean());
		dbb.setId(eb.getId());
		dbb.setName(eb.getName());
		HashMap<String,String> pd=new HashMap<String,String>();
		pd.put(null, JSONObject.toJSONString(getSearchBean()));
	    this.setPostdata(pd);
	    System.out.println("In TerapeakItemHandler: postdata is "+pd);
	    System.out.println("CategorySpider " + eb.getId() + "'s handler is created.");
		// TODO Auto-generated constructor stub
	}
	
	public void handle(String content) throws Exception {
		// TODO Auto-generated method stub
	//	System.out.println("Id "+this.getEb().getId() + ": content="+content);
		System.out.println("No."+i+" Id "+this.getEb().getId() + "is in handle");
		if(i<=num+1){
        	TerapeakBean tmp=JSONObject.parseObject(content,TerapeakBean.class);
        	formatTerapeakCategorySoldData(tmp);
        	formatTerapeakCategoryToalListingsData(tmp);
        	formatTerapeakCategoryRevenueData(tmp);
        	//System.err.println("ok");
/*        	if(tmp.getAverage_end_price().getData().size()>0&&i==num+1){
        		System.err.println(tmp.getAverage_end_price().getData().get(
        				tmp.getAverage_end_price().getData().size()-1)[0]+":::i="+i+":::num="+num+":::"
        				+JSONObject.toJSONString(sr)+":::"+new DateTime().toString("yyyy-MM-dd"));
        	}*/
        	TerapeakBean tb=dbb.getData();
        	DateTime otime=null;
        	if(tb.getAverage_end_price()!=null&&tb.getAverage_end_price().getData().size()>0){
				Object[] obs=tb.getAverage_end_price().getData().get(
						tb.getAverage_end_price().getData().size()-1);
				otime=DateTime.parse(((String)obs[0]).split(" ")[0],DateTimeFormat.forPattern("yyyy/MM/dd"));	
			}
        	if(otime!=null){
        		//System.err.println("otime is not null"+otime.toString("yyyy-MM-dd"));
        		Iterator<Object[]>  iter=tmp.getAverage_end_price().getData().iterator();
        		while(iter.hasNext()){
        			Object[] ob=iter.next();
        			DateTime ntime=DateTime.parse(((String)ob[0]).split(" ")[0],DateTimeFormat.forPattern("yyyy/MM/dd"));
        			if(ntime.isAfter(otime)){
        				break;
        			}
        			iter.remove();
        		}
        		iter=tmp.getBids().getData().iterator();
        		while(iter.hasNext()){
        			Object[] ob=iter.next();
        			DateTime ntime=DateTime.parse(((String)ob[0]).split(" ")[0],DateTimeFormat.forPattern("yyyy/MM/dd"));
        			if(ntime.isAfter(otime)){
        				break;
        			}
        			iter.remove();
        		}
        		
        		iter=tmp.getBids_per_listings().getData().iterator();
        		while(iter.hasNext()){
        			Object[] ob=iter.next();
        			DateTime ntime=DateTime.parse(((String)ob[0]).split(" ")[0],DateTimeFormat.forPattern("yyyy/MM/dd"));
        			if(ntime.isAfter(otime)){
        				break;
        			}
        			iter.remove();
        		}
        		iter=tmp.getItems_sold().getData().iterator();
        		while(iter.hasNext()){
        			Object[] ob=iter.next();
        			DateTime ntime=DateTime.parse(((String)ob[0]).split(" ")[0],DateTimeFormat.forPattern("yyyy/MM/dd"));
        			if(ntime.isAfter(otime)){
        				break;
        			}
        			iter.remove();
        		}
        		
        		
        		iter=tmp.getRevenue().getData().iterator();
        		while(iter.hasNext()){
        			Object[] ob=iter.next();
        			DateTime ntime=DateTime.parse(((String)ob[0]).split(" ")[0],DateTimeFormat.forPattern("yyyy/MM/dd"));
        			if(ntime.isAfter(otime)){
        				break;
        			}
        			iter.remove();
        		}
        		
        		iter=tmp.getTotal_listings().getData().iterator();
        		while(iter.hasNext()){
        			Object[] ob=iter.next();
        			DateTime ntime=DateTime.parse(((String)ob[0]).split(" ")[0],DateTimeFormat.forPattern("yyyy/MM/dd"));
        			if(ntime.isAfter(otime)){
        				break;
        			}
        			iter.remove();
        		}
        		
        		iter=tmp.getSell_through().getData().iterator();
        		while(iter.hasNext()){
        			Object[] ob=iter.next();
        			DateTime ntime=DateTime.parse(((String)ob[0]).split(" ")[0],DateTimeFormat.forPattern("yyyy/MM/dd"));
        			if(ntime.isAfter(otime)){
        				break;
        			}
        			iter.remove();
        		}
        	
        		dbb.getData().addAverage_end_price(tmp.getAverage_end_price());
        		dbb.getData().addBids(tmp.getBids());
        		dbb.getData().addBids_per_listings(tmp.getBids_per_listings());
        		dbb.getData().addItems_sold(tmp.getItems_sold());
        		dbb.getData().addRevenue(tmp.getRevenue());
        		dbb.getData().addTotal_listings(tmp.getTotal_listings());
        		dbb.getData().addSell_through(tmp.getSell_through());
        	}else{
        		dbb.setData(tmp);
        	}
        }
		if(i>num){
			//System.err.println(JSONObject.toJSONString(tb));
			super.stop();
			return;
		}
		SearchBean sb=getSearchBean();
        HashMap<String,String> pd=new HashMap<String,String>();
        pd.put(null, JSONObject.toJSONString(sb));
		request("/services/ebay/categories/trends?token=4e5396e3fe80ee1249a0b8147c08c5636a"
				+ "95579b274624fc6ce568ef3d2cdde5", HttpMethod.POST, 
					hs, pd, "https");
	}

	@Override
	protected void onBefore() {
		// TODO Auto-generated method stub
		StorageBean.Builder builder = StorageBean.Builder.newBuilder(OperationType.FIND, "ebayUK", "category_data");
		EqualCondition<Object> con = new EqualCondition<Object>("id", this.getEb().getId());
		builder.addConditon(con);			
		StorageBean sb=builder.build();
		List<DataBean> re = null;
		try {
	//		System.err.println("zl: before find.");
			re = this.getSpider().getDbmanager().find(sb, DataBean.class);
	//		System.err.println("zl: after find.");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (re == null || re.isEmpty()) {
			this.getSpider().finish();
			return;
		}
		if(re.size()>0){
			//System.err.println("data is "+ (String)re.get(0).get("data"));
			TerapeakBean tb=re.get(0).getData();
//			this.setTb(tb);
			if(tb.getAverage_end_price().getData().size()>0){
				Object[] obs=tb.getAverage_end_price().getData().get(tb.getAverage_end_price().getData().size()-1);
				DateTime ntime=new DateTime();
				
				DateTime otime=DateTime.parse(((String)obs[0]).split(" ")[0],DateTimeFormat.forPattern("yyyy/MM/dd"));							
				int days=ntime.getDayOfYear()-otime.getDayOfYear()+360*(ntime.getYear()-otime.getYear());
				if(days<=0){
					try {
						super.stop();
					} catch (Exception e) {
					}
					return;
				}
				int tt=(days-1)/this.getDays()+1;
				this.setNum(tt);
				this.setI(this.getI()-1);
				HashMap<String,String> pd=new HashMap<String,String>();
				pd.put(null, JSONObject.toJSONString(this.getSearchBean()));
			    this.setPostdata(pd);
			}
		}
		
		System.out.println("CategorySpider " + this.getEb().getId() + " on before.");
	}


	@Override
	protected void onNormalFinished() {
		// TODO Auto-generated method stub
		this.getSpider().finish();
	}

	
	@Override
	protected void onDataFinished() {
		// TODO Auto-generated method stub
		try {
			System.err.println("In onDataFinished: save data.");
			TerapeakBean tb=dbb.getData();
			if((tb!=null&&tb.getAverage_end_price()!=null&&tb.getAverage_end_price().getData()!=null&&
					tb.getAverage_end_price().getData().size()>0)){
				//System.err.println(JSONObject.toJSONString(tb));
				StorageBean.Builder builder = StorageBean.Builder.newBuilder(OperationType.UPORINSERT, "ebayUK", "category_data");		
				dbb.setCatch_time(new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
				EqualCondition<Object> con = new EqualCondition<Object>("id", eb.getId());
				builder.addConditon(con);			
				storage(builder, dbb);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("In onDataFinished: exception happens.");
		}
		try {
			super.stop();
		} catch (Exception e) {
		}
		System.err.println("onDataFinished :"+eb.getId());
	}
	
	private void formatTerapeakCategorySoldData(TerapeakBean tmp) {
		CommonData cd=tmp.getItems_sold();
		if(cd==null||cd.getData()==null||cd.getData().size()==0){
			return;
		}
		double even=(cd.getMaxValue()+cd.getMinValue())/2;
		double even_guess=0;
		for(Object[] obs:cd.getData()){
			even_guess+=Double.parseDouble(obs[1].toString());
		}
		even_guess/=cd.getData().size();
		even/=even_guess;
		for(Object[] obs:cd.getData()){
			obs[1]=even*Double.parseDouble(obs[1].toString());
		}
	}
	private void formatTerapeakCategoryToalListingsData(TerapeakBean tmp) {
		CommonData cd=tmp.getTotal_listings();
		if(cd==null||cd.getData()==null||cd.getData().size()==0){
			return;
		}
		double even=(cd.getMaxValue()+cd.getMinValue())/2;
		double even_guess=0;
		for(Object[] obs:cd.getData()){
			even_guess+=Double.parseDouble(obs[1].toString());
		}
		even_guess/=cd.getData().size();
		even/=even_guess;
		for(Object[] obs:cd.getData()){
			obs[1]=even*Double.parseDouble(obs[1].toString());
		}
	}
	private void formatTerapeakCategoryRevenueData(TerapeakBean tmp) {
		CommonData cd=tmp.getRevenue();
		if(cd==null||cd.getData()==null||cd.getData().size()==0){
			return;
		}
		double even=(cd.getMaxValue()+cd.getMinValue())/2;
		double even_guess=0;
		for(Object[] obs:cd.getData()){
			even_guess+=Double.parseDouble(obs[1].toString());
		}
		even_guess/=cd.getData().size();
		even/=even_guess;
		for(Object[] obs:cd.getData()){
			obs[1]=even*Double.parseDouble(obs[1].toString());
		}
	}
	/**
	 * 
	 * @return
	 */
	public SearchBean getSearchBean(){
		DateTime dt = new DateTime();
        DateTime dt5 = dt.minusDays(days*num-days*i);  
        sr.setDate_range(days);
        sr.setDate(dt5.toString("yyyy-MM-dd"));
        i++;
        return sr;
	}

}
