package net.lab1024.sa.admin.module.flow.enums;

public enum FlowStatusEnum {

    PENDING_SUBMIT(0, "待提交"),
    APPROVING(1, "审批中"),
    APPROVED(2, "审批通过"),
    AUTO_APPROVED(3, "自动通过"),
    TERMINATED(4, "终止"),
    INVALID(5, "作废"),
    REVOKED(6, "撤销"),
    RETRIEVED(7, "取回"),
    COMPLETED(8, "已完成"),
    RETURNED(9, "已退回"),
    EXPIRED(10, "失效");

    private final int value;
    private final String desc;

    FlowStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    // 通过数值获取枚举实例
    public static FlowStatusEnum fromValue(int value) {
        for (FlowStatusEnum status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的状态值: " + value);
    }

    // 通过描述获取枚举实例
    public static FlowStatusEnum fromDesc(String desc) {
        for (FlowStatusEnum status : values()) {
            if (status.desc.equals(desc)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的状态描述: " + desc);
    }
}
