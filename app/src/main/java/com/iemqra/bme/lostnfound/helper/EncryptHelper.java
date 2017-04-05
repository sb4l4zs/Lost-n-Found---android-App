package com.iemqra.bme.lostnfound.helper;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class EncryptHelper {

    public static String generateSalt() {
        return UUID.randomUUID().toString().substring(0, 10);
    }

    public static String encryptPassword(String pw, String salt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        byte[] hash = sha1.digest((pw + salt).getBytes("UTF-8"));
        byte[] s = (salt).getBytes("UTF-8");

        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        ms.write(hash, 0, hash.length);
        ms.write(s, 0, s.length);
        byte[] merged = ms.toByteArray();

        return Base64.encodeToString(merged, merged.length);
    }
}
