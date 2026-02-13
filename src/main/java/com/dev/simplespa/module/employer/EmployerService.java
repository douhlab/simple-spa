package com.dev.simplespa.module.employer;

import java.util.List;
import java.util.Optional;

import com.dev.simplespa.dao.EmployerDao;
import com.dev.simplespa.model.domain.Employer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployerService {

    private final EmployerDao employerDao;

    public Employer create(Employer employer) {
        return employerDao.create(employer);
    }

    public Optional<Employer> findById(int id) {
        return employerDao.findById(id);
    }

    public List<Employer> findAll() {
        return employerDao.findAll();
    }
}
