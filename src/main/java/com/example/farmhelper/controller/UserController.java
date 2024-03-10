package com.example.farmhelper.controller;

import com.example.farmhelper.model.request.UserRequest;
import com.example.farmhelper.model.response.UserResponse;
import com.example.farmhelper.service.UserService;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @PostMapping("/assign-role")
    @ResponseStatus(HttpStatus.OK)
    public void assignRoleToUser(@Valid @RequestBody UserRequest userRequest) {
        userService.addRoleToUser(userRequest);
    }

    @DeleteMapping("/delete/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String username) {
        userService.deactivateUserByUsername(username);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> findAllUsersWithRole(
        @RequestParam(value = "search", required = false, defaultValue = "")
        String search) {
        return userService.findAllUsersWithRole(search);
    }

    @GetMapping("/unassigned-users")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getListOfUnassignedUsers(
        @RequestParam(value = "search", required = false, defaultValue = "") String search) {
        return userService.getListOfUnnasignedUsers(search);
    }
}
