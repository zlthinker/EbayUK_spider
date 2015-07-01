package terapeak_spider.test;

import java.util.List;

/**
 * @author zl
 *
 */
public class CategoryBean {
	private String id;
	private String parentId;
	private String name;
	private boolean isLeaf;
	private List<String> children;
	

	public CategoryBean() {
		
	}
	
	public CategoryBean(String id, String parentId, String name,
			boolean isLeaf, List<String> children) {
		this.id = id;
		this.parentId = parentId;
		this.name = name;
		this.isLeaf = isLeaf;
		this.children = children;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public List<String> getChildren() {
		return children;
	}

	public void setChildren(List<String> children) {
		this.children = children;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
