package com.recordit.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recordit.server.dto.record.category.RecordCategoryResponseDto;
import com.recordit.server.repository.RecordCategoryRepository;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/record/category")
public class RecordCategoryController {

	private final RecordCategoryRepository recordCategoryRepository;

	@ApiOperation(
			value = "레코트 카테고리 전체 조회",
			notes = "레코트 카테고리 전체를 조회합니다."
	)
	@ApiResponses({
			@ApiResponse(
					code = 200, message = "API 정상 작동 / 레코드 카테고리 목록 반환",
					response = RecordCategoryResponseDto.class
			)
	})
	@GetMapping
	public ResponseEntity<?> getAllRecordCategories() {
		return null;
	}
}