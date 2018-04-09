package org.distributed.test.entity;

import org.springframework.beans.BeanUtils;

public class GiftV extends Gift {
	private Long oldv;

	public GiftV() {
		
	}
	
	public GiftV(Gift gift, Long newV) {
		BeanUtils.copyProperties(gift, this);
		this.oldv = gift.getV();
		this.setV(newV);
	}
	
	/**
	 * @return the oldv
	 */
    public Long getOldv() {
	    return oldv;
    }

	/**
	 * @param oldv the oldv to set
	 */
    public void setOldv(Long oldv) {
	    this.oldv = oldv;
    }
}
