package io.magician.common.util;

/**
 * byte工具类
 */
public class ByteUtil {

    /**
     * 从 byte[] 里面截取数据
     *
     * @param nowBytes   被截取的数据
     * @param startIndex 从哪个坐标开始截取
     * @param endIndex   取多长
     * @return 被截取出来的数据
     * @throws Exception
     */
    public static byte[] subByte(byte[] nowBytes, int startIndex, int endIndex) throws Exception {
        if (startIndex > endIndex) {
            throw new Exception("结束坐标不能小于开始坐标");
        }

        if ((nowBytes.length - 1) < startIndex) {
            throw new Exception("开始坐标不能大于被截取的数据长度");
        }

        if (nowBytes.length < (endIndex - startIndex)) {
            throw new Exception("结束坐标距离开始坐标的位置不能大于被截取数据的长度");
        }

        /* 计算要取的长度 */
        int length = endIndex - startIndex;

        /* 开始从nowBytes里面取数据 */
        byte[] bytes = new byte[length];
        int index = 0;
        for (int i = startIndex; i < length; i++) {
            if (i > (nowBytes.length - 1)) {
                break;
            }
            bytes[index] = nowBytes[i];
            index++;
        }
        return bytes;
    }

    /**
     * 从nowBytes中寻找targetByte的位置
     *
     * @return -1 表示头还没读完，>-1 表示targetByte的位置
     */
    public static int byteIndexOf(byte[] nowBytes, byte[] targetByte) {
        int startIndex = 0;
        int endIndex = targetByte.length;

        while (true) {
            int index = 0;
            boolean exist = true;

            /* 如果剩余长度已经 小于 targetByte的长度 就不用继续了 */
            if ((nowBytes.length - startIndex) < targetByte.length) {
                return -1;
            }
            /* 从startIndex开始比较，往后比较到endIndex的位置，如果全都相等就说明找到了targetByte */
            for (int i = startIndex; i < endIndex; i++) {
                if (index > targetByte.length - 1) {
                    return -1;
                }
                /* 只要有一个不相同就说明不相同，重新设置坐标，再次比较 */
                if (nowBytes[i] != targetByte[index]) {
                    startIndex++;
                    endIndex++;
                    if (startIndex > (nowBytes.length - 1) || endIndex > nowBytes.length) {
                        /* 如果坐标已经超出数据的范围了，说明没找到 */
                        return -1;
                    }
                    exist = false;
                    break;
                }
                index++;
            }
            if (exist) {
                /* 如果exist等于true，说明上面的for循环里没有进入过if，也就是说已经找到targetByte了 */
                return startIndex;
            }
        }
    }

    /**
     * byte[]转int
     *
     * @param b
     * @param start
     * @param len
     * @return
     */
    public static int bytesToInt(byte[] b, int start, int len) {
        int sum = 0;
        int end = start + len;
        for (int i = start; i < end; i++) {
            int n = ((int) b[i]) & 0xff;
            n <<= (--len) * 8;
            sum += n;
        }
        return sum;
    }

    /**
     * int转byte[]
     * @param n
     * @param len
     * @return
     */
    public static byte[] intToBytes(int n, int len) {
        byte[] b = new byte[len];
        for (int i = len; i > 0; i--) {
            b[(i - 1)] = ((byte) (n >> 8 * (len - i) & 0xFF));
        }
        return b;
    }
}
