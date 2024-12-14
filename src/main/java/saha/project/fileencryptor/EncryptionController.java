package saha.project.fileencryptor;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
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

  @FXML
  protected void onHelloButtonClick() {
    welcomeText.setText("Welcome to JavaFX Application!");
  }

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
    encryptionService.performOperation(
        selectedFolderLabel.getText(), passwordField.getText(), true);
    doneLabel.setText("Encryption done!");
  }

  @FXML
  protected void onDecryptButtonClick() {
    encryptionService.performOperation(
        selectedFolderLabel.getText(), passwordField.getText(), false);
    doneLabel.setText("Decryption done!");
  }
}