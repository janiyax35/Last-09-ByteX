package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Role;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User saveUser(User user) {
        // In a real app, password would be encoded here.
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User registerNewCustomer(User user) throws Exception {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new Exception("Error: Username is already taken!");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("Error: Email is already in use!");
        }
        user.setRole(Role.CUSTOMER);
        // Password is saved in plain text as requested.
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User updateUser(Long id, User userWithUpdates) throws Exception {
        User existingUser = findById(id).orElseThrow(() -> new Exception("User not found with id: " + id));

        if (!existingUser.getEmail().equals(userWithUpdates.getEmail())) {
            if (userRepository.findByEmail(userWithUpdates.getEmail()).isPresent()) {
                throw new Exception("Error: Email is already in use!");
            }
            existingUser.setEmail(userWithUpdates.getEmail());
        }
        existingUser.setFullName(userWithUpdates.getFullName());
        existingUser.setPhoneNumber(userWithUpdates.getPhoneNumber());
        if (userWithUpdates.getRole() != null) {
            existingUser.setRole(userWithUpdates.getRole());
        }
        if (userWithUpdates.getPassword() != null && !userWithUpdates.getPassword().isEmpty()) {
            existingUser.setPassword(userWithUpdates.getPassword());
        }
        return userRepository.save(existingUser);
    }

    @Override
    public List<User> findUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public User updateUserProfile(Long id, User userWithUpdates) throws Exception {
        User existingUser = findById(id).orElseThrow(() -> new Exception("User not found with id: " + id));

        if (!existingUser.getEmail().equals(userWithUpdates.getEmail())) {
            if (userRepository.findByEmail(userWithUpdates.getEmail()).isPresent()) {
                throw new Exception("Error: Email is already in use!");
            }
            existingUser.setEmail(userWithUpdates.getEmail());
        }
        existingUser.setFullName(userWithUpdates.getFullName());
        existingUser.setPhoneNumber(userWithUpdates.getPhoneNumber());
        if (userWithUpdates.getPassword() != null && !userWithUpdates.getPassword().isEmpty()) {
            existingUser.setPassword(userWithUpdates.getPassword());
        }
        return userRepository.save(existingUser);
    }
}
