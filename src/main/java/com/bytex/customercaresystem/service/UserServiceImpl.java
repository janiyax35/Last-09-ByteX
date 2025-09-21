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
        // In a real app, you would encode the password here before saving
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

        User newUser = new User();
        newUser.setFullName(user.getFullName());
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword()); // Storing password in plain text as requested
        newUser.setRole(Role.CUSTOMER);

        return userRepository.save(newUser);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User updateUser(Long id, User userWithUpdates) throws Exception {
        User existingUser = findById(id).orElseThrow(() -> new Exception("User not found with id: " + id));

        // Prevent username changes from the profile update form
        if (!existingUser.getUsername().equals(userWithUpdates.getUsername())) {
            // This case should ideally not be reached if the form field is readonly,
            // but as a security measure, we can log it or throw an exception.
            // For now, we will simply ignore the change and not update the username.
        }

        // Check for email uniqueness if it has changed
        if (!existingUser.getEmail().equals(userWithUpdates.getEmail())) {
            if (userRepository.findByEmail(userWithUpdates.getEmail()).isPresent()) {
                throw new Exception("Error: Email is already in use!");
            }
            existingUser.setEmail(userWithUpdates.getEmail());
        }

        existingUser.setFullName(userWithUpdates.getFullName());
        existingUser.setPhoneNumber(userWithUpdates.getPhoneNumber());
        existingUser.setRole(userWithUpdates.getRole());

        // Only update password if a new one is provided in the form
        if (userWithUpdates.getPassword() != null && !userWithUpdates.getPassword().isEmpty()) {
            existingUser.setPassword(userWithUpdates.getPassword());
        }

        return userRepository.save(existingUser);
    }

    @Override
    public List<User> findUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }
}
