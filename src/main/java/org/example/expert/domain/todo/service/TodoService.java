package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.todo.repository.TodoRepositoryCustom;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) //lv1-1. 문제 발생 원인
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;
    private final UserRepository userRepository;
    private final TodoRepositoryCustom todoRepositoryCustom;

    @Transactional //lv1-1. 작성 부분에 트랜잭셔널 추가
    public TodoSaveResponse saveTodo(Principal principal, TodoSaveRequest todoSaveRequest) {
        //Lv2-9
        Long userId = Long.valueOf(principal.getName());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail(), user.getNickname())//lv1-2
        );
    }

    public Page<TodoResponse> getTodos(int page, int size, String weather, LocalDate startDate, LocalDate endDate) { // lv1-3
        Pageable pageable = PageRequest.of(page - 1, size);

        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(23, 59, 59) : null;

        Page<Todo> todos;

        if (weather == null && startDateTime == null && endDateTime == null) {
            todos = todoRepository.findAllByOrderByModifiedAtDesc(pageable);
        }

        if (weather != null && startDateTime != null && endDateTime != null) {
            todos = todoRepository.findByWeatherAndModifiedAtBetween(weather, startDateTime, endDateTime, pageable);
        }

        if (weather != null) {
            todos = todoRepository.findAllByWeatherOrderByModifiedAtDesc(weather, pageable);
        }

        else {
            todos = todoRepository.findAllByModifiedAtBetween(startDateTime, endDateTime, pageable);
        }

        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail(), (todo.getUser().getNickname() != null) ? todo.getUser().getNickname() : "Guest"),//lv1-2, Null값 방어
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));


    }

    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail(), user.getNickname()),//lv1-2
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }

    //lv3-10
    public Page<TodoSearchResponse> searchTodos(int page, int size, String title, String start, String end, String nickname) {
        Pageable pageable = PageRequest.of(page - 1, size);
        if(title == null){
            throw new InvalidRequestException("제목을 입력해주세요.");
        }

        if(nickname == null){
            throw new InvalidRequestException("닉네임을 입력해주세요.");
        }

        if(start == null){
            throw new InvalidRequestException("시작 범위날짜를 입력해주세요");
        }

        if(end == null){
            throw new InvalidRequestException("종료 범위날짜를 입력해주세요");
        }

        Page<TodoSearchResponse> todos = todoRepositoryCustom.findTodoWithCommentAndManagerCounts(pageable, title, start, end, nickname);
        return todos.map(todo -> new TodoSearchResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getMangerCount(),
                todo.getCommentCount()
        ));
    }
}
