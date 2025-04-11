export const FLOW_STATUS_ENUM = {
    PENDING_SUBMIT: {
        value: 0,
        desc: '待提交',
    },
    APPROVING: {
        value: 1,
        desc: '审批中',
    },
    APPROVED: {
        value: 2,
        desc: '审批通过',
    },
    AUTO_APPROVED: {
        value: 3,
        desc: '自动通过',
    },
    TERMINATED: {
        value: 4,
        desc: '终止',
    },
    INVALID: {
        value: 5,
        desc: '作废',
    },
    REVOKED: {
        value: 6,
        desc: '撤销',
    },
    RETRIEVED: {
        value: 7,
        desc: '取回',
    },
    COMPLETED: {
        value: 8,
        desc: '已完成',
    },
    RETURNED: {
        value: 9,
        desc: '已退回',
    },
    EXPIRED: {
        value: 10,
        desc: '失效',
    },
};
export const COOPERATE_TYPE_ENUM = {
    APPROVAL: { value: 1, desc: '审批' },
    TRANSFER: { value: 2, desc: '转办' },
    DELEGATE: { value: 3, desc: '委派' },
    COUNTERSIGN: { value: 4, desc: '会签' },
    VOTE_SIGN: { value: 5, desc: '票签' },
    ADD_SIGN: { value: 6, desc: '加签' },
    REMOVE_SIGN: { value: 7, desc: '减签' },
}

export default {
    FLOW_STATUS_ENUM,
    COOPERATE_TYPE_ENUM
};