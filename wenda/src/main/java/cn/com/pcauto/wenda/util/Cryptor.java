package cn.com.pcauto.wenda.util;


import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Cryptor ��������ڶ��ı�/��ݽ��м��ܻ���ϢժҪ����ȡ��Ŀǰ֧��DES��DESede��Blowfish�����㷨���Լ�MD5��SHA-1��ϢժҪ��ȡ�㷨��<br>
 *
 * @author Rex
 * @version 1.0(beta)
 *          date 2007-02-02
 *          time 9:30:24
 * @since Common API 1.0
 */
public class Cryptor {
    private static Log logger = LogFactory.getLog(Cryptor.class);

    /* *************** ��ʹ�õ�ժҪ�㷨 BEGIN ****************/
    /**
     * MD5��ϢժҪ��ȡ�㷨
     */
    public static final String MD5 = "MD5";
    /**
     * SHA-1��ϢժҪ��ȡ�㷨
     */
    public static final String SHA1 = "SHA-1";
    /* *************** ��ʹ�õ�ժҪ�㷨 END   ****************/

    /* *************** ��ʹ�õļ����㷨 BEGIN ****************/
    /**
     * DES�����㷨
     */
    public static final String DES = "DES";
    /**
     * DESede�����㷨
     */
    public static final String DESede = "DESede";
    /**
     * Blowfish�����㷨
     */
    public static final String Blowfish = "Blowfish";
    /* *************** ��ʹ�õļ����㷨 END ******************/

    /* *********** �ɼ̳� Singleton ����� BEGIN *************/
    private static Map cryptors = new HashMap();

    static {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        Cryptor cryptor = new Cryptor();
        cryptors.put(cryptor.getClass().getName(), cryptor);
    }

    /**
     * ���� Singleton �̳еĹ��캯��
     */
    protected Cryptor() {

    }

    /**
     * ��ȡ Cryptor ��������ĵ���ʵ��
     *
     * @param name ��Ҫ��ȡ�� Cryptor ���������ʵ�������
     * @return Cryptor ���������ʵ��
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Cryptor getInstance(String name)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (name == null) {
            name = "cn.cn.com.pc.common.security.Cryptor";
        }
        if (cryptors.get(name) == null) {
            cryptors.put(name, Class.forName(name).newInstance());
        }

        return (Cryptor) cryptors.get(name);
    }
    /* *********** �ɼ̳� Singleton ����� END   *************/

    /**
     * ��ȡ Singleton Cryptor ʵ��ľ�̬����
     *
     * @return Cryptorʵ��
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Cryptor getInstance()
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (cryptors.get("cn.cn.com.pc.common.security.Cryptor") == null) {
            cryptors.put("cn.cn.com.pc.common.security.Cryptor",
                    Class.forName("cn.cn.com.pc.common.security.Cryptor").newInstance());
        }

        return (Cryptor) cryptors.get("cn.cn.com.pc.common.security.Cryptor");
    }

    /**
     * ��ȡ�ı���ժҪ��Ϣ
     *
     * @param text      ׼����ȡժҪ���ı�
     * @param algorithm Ҫʹ�õ�ժҪ�㷨
     * @return ժҪ
     */
    public static String digest(String text, String algorithm) throws NoSuchAlgorithmException {
        byte[] textBytes = text.getBytes();
        byte[] digestBytes = digest(textBytes, algorithm);
        return byte2hex(digestBytes);
    }

    /**
     * ��ȡ��ݵ�ժҪ��Ϣ
     *
     * @param data      ׼����ȡժҪ�����
     * @param algorithm Ҫʹ�õ�ժҪ�㷨
     * @return ժҪ
     */
    public static byte[] digest(byte[] data, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest digest = null;
        digest = MessageDigest.getInstance(algorithm);
        digest.update(data);
        byte[] digestBytes = digest.digest();
        logger.debug(algorithm + "��:" + byte2hex(digestBytes));
        return digestBytes;
    }

    /**
     * ������Ϸ�����Կ
     *
     * @param algorithm Ҫ���õļ����㷨�������� DES, DESede, Blowfish
     * @return ��Կ
     * @throws NoSuchAlgorithmException
     */
    public static String generateKey(String algorithm) throws NoSuchAlgorithmException {
        KeyGenerator keygen = KeyGenerator.getInstance(algorithm);
        SecretKey key = keygen.generateKey();
        String keyStr = byte2hex(key.getEncoded());
        logger.debug("�����Կ:" + keyStr);
        return keyStr;
    }

