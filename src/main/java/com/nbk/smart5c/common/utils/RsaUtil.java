package com.nbk.smart5c.common.utils;


import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yaojx
 * @version 1.0
 * @description:
 */
public class RsaUtil {

    // 算法名称
    private static final String RSA_ALGORITHM = "RSA";

    // 签名算法
    private static final String SHA1_WITH_RSA = "SHA1withRSA";
    private static final String SHA256_WITH_RSA = "SHA256withRSA";

    // 加密算法（算法/工作模式/填充模式）
    // 使用OAEP更安全，如果兼容性问题可改为 "RSA/ECB/PKCS1Padding"
    private static final String RSA_CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
    // 兼容模式（如果上面的报错，改用这个）
    private static final String RSA_CIPHER_ALGORITHM_COMPAT = "RSA/ECB/PKCS1Padding";

    // 密钥长度（2048位，推荐）
    private static final int KEY_SIZE = 2048;

    // 分段加密最大长度（2048位密钥 - 11 = 245，OAEP模式更小，需要计算实际大小）
    // 为了安全，动态计算实际最大加密长度
    private static final int MAX_ENCRYPT_BLOCK = 190;  // OAEP模式保守值
    private static final int MAX_DECRYPT_BLOCK = 256;  // 解密块大小 = 密钥长度/8

