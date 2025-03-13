package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;

import java.util.Optional;

public interface TodoRepositoryCustom { //lv2-8
    Optional<Todo> findByIdWithUserCustom(Long todoId);
}