    /**
     * �����ı���Ϣ
     *
     * @param plain     ׼�����ܵ�����
     * @param key       ��Կ
     * @param algorithm Ҫʹ�õļ����㷨
     * @return ���ܺ������
     * @throws Exception
     */
    public static String encode(String plain, String key, String algorithm)
            throws IllegalBlockSizeException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException {
        logger.debug("����ǰ�����Ĵ�:" + plain);
        byte[] plainBytes = plain.getBytes();
        byte[] keyBytes = hex2byte(key);
        byte[] cipherBytes = encode(plainBytes, keyBytes, algorithm);
        return byte2hex(cipherBytes);
    }

    /**
     * �������
     *
     * @param data      ׼�����ܵ����
     * @param key       ��Կ
     * @param algorithm Ҫʹ�õļ����㷨
     * @return ���ܺ�����
     * @throws Exception
     */
    public static byte[] encode(byte[] data, byte[] key, String algorithm)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKey secKey = new javax.crypto.spec.SecretKeySpec(key, algorithm);
        logger.debug("����ǰ���ֽ���:" + byte2hex(data));
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secKey);
        byte[] cipherBytes = cipher.doFinal(data);
        logger.debug("���ܺ���ֽ���:" + byte2hex(cipherBytes));
        return cipherBytes;
    }

    /**
     * ���ܼ��ܺ������
     *
     * @param cipher    ׼�����ܵ������ı�
     * @param key       ��Կ
     * @param algorithm ���ڼ������ĵļ����㷨
     * @return ���ܺ������
     * @throws Exception
     */
    public static String decode(String cipher, String key, String algorithm)
            throws IllegalBlockSizeException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException {
        byte[] cipherBytes = hex2byte(cipher);
        byte[] keyBytes = hex2byte(key);
        byte[] plainBytes = decode(cipherBytes, keyBytes, algorithm);
        String plainStr = new String(plainBytes);
        logger.debug("���ܺ�����Ĵ�:" + plainStr);
        return plainStr;
    }

    /**
     * ���ܼ��ܺ�����
     *
     * @param data      ׼�����ܵ����
     * @param key       ��Կ
     * @param algorithm ���ڼ�����ݵļ����㷨
     * @return ���ܺ�����
     * @throws Exception
     */
    public static byte[] decode(byte[] data, byte[] key, String algorithm)
            throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        SecretKey secKey = new javax.crypto.spec.SecretKeySpec(key, algorithm);
        logger.debug("����ǰ���ֽ���:" + byte2hex(data));
        Cipher c = Cipher.getInstance(algorithm);
        c.init(Cipher.DECRYPT_MODE, secKey);
        byte[] plainBytes = c.doFinal(data);
        logger.debug("���ܺ���ֽ���:" + byte2hex(plainBytes));
        return plainBytes;
    }

    /**
     * ת�� byte ����Ϊ16�����ı���
     *
     * @param bs byte ����
     * @return 16�����ı���
     */
    public static String byte2hex(byte[] bs) {
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < bs.length; ++i) {
            hex.append(Integer.toHexString(0x0100 + (bs[i] & 0x00FF)).substring(1).toUpperCase());
        }
        return hex.toString();
    }

    /**
     * ת��16�����ı���Ϊ byte ����
     *
     * @param hex 16�����ı�������������/��ĸ���������Ϊż��
     * @return byte����
     * @throws Exception
     */
    public static byte[] hex2byte(String hex) {
        byte[] bs = new byte[hex.length() / 2];
        for (int i = 0; i < bs.length; i++) {
            bs[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bs;
    }

    public static void main(String[] args) {
        // ���key
        try {
            System.out.println("key = " + Cryptor.generateKey(Cryptor.DES));
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            String md5 = Cryptor.digest("956F5ACC348A2DE5", Cryptor.MD5);
//            String checkString = Cryptor.encode(md5, "6758C8D5F28C2025", Cryptor.DES);
//            System.out.println("check = " + checkString);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
