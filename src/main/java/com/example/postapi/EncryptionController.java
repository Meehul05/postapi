package com.example.postapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.util.Map;

@RestController
@RequestMapping("/api/encryption")
public class EncryptionController {

    @GetMapping("/test")
    public Map<String, String> testEncryption() throws Exception {

        SecretKey key = AesUtil.generateKey();

        String original = "my-secret-api-key";

        String encrypted = AesUtil.encrypt(original, key);

        String decrypted = AesUtil.decrypt(encrypted, key);

        return Map.of(
                "original", original,
                "encrypted", encrypted,
                "decrypted", decrypted
        );
    }
}