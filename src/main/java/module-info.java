module com.codedb {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    
     
    opens com.codedb.model to javafx.base;
    
    opens com.codedb.application to javafx.fxml;
    exports com.codedb.application;

    opens com.codedb.controller to javafx.fxml;
    exports com.codedb.controller;

    opens com.codedb.componentsHandler to javafx.fxml;
    exports com.codedb.componentsHandler;
}