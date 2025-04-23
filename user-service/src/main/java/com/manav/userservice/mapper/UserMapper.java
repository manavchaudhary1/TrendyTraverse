package com.manav.userservice.mapper;

import com.manav.userservice.dto.UserDto;
import com.manav.userservice.dto.UserKeycloakDTO;
import com.manav.userservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

    List<UserDto> toUserDtoList(List<User> users);

    @Mapping(target = "id", expression = "java(generateUUID())")
    @Mapping(target = "createdAt", expression = "java(getCurrentDateTime())")
    @Mapping(target = "lastLogin", expression = "java(getCurrentDateTime())")
    User toUser(UserKeycloakDTO userKeycloakDTO);

    User fromUserDto(UserDto userDto);


    @Named("generateUUID")
    default UUID generateUUID() {
        return UUID.randomUUID();
    }

    @Named("getCurrentDateTime")
    default LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
}