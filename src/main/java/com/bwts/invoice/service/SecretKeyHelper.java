package com.bwts.invoice.service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class SecretKeyHelper {

    public static String generateKey(String code) {
        try {
            return encrypt(code);
        } catch (Exception e) {
            throw new RuntimeException("can't generate secret key for third party", e);
        }
    }

    private static String randomString(int len) {
        final String baseString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(baseString.charAt(rnd.nextInt(baseString.length())));
        return sb.toString();
    }

    private static String encrypt(String data) throws Exception {
        String key = randomString(24);
        byte[] bt = encrypt(data.getBytes(), key.getBytes());
        BigInteger bigInteger = new BigInteger(bt);
        return bigInteger.toString(36);
    }

    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();

        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretkey = keyFactory.generateSecret(dks);

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, secretkey, sr);
        return cipher.doFinal(data);
    }
}
