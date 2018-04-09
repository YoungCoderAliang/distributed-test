package org.distributed.test.controller;

import javax.annotation.Resource;

import org.distributed.test.service.IDrawService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("draw")
public class DrawController {
	@Resource
	private IDrawService drawService;

}
