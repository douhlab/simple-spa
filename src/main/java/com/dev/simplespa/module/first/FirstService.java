package com.dev.simplespa.module.first;

import java.util.List;
import java.util.Optional;

import com.dev.simplespa.dao.FirstDao;
import com.dev.simplespa.model.domain.MyFirst;
import org.springframework.stereotype.Service;

@Service
public class FirstService {

    private final FirstDao firstDao;

    public FirstService(FirstDao firstDao) {
        this.firstDao = firstDao;
    }

    public Optional<MyFirst> getById(int id) {
        return firstDao.findById(id);
    }

    public List<MyFirst> getAll(int id) {
        return firstDao.findAll();
    }
}
