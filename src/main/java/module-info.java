module com.mycompany.montecarlo {
    requires javafx.controls;
    requires javafx.fxml;
    
    requires expr4j;
    requires exp4j;
    opens com.mycompany.montecarlo to javafx.fxml;
    exports com.mycompany.montecarlo;
}
