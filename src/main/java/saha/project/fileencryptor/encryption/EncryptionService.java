package saha.project.fileencryptor.encryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptionService {
  private static final Logger logger = LoggerFactory.getLogger(EncryptionService.class);

  EncryptionManager folderEncryptionManager = new FolderEncryptionManager();

  public void performOperation(final String path, final String key, final boolean isEncrypt) {
    try {
      if (isEncrypt) {
        folderEncryptionManager.encrypt(path, key);
      } else {
        folderEncryptionManager.decrypt(path, key);
      }
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }
}
