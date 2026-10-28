package com.example.postapi;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.util.Base64;

public class AesUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";

    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    public static String encrypt(String text, SecretKey key) throws Exception {

        Cipher cipher = Cipher.getInstance(ALGORITHM);

        byte[] iv = new byte[12];
        new java.security.SecureRandom().nextBytes(iv);

        cipher.init(
                Cipher.ENCRYPT_MODE,
                key,
                new GCMParameterSpec(128, iv)
        );

        byte[] encrypted = cipher.doFinal(text.getBytes());

        byte[] result = new byte[iv.length + encrypted.length];

        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(result);
    }

    public static String decrypt(String encryptedText, SecretKey key)
            throws Exception {

        byte[] data =
                Base64.getDecoder().decode(encryptedText);

        byte[] iv = new byte[12];
        byte[] encrypted = new byte[data.length - 12];

        System.arraycopy(data, 0, iv, 0, 12);
        System.arraycopy(data, 12, encrypted, 0, encrypted.length);

        Cipher cipher = Cipher.getInstance(ALGORITHM);

        cipher.init(
                Cipher.DECRYPT_MODE,
                key,
                new GCMParameterSpec(128, iv)
        );

        byte[] decrypted = cipher.doFinal(encrypted);

        return new String(decrypted);
    }
}