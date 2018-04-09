package org.distributed.test.service.impl;

import javax.annotation.Resource;

import org.distributed.test.entity.Gift;
import org.distributed.test.mapper.GiftMapper;
import org.distributed.test.mapper.GiftRecordMapper;
import org.distributed.test.service.IDrawService;
import org.distributed.test.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DrawService implements IDrawService {
	@Resource
	private GiftMapper giftMapper;
	@Resource
	private GiftRecordMapper giftRecordMapper;

	@Override
	@Transactional
	public void insertGift(String name, int num) {
		Gift g = new Gift();
		g.setGiftId(UUID.get());
		g.setGiftName(name);
		g.setNum(num);
		g.setV(0L);
		giftMapper.insertSelective(g);
	}
}
