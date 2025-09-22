package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Role;
import com.bytex.customercaresystem.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findAllUsers();
    void deleteUser(Long id);
    User registerNewCustomer(User userDto) throws Exception;
    Optional<User> findById(Long id);
    User updateUser(Long id, User user) throws Exception;
    List<User> findUsersByRole(Role role);
    User updateUserProfile(Long id, User userWithUpdates) throws Exception;
}
