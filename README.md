# Transaction Management API - Maybank Technical Test

This Spring Boot project was developed for the Maybank technical assessment.
It demonstrates:
- Batch file import
- RESTful APIs with pagination, search, and optimistic locking
- JPA-based persistence
- Swagger/OpenAPI documentation
- Unit and integration testing

---

## Tech Stack
- Java 17
- Spring Boot 3.x
- Spring Data JPA
- H2 (in-memory database)
- Swagger (OpenAPI 3)
- JUnit 5 + Mockito

---

## Getting Started

### Build & Run
```bash
mvn clean spring-boot:run
```

### File Import
- Place `dataSource.txt` in `assessment/dataSource.txt` once it process will to move `assessment/processed/dataSource.txt`
- On startup, the application will auto-import transactions from the file

---

## API Endpoints

### GET `/api/transactions`
Search & paginate transaction records.

**Query Parameters:**
- `accountNumber` (optional)
- `customerId` (optional)
- `description` (optional)
- `page` (default: 0)
- `size` (default: 10)

**Example:**
```http
GET /api/transactions?description=bill&page=0&size=5
```

### PUT `/api/transactions/{id}`
Update the description of a transaction record (with optimistic locking).

**Request Body:**
```json
{
  "description": "NEW DESCRIPTION"
}
```

---

## Swagger Documentation
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **API Docs JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## Running Tests
```bash
mvn test
```
Covers:
- Service logic
- Controller endpoints
- Search, update, and batch import behavior

---

## Design Patterns Used
| Pattern               | Purpose                                                                                                                                                                          |
|-----------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| DTO Pattern           | Used to separate internal Entity (TransactionRecordEntity) from external API models (TransactionRecordDTO, UpdateDescriptionRequest). Promotes loose coupling and encapsulation. |
| Service Layer         | Encapsulates business logic (TransactionRecordServiceImpl) separately from controller and repository layers, improving maintainability and testability.                          |
| Repository Pattern    | Spring Data JPA uses this pattern internally via TransactionRecordRepository, providing a clean abstraction over database access                                                 |
| Builder Pattern       | Used with Lombok’s @Builder to construct immutable DTOs and entities cleanly                                                                                                     |
| Specification Pattern | Used for flexible and dynamic filtering in the search(...) method using Spring Data’s JpaSpecificationExecutor                                                                   |
| Optimistic Locking    | Implemented via @Version field in TransactionRecord to safely handle concurrent updates without heavy database locks                                                             |

---

## Folder Structure
```
src
├── main
│   ├── java/com/maybank/assessment
│   │   ├── batch
│   │   ├── config
│   │   ├── controller
│   │   ├── dto
│   │   ├── entity
│   │   ├── exception
│   │   ├── repository
│   │   └── service
│   └── resources
│       └── dataSource.txt
└── test/java/... (JUnit Tests)
```
---
## Class Diagram (Simplified)
```
+-------------------------+
| TransactionRecordDTO    |
+-------------------------+
| - id                   |
| - accountNumber        |
| - trxAmount            |
| - description          |
| - trxDate              |
| - trxTime              |
| - customerId           |
+-------------------------+

+-----------------------------+          +----------------------+
| TransactionRecordService    |◄---------| TransactionController |
+-----------------------------+          +----------------------+
| +search(...)                |          | +GET /api/transactions|
| +updateDescription(...)     |          | +PUT /api/transactions/{id}|
+-----------------------------+          +----------------------+

+------------------------------+
| TransactionRecordRepository  |
+------------------------------+
| extends JpaRepository        |
| extends JpaSpecificationExec |
+------------------------------+

+------------------------+
| TransactionRecordEntity|
+------------------------+
| @Entity mapped fields  |
| @Version for locking   |
+------------------------+
```
---
## Activity Diagram

Search API Flow
```
User --> Controller --> Service --> Repository --> DB
 |         |             |           |
 |  GET /api/transactions?desc=...   |
 |         |             |--> build Specification
 |         |             |--> repository.findAll(spec, pageable)
 |         |<------------|
 |<-------- JSON Response
 ```
Update API Flow
```
User --> Controller --> Service --> Repository --> DB
 |         |             |           |
 | PUT /api/transactions/{id}       |
 |         |             |--> findById(id)
 |         |             |--> modify description
 |         |             |--> save() triggers @Version check
 |         |<------------|
 |<-------- updated DTO
```

---

## Author
**Fook Shen**  
[github.com/Kong-SnS](https://github.com/Kong-SnS)

---
