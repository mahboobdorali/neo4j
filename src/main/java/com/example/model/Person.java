package com.example.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("Person")
@Getter
@Setter
public class Person {

    @Id
    private String id;

    @Property("name")
    private String name;

    @Property("nationalCode")
    private String nationalCode;

    @Property("role")
    private String role;

}