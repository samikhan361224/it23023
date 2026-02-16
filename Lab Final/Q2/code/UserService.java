package com.example.TestSpringJPA;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public void saveUser(User user) {
        repository.save(user);
    }

    public void deleteUser(Long id) {
        repository.deleteById(id);
    }

    public User getUserById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
    }
}
