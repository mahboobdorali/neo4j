package com.example.Service;

import jakarta.annotation.PostConstruct;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CsvImportService {

    @Autowired
    private Neo4jClient neo4jClient;

    @PostConstruct
    public void importCsvToNeo4j() {
        try {
            // خواندن فایل person.csv
            ClassPathResource personResource = new ClassPathResource("person_nodes.csv");
            Reader personReader = new FileReader(personResource.getFile());
            Iterable<CSVRecord> personRecords = CSVFormat.DEFAULT
                    .withHeader("id", "label", "name", "nationalCode", "role")
                    .withFirstRecordAsHeader()
                    .parse(personReader);

            List<Map<String, Object>> persons = new ArrayList<>();
            for (CSVRecord record : personRecords) {
                Map<String, Object> person = new HashMap<>();
                person.put("id", record.get("id"));
                person.put("name", record.get("name"));
                person.put("nationalCode", record.get("nationalCode"));
                person.put("role", record.get("role"));
                persons.add(person);
            }

            // ذخیره گره‌های Person در Neo4j
            neo4jClient.query("UNWIND $persons AS p CREATE (:Person {id: p.id, name: p.name, nationalCode: p.nationalCode, role: p.role})")
                    .bind(persons).to("persons").run();

            // ادامه کد برای Device، Order و Edges مشابه نسخه قبلی
            // خواندن فایل device_node.csv
            ClassPathResource deviceResource = new ClassPathResource("device_nodes.csv");
            Reader deviceReader = new FileReader(deviceResource.getFile());
            Iterable<CSVRecord> deviceRecords = CSVFormat.DEFAULT
                    .withHeader("id", "label", "deviceId", "name", "installedAt")
                    .withFirstRecordAsHeader()
                    .parse(deviceReader);

            List<Map<String, Object>> devices = new ArrayList<>();
            for (CSVRecord record : deviceRecords) {
                Map<String, Object> device = new HashMap<>();
                device.put("id", record.get("id"));
                device.put("deviceId", record.get("deviceId"));
                device.put("name", record.get("name"));
                device.put("installedAt", record.get("installedAt"));
                devices.add(device);
            }

            // خواندن فایل order_node.csv
            ClassPathResource orderResource = new ClassPathResource("order_nodes.csv");
            Reader orderReader = new FileReader(orderResource.getFile());
            Iterable<CSVRecord> orderRecords = CSVFormat.DEFAULT
                    .withHeader("id", "label", "orderId", "status", "createdAt")
                    .withFirstRecordAsHeader()
                    .parse(orderReader);

            List<Map<String, Object>> orders = new ArrayList<>();
            for (CSVRecord record : orderRecords) {
                Map<String, Object> order = new HashMap<>();
                order.put("id", record.get("id"));
                order.put("orderId", record.get("orderId"));
                order.put("status", record.get("status"));
                order.put("createdAt", record.get("createdAt"));
                orders.add(order);
            }

            // ذخیره گره‌ها در Neo4j
            neo4jClient.query("UNWIND $devices AS d CREATE (:Device {id: d.id, deviceId: d.deviceId, name: d.name, installedAt: d.installedAt})")
                    .bind(devices).to("devices").run();
            neo4jClient.query("UNWIND $orders AS o CREATE (:Order {id: o.id, orderId: o.orderId, status: o.status, createdAt: o.createdAt})")
                    .bind(orders).to("orders").run();

            // خواندن فایل edge.csv و ایجاد روابط
            ClassPathResource edgeResource = new ClassPathResource("edges.csv");
            Reader edgeReader = new FileReader(edgeResource.getFile());
            Iterable<CSVRecord> edgeRecords = CSVFormat.DEFAULT
                    .withHeader("start", "end", "type")
                    .withFirstRecordAsHeader()
                    .parse(edgeReader);

            for (CSVRecord record : edgeRecords) {
                String start = record.get("start");
                String end = record.get("end");
                String type = record.get("type");

                String query = switch (type) {
                    case "WORKS_ON" ->
                            "MATCH (p:Person {id: $start}), (d:Device {id: $end}) CREATE (p)-[:WORKS_ON]->(d)";
                    case "CREATED_ORDER" ->
                            "MATCH (p:Person {id: $start}), (o:Order {id: $end}) CREATE (p)-[:CREATED_ORDER]->(o)";
                    case "TARGETS" ->
                            "MATCH (o:Order {id: $start}), (d:Device {id: $end}) CREATE (o)-[:TARGETS]->(d)";
                    default -> throw new IllegalArgumentException("Unknown relationship type: " + type);
                };

                neo4jClient.query(query)
                        .bind(start).to("start")
                        .bind(end).to("end")
                        .run();
            }

            System.out.println("Successfully imported CSV data to Neo4j");

        } catch (Exception e) {
            System.err.println("Error importing CSV to Neo4j: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
