package com.mars.server.http.util;

public class MsgUtil {

    public static String getMes(int code, String msg){
        try {
            StringBuffer msgStr = new StringBuffer();
            msgStr.append("{error_code:");
            msgStr.append(code);
            msgStr.append("error_info:");
            msgStr.append(msg);
            msgStr.append("}");
            return msgStr.toString();
        } catch (Exception e){
            return "error";
        }
    }
}
