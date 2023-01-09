package com.recordit.server.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.recordit.server.domain.Member;
import com.recordit.server.domain.Record;
import com.recordit.server.domain.RecordCategory;
import com.recordit.server.domain.RecordColor;
import com.recordit.server.domain.RecordIcon;
import com.recordit.server.dto.record.RecordDetailResponseDto;
import com.recordit.server.dto.record.WriteRecordRequestDto;
import com.recordit.server.dto.record.WriteRecordResponseDto;
import com.recordit.server.exception.member.MemberNotFoundException;
import com.recordit.server.exception.record.RecordColorNotFoundException;
import com.recordit.server.exception.record.RecordIconNotFoundException;
import com.recordit.server.exception.record.RecordNotFoundException;
import com.recordit.server.exception.record.category.RecordCategoryNotFoundException;
import com.recordit.server.repository.ImageFileRepository;
import com.recordit.server.repository.MemberRepository;
import com.recordit.server.repository.RecordCategoryRepository;
import com.recordit.server.repository.RecordColorRepository;
import com.recordit.server.repository.RecordIconRepository;
import com.recordit.server.repository.RecordRepository;
import com.recordit.server.util.SessionUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordService {
	private final ImageFileRepository imageFileRepository;
	private final SessionUtil sessionUtil;
	private final MemberRepository memberRepository;
	private final RecordCategoryRepository recordCategoryRepository;
	private final RecordColorRepository recordColorRepository;
	private final RecordIconRepository recordIconRepository;
	private final RecordRepository recordRepository;

	@Transactional
	public WriteRecordResponseDto writeRecord(WriteRecordRequestDto writeRecordRequestDto, List<MultipartFile> files) {
		List<String> urls = List.of();
		// if (!file.isEmpty()) { // 파일 데이터가 존재할 때
		// 	/* todo
		// 		1.이미지 저장 후 url 가져오기
		// 		2.imageFileRepository에 save
		// 		3.numOfImage에 file 개수 대입 (개발 초기 단계에는 1개만 가능)
		// 	 */
		// }

		Long userIdBySession = sessionUtil.findUserIdBySession();
		log.info("세션에서 찾은 사용자 ID : {}", userIdBySession);

		Member member = memberRepository.findById(userIdBySession)
				.orElseThrow(() -> new MemberNotFoundException("회원 정보를 찾을 수 없습니다."));

		RecordCategory recordCategory = recordCategoryRepository.findById(writeRecordRequestDto.getRecordCategoryId())
				.orElseThrow(() -> new RecordCategoryNotFoundException("카테고리 정보를 찾을 수 없습니다."));

		RecordColor recordColor = recordColorRepository.findByName(writeRecordRequestDto.getColorName())
				.orElseThrow(() -> new RecordColorNotFoundException("컬러 정보를 찾을 수 없습니다."));

		RecordIcon recordIcon = recordIconRepository.findByName(writeRecordRequestDto.getIconName())
				.orElseThrow(() -> new RecordIconNotFoundException("아이콘 정보를 찾을 수 없습니다."));

		Record record = Record.of(
				writeRecordRequestDto,
				recordCategory,
				member,
				recordColor,
				recordIcon
		);

		Long recordId = recordRepository.save(record).getId();
		log.info("저장한 레코드 ID : ", record);

		return WriteRecordResponseDto.builder()
				.recordId(recordId)
				.build();
	}

	@Transactional(readOnly = true)
	public RecordDetailResponseDto getDetailRecord(Long recordId) {
		Record record = recordRepository.findById(recordId)
				.orElseThrow(() -> new RecordNotFoundException("레코드 정보를 찾을 수 없습니다."));

		List<String> imageUrls = List.of();
		// List<String> urls = imageFileService.findByRecordId(record.getId(), record.getNumOfImage());

		return RecordDetailResponseDto.builder()
				.recordId(record.getId())
				.categoryId(record.getRecordCategory().getId())
				.categoryName(record.getRecordCategory().getName())
				.title(record.getTitle())
				.content(record.getContent())
				.writer(record.getWriter().getNickname())
				.colorName(record.getRecordColor().getName())
				.iconName(record.getRecordIcon().getName())
				.createdAt(record.getCreatedAt())
				.imageUrls(imageUrls)
				.build();
	}
}
