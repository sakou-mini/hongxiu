package com.donglai.model.db.entity.blogs;

public enum AuditEnum {
    AUDIT_EMPTY(-1),
    AUDIT_PASS(0),
    AUDIT_NOT_PASS(1),
    AUDIT_DELETE(2);
    public int code;

    AuditEnum(int code) {
        this.code = code;
    }

    public static AuditEnum forNumber(int code) {
        switch (code) {
            case -1:
                return AUDIT_EMPTY;
            case 0:
                return AUDIT_PASS;
            case 1:
                return AUDIT_NOT_PASS;
            case 2:
                return AUDIT_DELETE;
            default:
                return null;
        }
    }
}
