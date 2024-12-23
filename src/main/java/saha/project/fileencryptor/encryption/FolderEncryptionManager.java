package saha.project.fileencryptor.encryption;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;

public class FolderEncryptionManager extends AbstractEncryptionManager
    implements EncryptionManager {
  public static AtomicLong sizeProcessed = new AtomicLong(0);
  private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
  private static final Logger logger = LoggerFactory.getLogger(FolderEncryptionManager.class);
  private static ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

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
    sizeProcessed = new AtomicLong(0);
    File folder = new File(folderPath);
    validatePath(folder);
    processFiles(folder, generateKeyFromPasskey(key), new IvParameterSpec(new byte[16]), isEncrypt);
    logger.info("Finished Processing. Total size: {}", byteCountToDisplaySize(sizeProcessed.get()));
  }

  private void processFiles(
      File folder, SecretKey secretKey, IvParameterSpec ivSpec, boolean encrypt) throws Exception {
    var files = folder.listFiles();
    if (files == null) {
      return;
    }

    long totalSize = getTotalSize(files);
    logger.info(
        "Processing {} files. Total size: {}", files.length, byteCountToDisplaySize(totalSize));
    for (File file : files) {
      long size = file.length();
      if (file.isDirectory()) {
        processFiles(file, secretKey, ivSpec, encrypt);
      } else {
        executor.submit(
            () -> {
              try {
                if (encrypt) {
                  encryptFile(file, secretKey, ivSpec);
                } else {
                  decryptFile(file, secretKey, ivSpec);
                }
                sizeProcessed.addAndGet(size);
                logger.info(
                    "Processed {} out of {}",
                    byteCountToDisplaySize(sizeProcessed.get()),
                    byteCountToDisplaySize(totalSize));
              } catch (Exception e) {
                logger.error("Error processing file: " + file.getAbsolutePath(), e);
              }
            });
      }
    }
  }

  private void encryptFile(File file, SecretKey secretKey, IvParameterSpec ivSpec)
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

  private void decryptFile(File file, SecretKey secretKey, IvParameterSpec ivSpec)
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

  private long getTotalSize(File[] files) {
    return Arrays.stream(files).mapToLong(File::length).sum();
  }
}
