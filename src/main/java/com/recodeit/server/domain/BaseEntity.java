package com.recodeit.server.domain;

import java.time.LocalDateTime;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

	@NotNull
	@CreatedDate
	private LocalDateTime createdAt;

	@NotNull
	@LastModifiedDate
	private LocalDateTime modifiedAt;

	private LocalDateTime deletedAt;
}
