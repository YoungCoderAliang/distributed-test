package org.distributed.test.service.impl;

import java.util.Random;

import javax.annotation.Resource;

import org.distributed.test.entity.Gift;
import org.distributed.test.entity.GiftRecord;
import org.distributed.test.entity.GiftV;
import org.distributed.test.mapper.GiftMapperEx;
import org.distributed.test.mapper.GiftRecordMapper;
import org.distributed.test.service.IDrawService;
import org.distributed.test.service.SimpleResponse;
import org.distributed.test.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DrawService implements IDrawService {
	private static Logger logger = LoggerFactory.getLogger(DrawService.class);
	@Resource
	private GiftMapperEx giftMapperEx;
	@Resource
	private GiftRecordMapper giftRecordMapper;
	private volatile boolean hasLeft = true;
	private Random r = new Random(System.currentTimeMillis());

	@Override
	@Transactional
	public void insertGift(String name, int num) {
		Gift g = new Gift();
		g.setGiftId(UUID.get());
		g.setGiftName(name);
		g.setNum(num);
		g.setV(0L);
		giftMapperEx.insertSelective(g);
	}

	@Override
	@Transactional
	public SimpleResponse<Void> draw(Long userId) {
		Long giftId = 1597273180490170490L;
		SimpleResponse<Void> resp = new SimpleResponse<Void>();
		if (hasLeft && r.nextInt(2500) == 0) {
			Gift g = giftMapperEx.selectByPrimaryKey(giftId);
			if (g.getNum() > 0) {
				g.setNum(g.getNum() - 1);
				int count = giftMapperEx.updateByVersion(new GiftV(g, UUID.get()));
				if (count == 0) {
					logger.error("update by version fail. set as no draw.");
					resp.setError("未中奖：更新失败");
					return resp;
				}
				GiftRecord record = new GiftRecord();
				record.setGiftId(giftId);
				record.setUserId(userId);
				giftRecordMapper.insertSelective(record);
				return resp;
			} else {
				hasLeft = false;
			}
		}
		resp.setError("未中奖");
		return resp;
	}
}
