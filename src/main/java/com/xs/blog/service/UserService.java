package com.xs.blog.service;

import com.xs.blog.pojo.User;

public interface UserService {

    User checkUser(String username, String password);
}
