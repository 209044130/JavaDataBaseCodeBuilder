package com.codedb.controller;

import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.codedb.model.StaticImage;
import com.codedb.model.TableInfoData;
import com.codedb.model.ToolsNodeData;
import com.codedb.model.TreeNodeData;
import com.codedb.utils.DBTools;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
	private TextArea centerTopTextArea;

	@FXML
	private TextArea centerBottomTextArea;

	@FXML
	private ProgressBar bottomProcessBar;

	@FXML
	private TextField status;

	public void setCon(Connection con) {
		this.con = con;
	}

	/**
	 * @Description 初始化
	 * @Params [list：数据库名称列表]
	 **/
	public void init(List<String> list) {
		// 挂载progressBar管理器
		ProgressHandle.register(bottomProcessBar);
		// 挂载status管理器
		StatusHandle.register(status);
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
		StatusHandle.set("准备就绪!", StatusHandle.SUCCESS);
	}

	/**
	 * @Description 选中后初始化
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
				showTableInfo(treeNodeData.getParent().getTitle(), treeNodeData.getTitle());
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
		leftToolListView.getItems().add(new ToolsNodeData("数据库名称：" + title, ToolsNodeData.DB_NAME));
		leftToolListView.getItems().add(new ToolsNodeData("数据库名称：" + title, ToolsNodeData.DB_NAME));
		leftToolListView.getItems().add(new ToolsNodeData("数据库名称：" + title, ToolsNodeData.DB_NAME));
		leftToolListView.getItems().add(new ToolsNodeData("数据库名称：" + title, ToolsNodeData.DB_NAME));
		leftToolListView.getItems().add(new ToolsNodeData("数据库名称：" + title, ToolsNodeData.DB_NAME));
		leftToolListView.getItems().add(new ToolsNodeData("数据库名称：" + title, ToolsNodeData.DB_NAME));
		leftToolListView.setOnMouseClicked(mouseEvent -> {
			ToolsNodeData cell = leftToolListView.getSelectionModel().getSelectedItem();
			if (cell != null) {
				switch (cell.getType()) {
					case ToolsNodeData.DB_NAME :
						System.out.println(cell.getName());
						break;
				}
			}
		});
		StatusHandle.set("选中数据库 " + title);
	}

	/**
	 * @Description 显示表的结构信息
	 * @Params [parentTitle 父节点（数据库）名称, title 表名]
	 **/
	private void showTableInfo(String parentTitle, String title) {
		String tabName = title + "(" + parentTitle + ") 结构";
		// 检查当前是否已经有该标签
		for (Tab temp : tabPane.getTabs()) {
			if (temp.getText().equals(tabName)) {
				tabPane.getSelectionModel().select(temp);
				return;
			}
		}
		// 创建新的tab
		ConcurrentMap<String, String> table = new ConcurrentHashMap<>();
		try {
			table = DBTools.getTableStructure(con, parentTitle, title);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Tab tableInfo = new Tab();
		tableInfo.setClosable(true);
		tableInfo.setText(tabName);
		VBox vBox = new VBox();
		TableView<TableInfoData> tableView = new TableView<TableInfoData>();
		TableColumn<TableInfoData, String> tableColumn1 = new TableColumn<TableInfoData, String>("字段名");
		TableColumn<TableInfoData, String> tableColumn2 = new TableColumn<TableInfoData, String>("类型");
		tableColumn1.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumn2.setCellValueFactory(new PropertyValueFactory<>("type"));
		tableColumn1.setMinWidth(180);
		tableColumn2.setMinWidth(180);
		tableView.getColumns().addAll(tableColumn1, tableColumn2);
		// 添加到数据源列表
		ObservableList<TableInfoData> data = FXCollections.observableArrayList();
		for (String k : table.keySet()) {
			TableInfoData d = new TableInfoData(k, table.get(k));
			data.add(d);
		}
		// 设置数据源
		tableView.setItems(data);
		tableInfo.setContent(tableView);
		tabPane.getTabs().add(tableInfo);
	}

	/**
	 * @Description DBTools功能绑定
	 **/
	private class DBToolsFunctionBinder {
		/**
		 * @Description
		 * @Params
		 **/
		public void DataSetToDB() {

		}
	}
}

/**
 * @Description 处理进度条
 * @Params
 **/
class ProgressHandle {
	public static ProgressBar progressBar = null;

	public static void register(ProgressBar pg) {
		progressBar = pg;
		progressBar.setPadding(new Insets(0));
	}

	public static void set(double percent) {
		progressBar.setProgress(percent);
	}

	public static void reset() {
		progressBar.setProgress(0);
	}
}

/**
 * @Description 处理status文字
 **/
class StatusHandle {
	public static TextField status = null;

	public static final int NORMAL = 0;
	public static final int SUCCESS = 1;
	public static final int WARNING = 2;
	public static final int ERROR = 3;

	public static void register(TextField s) {
		status = s;
	}

	public static void set(String text) {
		set(text, 0);
	}

	public static void set(String text, int mode) {
		switch (mode) {
			case SUCCESS :
				status.setStyle("-fx-text-fill: #5ebd00");
				break;
			case WARNING :
				status.setStyle("-fx-text-fill: #fdb924");
				break;
			case ERROR :
				status.setStyle("-fx-text-fill: #e10000");
				break;
			default :
				status.setStyle("-fx-text-fill: black");
				break;
		}
		status.setText(text);
	}

	public static void reset() {
		status.setText("");
	}
}
