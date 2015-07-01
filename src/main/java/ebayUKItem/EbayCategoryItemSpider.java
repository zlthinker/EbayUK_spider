package ebayUKItem;

import org.epiclouds.handlers.AbstractHandler;
import org.epiclouds.spiders.spiderobject.abstracts.AbstractSpiderObject;

public class EbayCategoryItemSpider extends AbstractSpiderObject{

	private String host = "www.ebay.co.uk";
	private String url = "/item/";
	private String categoryId;
	private String itemId;
	public EbayCategoryItemSpider(AbstractSpiderObject parent, int totalSpiderNum, String categoryId, String itemId) {
		super(parent, totalSpiderNum);
		this.categoryId = categoryId;
		this.itemId = itemId;
	}
	
	
	@Override
	public AbstractHandler createSpiderHandler() {
		return new EbayCategoryItemHandler(host, url,
				this, itemId, categoryId);
	}

}
