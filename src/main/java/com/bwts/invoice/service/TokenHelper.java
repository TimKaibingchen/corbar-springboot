package com.bwts.invoice.service;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.Timestamp;

import org.springframework.http.HttpStatus;

import com.bwts.common.exception.APIException;
import com.bwts.common.exception.ErrorCode;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class TokenHelper {

    public static String generateToken(String code, String key) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String timeValue = String.valueOf(now.getTime());
        String signature = generateSignature(code, timeValue, key);
        return encodeToken(new String[] { code, timeValue, signature });
    }

    public static String[] decodeToken(String tokenValue) {
        for (int tokenAsPlainText = 0; tokenAsPlainText < tokenValue.length() % 4; ++tokenAsPlainText) {
            tokenValue = tokenValue + "=";
        }
        try {
            String message = new String(new BASE64Decoder().decodeBuffer(tokenValue));
            return message.split(":");
        } catch (IOException e) {
            throw new APIException(HttpStatus.FORBIDDEN, ErrorCode.WRONG_TOKEN_FORMAT, "can not decode token");
        }
    }

    public static String generateSignature(String code, String time, String secretKey) {
        String timeValue = String.valueOf(time);
        String data = code + timeValue + secretKey;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] tokenMessage = messageDigest.digest(data.getBytes("UTF-8"));
            return new BASE64Encoder().encode(tokenMessage);
        } catch (Exception e) {
            throw new RuntimeException("Can not encode token", e);
        }
    }

    private static String encodeToken(String[] values) {
        StringBuilder sb = new StringBuilder();

        for (int value = 0; value < values.length; ++value) {
            sb.append(values[value]);
            if (value < values.length - 1) {
                sb.append(":");
            }
        }

        String var4 = sb.toString();
        sb = new StringBuilder(new BASE64Encoder().encode(var4.getBytes()));
        while (sb.charAt(sb.length() - 1) == 61) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
