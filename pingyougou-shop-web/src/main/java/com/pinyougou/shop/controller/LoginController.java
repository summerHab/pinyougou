package com.pinyougou.shop.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * 登录名的控制器
 * @author ASUS
 *
 */
@RestController
@RequestMapping("/login")
public class LoginController {
	
	@RequestMapping("/name")
	public Map loginName(){
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		Map map=new HashMap<>();
		map.put("loginName", name);
		return map;
	}
}
