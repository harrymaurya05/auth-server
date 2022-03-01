/*
 *  Copyright 2011 Unicommerce eSolutions (P) Limited All Rights Reserved.
 *  UNICOMMERCE ESOLUTIONS PROPRIETARYARY/CONFIDENTIAL. Use is subject to license terms.
 *  
 *  @version     1.0, Dec 20, 2011
 *  @author singla
 */
package com.auth.server.utils.encrption;

import com.auth.server.utils.string.StringUtils;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author singla
 */
public class EncryptionUtils {

    private static final Logger     LOG                      = LoggerFactory.getLogger(EncryptionUtils.class);
    public static final String      ENCRYPTION_SCHEME        = "DESede";
    public static final String      ENCRYPTION_KEY           = "UCUNIWARE ENCRYPTION KEY";
    private static final String     UNICODE_FORMAT           = "UTF8";
    public static final  String     EXPORT_KEY_PATH_PREFIX    = "transit/export/encryption-key/";
    public static final  String     DEFAULT_VAULT_KEY_VERSION = "1";

    private static final String     PBE_ALGORITHM            = "PBKDF2WithHmacSHA512";
    private static final int        PBE_ITERATION_COUNT      = 10000;
    private static final int        PBE_ALGORITHM_KEY_LENGTH = 128;
    private static final char[]     HEX                      = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private static KeySpec          DE_SEDE_KEY_SPEC;
    private static SecretKeyFactory DE_SEDE_key_Factory;
    private static Cipher           DE_SEDE_CIPHER;
    private static SecretKeyFactory PBE_SECRET_KEY_FACTORY;

    static {
        try {
            byte[] keyAsBytes = ENCRYPTION_KEY.getBytes(UNICODE_FORMAT);
            DE_SEDE_KEY_SPEC = new DESedeKeySpec(keyAsBytes);
            DE_SEDE_key_Factory = SecretKeyFactory.getInstance(ENCRYPTION_SCHEME);
            DE_SEDE_CIPHER = Cipher.getInstance(ENCRYPTION_SCHEME);
        } catch (Exception e) {
            LOG.error("Error while creating SecretKeyFactory for: " + ENCRYPTION_SCHEME, e);
        }
        try {
            PBE_SECRET_KEY_FACTORY = SecretKeyFactory.getInstance(PBE_ALGORITHM);
        } catch (Exception e) {
            LOG.error("Error while creating SecretKeyFactory for: " + PBE_ALGORITHM, e);
            throw new IllegalStateException("Error while creating SecretKeyFactory for: " + PBE_ALGORITHM);
        }
    }

