package org.distributed.test.controller;

import javax.annotation.Resource;

import org.distributed.test.service.IDrawService;
import org.distributed.test.service.SimpleResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("draw")
public class DrawController {
	@Resource
	private IDrawService drawService;

	@RequestMapping("/insert")
	public SimpleResponse<Void> insert(@RequestParam("name") String name, @RequestParam("num") int num) {
		drawService.insertGift(name, num);
		return new SimpleResponse<Void>();
	}
}
