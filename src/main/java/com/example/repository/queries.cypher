// Query 1: یافتن همه سفارش‌های ثبت‌شده توسط یک فرد خاص (مثلاً P119)
MATCH (p:Person {id: 'P119'})-[:CREATED_ORDER]->(o:Order)
RETURN p.name AS PersonName, o.orderId AS OrderId, o.status AS Status, o.createdAt AS CreatedAt;

// Query 2: استخراج لیست دستگاه‌هایی که بیشترین تعداد افراد روی آن‌ها کار کرده‌اند
MATCH (p:Person)-[:WORKS_ON]->(d:Device)
WITH d, COUNT(p) AS personCount
ORDER BY personCount DESC
LIMIT 5
RETURN d.name AS DeviceName, d.deviceId AS DeviceId, personCount AS NumberOfPersons;

// Query 3: نمایش مسیر بین یک فرد و دستگاه از طریق سفارشات
MATCH path = (p:Person {id: 'P119'})-[:CREATED_ORDER]->(o:Order)-[:TARGETS]->(d:Device)
RETURN p.name AS PersonName, o.orderId AS OrderId, d.name AS DeviceName, d.deviceId AS DeviceId;