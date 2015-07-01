package terapeak_spider.test;

import io.netty.handler.codec.http.HttpMethod;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.epiclouds.handlers.AbstractNettyCrawlerHandler;
import org.epiclouds.handlers.util.ProxyStateBean;
import org.epiclouds.spiders.dbstorage.condition.impl.EqualCondition;
import org.epiclouds.spiders.dbstorage.data.impl.DBDataEntry;
import org.epiclouds.spiders.dbstorage.manager.abstracts.DBMangerInterface;
import org.epiclouds.spiders.dbstorage.manager.abstracts.StorageBean;
import org.epiclouds.spiders.dbstorage.manager.abstracts.StorageBean.OperationType;
import org.epiclouds.spiders.spiderobject.abstracts.AbstractSpiderObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class TerapeakCategoryHandler extends AbstractNettyCrawlerHandler{
	
	List<CategoryBean> resultList;

	public TerapeakCategoryHandler(AbstractSpiderObject spider) {
		super(null, null, spider);
		this.setMd(md);
		// TODO Auto-generated constructor stub
	}

	public void handle(String content) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onBefore() {
		DBMangerInterface DBManager = this.getSpider().getDbmanager();
		StorageBean.Builder builder = StorageBean.Builder.newBuilder(OperationType.FIND, "ebayUK", "categories");	
		EqualCondition<Boolean> condition = new EqualCondition<Boolean>("isLeaf", false);
		builder.addConditon(condition);
		StorageBean sb = builder.build();
		try {
			resultList = DBManager.find(sb, CategoryBean.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		for(CategoryBean cb : resultList) {
			System.out.println("CategorySpider " + cb.getId() + " is added.");
			SearchBean sbean = new SearchBean();
			sbean.setId(cb.getId());
			sbean.setSiteID("3");
			sbean.setDate_range(7);
			sbean.setCurrency("1");			
			TerapeakSpider ts = new TerapeakSpider(cb, this.getSpider(), 1, sbean);
			this.getSpider().addChild(ts);	
			
		}
		
	}

	@Override
	protected void onNormalFinished() {
		// TODO Auto-generated method stub
		if (this.getSpider().getChildren().size() < 1) {
			this.getSpider().finish();
		}
	}

	@Override
	protected void onDataFinished() {
		// TODO Auto-generated method stub
		
	}

}
