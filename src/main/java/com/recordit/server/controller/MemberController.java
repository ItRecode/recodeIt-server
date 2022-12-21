package com.recordit.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recordit.server.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/member/oauth/{logintype}")
	public void oauthLogin(@PathVariable("logintype") String loginType) {
		memberService.oauthLogin(loginType);
	}

	@PostMapping("/member/oauth/{loginType}")
	public void oauthRegister(@PathVariable("logintype") String loginType) {
		memberService.oauthRegister(loginType);
	}
}
