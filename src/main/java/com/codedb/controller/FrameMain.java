package com.codedb.controller;

import java.sql.Connection;
import java.util.List;
import java.util.Objects;

import com.codedb.componentsHandler.*;
import com.codedb.model.HistoryItemData;
import com.codedb.model.StaticImage;
import com.codedb.model.ToolsNodeData;
import com.codedb.model.TreeNodeData;
import com.codedb.utils.DBTools;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;

public class FrameMain {
	private Connection con = null;

	@FXML
	private BorderPane basePane;

	@FXML
	private SplitPane topPane;

	@FXML
	private SplitPane leftPane;

	@FXML
	private TabPane tabPane;

	@FXML
	private SplitPane rightPane;

	@FXML
	private SplitPane bottomPane;

	@FXML
	private MenuBar menuBar;

	@FXML
	private TreeTableView<TreeNodeData> leftTreeTableView;

	@FXML
	private ListView<ToolsNodeData> leftToolListView;

	@FXML
	private VBox leftVBox;

	@FXML
	private ListView<HistoryItemData> rightHistoryListView;

	@FXML
	private ProgressBar bottomProcessBar;

	@FXML
	private TextField status;

	@FXML
	private CheckMenuItem menuShowAnnotation, menuUseIndex, menuUseHistory;

	@FXML
	private MenuItem menuCloseAllTab, menuCloseAllStructureTab, menuCloseAllResultTab, menuHello, menuClearHistory;

	public void setCon(Connection con) {
		this.con = con;
	}

