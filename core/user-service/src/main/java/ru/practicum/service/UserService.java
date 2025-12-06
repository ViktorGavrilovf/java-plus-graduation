package ru.practicum.service;

import ru.practicum.dto.user.NewUserRequestDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;

import java.util.List;

public interface UserService {

    UserDto addUser(NewUserRequestDto newUserRequestDto);

    void deleteUser(Long userId);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    UserShortDto getUser(Long userId);
}