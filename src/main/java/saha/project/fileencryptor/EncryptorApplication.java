package saha.project.fileencryptor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class EncryptorApplication extends Application {
  private static final String ICON_PATH = "/icon.png";

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader =
        new FXMLLoader(EncryptorApplication.class.getResource("hello-view.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 420, 240);
    stage.setTitle("Shuvra's File Encryptor");
    // Set the application icon
    try (InputStream stream = getClass().getResourceAsStream(ICON_PATH)) {
      if (stream != null) {
        stage.getIcons().add(new Image(stream));
      }
    }
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}
