package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.model.user.User;
import ru.practicum.model.user.dto.NewUserDto;
import ru.practicum.model.user.dto.UserDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User newUserDtoToUser(NewUserDto newUserDto);

    UserDto userToUserDto(User user);

    List<UserDto> listEntitiesToListUserDto(List<User> users);
}
