package com.codedb.model;

public class TreeNodeData {
	public static final Integer ROOT_NODE = 0;
	public static final Integer DB_NODE = 1;
	public static final Integer TABLE_NODE = 2;

	private String title = "";

	private Integer type = ROOT_NODE;

	public TreeNodeData(String t, Integer type) {
		this.title = t;
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}
