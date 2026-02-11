package com.dev.simplespa.module.first;

import java.util.List;

import com.dev.simplespa.dao.FirstDao;
import com.dev.simplespa.model.domain.MyFirst;
import org.springframework.stereotype.Service;

@Service
public class FirstService {

    private final FirstDao firstDao;

    public FirstService(FirstDao firstDao) {
        this.firstDao = firstDao;
    }

    public List<MyFirst> getAll(int id) {
        return firstDao.findAll();
    }
}
