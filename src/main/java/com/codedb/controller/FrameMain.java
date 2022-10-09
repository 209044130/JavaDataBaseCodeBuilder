package com.codedb.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
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
	private ListView<String> rightHistoryListView;

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
		// 挂载tabPane管理器
		TabPaneHandle.register(tabPane);
		// 挂载历史记录管理器

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
						DBToolsFunctionBinder.dbName(cell.getDbName());
						break;
				}
			}
		});
		StatusHandle.set("选中数据库 " + title);
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
		leftToolListView.getItems()
				.add(new ToolsNodeData("从ResultSet到数据库" + title, ToolsNodeData.RESULTSET_TO_DB, dbName, title));
		leftToolListView.getItems().add(new ToolsNodeData("数据库名称：" + title, ToolsNodeData.DB_NAME, dbName, title));
		leftToolListView.getItems().add(new ToolsNodeData("数据库名称：" + title, ToolsNodeData.DB_NAME, dbName, title));
		leftToolListView.getItems().add(new ToolsNodeData("数据库名称：" + title, ToolsNodeData.DB_NAME, dbName, title));
		// 绑定工具栏点击事件
		leftToolListView.setOnMouseClicked(mouseEvent -> {
			ToolsNodeData cell = leftToolListView.getSelectionModel().getSelectedItem();
			if (cell != null) {
				switch (cell.getType()) {
					// 点击 表名称
					case ToolsNodeData.TABLE_NAME :
						DBToolsFunctionBinder.tableName(cell.getTableName());
						break;
					// 显示表结构
					case ToolsNodeData.SHOW_TABLE_STRUCTURE :
						DBToolsFunctionBinder.showTableInfo(con, cell.getDbName(), cell.getTableName());
						break;
					// resultset 到 db
					case ToolsNodeData.RESULTSET_TO_DB :
						DBToolsFunctionBinder.resultSetToDB(con, cell.getDbName(), cell.getTableName());
						break;
				}
			}
		});
		StatusHandle.set("选中表 " + title);
	}

	public void test(String name) {
		try {
			// Class.forName("com.mysql.cj.jdbc.Driver");
			// Connection con = DriverManager.getConnection(url, userName, password);
			String sql1 = "SELECT * FROM city WHERE POPULATION < 100000";
			PreparedStatement statement = con.prepareStatement(sql1);
			statement.execute("use test");
			ResultSet resultSet = statement.executeQuery();
			String sql2 = "INSERT INTO city2 VALUES(?,?,?,?,?)";
			statement = con.prepareStatement(sql2);
			while (resultSet.next()) {
				statement.setObject(1, resultSet.getObject("id"));
				statement.setObject(2, resultSet.getObject("name"));
				statement.setObject(3, resultSet.getObject("countrycode"));
				statement.setObject(4, resultSet.getObject("district"));
				statement.setObject(5, resultSet.getObject("population"));
				statement.addBatch();
			}
			statement.executeBatch();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @Description DBTools功能绑定
	 **/
	private class DBToolsFunctionBinder {
		public static void dbName(String dbName) {
			StatusHandle.set("选中数据库 " + dbName);
		}

		public static void tableName(String tableName) {
			StatusHandle.set("选中表 " + tableName);
		}

		public static void showTableInfo(Connection con, String parentTitle, String title) {
			TabPaneHandle.showTableInfo(con, parentTitle, title);
		}

		public static void resultSetToDB(Connection con, String dbName, String tableName) {
			try {
				ConcurrentMap<String, String> fields = DBTools.getTableStructure(con, dbName, tableName);
				// 处理参数
				List<String> params = new LinkedList<>(fields.keySet());
				// 根据参数处理sql2语句的代码模板
				String tempSql2 = "String sql2 = \"INSERT INTO " + tableName + " VALUES(";
				for (int i = 0; i < params.size(); i++) {
					if (i == params.size() - 1)
						tempSql2 += params.get(i) + ")  VALUES(";
					else
						tempSql2 += params.get(i) + ",";
				}
				for (int i = 0; i < params.size(); i++) {
					if (i == params.size() - 1)
						tempSql2 += "?)\";";
					else
						tempSql2 += "?,";
				}
				String res = "";
				// 基本模板
				res += "try {\n" + "\tString sql1 = \"SELECT * FROM " + tableName + " WHERE <条件>\";\n"
						+ "\tPreparedStatement statement = con.prepareStatement(sql1);\n"
						+ "\tResultSet resultSet = statement.executeQuery();\n" + "\t" + tempSql2 + "\n"
						+ "\twhile (resultSet.next()) {\n";
				for (int i = 0; i < params.size(); i++) {
					res += "\t\tstatement.setObject(" + (i + 1) + ", resultSet.getObject(\"" + params.get(i)
							+ "\"));\n";
				}
				res += "\t}\n" + "\tstatement.executeBatch();\n" + "} catch (Exception e) {\n"
						+ "\te.printStackTrace();\n" + "}\n";
				TabPaneHandle.create(tableName + "(" + dbName + ")" + "从ResultSet到数据库", res);

				// try {
				// String sql1 = "SELECT * FROM city WHERE POPULATION < 100000";
				// PreparedStatement statement = con.prepareStatement(sql1);
				// ResultSet resultSet = statement.executeQuery();
				// String sql2 = "INSERT INTO city VALUES(?,?,?,?,?)";
				// statement = con.prepareStatement(sql2);
				// while (resultSet.next()) {
				// statement.setObject(1, resultSet.getObject("id"));
				// statement.setObject(2, resultSet.getObject("name"));
				// statement.setObject(3, resultSet.getObject("countrycode"));
				// statement.setObject(4, resultSet.getObject("district"));
				// statement.setObject(5, resultSet.getObject("population"));
				// statement.addBatch();
				// }
				// statement.executeBatch();
				//
				// } catch (Exception e) {
				// e.printStackTrace();
				// }

			} catch (Exception e) {
				e.printStackTrace();
			}
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

class TabPaneHandle {
	public static TabPane tabPane;

	public static void register(TabPane tp) {
		tabPane = tp;
	}

	/**
	 * @Description 显示表的结构信息
	 * @Params [parentTitle 父节点（数据库）名称, title 表名]
	 **/
	public static void showTableInfo(Connection con, String parentTitle, String title) {
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

	public static void create(String title, String content) {
		Tab tab = new Tab();
		tab.setText(title);
		tab.setClosable(true);
		TextArea textArea = new TextArea();
		textArea.setText(content);
		tab.setContent(textArea);
		tabPane.getTabs().add(tab);
	}
}