	/**
	 * @Description 初始化
	 * @Params [list：数据库名称列表]
	 **/
	public void init(List<String> list) {
		// 挂载progressBar管理器
		MainProgressHandle.register(bottomProcessBar);
		// 挂载status管理器
		MainStatusHandle.register(status);
		// 挂载tabPane管理器
		MainTabPaneHandle.register(tabPane);
		// 挂载历史记录管理器
		MainHistoryHandle.register(rightHistoryListView);
		// 菜单按钮挂载点击事件

		// 数据库名渲染到treetableview
		TreeItem<TreeNodeData> root = new TreeItem<TreeNodeData>(new TreeNodeData("MySQL", TreeNodeData.ROOT_NODE));
		leftTreeTableView.setRoot(root);
		TreeTableColumn<TreeNodeData, TreeNodeData> titleColumn = new TreeTableColumn<>("MySQL数据库");
		titleColumn.setCellValueFactory(
				new Callback<TreeTableColumn.CellDataFeatures<TreeNodeData, TreeNodeData>, ObservableValue<TreeNodeData>>() {
					@Override
					public ObservableValue<TreeNodeData> call(
							TreeTableColumn.CellDataFeatures<TreeNodeData, TreeNodeData> param) {
						ObservableValue<TreeNodeData> data = new ReadOnlyObjectWrapper<>(param.getValue().getValue());
						return data;
					}
				});
		titleColumn.setCellFactory(
				new Callback<TreeTableColumn<TreeNodeData, TreeNodeData>, TreeTableCell<TreeNodeData, TreeNodeData>>() {
					@Override
					public TreeTableCell<TreeNodeData, TreeNodeData> call(
							TreeTableColumn<TreeNodeData, TreeNodeData> treeNodeDataStringTreeTableColumn) {
						TreeTableCell<TreeNodeData, TreeNodeData> treeTableCell = new TreeTableCell<TreeNodeData, TreeNodeData>() {
							@Override
							protected void updateItem(TreeNodeData s, boolean b) {
								super.updateItem(s, b);
								if (!b) {
									HBox hBox = new HBox();
									Image image = null;
									if (Objects.equals(s.getType(), TreeNodeData.TABLE_NODE)) {
										image = StaticImage.table;
									} else {
										image = StaticImage.db;
									}
									hBox.getChildren().add(new ImageView(image));
									this.setText(s.getTitle());
									this.setGraphic(hBox);
								} else {
									this.setText("");
									this.setGraphic(null);
								}
							}
						};
						return treeTableCell;
					}
				});
		titleColumn.setPrefWidth(leftTreeTableView.getMinWidth());
		leftTreeTableView.getColumns().add(titleColumn);
		leftTreeTableView.setShowRoot(false);
		for (String dbName : list) {
			TreeNodeData dbNodeData = new TreeNodeData(dbName, TreeNodeData.DB_NODE);
			TreeItem<TreeNodeData> ti = new TreeItem<>(dbNodeData);
			root.getChildren().add(ti);
			List<String> tables = null;
			try {
				tables = DBTools.getTablesName(con, dbName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (String tableName : tables) {
				TreeNodeData treeNodeData = new TreeNodeData(tableName, TreeNodeData.TABLE_NODE);
				treeNodeData.setParent(dbNodeData);
				TreeItem tableTreeItem = new TreeItem<>(treeNodeData);
				ti.getChildren().add(tableTreeItem);
			}
		}
		// 左侧树形图设置点击事件
		leftTreeTableView.setOnMouseClicked(mouseEvent -> {
			TreeItem<TreeNodeData> item = leftTreeTableView.getSelectionModel().getSelectedItem();
			if (item != null) {
				initInfoAndTools(item.getValue());
			}
		});
		MainStatusHandle.set("准备就绪!", MainStatusHandle.SUCCESS);
	}

	/**
	 * @Description 树状表选中后初始化
	 * @Params [treeNodeData: 选中的对象]
	 **/
	public void initInfoAndTools(TreeNodeData treeNodeData) {
		leftToolListView.getItems().clear();
		Integer type = treeNodeData.getType();
		switch (type) {
			case 1 :
				getDBTools(treeNodeData.getTitle());
				break;
			case 2 :
				getTableTools(treeNodeData.getParent().getTitle(), treeNodeData.getTitle());
				break;
			default :
				break;
		}
	}

	/**
	 * @Description 获取数据库的工具集
	 * @Params [title：数据库名称]
	 **/
	public void getDBTools(String title) {
		leftToolListView.setEditable(false);
		leftToolListView.setCellFactory(new Callback<ListView<ToolsNodeData>, ListCell<ToolsNodeData>>() {
			@Override
			public ListCell<ToolsNodeData> call(ListView<ToolsNodeData> stringListView) {
				ListCell<ToolsNodeData> listCell = new ListCell<ToolsNodeData>() {
					@Override
					protected void updateItem(ToolsNodeData item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							this.setText(null);
							this.setGraphic(null);
							return;
						}
						this.setFont(new Font(14));
						this.setText(item.getName());
					}
				};
				return listCell;
			}
		});
		ListCell listCell = new ListCell();
		leftToolListView.getItems().add(new ToolsNodeData("数据库名称：" + title, ToolsNodeData.DB_NAME, title));
		// 绑定工具栏点击事件
		leftToolListView.setOnMouseClicked(mouseEvent -> {
			ToolsNodeData cell = leftToolListView.getSelectionModel().getSelectedItem();
			if (cell != null) {
				switch (cell.getType()) {
					// 点击 数据库名称
					case ToolsNodeData.DB_NAME :
						MainToolsFunctionBinder.dbName(cell.getDbName());
						break;
				}
			}
		});
		MainStatusHandle.set("选中数据库 " + title);
	}

	/**
	 * @Description 获取表的工具集
	 * @Params [title 表名称]
	 **/
	public void getTableTools(String dbName, String title) {
		leftToolListView.setEditable(false);
		leftToolListView.setCellFactory(new Callback<ListView<ToolsNodeData>, ListCell<ToolsNodeData>>() {
			@Override
			public ListCell<ToolsNodeData> call(ListView<ToolsNodeData> stringListView) {
				ListCell<ToolsNodeData> listCell = new ListCell<ToolsNodeData>() {
					@Override
					protected void updateItem(ToolsNodeData item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							this.setText(null);
							this.setGraphic(null);
							return;
						}
						this.setFont(new Font(14));
						this.setText(item.getName());
					}
				};
				return listCell;
			}
		});
		ListCell listCell = new ListCell();
		leftToolListView.getItems().add(new ToolsNodeData("表名称：" + title, ToolsNodeData.TABLE_NAME, dbName, title));
		leftToolListView.getItems().add(new ToolsNodeData("显示表结构", ToolsNodeData.SHOW_TABLE_STRUCTURE, dbName, title));
		leftToolListView.getItems().add(new ToolsNodeData("从数据集到数据库", ToolsNodeData.RESULTSET_TO_DB, dbName, title));
		leftToolListView.getItems().add(new ToolsNodeData("从页面到数据库", ToolsNodeData.TEXT_TO_DB, dbName, title));
		leftToolListView.getItems().add(new ToolsNodeData("从数据库到数据集", ToolsNodeData.DB_TO_RESULTSET, dbName, title));
		leftToolListView.getItems().add(new ToolsNodeData("从数据集到页面", ToolsNodeData.RESULTSET_TO_TEXT, dbName, title));
		// 绑定工具栏点击事件
		leftToolListView.setOnMouseClicked(mouseEvent -> {
			ToolsNodeData cell = leftToolListView.getSelectionModel().getSelectedItem();
			if (cell != null) {
				switch (cell.getType()) {
					// 点击 表名称
					case ToolsNodeData.TABLE_NAME :
						MainToolsFunctionBinder.tableName(cell.getTableName());
						break;
					// 显示表结构
					case ToolsNodeData.SHOW_TABLE_STRUCTURE :
						MainToolsFunctionBinder.showTableInfo(con, cell.getDbName(), cell.getTableName());
						break;
					// 数据集 到 数据库
					case ToolsNodeData.RESULTSET_TO_DB :
						MainToolsFunctionBinder.resultSetToDB(con, cell.getDbName(), cell.getTableName(),
								menuShowAnnotation.isSelected(), menuUseIndex.isSelected());
						break;
					// 从 页面 到 数据库
					case ToolsNodeData.TEXT_TO_DB :
						MainToolsFunctionBinder.frameToDB(con, cell.getDbName(), cell.getTableName(),
								menuShowAnnotation.isSelected());
						break;
					// 从 数据库 到 数据集
					case ToolsNodeData.DB_TO_RESULTSET :
						MainToolsFunctionBinder.dbToResultSet(con, cell.getDbName(), cell.getTableName(),
								menuShowAnnotation.isSelected());
						break;
					// 从 数据集 到 页面
					case ToolsNodeData.RESULTSET_TO_TEXT :
						MainToolsFunctionBinder.resultSetToFrame(con, cell.getDbName(), cell.getTableName(),
								menuShowAnnotation.isSelected(), menuUseIndex.isSelected());
						break;
				}
			}
		});
		MainStatusHandle.set("选中表 " + title);

	}

