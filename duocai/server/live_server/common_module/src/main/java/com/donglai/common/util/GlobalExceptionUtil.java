package com.donglai.common.util;

public class GlobalExceptionUtil {
    public static String getExceptionInfo(Exception e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        StringBuffer sb = new StringBuffer();
        sb.append(e.toString()).append("\r\n");
        for (StackTraceElement stackTraceElement : stackTrace) {
            sb.append("\tat ").append(stackTraceElement.getClassName()).append(".")
                    .append(stackTraceElement.getMethodName())
                    .append("(").append(stackTraceElement.getFileName()).append(":").append(stackTraceElement.getLineNumber())
                    .append(")\r\n");
        }
        return sb.toString();
    }
}
