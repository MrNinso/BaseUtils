package com.developer.base.utils.lib.tool;

import android.util.Base64;

public class BaseCrypto {
    private static BaseCrypto mInstance;

    private CryptoConfig mConfig;

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

    public static class CryptoConfig {
        private int Base64Flags = Base64.DEFAULT;

        public CryptoConfig setBase64Flags(int flags) {
            this.Base64Flags = flags;
            return this;
        }
    }

}
