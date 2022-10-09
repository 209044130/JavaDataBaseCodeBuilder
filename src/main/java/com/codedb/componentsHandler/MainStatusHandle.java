package com.codedb.componentsHandler;

import javafx.scene.control.TextField;

/**
 * @Description 处理status文字
 **/
public class MainStatusHandle
{	public static TextField status = null;

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
