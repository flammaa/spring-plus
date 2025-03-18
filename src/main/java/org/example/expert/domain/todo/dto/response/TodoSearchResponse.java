package org.example.expert.domain.todo.dto.response;

import lombok.Getter;

@Getter
public class TodoSearchResponse { //Lv.3-10 요구사항
    private final Long id;
    private final String title; //제목만
    private final Long mangerCount; // 일정의 담당자 수
    private final Long commentCount; // 일정의 댓글 수

    public TodoSearchResponse(Long id, String title, Long mangerCount, Long commentCount) {
        this.id = id;
        this.title = title;
        this.mangerCount = mangerCount;
        this.commentCount = commentCount;
    }
}
