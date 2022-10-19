package com.codedb.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

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
	private Boolean isNullable = true;
	// 是否是主键
	private Boolean isKey = false;
	// 获取表字段注解信息
	private String remarks = "";

	public String stringMapper(Integer index) {
		return switch (index) {
			case 1 -> columnName;
			case 2 -> typeName;
			case 3 -> charOctetLength;
			case 4 -> numPrecRadix;
			default -> remarks;
		};
	}

	public Boolean booleanMapper(Integer index) {
		return switch (index) {
			case 1 -> isNullable;
			default -> isKey;
		};
	}

	public static Callback<TableColumn.CellDataFeatures<TableInfoData, String>, ObservableValue<String>> getStringValueCell(
			Integer index) {
		Callback<TableColumn.CellDataFeatures<TableInfoData, String>, ObservableValue<String>> callback = new Callback<TableColumn.CellDataFeatures<TableInfoData, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(
					TableColumn.CellDataFeatures<TableInfoData, String> objectStringCellDataFeatures) {
				ObservableValue<String> observableValue = new SimpleStringProperty(
						objectStringCellDataFeatures.getValue().stringMapper(index));
				return observableValue;
			}
		};
		return callback;
	}

	public static Callback<TableColumn.CellDataFeatures<TableInfoData, Boolean>, ObservableValue<Boolean>> getBooleanValueCell(
			Integer index) {
		Callback<TableColumn.CellDataFeatures<TableInfoData, Boolean>, ObservableValue<Boolean>> callback = new Callback<TableColumn.CellDataFeatures<TableInfoData, Boolean>, ObservableValue<Boolean>>() {
			@Override
			public ObservableValue<Boolean> call(
					TableColumn.CellDataFeatures<TableInfoData, Boolean> tableInfoDataBooleanCellDataFeatures) {
				ObservableValue<Boolean> observableValue = new SimpleBooleanProperty(
						tableInfoDataBooleanCellDataFeatures.getValue().booleanMapper(index));
				return observableValue;
			}
		};
		return callback;
	}

	public TableInfoData() {
	}

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
