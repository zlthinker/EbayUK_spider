package org.epiclouds.spider;

import java.util.List;

import org.epiclouds.bean.SearchBean;
import org.epiclouds.handlers.AbstractHandler;
import org.epiclouds.spiders.dbstorage.condition.impl.EqualCondition;
import org.epiclouds.spiders.dbstorage.manager.abstracts.DBMangerInterface;
import org.epiclouds.spiders.dbstorage.manager.abstracts.StorageBean;
import org.epiclouds.spiders.dbstorage.manager.abstracts.StorageBean.OperationType;
import org.epiclouds.spiders.spiderobject.abstracts.AbstractSpiderObject;
import org.epiclouds.bean.CategoryBean;

public class TerapeakManageSpider extends AbstractSpiderObject{

	private List<CategoryBean> resultList;
	
	public TerapeakManageSpider(AbstractSpiderObject parent, int totalSpiderNum) {
		super(parent);
	}
	
	public TerapeakManageSpider() {
		super(null);
	}
	
	@Override
	public void start() throws Exception {
		super.start();
		DBMangerInterface DBManager = this.getDbmanager();
		StorageBean.Builder builder = StorageBean.Builder.newBuilder(OperationType.FIND, "ebayUK", "categories");
		EqualCondition<Boolean> condition = new EqualCondition<Boolean>("isLeaf", true);
		builder.addConditon(condition);
		StorageBean sb = builder.build();
		try {
			resultList = DBManager.find(sb, CategoryBean.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(resultList == null || resultList.isEmpty()) {
			finish();
			return;
		}
		
		for(CategoryBean cb : resultList) {
	//		System.out.println("CategorySpider " + cb.getId() + " is added.");
			SearchBean sbean = new SearchBean();
			sbean.setId(cb.getId());
			sbean.setSiteID("3");
			sbean.setDate_range(80);
			sbean.setCurrency("1");			
			TerapeakSpider ts = new TerapeakSpider(cb, this, 1, sbean);
			this.addChild(ts);			
		}
	}
	
	public AbstractHandler createSpiderHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInfo() {
		// TODO Auto-generated method stub
		return null;
	}




}
