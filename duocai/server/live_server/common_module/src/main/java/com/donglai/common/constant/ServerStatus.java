package com.donglai.common.constant;

public enum ServerStatus {

        STOP(-1),

        RUNNING(1);
        int statueCode;

        ServerStatus(int statueCode) {
            this.statueCode = statueCode;
        }

        public int getStatueCode() {
            return statueCode;
        }
    }