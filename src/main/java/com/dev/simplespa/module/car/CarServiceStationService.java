package com.dev.simplespa.module.car;

import java.util.List;

import com.dev.simplespa.dao.CarServiceStationDao;
import com.dev.simplespa.model.domain.car.CarServiceStation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceStationService {

	private final CarServiceStationDao carServiceStationDao;

	public List<CarServiceStation> findAll() {
		return carServiceStationDao.findAll();
	}
}
