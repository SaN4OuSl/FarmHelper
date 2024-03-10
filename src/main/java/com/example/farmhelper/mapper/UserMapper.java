package com.example.farmhelper.mapper;

import com.example.farmhelper.entity.User;
import com.example.farmhelper.model.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "user", target = "fullName", qualifiedByName = "getFullName")
    UserResponse toUserRoleResponse(User user);

    @Named("getFullName")
    default String getFullName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }
}