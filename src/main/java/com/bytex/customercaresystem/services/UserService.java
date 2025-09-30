package com.bytex.customercaresystem.services;

import com.bytex.customercaresystem.models.User;
import com.bytex.customercaresystem.models.enums.UserRole;
import com.bytex.customercaresystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User saveCustomer(User user) {
        // Ensure the role is set to CUSTOMER for self-registration
        user.setRole(UserRole.CUSTOMER);
        // In a real app, you'd encode the password here.
        // For this project, we use NoOpPasswordEncoder as requested.
        return userRepository.save(user);
    }

    @Transactional
    public User saveUser(User user) {
        // For admin to create any user type
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}