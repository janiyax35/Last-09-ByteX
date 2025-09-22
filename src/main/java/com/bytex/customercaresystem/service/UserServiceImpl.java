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
    public User updateUser(Long id, User userWithUpdates, org.springframework.security.core.Authentication authentication) throws Exception {
        User existingUser = findById(id).orElseThrow(() -> new Exception("User not found with id: " + id));

        // The username from the submitted form is ignored to prevent users from changing their own username.
        if (!existingUser.getUsername().equals(userWithUpdates.getUsername())) {
            // We do not update the username.
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

        // Simplified and corrected logic for role updates.
        String loggedInUsername = authentication.getName();
        User loggedInUser = findByUsername(loggedInUsername)
                .orElseThrow(() -> new IllegalStateException("Currently authenticated user not found in database"));

        // Check if the admin is trying to edit their own profile.
        if (existingUser.getUserId().equals(loggedInUser.getUserId())) {
            // If the role in the form is different from their current role, it's an error.
            if (userWithUpdates.getRole() != null && existingUser.getRole() != userWithUpdates.getRole()) {
                throw new Exception("Admins cannot change their own role.");
            }
        } else {
            // If editing another user, update their role.
            if (userWithUpdates.getRole() != null) {
                existingUser.setRole(userWithUpdates.getRole());
            }
        }

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

    @Override
    public User updateUserProfile(Long id, User userWithUpdates) throws Exception {
        User existingUser = findById(id).orElseThrow(() -> new Exception("User not found with id: " + id));

        // In the profile context, we do not allow username changes.
        // We also check if the email is being changed to one that already exists.
        if (!existingUser.getEmail().equals(userWithUpdates.getEmail())) {
            if (userRepository.findByEmail(userWithUpdates.getEmail()).isPresent()) {
                throw new Exception("Error: Email is already in use!");
            }
            existingUser.setEmail(userWithUpdates.getEmail());
        }

        existingUser.setFullName(userWithUpdates.getFullName());
        existingUser.setPhoneNumber(userWithUpdates.getPhoneNumber());

        // Note: Role is intentionally NOT updated here.

        // Only update password if a new one is provided in the form
        if (userWithUpdates.getPassword() != null && !userWithUpdates.getPassword().isEmpty()) {
            existingUser.setPassword(userWithUpdates.getPassword());
        }

        return userRepository.save(existingUser);
    }
}
