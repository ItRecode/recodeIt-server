package com.recordit.server.dto.comment;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@ApiModel
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WriteCommentRequestDto {
	@ApiModelProperty(notes = "레코드의 id", required = true)
	private Long recordId;

	@ApiModelProperty(notes = "자식 댓글일 경우 부모 댓글의 id")
	private Long parentId;

	@ApiModelProperty(notes = "댓글 내용", required = true)
	@Size(max = 200)
	private String comment;

}