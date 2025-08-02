package com.example.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("Device")
@Getter
@Setter
public class Device {

    @Id
    private String id;

    @Property("deviceId")
    private String deviceId;

    @Property("name")
    private String name;

    @Property("installedAt")
    private String installedAt;

}