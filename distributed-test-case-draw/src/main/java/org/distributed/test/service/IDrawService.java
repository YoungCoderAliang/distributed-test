package org.distributed.test.service;

public interface IDrawService {
	public void insertGift(String name, int num);

	public SimpleResponse<Void> draw(Long userId);
}
