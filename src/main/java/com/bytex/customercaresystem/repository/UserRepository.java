package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     * @param username The username to search for.
     * @return An Optional containing the user if found.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their email address.
     * @param email The email to search for.
     * @return An Optional containing the user if found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by either their username or their email address.
     * This will be used for the login functionality.
     * @param username The username to search for.
     * @param email The email to search for.
     * @return An Optional containing the user if found.
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Finds all users with a specific role.
     * @param role The role to search for.
     * @return A list of users with the given role.
     */
    java.util.List<User> findByRole(com.bytex.customercaresystem.model.Role role);
}
