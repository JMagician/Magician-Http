package io.magician.tcp.websocket.util;

import io.magician.common.constant.CommonConstant;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * 加密工具类
 */
public class SocketEncryptionUtil {

    /**
     * sha1加密后再用base64转码
     * @param str
     * @return
     * @throws Exception
     */
    public static byte[] getSha1AndBase64(String str) throws Exception {
        byte[] shaByte = shaEncode(str);
        return Base64.getEncoder().encode(shaByte);
    }

    /**
     * sha1加密
     * @param inStr
     * @return
     * @throws Exception
     */
    public static byte[] shaEncode(String inStr) throws Exception {
        MessageDigest sha1 = MessageDigest.getInstance("sha1");
        sha1.update(inStr.getBytes(CommonConstant.ENCODING));

        return sha1.digest();
    }
}
