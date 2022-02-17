package com.donglaistd.jinli.constant;

public enum LiveStreamUrlType {
    flv(0),
    m3u8(1);
    int value;
    LiveStreamUrlType(int value) {
        this.value = value;
    }
    public LiveStreamUrlType valueOf(int typeValue){
        switch (typeValue){
            case 0: return flv;
            case 1: return m3u8;
            default: return null;
        }
    }
}
