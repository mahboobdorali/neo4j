package com.example.controller;

import com.example.Service.QueryService;
import com.example.model.dto.DeviceDto;
import com.example.model.dto.OrderDto;
import com.example.model.dto.PathDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/queries")
public class QueryController {

    @Autowired
    private QueryService queryService;

    @GetMapping("/orders/{personId}")
    public List<OrderDto> getOrdersByPerson(@PathVariable String personId) {
        return queryService.getOrdersByPerson(personId);
    }

    @GetMapping("/top-devices")
    public List<DeviceDto> getTopDevicesByPersonCount() {
        return queryService.getTopDevicesByPersonCount();
    }

    @GetMapping("/paths/{personId}")
    public List<PathDto> getPersonToDevicePaths(@PathVariable String personId) {
        return queryService.getPersonToDevicePaths(personId);
    }
}
