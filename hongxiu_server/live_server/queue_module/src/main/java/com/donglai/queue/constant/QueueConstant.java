package com.donglai.queue.constant;

public class QueueConstant {
    private static String DefaultMessageService = "Queue_Default_Message_Service";

    public static String getDefaultMessageService() {
        return DefaultMessageService;
    }

    public static void setDefaultMessageService(String defaultMessageService) {
        DefaultMessageService = defaultMessageService;
    }
}