    public static String encrypt(String unencryptedString) {
        if (unencryptedString == null || unencryptedString.trim().length() == 0)
            return unencryptedString;

        try {
            SecretKey key = DE_SEDE_key_Factory.generateSecret(DE_SEDE_KEY_SPEC);
            DE_SEDE_CIPHER.init(Cipher.ENCRYPT_MODE, key);
            byte[] clearText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] cipherText = DE_SEDE_CIPHER.doFinal(clearText);
            Base64 base64encoder = new Base64();
            return base64encoder.encodeBase64String(cipherText);
        } catch (Exception e) {
            // Unable to encrypt
            return unencryptedString;
        }
    }

    public static String encrypt(int unencryptedInt) {
        return encrypt(String.valueOf(unencryptedInt));
    }

    public static String decrypt(String encryptedString) {
        if (encryptedString == null || encryptedString.trim().length() <= 0)
            return encryptedString;

        try {
            SecretKey key = DE_SEDE_key_Factory.generateSecret(DE_SEDE_KEY_SPEC);
            DE_SEDE_CIPHER.init(Cipher.DECRYPT_MODE, key);
            Base64 base64decoder = new Base64();
            byte[] cleartext = base64decoder.decode(encryptedString);
            byte[] ciphertext = DE_SEDE_CIPHER.doFinal(cleartext);

            return new String(ciphertext, UNICODE_FORMAT);
        } catch (Exception e) {
            // Unable to decrypt
            return encryptedString;
        }
    }

    public static String md5Encode(String input, String salt) {
        return md5Encode(mergePasswordAndSalt(input, salt));
    }

    private static String mergePasswordAndSalt(String input, String salt) {
        if (input == null) {
            input = "";
        }

        if (StringUtils.isEmpty(salt)) {
            return input;
        } else {
            return input + "{" + salt + "}";
        }
    }

    public static String md5Encode(String input) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e1) {
            throw new IllegalArgumentException("No such algorithm MD5");
        }

        byte[] digest;

        try {
            digest = messageDigest.digest(input.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported!");
        }
        return hexEncode(digest);
    }
    public static String pbkdf2Encode(String text, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(text.toCharArray(), salt.getBytes(UNICODE_FORMAT), PBE_ITERATION_COUNT, PBE_ALGORITHM_KEY_LENGTH);
            return hexEncode(PBE_SECRET_KEY_FACTORY.generateSecret(spec).getEncoded());
        } catch (UnsupportedEncodingException e) {
            LOG.error("Error while encoding key", e);
            throw new IllegalStateException("UTF-8 not supported!");
        } catch (InvalidKeySpecException e) {
            LOG.error("Error while encoding key", e);
            throw new IllegalStateException(e);
        }
    }

    public static String hexEncode(byte[] bytes) {
        final int nBytes = bytes.length;
        char[] result = new char[2 * nBytes];

        int j = 0;
        for (int i = 0; i < nBytes; i++) {
            // Char for top 4 bits
            result[j++] = HEX[(0xF0 & bytes[i]) >>> 4];
            // Bottom 4
            result[j++] = HEX[(0x0F & bytes[i])];
        }

        return new String(result);
    }

    public static byte[] hexDecode(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String hexEncode(String input) {
        try {
            return hexEncode(input.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String hexDecodeAsString(String input) {
        try {
            return new String(hexDecode(input), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String base64UrlDecode(String input) {
        Base64 decoder = new Base64(true);
        byte[] decodedBytes = decoder.decode(input);
        return new String(decodedBytes);
    }

    public static byte[] base64Decode(String input) {
        Base64 decoder = new Base64();
        return decoder.decode(input);

    }

    public static String base64Encode(String input) {
        try {
            Base64 encoder = new Base64();
            return encoder.encodeBase64String(input.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String base64Encode(byte[] input) {
        Base64 encoder = new Base64();
        return encoder.encodeBase64String(input);
    }

    public static String createBase64EncodedHMACSHA1(String secretKey, String message) {
        try {
            Mac sha1HMAC = Mac.getInstance("HmacSHA1");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA1");
            sha1HMAC.init(secret_key);
            return base64Encode(sha1HMAC.doFinal(message.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String createHMACSHA256(String secretKey, String message) {
        try {
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            sha256HMAC.init(secret_key);
            return base64Encode(sha256HMAC.doFinal(message.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String createHexEncodedHMACSHA256(String secretKey, String message) {
        try {
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            sha256HMAC.init(secret_key);
            return hexEncode(sha256HMAC.doFinal(message.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String createHexEncodedMDSHA256(String message) {
        return createHexEncodedMessageDigest("SHA-256", message);
    }

    public static String createHexEncodedMessageDigest(String algorithm, String message) {
        try {
            MessageDigest sha256MD = MessageDigest.getInstance(algorithm);
            sha256MD.update(message.getBytes("UTF-8"));
            byte[] digest = sha256MD.digest();
            return hexEncode(digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String createHexEncodedHMACSHA1(String secretKey, String message) {
        try {
            Mac sha1HMAC = Mac.getInstance("HmacSHA1");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA1");
            sha1HMAC.init(secret_key);
            return hexEncode(sha1HMAC.doFinal(message.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    public static String base64EncodeFile(String filePath) {
//        FileInputStream stream = null;
//        try {
//            stream = new FileInputStream(filePath);
//            return EncryptionUtils.base64Encode(FileUtils.toByteArray(stream));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        } finally {
//            FileUtils.safelyCloseInputStream(stream);
//        }
//    }
//
//    public static void base64DecodeToFile(String base64String, String filePath) {
//        FileOutputStream stream = null;
//        try {
//            stream = new FileOutputStream(filePath);
//            stream.write(EncryptionUtils.base64Decode(base64String));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        } finally {
//            FileUtils.safelyCloseOutputStream(stream);
//        }
//    }

    //Return size in KB
    public static BigDecimal base64FileSize(String base64String) {
        if (base64String == null) {
            return BigDecimal.ZERO;
        } else {
            return new BigDecimal( (3.0*base64String.length())/4096.0);
        }
    }

    public static String urlEncode(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String urlDecode(String encodedString) {
        try {
            return URLDecoder.decode(encodedString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    public static PublicKey getPublic(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }


    public static String rsaEncode(String msg, String filename)
            throws IOException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {

        PublicKey publicKey = getPublic(filename);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return java.util.Base64.getEncoder().encodeToString(cipher.doFinal(msg.getBytes("UTF-8")));
    }

    public static String rsaEncode_AES(String msg, String filename)
            throws IOException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {

        PublicKey publicKey = getPublic(filename);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return java.util.Base64.getEncoder().encodeToString(cipher.doFinal(msg.getBytes("UTF-8")));
    }



    public static PrivateKey getPrivate(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public static String decryptText(String msg, String filename)

            throws InvalidKeyException, UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException {
        PrivateKey privateKey=null;
        Cipher ch= null;
        try {
            privateKey = getPrivate(filename);
            ch = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ch.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(ch.doFinal(java.util.Base64.getDecoder().decode(msg)), "UTF-8");
    }

    public static String decryptText_All(String msg, String filename)

            throws InvalidKeyException, UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException {
        PrivateKey privateKey=null;
        Cipher ch= null;
        try {
            privateKey = getPrivate(filename);
            ch = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ch.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(ch.doFinal(java.util.Base64.getDecoder().decode(msg)), "UTF-8");
    }

    public static String decryptText_Aes(String msg, String filename)

            throws InvalidKeyException, UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException {
        PrivateKey privateKey=null;
        Cipher ch= null;
        try {
            privateKey = getPrivate(filename);
            ch = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ch.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(ch.doFinal(java.util.Base64.getDecoder().decode(msg)), "UTF-8");
    }

}
