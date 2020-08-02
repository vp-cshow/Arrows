package com.vincent.arrowsserver.controller;

import com.vincent.arrowsserver.model.User;
import com.vincent.arrowsserver.persistence.PasswordStorage;
import com.vincent.arrowsserver.persistence.UserRepository;
import org.apache.coyote.Response;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    public UserRepository userRepository;
    /**
     * Get all users list.
     *
     * @return the list
     */
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUsersById(@PathVariable(value = "id") Long userId)
            {
        User user =
                userRepository
                        .findById(userId)
                        .orElse(null);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/users/{name}")
    public ResponseEntity<User> getUsersByName(@PathVariable(value = "name") String username)
    {
        User user =
                userRepository
                        .findByUsername(username)
                        .orElse(null);
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/users/authUser")
    public Map<String, String> authUser(@RequestParam String username, @RequestParam String password) throws PasswordStorage.InvalidHashException, PasswordStorage.CannotPerformOperationException {
        Optional<User> userObject = userRepository.findByUsername(username);
        Map<String, String> response = new HashMap<>();
        if (userObject.isPresent()) {
            boolean isValid = PasswordStorage.verifyPassword(password, userObject.get().getPassword());
            if (isValid) {
                response.put("result", "ok");
            }
            else {
                response.put("result", "badPW");
            }
        }
        else {
            response.put("result", "noUser");
        }
        return response;
    }

    @PostMapping("/users")
    public Map<String,String> createUser(@Valid @ModelAttribute User user) {
        Map<String, String> response = new HashMap<>();
        Optional<User> taken = userRepository.findByUsername(user.getUsername());
        if (taken.isPresent()) {
            response.put("result", "userTaken");
            return response;
        }
        response.put("result", "ok");
        userRepository.save(user);
        return response;
    }
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable(value = "id") Long userId, @Valid @RequestBody User userDetails)
            throws InvalidPropertyException, PasswordStorage.CannotPerformOperationException {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new InvalidPropertyException(User.class, "userId", "Couldn't find Id."));
        user.setUsername(userDetails.getUsername());
        try {
            user.setPassword(userDetails.getPassword());
        }
        catch (PasswordStorage.CannotPerformOperationException e) {
            throw new InvalidPropertyException(User.class, "password", "Couldn't hash password");
        }

        final User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }
    /**
     * Delete user map.
     *
     * @param userId the user id
     * @return the map
     * @throws Exception the exception
     */
    @DeleteMapping("/user/{id}")
    public Map<String, Boolean> deleteUser(@PathVariable(value = "id") Long userId) throws Exception {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new InvalidPropertyException(User.class, "userId", "Couldn't find Id."));
        userRepository.delete(user);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }
}
