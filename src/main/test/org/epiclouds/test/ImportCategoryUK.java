package org.epiclouds.test;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.io.File;

import org.bson.BSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import jxl.*;



public class ImportCategoryUK {
	
	private static volatile MongoClient client;
	private static Workbook book;
	private static Sheet sheet;
	private static String database = "ebayUK";
	private static String filepath = "F:/eclipse/ebayCategories20150709.xls";
	private static int sheetnum = 2;
	
	public static void main (String args[]) throws Exception {
		if(client==null){
			MongoCredential credential = MongoCredential.createCredential("root", "admin","123Yuanshuju456".toCharArray());
			client = new MongoClient(new ServerAddress("106.3.38.50",27017), Arrays.asList(credential));
		}
		DB db=client.getDB(database);
		DBCollection col=db.getCollection("categories");
		
		book = Workbook.getWorkbook(new File(filepath));
		sheet = book.getSheet(sheetnum);
		int i = 0;
		while(!"".equals(sheet.getCell(0, ++i).getContents())) {
//			boolean isleaf = !"".equals(sheet.getCell(4, i).getContents());
			String name = getName(i);
			
			String id = sheet.getCell(0, i).getContents();
			System.out.println("i="+i+", id="+id);
			String parentId = sheet.getCell(8, i).getContents();
			if (id == parentId)
				parentId = "-1";
			insertData(col, id, parentId, name);
			if (!parentId.equals("-1"))
				updateChild(col, id, parentId);
			
		}
		
	}
	
	public static void insertData(DBCollection collection, String id, String parentId, String name) {
		DBObject updateCondition = new BasicDBObject();
		updateCondition.put("id", id);		
		DBObject existence = collection.findOne(updateCondition);
		if (existence != null) {
			System.out.println("Entry id "+id+" is repeated.");
			return;
		}
		DBObject insert = new BasicDBObject();
		insert.put("id", id);
		
		insert.put("parentId", parentId);
		insert.put("name", name);
		insert.put("isLeaf", true);
		insert.put("children", new ArrayList<String>());
		collection.insert(insert);
		System.out.println("Entry id "+id+" is inserted.");
	}
	
	public static void updateChild(DBCollection collection, String childId, String parentId) {
		DBObject updateCondition = new BasicDBObject();
		updateCondition.put("id", parentId);
//		System.out.println("updateCondition: "+updateCondition);
		
		DBObject parent = collection.findOne(updateCondition);
	//	DBObject children = (DBObject)parent.get("children");
//		System.out.println(parent);
		ArrayList<String> childrenList = (ArrayList<String>) parent.get("children");
		childrenList.add(childId);
				
		DBObject updateValue = new BasicDBObject();
		updateValue.put("children", childrenList);
		updateValue.put("isLeaf", false);
		
		DBObject updateSetValue=new BasicDBObject("$set",updateValue); 
		collection.update(updateCondition, updateSetValue);
		System.out.println("Add child "+childId+" to parent "+parentId);
		
	}
	
	public static String getName(int i) {
		int j = 6;
		while("".equals(sheet.getCell(j, i).getContents())) {
			j --;
		}
		return sheet.getCell(j, i).getContents();
	}
}
