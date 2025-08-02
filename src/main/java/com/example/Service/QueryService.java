package com.example.Service;

import com.example.model.dto.DeviceDto;
import com.example.model.dto.OrderDto;
import com.example.model.dto.PathDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class QueryService {

    @Autowired
    private Neo4jClient neo4jClient;

    // Query 1: یافتن همه سفارش‌های ثبت‌شده توسط یک فرد خاص
    public List<OrderDto> getOrdersByPerson(String personId) {
        String query = """
                MATCH (p:Person {id: $personId})-[:CREATED_ORDER]->(o:Order)
                RETURN p.name AS PersonName, o.orderId AS OrderId, o.status AS Status, o.createdAt AS CreatedAt
                """;
        return neo4jClient.query(query)
                .bind(personId).to("personId")
                .fetchAs(OrderDto.class)
                .mappedBy((typeSystem, record) -> new OrderDto(
                        record.get("PersonName").asString(),
                        record.get("OrderId").asString(),
                        record.get("Status").asString(),
                        record.get("CreatedAt").asString()
                ))
                .all()
                .stream()
                .toList();
    }

    // Query 2: استخراج لیست دستگاه‌هایی که بیشترین تعداد افراد روی آن‌ها کار کرده‌اند
    public List<DeviceDto> getTopDevicesByPersonCount() {
        String query = """
                MATCH (p:Person)-[:WORKS_ON]->(d:Device)
                WITH d, COUNT(p) AS personCount
                ORDER BY personCount DESC
                LIMIT 5
                RETURN d.name AS DeviceName, d.deviceId AS DeviceId, personCount AS NumberOfPersons
                """;
        return neo4jClient.query(query)
                .fetchAs(DeviceDto.class)
                .mappedBy((typeSystem, record) -> new DeviceDto(
                        record.get("DeviceName").asString(),
                        record.get("DeviceId").asString(),
                        record.get("NumberOfPersons").asLong()
                ))
                .all()
                .stream()
                .toList();
    }

    // Query 3: نمایش مسیر بین یک فرد و دستگاه از طریق سفارشات
    public List<PathDto> getPersonToDevicePaths(String personId) {
        String query = """
                MATCH path = (p:Person {id: $personId})-[:CREATED_ORDER]->(o:Order)-[:TARGETS]->(d:Device)
                RETURN p.name AS PersonName, o.orderId AS OrderId, d.name AS DeviceName, d.deviceId AS DeviceId
                """;
        return neo4jClient.query(query)
                .bind(personId).to("personId")
                .fetchAs(PathDto.class)
                .mappedBy((typeSystem, record) -> new PathDto(
                        record.get("PersonName").asString(),
                        record.get("OrderId").asString(),
                        record.get("DeviceName").asString(),
                        record.get("DeviceId").asString()
                ))
                .all()
                .stream()
                .toList();
    }
}