package saha.project.fileencryptor.encryption;

import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public abstract class AbstractEncryptionManager {
  private static final String HASH_ALGORITHM = "SHA-256";
  private static final String ENC_ALGORITHM = "AES";
  private static final String CHARSET = "UTF-8";

  protected static SecretKey generateKeyFromPasskey(String passkey) throws Exception {
    // Generate a 256-bit key from the passkey
    MessageDigest sha = MessageDigest.getInstance(HASH_ALGORITHM);
    byte[] keyBytes = sha.digest(passkey.getBytes(CHARSET));
    return new SecretKeySpec(keyBytes, ENC_ALGORITHM);
  }

  protected IvParameterSpec ivSpec() {
    // Create an initialization vector (IV)
    byte[] iv = new byte[16]; // 16 bytes for AES
    Arrays.fill(iv, (byte) 0); // Use a predictable IV for simplicity
    return new IvParameterSpec(iv);
  }
}
