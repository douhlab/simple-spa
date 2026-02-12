package com.dev.simplespa.module.first;

import java.util.List;
import java.util.Optional;

import com.dev.simplespa.dao.FirstDao;
import com.dev.simplespa.model.domain.MyFirst;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FirstService {

    private final FirstDao firstDao;

    public Optional<MyFirst> getById(int id) {
        return firstDao.findById(id);
    }

    public List<MyFirst> getAll(int id) {
        return firstDao.findAll();
    }
}
