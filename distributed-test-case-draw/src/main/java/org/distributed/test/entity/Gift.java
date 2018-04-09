package org.distributed.test.entity;

import java.io.Serializable;

public class Gift implements Serializable {
    private Long giftId;

    private String giftName;

    private Integer num;

    private Long v;

    private static final long serialVersionUID = 1L;

    public Long getGiftId() {
        return giftId;
    }

    public void setGiftId(Long giftId) {
        this.giftId = giftId;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Long getV() {
        return v;
    }

    public void setV(Long v) {
        this.v = v;
    }
}