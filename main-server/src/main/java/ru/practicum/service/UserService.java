package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exception.UserNotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.user.User;
import ru.practicum.model.user.dto.NewUserDto;
import ru.practicum.model.user.dto.UserDto;
import ru.practicum.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto createUser(NewUserDto newUserDto) {
        User user = userMapper.newUserDtoToUser(newUserDto);
        user = userRepository.save(user);
        return userMapper.userToUserDto(user);
    }

    public List<UserDto> getUsers(List<Integer> ids, int from, int size) {
        List<User> result = new ArrayList<>();
        int page = from > 0 ? from / size : 0;
        Pageable userPageable = PageRequest.of(page, size);
        if (ids == null || ids.size() == 0) {
            result.addAll(userRepository.findAll(userPageable).getContent());
        } else {
            result.addAll(userRepository.findByIdIn(ids, userPageable));
        }
        return userMapper.listEntitiesToListUserDto(result);
    }

    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        userRepository.deleteById(userId);
    }

}
