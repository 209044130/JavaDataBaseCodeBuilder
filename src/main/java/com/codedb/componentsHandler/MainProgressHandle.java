package com.codedb.componentsHandler;

import javafx.geometry.Insets;
import javafx.scene.control.ProgressBar;

/**
 * @Description 处理进度条
 * @Params
 **/
public class MainProgressHandle
{	public static ProgressBar progressBar = null;

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