    /**
     * 生成密钥对（每次生成全新的）
     *
     * @return 包含公钥和私钥的Map
     */
    public static Map<String, String> generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            // 使用 SecureRandom 默认的随机源，不要设置固定种子
            keyPairGenerator.initialize(KEY_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // 获取公钥
            String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            // 获取私钥
            String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

            Map<String, String> keyMap = new HashMap<>();
            keyMap.put("publicKey", publicKey);
            keyMap.put("privateKey", privateKey);
            return keyMap;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("生成RSA密钥对失败", e);
        }
    }

    /**
     * 从Base64字符串加载公钥
     */
    public static PublicKey loadPublicKey(String publicKeyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 从Base64字符串加载私钥
     */
    public static PrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 获取加密Cipher实例（自动选择兼容模式）
     */
    private static Cipher getCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
        try {
            return Cipher.getInstance(RSA_CIPHER_ALGORITHM);
        } catch (Exception e) {
            // OAEP模式不支持时降级使用PKCS1Padding
            return Cipher.getInstance(RSA_CIPHER_ALGORITHM_COMPAT);
        }
    }

    /**
     * 公钥加密（支持长文本，自动分段）
     *
     * @param data         待加密的明文
     * @param publicKeyStr Base64编码的公钥
     * @return Base64编码的密文
     */
    public static String encryptByPublicKey(String data, String publicKeyStr) throws Exception {
        PublicKey publicKey = loadPublicKey(publicKeyStr);
        Cipher cipher = getCipher();
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        int inputLen = dataBytes.length;

        // 动态计算实际最大加密长度
        int maxEncryptBlock = getMaxEncryptBlockSize(cipher, publicKey);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        int i = 0;

        while (inputLen - offset > 0) {
            int len = Math.min(maxEncryptBlock, inputLen - offset);
            byte[] encrypted = cipher.doFinal(dataBytes, offset, len);
            out.write(encrypted);
            offset += len;
            i++;
        }

        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    /**
     * 私钥解密（支持长文本，自动分段）
     *
     * @param encryptedData Base64编码的密文
     * @param privateKeyStr Base64编码的私钥
     * @return 解密后的明文
     */
    public static String decryptByPrivateKey(String encryptedData, String privateKeyStr) throws Exception {
        PrivateKey privateKey = loadPrivateKey(privateKeyStr);
        Cipher cipher = getCipher();
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        int inputLen = encryptedBytes.length;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;

        while (inputLen - offset > 0) {
            int len = Math.min(MAX_DECRYPT_BLOCK, inputLen - offset);
            byte[] decrypted = cipher.doFinal(encryptedBytes, offset, len);
            out.write(decrypted);
            offset += len;
        }

        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * 私钥加密（用于签名场景，不推荐用于大量数据加密）
     *
     * @param data          待加密的明文
     * @param privateKeyStr Base64编码的私钥
     * @return Base64编码的密文
     */
    public static String encryptByPrivateKey(String data, String privateKeyStr) throws Exception {
        PrivateKey privateKey = loadPrivateKey(privateKeyStr);
        Cipher cipher = getCipher();
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        // 私钥加密通常用于签名，数据量小，不分段也可以
        byte[] encrypted = cipher.doFinal(dataBytes);
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * 公钥解密（用于验签场景）
     *
     * @param encryptedData Base64编码的密文
     * @param publicKeyStr  Base64编码的公钥
     * @return 解密后的明文
     */
    public static String decryptByPublicKey(String encryptedData, String publicKeyStr) throws Exception {
        PublicKey publicKey = loadPublicKey(publicKeyStr);
        Cipher cipher = getCipher();
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decrypted = cipher.doFinal(encryptedBytes);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * 生成数字签名（使用SHA256withRSA，推荐）
     *
     * @param data          待签名的数据
     * @param privateKeyStr Base64编码的私钥
     * @return Base64编码的签名
     */
    public static String sign(String data, String privateKeyStr) throws Exception {
        return sign(data, privateKeyStr, SHA256_WITH_RSA);
    }

    /**
     * 生成数字签名
     *
     * @param data          待签名的数据
     * @param privateKeyStr Base64编码的私钥
     * @param signAlgorithm 签名算法（SHA1withRSA 或 SHA256withRSA）
     * @return Base64编码的签名
     */
    public static String sign(String data, String privateKeyStr, String signAlgorithm) throws Exception {
        PrivateKey privateKey = loadPrivateKey(privateKeyStr);
        Signature signature = Signature.getInstance(signAlgorithm);
        signature.initSign(privateKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        byte[] signBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signBytes);
    }

    /**
     * 验证数字签名
     *
     * @param data         原始数据
     * @param sign         Base64编码的签名
     * @param publicKeyStr Base64编码的公钥
     * @return 是否验证通过
     */
    public static boolean verify(String data, String sign, String publicKeyStr) throws Exception {
        return verify(data, sign, publicKeyStr, SHA256_WITH_RSA);
    }

    /**
     * 验证数字签名
     *
     * @param data          原始数据
     * @param sign          Base64编码的签名
     * @param publicKeyStr  Base64编码的公钥
     * @param signAlgorithm 签名算法（需与签名时使用的算法一致）
     * @return 是否验证通过
     */
    public static boolean verify(String data, String sign, String publicKeyStr, String signAlgorithm) throws Exception {
        PublicKey publicKey = loadPublicKey(publicKeyStr);
        Signature signature = Signature.getInstance(signAlgorithm);
        signature.initVerify(publicKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        byte[] signBytes = Base64.getDecoder().decode(sign);
        return signature.verify(signBytes);
    }

    /**
     * 获取最大加密块大小（动态计算，兼容不同填充模式）
     */
    private static int getMaxEncryptBlockSize(Cipher cipher, Key key) throws Exception {
        // 临时测试加密一个字节，计算能加密的最大长度
        // 实际生产中可以使用固定值，OAEP模式: 190, PKCS1Padding模式: 245
        try {
            // 对于OAEP模式，更精确的计算方式
            if (RSA_CIPHER_ALGORITHM.contains("OAEP")) {
                return MAX_ENCRYPT_BLOCK;
            } else {
                return KEY_SIZE / 8 - 11;  // PKCS1Padding: 256 - 11 = 245
            }
        } catch (Exception e) {
            return MAX_ENCRYPT_BLOCK;
        }
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) throws Exception {
//        System.out.println("========== RSA加密工具测试 ==========\n");
//
//        // 1. 生成密钥对
//        Map<String, String> keyPair = generateKeyPair();
//        String publicKey = keyPair.get("publicKey");
////        String publicKey = RSASecret.PUBLIC_KEY;
//        String privateKey = keyPair.get("privateKey");
//        String privateKey = RSASecret.SECRET_KEY;

//        System.out.println("生成的公钥：");
//        System.out.println(publicKey);
//        System.out.println("\n生成的私钥：");
//        System.out.println(privateKey);
//
//        // 2. 测试加解密
//        System.out.println("\n========== 测试加解密 ==========");
//        String originalText = "Hello, 这是一段测试文本！RSA加密测试。";
//        System.out.println("原始数据：" + originalText);
//
//        // 公钥加密
//        String encrypted = encryptByPublicKey(originalText, publicKey);
//        System.out.println("公钥加密后：" + encrypted);
//
//        // 私钥解密
//        String decrypted = decryptByPrivateKey(encrypted, privateKey);
//        System.out.println("私钥解密后：" + decrypted);
//
//        String a = originalText.equals(decrypted) ? "✅ 成功" : "❌ 失败";
//        System.out.println("加解密验证：" + a);
//
//        // 3. 测试数字签名
//        System.out.println("\n========== 测试数字签名 ==========");
//        String signData = "需要签名的数据123456";
//        System.out.println("待签名数据：" + signData);
//
//        String signature = sign(signData, privateKey);
//        System.out.println("生成的签名：" + signature);
//
//        boolean verified = verify(signData, signature, publicKey);
//        System.out.println("签名验证结果：" + (verified ? "✅ 验证通过" : "❌ 验证失败"));
//
//        // 4. 测试篡改检测
//        System.out.println("\n========== 测试篡改检测 ==========");
//        String tamperedData = signData + "被篡改";
//        boolean tamperedVerify = verify(tamperedData, signature, publicKey);
//        System.out.println("篡改后数据验证：" + (tamperedVerify ? "❌ 验证通过（有问题）" : "✅ 正确拒绝"));
//
//        // 5. 测试长文本
//        System.out.println("\n========== 测试长文本加解密 ==========");
//        StringBuilder longText = new StringBuilder();
//        for (int i = 0; i < 100; i++) {
//            longText.append("这是第").append(i).append("行测试数据。");
//        }
//        String longString = longText.toString();
//        System.out.println("长文本长度：" + longString.length() + " 字符");
//
//        String longEncrypted = encryptByPublicKey(longString, publicKey);
//        System.out.println("长文本加密后长度：" + longEncrypted.length() + " 字符");
//
//        String longDecrypted = decryptByPrivateKey(longEncrypted, privateKey);
//        String value = longString.equals(longDecrypted) ? "✅ 成功" : "❌ 失败";
//        System.out.println("长文本解密验证：" + value);
//
//        System.out.println("\n========== 测试完成 ==========");
    }
}