	/**
	 * @Description 打开介绍页面
	 **/
	public void menuHelloAction(ActionEvent actionEvent) {

	}

	/**
	 * @Description 关闭所有页签
	 **/
	public void menuCloseAllTabAction(ActionEvent actionEvent) {
		MainTabPaneHandle.clearAll();
	}

	/**
	 * @Description 关闭所有表结构页签
	 **/
	public void menuCloseAllStructureTabAction(ActionEvent actionEvent) {
		MainTabPaneHandle.clearAllStructureTab();
	}

	/**
	 * @Description 关闭所有代码生成结果页签
	 **/
	public void menuCloseAllResultTabAction(ActionEvent actionEvent) {
		MainTabPaneHandle.clearAllResultTab();

	}

	/**
	 * @Description 清空历史记录
	 **/
	public void menuClearHistoryAciton(ActionEvent actionEvent) {
		MainHistoryHandle.clearAll();
	}

	/**
	 * @Description 开启关闭历史记录
	 **/
	public void menuUseHistoryAction(ActionEvent actionEvent) {
		boolean selected = menuUseHistory.isSelected();
		MainHistoryHandle.setEnable(selected);
		if (selected) {
			leftVBox.setVisible(true);

		} else {
			MainHistoryHandle.clearAll();
			leftVBox.setVisible(false);
		}
	}

}
