package unionware.base.app.utils;


import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;


public class RSAUtil {
    private static final String KEY_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;//设置长度
    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    public static final String RSA_TYPE = "RSA/ECB/PKCS1Padding";


    /**
     * 生成公、私钥
     * 根据需要返回String或byte[]类型
     *
     * @return
     */
    public static ArrayList<String> createRSAKeys() {
        ArrayList<String> array = new ArrayList<>();
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE, new SecureRandom());
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            //获取公、私钥值
            String publicKeyValue = Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
            String privateKeyValue = Base64.encodeToString(privateKey.getEncoded(), Base64.DEFAULT);

            //存入
            array.add(publicKeyValue);
            array.add(privateKeyValue);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }


    /**
     * 获取RSA公钥 根据钥匙字段
     *
     * @param key
     * @return
     */
    public static PublicKey getPublicKey(String key) {
        try {
            byte[] byteKey = Base64.decode(key, Base64.DEFAULT);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(byteKey);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            return keyFactory.generatePublic(x509EncodedKeySpec);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取RSA私钥   根据钥匙字段
     *
     * @param key
     * @return
     */
    private static PrivateKey getPrivateKey(String key) {
        try {
            byte[] byteKey = Base64.decode(key, Base64.DEFAULT);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(byteKey);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

            return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 私钥加密（分段
     *
     * @param data
     * @param
     * @return
     */

//    public static String privateEncrypt(String data, String privateKeyName) {
//        try {
//            RSAPrivateKey privateKey = (RSAPrivateKey) getPrivateKey(privateKeyName);
//            Cipher cipher = Cipher.getInstance(RSA_TYPE);
//            //每个Cipher初始化方法使用一个模式参数opmod，并用此模式初始化Cipher对象。此外还有其他参数，包括密钥key、包含密钥的证书certificate、算法参数params和随机源random。
//            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
//            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(StandardCharsets.UTF_8), privateKey.getModulus().bitLength()));
//        } catch (Exception e) {
//            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
//        }
//    }

    /**
     * 公钥解密（分段
     *
     * @param data
     * @return
     */

//    public static String publicDecrypt(String data, String publicKeyName) {
//        try {
//            Cipher cipher = Cipher.getInstance(RSA_TYPE);
//            RSAPublicKey publicKey = (RSAPublicKey) getPublicKey(publicKeyName);
//            cipher.init(Cipher.DECRYPT_MODE, publicKey);
//            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decode(data),Base64.DEFAULT, publicKey.getModulus().bitLength()), StandardCharsets.UTF_8);
//        } catch (Exception e) {
//            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
//        }
//    }


    /**
     * 公钥加密(分段
     *
     * @param data
     * @return
     */
//    public static String publicEncrypt(String data, String publicKeyName) {
//        try {
//            RSAPublicKey key = (RSAPublicKey) getPublicKey(publicKeyName);
//            Cipher cipher = Cipher.getInstance(RSA_TYPE);
//            cipher.init(Cipher.ENCRYPT_MODE, key);
//            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(StandardCharsets.UTF_8), key.getModulus().bitLength()));
//        } catch (Exception e) {
//            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
//        }
//    }

    /**
     * 私钥解密(分段
     *
     * @param data
     * @return
     */

    public static String privateDecrypt(String data, String privateKeyName) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_TYPE);
            RSAPrivateKey privateKey = (RSAPrivateKey) getPrivateKey(privateKeyName);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decode(data, Base64.DEFAULT), privateKey.getModulus().bitLength()), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String decryptPivate(String data, String privateKeyName) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(RSA_TYPE);
        RSAPrivateKey privateKey = (RSAPrivateKey) getPrivateKey(privateKeyName);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decode(data, Base64.DEFAULT), privateKey.getModulus().bitLength()), StandardCharsets.UTF_8);
    }


    /**
     * rsa切割解码  , ENCRYPT_MODE,加密数据   ,DECRYPT_MODE,解密数据
     *
     * @param cipher
     * @param opmode
     * @param datas
     * @param keySize
     * @return
     */
    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
        //最大块
        int maxBlock = 0;
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try {
            while (datas.length > offSet) {
                if (datas.length - offSet > maxBlock) {
                    //可以调用以下的doFinal（）方法完成加密或解密数据：
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(datas, offSet, datas.length - offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        } catch (Exception e) {
            throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();
        return resultDatas;
    }

    /**
     * RSA私钥 签名
     *
     * @param requestData    签名内容
     * @param privateKeyName 私钥
     * @return
     */
    public static String sign(String requestData, String privateKeyName) {
        String signature = null;
        byte[] signed = null;
        try {
            PrivateKey privateKey = getPrivateKey(privateKeyName);
            Signature Sign = Signature.getInstance(SIGNATURE_ALGORITHM);
            Sign.initSign(privateKey);
            Sign.update(requestData.getBytes());
            signed = Sign.sign();
            signature = Base64.encodeToString(signed, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return signature;
    }


    /**
     * 公钥验证签名
     *
     * @param requestData   签名内容
     * @param signature     base64签名
     * @param publicKeyName 公钥
     * @return
     */
    public static boolean verifySign(String requestData, String signature, String publicKeyName) {
        boolean verifySignSuccess = false;
        try {
            PublicKey publicKey = getPublicKey(publicKeyName);
            Signature verifySign = Signature.getInstance(SIGNATURE_ALGORITHM);
            verifySign.initVerify(publicKey);
            verifySign.update(requestData.getBytes());

            verifySignSuccess = verifySign.verify(Base64.decode(signature, Base64.DEFAULT));
            System.out.println(" >>> " + verifySignSuccess);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return verifySignSuccess;
    }


    /**
     * 加密
     *
     * @param clearText     加密字符串
     * @param publicKeyName 公钥
     * @return
     */
    public static String encrypt(String clearText, String publicKeyName) {
        String encryptedBase64 = "";
        try {
            Key key = getPublicKey(publicKeyName);
            final Cipher cipher = Cipher.getInstance(RSA_TYPE);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //
            byte[] encryptedBytes = cipher.doFinal(clearText.getBytes(StandardCharsets.UTF_8));
            encryptedBase64 = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedBase64;
    }

    /**
     * 解密
     *
     * @param encryptedBase64 加密后字符串
     * @param privateKeyName  私钥
     * @return
     */
    public static String decrypt(String encryptedBase64, String privateKeyName) {
        String decryptedString = "";
        try {
            Key key = getPrivateKey(privateKeyName);
            final Cipher cipher = Cipher.getInstance(RSA_TYPE);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedBytes = Base64.decode(encryptedBase64, Base64.DEFAULT);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            decryptedString = new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedString;
    }

}




