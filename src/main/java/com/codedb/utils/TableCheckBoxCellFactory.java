package com.codedb.utils;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class TableCheckBoxCellFactory
{

    public static <S> Callback<TableColumn<S, Boolean>, TableCell<S, Boolean>> tableCheckBoxColumn() {
        return tableCheckBoxColumn(null, null);
    }

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> tableCheckBoxColumn(
            Callback<Integer, ObservableValue<Boolean>> paramCallback) {
        return tableCheckBoxColumn(paramCallback, null);
    }

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> tableCheckBoxColumn(
            Callback<Integer, ObservableValue<Boolean>> paramCallback, boolean paramBoolean) {
        Callback<T, String> callback = new Callback<T, String>() {
            @Override
            public String call(T t) {
                return ((t == null) ? "" : t.toString());
            }
        };
        return tableCheckBoxColumn(paramCallback, callback);
    }

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> tableCheckBoxColumn(
            final Callback<Integer, ObservableValue<Boolean>> getSelectedProperty, final Callback<T, String> toString) {
        return new Callback<TableColumn<S, T>, TableCell<S, T>>() {
            @Override
            public TableCell<S, T> call(TableColumn<S, T> paramTableColumn) {
                return new CheckBoxTableCell<S, T>(getSelectedProperty, (StringConverter<T>) toString);
            }
        };
    }
}
