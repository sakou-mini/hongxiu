package com.donglaistd.jinli.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class LiveUserConstant {
    public static String DefaultQuickChat;

    @Value("${quick.chat.default}")
    public void setDefaultQuickChat(String defaultQuickChat) {
        DefaultQuickChat = new String(defaultQuickChat.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }
}
