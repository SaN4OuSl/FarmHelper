package com.example.farmhelper.service;

import com.example.farmhelper.entity.User;
import com.example.farmhelper.model.request.UserRequest;
import com.example.farmhelper.model.response.UserResponse;
import java.util.List;

public interface UserService {

    void addRoleToUser(UserRequest userRequest);

    List<UserResponse> findAllUsersWithRole(String search);

    List<String> getListOfUnnasignedUsers(String search);

    void deactivateUserByUsername(String username);

    User getCurrentUser();
}
