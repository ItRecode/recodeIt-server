package com.recordit.server.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.recordit.server.constant.RefType;
import com.recordit.server.domain.Comment;
import com.recordit.server.domain.Member;
import com.recordit.server.domain.Record;
import com.recordit.server.dto.comment.CommentRequestDto;
import com.recordit.server.dto.comment.CommentResponseDto;
import com.recordit.server.dto.comment.WriteCommentRequestDto;
import com.recordit.server.dto.comment.WriteCommentResponseDto;
import com.recordit.server.exception.comment.CommentNotFoundException;
import com.recordit.server.exception.comment.EmptyContentException;
import com.recordit.server.exception.member.MemberNotFoundException;
import com.recordit.server.exception.member.NotFoundUserInfoInSessionException;
import com.recordit.server.exception.record.RecordNotFoundException;
import com.recordit.server.repository.CommentRepository;
import com.recordit.server.repository.MemberRepository;
import com.recordit.server.repository.RecordRepository;
import com.recordit.server.util.SessionUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

	private final ImageFileService imageFileService;
	private final CommentRepository commentRepository;
	private final MemberRepository memberRepository;
	private final RecordRepository recordRepository;
	private final SessionUtil sessionUtil;

	@Transactional
	public WriteCommentResponseDto writeComment(
			WriteCommentRequestDto writeCommentRequestDto,
			MultipartFile attachment
	) {
		validateEmptyContent(writeCommentRequestDto, attachment);

		Member writer = findWriterIfPresent();

		Record record = recordRepository.findById(writeCommentRequestDto.getRecordId())
				.orElseThrow(() -> new RecordNotFoundException("댓글을 작성할 레코드가 존재하지 않습니다."));

		Comment parentComment = (writeCommentRequestDto.getParentId() == null) ? null :
				commentRepository.findById(writeCommentRequestDto.getParentId())
						.orElseThrow(() -> new CommentNotFoundException("지정한 부모 댓글이 존재하지 않습니다."));
		validateParentHasParent(parentComment);

		Comment saveComment = commentRepository.save(
				Comment.of(
						writer,
						record,
						parentComment,
						writeCommentRequestDto.getComment()
				)
		);

		imageFileService.saveAttachmentFile(RefType.COMMENT, saveComment.getId(), attachment);

		log.info("저장한 댓글 ID : {}", saveComment.getId());

		return WriteCommentResponseDto.builder()
				.commentId(saveComment.getId())
				.build();
	}

	@Transactional(readOnly = true)
	public CommentResponseDto getCommentsBy(CommentRequestDto commentRequestDto) {
		Page<Comment> findComments;
		PageRequest pageRequest = PageRequest.of(
				commentRequestDto.getPage(),
				commentRequestDto.getSize(),
				Sort.Direction.ASC,
				"createdAt"
		);

		if (commentRequestDto.getParentId() == null) {
			Record findRecord = recordRepository.findById(commentRequestDto.getRecordId())
					.orElseThrow(() -> new RecordNotFoundException("레코드 정보를 찾을 수 없습니다."));

			findComments = commentRepository.findAllByRecordWithPagination(findRecord, pageRequest);
		} else {
			Comment parentComment = commentRepository.findById(commentRequestDto.getParentId())
					.orElseThrow(() -> new CommentNotFoundException("지정한 부모 댓글이 존재하지 않습니다."));
			validateParentHasParent(parentComment);

			findComments = commentRepository.findAllByParentComment(parentComment, pageRequest);
		}

		List<String> imageFileUrls = findComments.stream()
				.map(comment -> imageFileService.getImageFile(RefType.COMMENT, comment.getId()))
				.collect(Collectors.toList());

		return CommentResponseDto.builder()
				.comments(findComments)
				.imageFileUrls(imageFileUrls)
				.build();
	}

	private void validateEmptyContent(
			WriteCommentRequestDto writeCommentRequestDto,
			MultipartFile attachment
	) {
		if (attachment.isEmpty() && !StringUtils.hasText(writeCommentRequestDto.getComment())) {
			throw new EmptyContentException("댓글 내용과 이미지 파일 모두 비어있을 수 없습니다.");
		}
	}

	private void validateParentHasParent(Comment parentComment) {
		if (parentComment.getParentComment() != null) {
			throw new IllegalStateException("자식 댓글을 기준으로 조회 불가능합니다.");
		}
	}

	private Member findWriterIfPresent() {
		try {
			Long userIdInSession = sessionUtil.findUserIdBySession();
			return memberRepository.findById(userIdInSession)
					.orElseThrow(() -> new MemberNotFoundException("세션에 저장된 사용자가 DB에 존재하지 않습니다."));
		} catch (NotFoundUserInfoInSessionException e) {
			return null;
		}
	}
}
