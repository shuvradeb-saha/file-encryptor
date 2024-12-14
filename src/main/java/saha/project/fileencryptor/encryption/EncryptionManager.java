package saha.project.fileencryptor.encryption;

public interface EncryptionManager {
  void encrypt(String contentPath, String key) throws Exception;

  void decrypt(String contentPath, String key) throws Exception;
}
