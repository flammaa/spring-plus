package org.example.expert.domain.common.dto;

import lombok.Getter;
import org.example.expert.domain.user.enums.UserRole;

@Getter
public class AuthUser {

    private final Long id;
    private final String email;
    private final UserRole userRole;
    private final String nickname;//lv1-2. jwt에 닉네임 반환

    public AuthUser(Long id, String email, UserRole userRole, String nickname) {//lv1-2. jwt에 닉네임 반환
        this.id = id;
        this.email = email;
        this.userRole = userRole;
        this.nickname = nickname;//lv1-2. jwt에 닉네임 반환
    }
}
