package org.distributed.test.entity;

import java.io.Serializable;

public class GiftRecord implements Serializable {
    private Long giftId;

    private Long userId;

    private static final long serialVersionUID = 1L;

    public Long getGiftId() {
        return giftId;
    }

    public void setGiftId(Long giftId) {
        this.giftId = giftId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}