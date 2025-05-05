module dev.felipe9999.truthtablecalculator {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires java.logging;
    requires kotlinx.coroutines.core;


    opens dev.felipe9999.truthtablecalculator to javafx.fxml;
    exports dev.felipe9999.truthtablecalculator;
}