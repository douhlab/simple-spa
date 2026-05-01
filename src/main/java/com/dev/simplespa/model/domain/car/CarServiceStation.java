package com.dev.simplespa.model.domain.car;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class CarServiceStation {

    private int id;
    private String name;
    private String location;
    private ServiceStationType type;
    private String note;

}
