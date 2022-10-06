package com.codedb.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

import java.util.Spliterator;

public class FrameMain
{
    @FXML
    private BorderPane basePane;

    @FXML
    private SplitPane topPane;

    @FXML
    private SplitPane leftPane;

    @FXML
    private FlowPane centerPane;

    @FXML
    private SplitPane rightPane;

    @FXML
    private SplitPane bottomPane;

    @FXML
    private MenuBar menuBar;

    @FXML
    private TreeView leftTreeView;

    @FXML
    private ListView leftListView;

    @FXML
    private TextArea centerTopTextArea;

    @FXML
    private TextArea centerBottomTextArea;


}
