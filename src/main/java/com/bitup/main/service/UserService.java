package com.bitup.main.service;


import com.bitup.main.domain.User;

import java.util.List;

public interface UserService {
    public User findUserByEmail(String email);
    public User findUserById(Integer id);
    public void saveNewUser(User user);
    public void saveUser(User user);
    public List<User> findAll();
}