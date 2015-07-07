package org.epiclouds.test;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.net.UnknownHostException;
import java.util.Arrays;

import org.epiclouds.bean.DataBean;
import org.epiclouds.bean.TerapeakBean;

public class TestCategoryNum {
	private static volatile MongoClient client;
	private static int count = 0;
	private static int total = 0;
	
	public static void main(String args[]) throws UnknownHostException {
		if(client==null){
			MongoCredential credential = MongoCredential.createCredential("root", "admin","123Yuanshuju456".toCharArray());
			client = new MongoClient(new ServerAddress("106.3.38.50",27017), Arrays.asList(credential));
		}
		DB db=client.getDB("ebayUK");
		DBCollection col=db.getCollection("category_data");
		DBCursor cursor = col.find(new BasicDBObject());
		DBObject result;
		int i=0;
		while(cursor.hasNext()) {
			if ((result = cursor.next()) != null) {
				DataBean databean = JSONObject.parseObject(result.toString(),DataBean.class);
			//	System.out.println(databean.toString());
				TerapeakBean tb = databean.getData();
				int num = tb.getTotal_listings().getData().size();
				total ++;
				if (num < 320){
					count ++;
					col.remove(result);
					System.out.println("total="+total+", count="+count+", id="+ result.get("id") + " is removed.");
				}
			}
		}
	//	System.out.println("In total " +total+" entries, "+count+" entries' size is larger than 600.");
	}

}
