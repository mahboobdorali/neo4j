package com.example.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("Order")
@Getter
@Setter
public class Order {

    @Id
    private String id;

    @Property("orderId")
    private String orderId;

    @Property("status")
    private String status;

    @Property("createdAt")
    private String createdAt;
}