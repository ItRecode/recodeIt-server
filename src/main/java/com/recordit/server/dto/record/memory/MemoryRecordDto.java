package com.recordit.server.dto.record.memory;

import java.util.List;
import java.util.stream.Collectors;

import com.recordit.server.domain.Comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@ApiModel
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemoryRecordDto {
	@ApiModelProperty(notes = "추억 레코드 아이디")
	private Long recordId;

	@ApiModelProperty(notes = "추억 레코드 제목")
	private String title;

	@ApiModelProperty(notes = "추억 레코드 아이콘 이름")
	private String iconName;

	@ApiModelProperty(notes = "추억 레코드 아이콘 색상")
	private String iconColor;

	@ApiModelProperty(notes = "추억 레코드 댓글 리스트")
	private List<MemoryRecordCommentDto> memoryRecordComments;

	@Builder
	public MemoryRecordDto(
			Long recordId,
			String title,
			String iconName,
			String iconColor,
			List<Comment> memoryRecordComments
	) {
		this.recordId = recordId;
		this.title = title;
		this.iconName = iconName;
		this.iconColor = iconColor;
		this.memoryRecordComments = memoryRecordComments.stream()
				.map(
						comment -> MemoryRecordCommentDto.builder()
								.commentId(comment.getId())
								.content(comment.getContent())
								.build()
				)
				.collect(Collectors.toList());
	}
}