module saha.project.fileencryptor {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires org.slf4j;

    opens saha.project.fileencryptor to javafx.fxml;
    exports saha.project.fileencryptor;
}