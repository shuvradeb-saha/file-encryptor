package saha.project.fileencryptor.encryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Collections;

public class FolderEncryptionManager extends AbstractEncryptionManager
    implements EncryptionManager {
  private static final Logger logger = LoggerFactory.getLogger(FolderEncryptionManager.class);
  private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

  @Override
  public void encrypt(String folderPath, String key) throws Exception {
    startOperation(folderPath, key, true);
  }

  @Override
  public void decrypt(String folderPath, String key) throws Exception {
    startOperation(folderPath, key, false);
  }

  private void validatePath(File folder) {
    if (!folder.exists() || !folder.isDirectory()) {
      throw new IllegalArgumentException("Invalid folder path");
    }
  }

  private void startOperation(final String folderPath, final String key, final boolean isEncrypt)
      throws Exception {
    File folder = new File(folderPath);
    validatePath(folder);
    processFiles(folder, generateKeyFromPasskey(key), new IvParameterSpec(new byte[16]), isEncrypt);
  }

  private static void processFiles(
      File folder, SecretKey secretKey, IvParameterSpec ivSpec, boolean encrypt) throws Exception {
    var files = folder.listFiles();
    if (files == null) {
      return;
    }

    for (File file : files) {
      if (file.isDirectory()) {
        processFiles(file, secretKey, ivSpec, encrypt);
      } else {
        if (encrypt) {
          encryptFile(file, secretKey, ivSpec);
        } else {
          decryptFile(file, secretKey, ivSpec);
        }
      }
    }
  }

  private static void encryptFile(File file, SecretKey secretKey, IvParameterSpec ivSpec)
      throws Exception {
    // Read file content
    byte[] fileData = Files.readAllBytes(file.toPath());

    // Encrypt the file data
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
    byte[] encryptedData = cipher.doFinal(fileData);

    // Write encrypted data back to file
    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(encryptedData);
    }
  }

  private static void decryptFile(File file, SecretKey secretKey, IvParameterSpec ivSpec)
      throws Exception {
    // Read encrypted file content
    byte[] fileData = Files.readAllBytes(file.toPath());

    // Decrypt the file data
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
    byte[] decryptedData = cipher.doFinal(fileData);

    // Write decrypted data back to file
    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(decryptedData);
    }
  }
}
