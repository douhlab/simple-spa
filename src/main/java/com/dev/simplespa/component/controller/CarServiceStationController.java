package com.dev.simplespa.component.controller;

import java.util.List;

import com.dev.simplespa.model.domain.car.CarServiceStation;
import com.dev.simplespa.module.car.CarServiceStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/${application.product}")
public class CarServiceStationController {

	private final CarServiceStationService carServiceStationService;

	@GetMapping("/v1/api/service-stations/all")
	public List<CarServiceStation> findAll() {
		return carServiceStationService.findAll();
	}
}
