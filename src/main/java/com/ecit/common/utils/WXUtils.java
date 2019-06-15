package com.ecit.common.utils;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.Arrays;

/**
 * 微信解密
 */
public class WXUtils {
    private static final Logger LOGGER = LogManager.getLogger(WXUtils.class);

    public static JsonObject decrypt(String encryptedData, String sessionKey, String iv){
        // 被加密的数据
        byte[] dataByte = Base64.decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decode(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decode(iv);
 
        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                return new JsonObject(result);
            }
        } catch (Exception e) {
            LOGGER.info("解密异常：", e);
        }
        return null;
    }

    public static void main(String[] args) throws Exception{
        String appId = "wx4f4bc4dec97d474b";
        String encryptedData = "curkY1d2JLJBwatRtlAMdlRLPti0MTgScVK/tdKRIgAXBKWRH21mQq1Ge9vpGmt/SdYInxKPVplow+c5+orpW5Lea1qsdiFtyXTs2eDNk0a5nY5SvHQFl4czvF6eUjgoT1aa+wcD1QwPGdF2q8OhRv3MUMznmnUcShKZ2GZFswK4+Pc4KXliFA+au1FibGtuIiVhBq1hcrECkckcFCQXyJISoM4QirbnmgNiR9AxG3f9Vx4WFKKYewFhM6LMiMKdhJUFf83d8GeC7MWk5X6aJNPushqFakO1/D6zV0k94+BffKH9pCjtQhOG9cCx5eiFqFXa6qvaaZn8/omCXa+DEshq22vXwNW5358YeBY8vfOwAfNFhYS2YFfy9AzdJ6YK99FJCJTc/MALn/XTcClktDxcDEXmFwExWGzp1JrS3QtEnoqWr8clMygu44wJiKDOUXkDT3Yjop8VynFjvwgCTYy8+iIx2hwkNxF/MknihXLOD5gn1xx1Ll8Kl5Y1FOfg";
        String sessionKey = "sXJSOj+FJjN45CsFnMk49g==";
        String iv = "819uQugFsJkf2vJP7AE+kw==";
        System.out.println(decrypt(encryptedData, sessionKey, iv));
    }
 
}