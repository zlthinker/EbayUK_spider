package terapeak_spider.test;

import io.netty.handler.codec.http.HttpMethod;

import java.net.InetSocketAddress;
import java.util.Map;

import org.epiclouds.handlers.AbstractHandler;
import org.epiclouds.handlers.util.ProxyStateBean;
import org.epiclouds.spiders.spiderobject.abstracts.AbstractSpiderObject;

public class TerapeakSpider extends AbstractSpiderObject{
	private String host = "sell.terapeak.com";
	private String URL= "/services/ebay/categories/trends?token=4e5396e3fe80ee1249a0b8147c08c5636a95579b274624fc6ce568ef3d2cdde5";
	private CategoryBean cb;
	private SearchBean sb;
	private int days = 80;
	private int num = 9;
	
	public TerapeakSpider(CategoryBean cb,
			AbstractSpiderObject parent, int totalSpiderNum, SearchBean sb) {
		super(parent, totalSpiderNum);
		this.cb = cb;
		this.sb = sb;
	}
	
	public TerapeakSpider(){
		super();
	}

	@Override
	public AbstractHandler createSpiderHandler() {
		// TODO Auto-generated method stub
		return new TerapeakItemHandler(host, URL, HttpMethod.POST, this, days, num, sb, cb);
	}

}
