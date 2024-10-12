package com.lniculae.animation_rendered_spring.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import org.springframework.stereotype.Service;

@Service
public class HashingService {
    public String sha256Hash(String content) {
         try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }
}
