package cn.rongcloud.ktvwithcalllib.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author gyn
 * @date 2022/7/11
 */
public class Sha1Util {

    public static String encryptToSHA(String s) {
        String signature = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(s.getBytes());
            byte[] bs = md.digest();
            signature = byte2hes(bs);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return signature;
    }

    public static String byte2hes(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int i = 0; i < b.length; i++) {
            stmp = Integer.toHexString(b[i] & 0XFF);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }
}
