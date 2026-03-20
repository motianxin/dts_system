package com.dts.system.service;

import com.dts.system.model.User;
import java.util.Optional;

public interface UserService {
    User register(User user);
    Optional<User> login(String username, String password);
    Optional<User> findByUsername(String username);
}