module com.pablor.tfg {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.pablor.tfg to javafx.fxml;
    exports com.pablor.tfg;
}