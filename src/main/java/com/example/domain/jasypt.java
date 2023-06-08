package com.example.domain;

import com.kenai.jffi.Main;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

public class jasypt {

    public static StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();

        config.setPassword("Knhj7JFXan5KU8FwiTFzKLmYQFBuODCg2jzcjCTmgdw=");
        config.setAlgorithm("PBEWITHMD5ANDDES");
        config.setKeyObtentionIterations(100);
        config.setPoolSize(1);
        config.setProvider(new BouncyCastleProvider());
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);

        return encryptor;
    }
    public static void main(String[] args) {

        StringEncryptor encryptor =  stringEncryptor();

      System.out.println("Hi: "+encryptor.encrypt("Hi"));
        System.out.println("Decrypt Hi: "+encryptor.decrypt("TYXhnVlg3kRS7ksDFdCuayzBL8yqH1fxSeN3I+OQVYQ="));
    }
}
