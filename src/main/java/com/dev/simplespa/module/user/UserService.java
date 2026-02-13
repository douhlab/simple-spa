package com.dev.simplespa.module.user;

import java.util.List;
import java.util.Optional;

import com.dev.simplespa.dao.UserDao;
import com.dev.simplespa.model.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;

    public User create(User user) {
        return userDao.create(user);
    }

    public List<User> getAll() {
        return userDao.findAll();
    }

    public Optional<User> getById(int id) {
        return userDao.findById(id);
    }

}
