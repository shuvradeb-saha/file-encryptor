package saha.project.fileencryptor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import saha.project.fileencryptor.encryption.EncryptionService;

import java.io.File;

public class EncryptionController {
  private static Logger logger = LoggerFactory.getLogger(EncryptionController.class);

  private EncryptionService encryptionService = new EncryptionService();

  @FXML private Label welcomeText;
  @FXML private Label selectedFolderLabel;
  @FXML private PasswordField passwordField;
  @FXML private Button encryptButton;
  @FXML private Button decryptButton;
  @FXML private Label doneLabel;
  @FXML private ProgressIndicator progressIndicator;

  @FXML
  protected void onChooseFolderButtonClick() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle("Select Folder");
    File selectedDirectory = directoryChooser.showDialog(new Stage());
    if (selectedDirectory != null) {
      selectedFolderLabel.setText(selectedDirectory.getAbsolutePath());
      encryptButton.setDisable(false);
      decryptButton.setDisable(false);
    } else {
      selectedFolderLabel.setText("No folder selected");
      encryptButton.setDisable(true);
      decryptButton.setDisable(true);
    }
  }

  @FXML
  protected void onEncryptButtonClick() {
    performOperation(true);
    doneLabel.setText("Encryption done!");
  }

  @FXML
  protected void onDecryptButtonClick() {
    performOperation(false);
    doneLabel.setText("Decryption done!");
  }

  private void performOperation(boolean isEncrypt) {
    String path = selectedFolderLabel.getText();
    String key = passwordField.getText();
    progressIndicator.setVisible(true);
    doneLabel.setText("");
    new Thread(
            () -> {
              try {
                encryptionService.performOperation(path, key, isEncrypt);
                Platform.runLater(
                    () -> doneLabel.setText(isEncrypt ? "Encryption done!" : "Decryption done!"));
              } catch (Exception e) {
                logger.error("Error during operation", e);
                Platform.runLater(() -> doneLabel.setText("Operation failed!"));
              } finally {
                Platform.runLater(() -> progressIndicator.setVisible(false));
              }
            })
        .start();
  }
}
