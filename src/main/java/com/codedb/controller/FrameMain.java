package com.codedb.controller;

import java.util.List;

import com.codedb.model.ToolsNodeData;
import com.codedb.model.TreeNodeData;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.util.Callback;

public class FrameMain {
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

	/**
	 * @Description 设置数据库列表（初始化）
	 * @Params [list：数据库名称列表]
	 **/
	public void setDBTreeViewData(List<String> list) {
		TreeItem<TreeNodeData> root = new TreeItem<TreeNodeData>(new TreeNodeData("MySQL", TreeNodeData.ROOT_NODE));
		leftTreeTableView.setRoot(root);
		TreeTableColumn<TreeNodeData, String> titleColumn = new TreeTableColumn<>("MySQL数据库");
		TreeTableColumn<TreeNodeData, String> typeColumn = new TreeTableColumn<>();
		titleColumn.setCellValueFactory(
				(TreeTableColumn.CellDataFeatures<TreeNodeData, String> param) -> new ReadOnlyStringWrapper(
						param.getValue().getValue().getTitle()));
		typeColumn.setCellValueFactory(
				(TreeTableColumn.CellDataFeatures<TreeNodeData, String> param) -> new ReadOnlyStringWrapper(
						param.getValue().getValue().getType().toString()));
		titleColumn.setPrefWidth(160);
		typeColumn.setPrefWidth(0);
		typeColumn.setMaxWidth(0);
		typeColumn.setMinWidth(0);
		typeColumn.setVisible(false);
		leftTreeTableView.getColumns().addAll(titleColumn, typeColumn);
		leftTreeTableView.setShowRoot(false);
		for (String dbName : list) {
			TreeItem<TreeNodeData> ti = new TreeItem<>(new TreeNodeData(dbName, TreeNodeData.DB_NODE));
			root.getChildren().add(ti);
			ti.getChildren().add(new TreeItem<>(new TreeNodeData("111", TreeNodeData.TABLE_NODE)));
		}

		leftTreeTableView.setOnMouseClicked(mouseEvent -> {
			TreeItem<TreeNodeData> item = leftTreeTableView.getSelectionModel().getSelectedItem();
			initTools(item.getValue());
		});
	}

	/**
	 * @Description 选中后初始化其工具栏
	 * @Params [treeNodeData: 选中的对象]
	 **/
	public void initTools(TreeNodeData treeNodeData) {
		leftToolListView.getItems().clear();
		Integer type = treeNodeData.getType();
		switch (type) {
			case 1 :
				getDBTools(treeNodeData.getTitle());
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
							return;
						}
						this.setFont(new Font(14));
						this.setText(item.getName());
					};
				};
				return listCell;
			}
		});
		leftToolListView.getItems().add(new ToolsNodeData("数据库名称：" + title, ToolsNodeData.DB_NAME));

		leftToolListView.setOnMouseClicked(mouseEvent -> {
			ToolsNodeData cell = leftToolListView.getSelectionModel().getSelectedItem();
			System.out.println(cell.getName());
		});
	}
}
