package com.xs.blog.service.impl;

import com.xs.blog.dao.UserRepository;
import com.xs.blog.pojo.User;
import com.xs.blog.service.UserService;
import com.xs.blog.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User checkUser(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, MD5Utils.code(password));
    }
}
