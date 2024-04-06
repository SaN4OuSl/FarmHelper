package com.example.farmhelper.service.impl;

import com.example.farmhelper.config.KeycloakConfigProperties;
import com.example.farmhelper.config.UserRoles;
import com.example.farmhelper.entity.User;
import com.example.farmhelper.exception.ResourceNotFoundException;
import com.example.farmhelper.exception.UserAccessException;
import com.example.farmhelper.mapper.UserMapper;
import com.example.farmhelper.model.request.UserRequest;
import com.example.farmhelper.model.response.UserResponse;
import com.example.farmhelper.repository.UserRepository;
import com.example.farmhelper.service.UserService;
import com.example.farmhelper.util.SecurityContextUtils;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final String keycloakRealm;

    private final UserRepository userRepository;

    private final Keycloak keycloak;

    public UserServiceImpl(KeycloakConfigProperties keycloakConfigProperties,
                           UserRepository userRepository,
                           Keycloak keycloak) {
        this.keycloakRealm = keycloakConfigProperties.getRealm();
        this.userRepository = userRepository;
        this.keycloak = keycloak;
    }

    @Override
    public void addRoleToUser(UserRequest userRequest) {
        log.info("Method addRoleToUser() started with userRequest = {}", userRequest);

        UserRepresentation userRepresentation =
            getUserByUsernameFromKeycloak(userRequest.getUsername());
        userRepository.findByUsernameAndIsActive(userRequest.getUsername(), false)
            .ifPresentOrElse(user -> {
                user.setIsActive(true);
                user.setRole(userRequest.getRole().name());
                userRepository.save(user);
            }, () -> {
                User newUser = User.builder()
                    .username(userRequest.getUsername())
                    .firstName(userRepresentation.getFirstName())
                    .lastName(userRepresentation.getLastName())
                    .email(userRepresentation.getEmail())
                    .role(userRequest.getRole().name())
                    .build();
                userRepository.save(newUser);
            });
        log.info("Method addRoleToUser() finished successfully");
    }

    @Override
    public List<String> getListOfUnnasignedUsers(String search) {
        log.info("Method getListOfUnnasignedUsers() started with search = {}", search);
        List<String> usernamesInDb = userRepository.findAllByIsActive(true).stream()
            .map(User::getUsername)
            .toList();

        List<String> listOfUsersFromKeycloak = getNotFiredUsersWithUsernameFromKeycloak(search)
            .map(UserRepresentation::getUsername)
            .filter(username -> !usernamesInDb.contains(username))
            .collect(Collectors.toList());

        log.info(
            "Method getListOfUnnasignedUsers() finished successfully, returned value: {}",
            listOfUsersFromKeycloak.size());
        return listOfUsersFromKeycloak;
    }

    @Override
    public List<UserResponse> findAllUsersWithRole(String search) {
        log.info("Method findAllUsers() started with search = {}", search);
        List<UserResponse> users =
            userRepository.findAllByUsernameContainingIgnoreCaseAndIsActive(search, true).stream()
                .map(UserMapper.INSTANCE::toUserRoleResponse)
                .toList();
        log.info("Method findAllUsers() finished successfully, returned value: {}",
            users.size());
        return users;
    }

    @Override
    public void deactivateUserByUsername(String username) {
        log.info("Method deleteUserByUsername() started with username = {}", username);
        User user = userRepository.findByUsernameAndIsActive(username, true).orElseThrow(() -> {
            log.warn("User with username = {} not found", username);
            return new ResourceNotFoundException(username);
        });
        if (Objects.equals(user.getRole(), UserRoles.ADMIN.name())) {
            throw new UserAccessException(username);
        }
        user.setIsActive(false);
        userRepository.save(user);
        log.info("Method deleteUserByUsername() finished successfully");
    }

    @Override
    public User getCurrentUser() {
        log.info("Method getCurrentUser() started");
        Long id = SecurityContextUtils.authenticatedUserId();
        if (id == null) {
            log.warn("User is not present in database");
            throw new ResourceNotFoundException("User");
        } else {
            return userRepository.findById(id).orElseThrow(() -> {
                log.warn("User with id = {} not found", id);
                return new ResourceNotFoundException("User", id.toString());
            });
        }
    }

    @Override
    public void synchronizeUsersWithKeycloak() {
        log.info("Method synchronizeUsersWithKeycloak() started");
        UsersResource keycloakUsers = keycloak.realm(keycloakRealm).users();
        userRepository.saveAll(userRepository.findAll().stream()
            .map(user -> synchronizeUserDataWithKeycloak(user, user.getUsername(), keycloakUsers))
            .toList());

        log.info("Method synchronizeUsersWithKeycloak() finished successfully");
    }

    private Stream<UserRepresentation> getNotFiredUsersWithUsernameFromKeycloak(String username) {
        return keycloak.realm(keycloakRealm).users().search(username).stream()
            .filter(UserRepresentation::isEnabled);
    }

    private UserRepresentation getUserByUsernameFromKeycloak(String username) {
        log.info("Method getUserByUsernameFromKeycloak() started with username = {}", username);
        if (username.isBlank()) {
            throw new ResourceNotFoundException("Field username");
        }
        UserRepresentation userRepresentation = getNotFiredUsersWithUsernameFromKeycloak(username)
            .filter(user -> user.getUsername().equals(username))
            .findFirst().orElseThrow(() -> {
                log.warn("User with username = {} not found", username);
                return new ResourceNotFoundException(username);
            });
        log.info("Method getUserByUsernameFromKeycloak() finished successfully, "
            + "returned userRepresentation with: id = {}", userRepresentation.getId());
        return userRepresentation;
    }

    private User synchronizeUserDataWithKeycloak(User userData, String username,
                                                 UsersResource keycloakUsers) {
        UserRepresentation userRepresentation = keycloakUsers.search(username).stream()
            .filter(user -> user.getUsername().equals(username))
            .findFirst().orElseThrow(() -> {
                log.warn("User with username = {} not found", username);
                return new ResourceNotFoundException(username);
            });
        userData.setFirstName(userRepresentation.getFirstName());
        userData.setLastName(userRepresentation.getLastName());
        userData.setEmail(userRepresentation.getEmail());
        return userData;
    }
}
