package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TodoRepositoryCustom { //lv2-8
    Optional<Todo> findByIdWithUserCustom(Long todoId);

    //lv3-10
    Page<TodoSearchResponse> findTodoWithCommentAndManagerCounts(Pageable pageable, String title, String start, String end, String nickName);
}
