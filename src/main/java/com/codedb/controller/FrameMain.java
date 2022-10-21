package com.codedb.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.codedb.componentsHandler.*;
import com.codedb.model.HistoryItemData;
import com.codedb.model.ToolsNodeData;
import com.codedb.model.TreeNodeData;
import com.codedb.utils.DBTools;
import com.codedb.utils.FrameManager;
import com.codedb.utils.ListViewImageCellFactory;
import com.codedb.utils.TreeTableImageCellFactory;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class FrameMain {
	public static Connection con = null;

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
	private MenuItem menuCreateDataBase, menuCloseAllTab, menuCloseAllStructureTab, menuCloseAllResultTab, menuHello,
			menuClearHistory;

	public CallBack callBack = new CallBack();

	/**
	 * @Description 初始化设置连接类
	 * @Params [con 传入connection对象]
	 **/
	public void setCon(Connection con) {
		FrameMain.con = con;
	}

	/**
	 * @Description 初始化
	 * @Params [list：数据库名称列表]
	 **/
	public void init() {
		// 挂载progressBar管理器
		MainProgressHandle.register(bottomProcessBar);
		// 挂载status管理器
		MainStatusHandle.register(status);
		// 挂载tabPane管理器
		MainTabPaneHandle.register(tabPane);
		// 挂载历史记录管理器
		MainHistoryHandle.register(rightHistoryListView);
		// 初始化左侧树形列表
		initLeftTreeView();
		// 清空工具栏
		leftToolListView.getItems().clear();
		MainStatusHandle.set("准备就绪!", MainStatusHandle.SUCCESS);
	}

	/**
	 * @Description 初始化左侧树形列表
	 **/
	public void initLeftTreeView() {
		List<String> list = new LinkedList<>();
		try {
			list = DBTools.getDatabasesName(con);
			// 清空工具栏
			leftToolListView.getItems().clear();
			// 数据库名渲染到treetableview
			leftTreeTableView.getColumns().clear();
			TreeItem<TreeNodeData> root = new TreeItem<TreeNodeData>(new TreeNodeData("MySQL", TreeNodeData.ROOT_NODE));
			leftTreeTableView.setRoot(root);
			TreeTableColumn<TreeNodeData, TreeNodeData> titleColumn = new TreeTableColumn<>("MySQL数据库");
			titleColumn.setCellValueFactory(
					new Callback<TreeTableColumn.CellDataFeatures<TreeNodeData, TreeNodeData>, ObservableValue<TreeNodeData>>() {
						@Override
						public ObservableValue<TreeNodeData> call(
								TreeTableColumn.CellDataFeatures<TreeNodeData, TreeNodeData> param) {
							ObservableValue<TreeNodeData> data = new ReadOnlyObjectWrapper<>(
									param.getValue().getValue());
							return data;
						}
					});
			titleColumn.setCellFactory(
					new Callback<TreeTableColumn<TreeNodeData, TreeNodeData>, TreeTableCell<TreeNodeData, TreeNodeData>>() {
						@Override
						public TreeTableCell<TreeNodeData, TreeNodeData> call(
								TreeTableColumn<TreeNodeData, TreeNodeData> treeNodeDataStringTreeTableColumn) {
							return TreeTableImageCellFactory.getTreeTableImageCell();
						}
					});
			titleColumn.setPrefWidth(leftTreeTableView.getMinWidth());
			leftTreeTableView.getColumns().add(titleColumn);
			leftTreeTableView.setShowRoot(false);
			// 添加数据库到树形列表
			for (String dbName : list) {
				TreeNodeData dbNodeData = new TreeNodeData(dbName, TreeNodeData.DB_NODE);
				TreeItem<TreeNodeData> ti = new TreeItem<>(dbNodeData);
				root.getChildren().add(ti);
				// 添加数据库中的表到树形列表
				List<String> tables = null;
				try {
					tables = DBTools.getTablesName(con, dbName);
					for (String tableName : tables) {
						TreeNodeData treeNodeData = new TreeNodeData(tableName, TreeNodeData.TABLE_NODE);
						treeNodeData.setParent(dbNodeData);
						TreeItem<TreeNodeData> tableTreeItem = new TreeItem<>(treeNodeData);
						ti.getChildren().add(tableTreeItem);
					}
				} catch (Exception e) {
					e.printStackTrace();
					MainHistoryHandle.add("数据库<" + dbName + ">的表信息获取失败", HistoryItemData.ERROR);
				}
			}
			// 左侧树形图设置点击事件
			leftTreeTableView.setOnMouseClicked(mouseEvent -> {
				TreeItem<TreeNodeData> item = leftTreeTableView.getSelectionModel().getSelectedItem();
				if (item != null) {
					initInfoAndTools(item.getValue());
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			MainHistoryHandle.add("数据库信息获取失败", HistoryItemData.ERROR);
		}
	}

	/**
	 * @Description 树状表选中后初始化
	 * @Params [treeNodeData: 选中的对象]
	 **/
	public void initInfoAndTools(TreeNodeData treeNodeData) {
		leftToolListView.getItems().clear();
		Integer type = treeNodeData.getType();
		switch (type) {
			case 1 -> getDBTools(treeNodeData.getTitle());
			case 2 -> getTableTools(treeNodeData.getParent().getTitle(), treeNodeData.getTitle());
			default -> {
			}
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
				return ListViewImageCellFactory.getListViewImageCell();
			}
		});
		leftToolListView.getItems().add(new ToolsNodeData("数据库名称：" + title, ToolsNodeData.DB_NAME, title));
		leftToolListView.getItems().add(new ToolsNodeData("添加表", ToolsNodeData.ADD_TABLE, title));
		leftToolListView.getItems().add(new ToolsNodeData("删除数据库", ToolsNodeData.REMOVE_DB, title));
		// 绑定工具栏点击事件
		leftToolListView.setOnMouseClicked(mouseEvent -> {
			ToolsNodeData cell = leftToolListView.getSelectionModel().getSelectedItem();
			if (cell != null) {
				switch (cell.getType()) {
					// 点击 数据库名称
					case ToolsNodeData.DB_NAME -> MainToolsFunctionBinder.dbName(cell.getDbName());
					case ToolsNodeData.ADD_TABLE -> MainToolsFunctionBinder.addTable(con, cell.getDbName());
					case ToolsNodeData.REMOVE_DB -> MainToolsFunctionBinder.removeDB(con, cell.getDbName());
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
				return ListViewImageCellFactory.getListViewImageCell();
			}
		});
		leftToolListView.getItems().add(new ToolsNodeData("表名称：" + title, ToolsNodeData.TABLE_NAME, dbName, title));
		leftToolListView.getItems().add(new ToolsNodeData("显示表结构", ToolsNodeData.SHOW_TABLE_STRUCTURE, dbName, title));
		leftToolListView.getItems().add(new ToolsNodeData("从数据集到数据库", ToolsNodeData.RESULTSET_TO_DB, dbName, title));
		leftToolListView.getItems().add(new ToolsNodeData("从页面到数据库", ToolsNodeData.TEXT_TO_DB, dbName, title));
		leftToolListView.getItems().add(new ToolsNodeData("从数据库到数据集", ToolsNodeData.DB_TO_RESULTSET, dbName, title));
		leftToolListView.getItems().add(new ToolsNodeData("从数据集到页面", ToolsNodeData.RESULTSET_TO_TEXT, dbName, title));
		leftToolListView.getItems().add(new ToolsNodeData("删除表", ToolsNodeData.REMOVE_TABLE, dbName, title));
		// 绑定工具栏点击事件
		leftToolListView.setOnMouseClicked(mouseEvent -> {
			ToolsNodeData cell = leftToolListView.getSelectionModel().getSelectedItem();
			if (cell != null) {
				switch (cell.getType()) {
					// 点击 表名称
					case ToolsNodeData.TABLE_NAME -> MainToolsFunctionBinder.tableName(cell.getTableName());
					// 显示表结构
					case ToolsNodeData.SHOW_TABLE_STRUCTURE -> MainToolsFunctionBinder.showTableInfo(con,
							cell.getDbName(), cell.getTableName());
					// 数据集 到 数据库
					case ToolsNodeData.RESULTSET_TO_DB -> MainToolsFunctionBinder.resultSetToDB(con, cell.getDbName(),
							cell.getTableName(), menuShowAnnotation.isSelected(), menuUseIndex.isSelected());
					// 从 页面 到 数据库
					case ToolsNodeData.TEXT_TO_DB -> MainToolsFunctionBinder.frameToDB(con, cell.getDbName(),
							cell.getTableName(), menuShowAnnotation.isSelected());
					// 从 数据库 到 数据集
					case ToolsNodeData.DB_TO_RESULTSET -> MainToolsFunctionBinder.dbToResultSet(con, cell.getDbName(),
							cell.getTableName(), menuShowAnnotation.isSelected());
					// 从 数据集 到 页面
					case ToolsNodeData.RESULTSET_TO_TEXT -> MainToolsFunctionBinder.resultSetToFrame(con,
							cell.getDbName(), cell.getTableName(), menuShowAnnotation.isSelected(),
							menuUseIndex.isSelected());
					// 删除表
					case ToolsNodeData.REMOVE_TABLE -> MainToolsFunctionBinder.removeTable(con, cell.getDbName(),
							cell.getTableName());
				}
			}
		});
		MainStatusHandle.set("选中表 " + title);

	}

	/**
	 * @Description 创建数据库
	 **/
	public void menuCreateDataBaseAction(ActionEvent actionEvent) {
		Stage stage = new Stage();
		stage.setTitle("创建数据库");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setResizable(false);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/codedb/fxml/addDataBase.fxml"));
		try {
			Parent p = fxmlLoader.load();
			stage.setScene(new Scene(p));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		stage.show();
		FrameManager.setFrame("AddDataBase", fxmlLoader.getController(), stage);
	}

	/**
	 * @Description 打开介绍页面
	 **/
	public void menuHelloAction(ActionEvent actionEvent) {
		// 打开欢迎使用（介绍）界面
		MainTabPaneHandle.createHelloTab();
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

	/**
	 * @Description 回调
	 **/
	class CallBack {
		public String handleAddDataBaseFrame(String dbName) {
			PreparedStatement preparedStatement;
			String error = "";
			try {
				preparedStatement = con.prepareStatement("CREATE DATABASE " + dbName + ";");
				if (preparedStatement.executeUpdate() == 1) {
					init();
					MainHistoryHandle.add("创建数据库<" + dbName + ">成功", HistoryItemData.SUCCESS);
				}
			} catch (SQLException e) {
				error = e.getMessage();
			}
			return error;
		}
	}
}
