module com.codedb {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.codedb.codedb to javafx.fxml;
    exports com.codedb.codedb;

    //暴露控制类给fxml
    opens com.codedb.controller to javafx.fxml;
    exports com.codedb.controller;
}