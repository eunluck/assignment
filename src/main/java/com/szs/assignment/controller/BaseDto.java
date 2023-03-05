package com.szs.assignment.controller;

import com.szs.assignment.model.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BaseDto {

    @Schema(description = "PK", example = "1")
    private Long seq;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    public BaseDto(BaseEntity baseEntity) {
        this.seq = baseEntity.getSeq();
        this.createdAt = baseEntity.getCreatedAt();
        this.updatedAt = baseEntity.getUpdatedAt();
    }
    public BaseDto(Long seq){
        this.seq = seq;
    }
}
