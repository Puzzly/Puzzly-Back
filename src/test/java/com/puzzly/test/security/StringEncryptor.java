package com.puzzly.test.security;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class StringEncryptor {


    @Test
    public void stringEncryptor() {
        System.out.println("hello");

        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
        pbeEnc.setPassword("_puzzly_0612_"); //2번 설정의 암호화 키를 입력

        String enc,des;
        //암호/복호
        enc = pbeEnc.encrypt("puzzly"); // 암호화 할 내용
        System.out.println("enc = ENC(" + enc+")"); //암호화 한 내용을 출력
        des = pbeEnc.decrypt(enc);
        System.out.println("des = " + des);

        enc = pbeEnc.encrypt("Puzz!yDB@06!2"); //암호화 할 내용
        System.out.println("enc = ENC(" + enc+")"); //암호화 한 내용을 출력
        des = pbeEnc.decrypt(enc);
        System.out.println("des = " + des);


    }

    @Test
    public void testEncryptor() {


        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
        pbeEnc.setPassword("_puzzly_0612_"); //2번 설정의 암호화 키를 입력

        String enc1 = "txxw253yqUZ4Pfm4uYwvBA==";
        String enc2 = "gkXgZJg7klYjQwuyO23Ggw==";


        String des1 = pbeEnc.decrypt(enc1);
        String des2 = pbeEnc.decrypt(enc2);

        System.out.println(des1);
        System.out.println(des2);

    }
}
