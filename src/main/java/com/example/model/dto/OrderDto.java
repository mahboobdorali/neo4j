package com.example.model.dto;

public record OrderDto(String personName,
                       String orderId,
                       String status,
                       String createdAt) {
}
