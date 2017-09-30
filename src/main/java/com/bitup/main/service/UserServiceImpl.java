package com.bitup.main.service;

import com.bitup.main.domain.Role;
import com.bitup.main.domain.User;
import com.bitup.main.repository.RoleRepository;
import com.bitup.main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service("userService")
public class UserServiceImpl implements UserService{

    @Autowired
    @Qualifier("userRepository")
    private UserRepository userRepository;
    @Autowired
    @Qualifier("roleRepository")
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public void saveNewUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        Role userRole = roleRepository.findByRole("USER");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        userRepository.save(user);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll(new Sort("id"));
    }

}