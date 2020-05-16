package com.developer.base.utils.lib.tool;

import android.util.Base64;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

public class BaseCrypto {
    private static BaseCrypto mInstance;

    private CryptoConfig mConfig;

    private final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private BaseCrypto(){
        mConfig = new CryptoConfig();
    }

    public static BaseCrypto getInstance() {
        if (mInstance == null) {
            mInstance = new BaseCrypto();
        }

        return mInstance;
    }

    public static void setConfig(CryptoConfig config) {
        BaseCrypto.getInstance().mConfig = config;
    }

    public static void resetConfig() {
        BaseCrypto.getInstance().mConfig = new CryptoConfig();
    }

    public byte[] toBase64(byte[] input) {
        return Base64.encode(input, this.mConfig.Base64Flags);
    }

    public byte[] fromBase64(byte[] input) {
        return Base64.decode(input, this.mConfig.Base64Flags);
    }

    public String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public String HmacSHA512Hex(byte[] key, byte[] input) {
        return bytesToHex(HmacSHA512(key, input));
    }

    public byte[] HmacSHA512(byte[] key, byte[] input) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(key, "HmacSHA512"));

            return mac.doFinal(input);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] makeAESSHA512Key(byte[] pass, int keySize) {
        try {
            byte[] key = new byte[keySize];
            MessageDigest m = MessageDigest.getInstance("SHA-512");
            m.update(pass,0,pass.length);

            byte[] k = new BigInteger(1, m.digest()).toString(16).getBytes();
            for (int i = 0; i < key.length; i++) {
                if (i < k.length)
                    key[i] = k[i];
                else
                    key[i] = 0x1;
            }

            return key;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AESEncryptMsg encryptAES(byte[] input, byte[] key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] cipherData = new byte[cipher.getOutputSize(input.length)];
            int cipherLength = cipher.update(input, 0, input.length, cipherData, 0);
            cipherLength += cipher.doFinal(cipherData, cipherLength);

            return new AESEncryptMsg(cipherData, cipherLength);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | ShortBufferException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] decryptAES(AESEncryptMsg aesEncryptMsg, byte[] key) {
        return decryptAES(aesEncryptMsg.cipherData, aesEncryptMsg.cipherLength, key);
    }

    public byte[] decryptAES(byte[] cipherData, int cipherLength, byte[] key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] plain = new byte[cipher.getOutputSize(cipherLength)];
            cipher.doFinal(plain, cipher.update(cipherData, 0, cipherLength, plain, 0));

            if (plain[plain.length - 1] == 0x0) {
                int end = plain.length - 1;

                while (plain[end] == 0x0) {
                    end--;
                }

                return Arrays.copyOf(plain, end+1);
            }

            return plain;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | ShortBufferException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class AESEncryptMsg {
        public byte[] cipherData;
        public int cipherLength;

        public AESEncryptMsg(byte[] cipherData, int cipherLength) {
            this.cipherData = cipherData;
            this.cipherLength = cipherLength;
        }
    }

    public static class CryptoConfig {
        private int Base64Flags = Base64.DEFAULT;

        public CryptoConfig setBase64Flags(int flags) {
            this.Base64Flags = flags;
            return this;
        }
    }

}
