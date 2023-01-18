
module random {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;


    opens random.client to javafx.fxml;
    exports random.client;
}