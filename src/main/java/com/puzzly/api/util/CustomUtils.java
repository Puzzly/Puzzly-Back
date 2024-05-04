package com.puzzly.api.util;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class CustomUtils {

    // Key String : _PUZZLY_PRV_KEY_$0501
    private static String PRIVATE_KEY = "50lsSVpazGW8a2zEWXo8KlmUX4OWLtLvQ6uM0GFwn6Q=";

    public static String aesCBCEncode(String plainText) throws Exception{
        SecretKeySpec secretKey = new SecretKeySpec(PRIVATE_KEY.substring(0,32).getBytes("UTF-8"), "AES");
        IvParameterSpec iv = new IvParameterSpec(PRIVATE_KEY.substring(0,16).getBytes("UTF-8"));
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        byte[] encryptionByte = c.doFinal(plainText.getBytes("UTF-8"));
        String str = new String(Base64.getEncoder().encode(encryptionByte));
        return new String(Base64.getEncoder().encode(encryptionByte));
    }

    public static String aesCBCDecode(String encodedText) throws Exception{
        SecretKeySpec secretKey = new SecretKeySpec(PRIVATE_KEY.substring(0,32).getBytes("UTF-8"), "AES");
        IvParameterSpec iv = new IvParameterSpec(PRIVATE_KEY.substring(0,16).getBytes("UTF-8"));

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, secretKey, iv);

        byte[] decodeByte = c.doFinal(Base64.getDecoder().decode(encodedText));
        String decoded = new String(decodeByte, "UTF-8");
        return decoded;
    }
}
