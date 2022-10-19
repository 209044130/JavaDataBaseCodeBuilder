package com.codedb.model;

public class TableInfoData {
	// 字段名称
	private String columnName = "";
	// 数据类型
	private String typeName = "";
	// 数据长度
	private String charOctetLength = "";
	// 小数位数
	private String numPrecRadix = "";
	// 获取字段是否可以为空
	private boolean isNullable = true;
	// 是否是主键
	private boolean isKey = false;
	// 获取表字段注解信息
	private String remarks = "";

	public TableInfoData(String columnName, String typeName, String charOctetLength, String numPrecRadix,
			boolean isNullable, boolean isKey, String remarks) {
		this.columnName = columnName;
		this.typeName = typeName;
		this.charOctetLength = charOctetLength;
		this.numPrecRadix = numPrecRadix;
		this.isNullable = isNullable;
		this.isKey = isKey;
		this.remarks = remarks;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getCharOctetLength() {
		return charOctetLength;
	}

	public void setCharOctetLength(String charOctetLength) {
		this.charOctetLength = charOctetLength;
	}

	public String getNumPrecRadix() {
		return numPrecRadix;
	}

	public void setNumPrecRadix(String numPrecRadix) {
		this.numPrecRadix = numPrecRadix;
	}

	public boolean isNullable() {
		return isNullable;
	}

	public void setNullable(boolean nullable) {
		isNullable = nullable;
	}

	public boolean isKey() {
		return isKey;
	}

	public void setKey(boolean key) {
		isKey = key;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
