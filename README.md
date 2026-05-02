# Smart Campus: Sensor & Room Management API

**Module:** 5COSC022W Client-Server Architectures
**Student:** Gahana Gautam | w2056409
**Stack:** Java 17 · JAX-RS (Jersey 2.39.1) · Grizzly Embedded HTTP Server · Jackson JSON
**Base URL:** `http://localhost:8080/api/v1`

---

## API Overview

A fully RESTful JAX-RS API that manages university campus Rooms, Sensors, and Sensor Readings for the Smart Campus initiative. All data is stored in-memory using `ConcurrentHashMap` inside a singleton `DataStore` class. No database is used.

---

## How to Build and Run

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/YOUR_USERNAME/smart-campus-api.git
cd smart-campus-api

# 2. Build the executable JAR
mvn clean package

# 3. Start the server
java -jar target/smart-campus-api-1.0-SNAPSHOT.jar
```

The server starts at `http://localhost:8080/api/v1`. Press CTRL+C to stop.

---

## Endpoint Reference

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/v1 | Discovery — API metadata and HATEOAS resource links |
| GET | /api/v1/rooms | List all rooms |
| POST | /api/v1/rooms | Create a new room |
| GET | /api/v1/rooms/{roomId} | Get a specific room by ID |
| DELETE | /api/v1/rooms/{roomId} | Delete a room (blocked if sensors are assigned) |
| GET | /api/v1/sensors | List all sensors supports optional ?type= filter |
| POST | /api/v1/sensors | Register a new sensor validates roomId exists |
| GET | /api/v1/sensors/{sensorId} | Get a specific sensor by ID |
| GET | /api/v1/sensors/{sensorId}/readings | List all readings for a sensor |
| POST | /api/v1/sensors/{sensorId}/readings | Add a new reading for a sensor |

---

## Sample curl Commands

```bash
# 1. Discovery — GET /api/v1
curl -X GET http://localhost:8080/api/v1

# 2. List all rooms
curl -X GET http://localhost:8080/api/v1/rooms

# 3. Create a new room
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"ENG-205","name":"Engineering Seminar Room","capacity":25}'

# 4. Register a new sensor with a valid roomId
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"LIGHT-001","type":"Light","status":"ACTIVE","currentValue":300.0,"roomId":"LAB-101"}'

# 5. Filter sensors by type
curl -X GET "http://localhost:8080/api/v1/sensors?type=CO2"

# 6. Post a sensor reading
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":24.8}'

# 7. Get reading history for a sensor
curl -X GET http://localhost:8080/api/v1/sensors/TEMP-001/readings

# 8. Delete a room that has no sensors — success
curl -X DELETE http://localhost:8080/api/v1/rooms/ENG-205

# 9. Delete a room that still has sensors — returns 409 Conflict
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301

# 10. Register sensor with non-existent roomId — returns 422
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"TEMP-999","type":"Temperature","roomId":"GHOST-999"}'
```

---

## Report — Answers to Questions

### Part 1.1 — JAX-RS Resource Class Lifecycle

By default, JAX-RS creates a **new instance of every resource class for each incoming HTTP request** (request-scoped). This means instance variables inside a resource class are not shared between requests and are discarded after the request completes. This has a direct consequence for in-memory data management: shared state such as rooms, sensors, and readings cannot be stored as instance fields inside a resource class because those fields would be reset on every request. To prevent this, all shared data in this project lives in `DataStore`, a singleton class instantiated once and reused across all requests. `DataStore` uses `ConcurrentHashMap` instead of `HashMap` to handle concurrent requests safely if two requests write to the map simultaneously, `ConcurrentHashMap` ensures neither write is lost and no `ConcurrentModificationException` is thrown, preventing race conditions and data loss.

### Part 1.2 — Why HATEOAS is a Hallmark of Advanced RESTful Design

HATEOAS (Hypermedia As The Engine Of Application State) means API responses include links to related resources and available actions rather than requiring clients to construct URLs from external documentation. For example, the discovery endpoint at `GET /api/v1` returns links to `/api/v1/rooms` and `/api/v1/sensors` directly in the response body. This benefits client developers in two ways: first, it makes the API self-documenting and navigable without reading static documentation; second, it decouples the client from hard-coded URLs if a resource path changes server-side, clients following the links dynamically continue to work without any code changes on their side, whereas clients with hard-coded paths would break immediately.

### Part 2.1 — Returning Only IDs vs Full Room Objects in a List

Returning only IDs in a list response is more bandwidth-efficient for large datasets but forces the client to make one additional HTTP request for every ID it wants details on this is the N+1 problem and can cause significant latency at scale. Returning full room objects delivers all information in one round-trip, which reduces network overhead and simplifies client-side processing, but increases payload size considerably when there are thousands of rooms. The appropriate balance depends on the use case: lightweight ID lists suit summary views and dashboards, while full objects suit detail-heavy interfaces where all fields are needed immediately.

### Part 2.2 — Is DELETE Idempotent in This Implementation?

Yes, DELETE is idempotent in terms of server-side state. RFC 7231 defines idempotency as: multiple identical requests produce the same server state as a single request. After the first successful DELETE, the room no longer exists. Every subsequent DELETE on the same room ID finds nothing to delete the server state remains identical (room absent) regardless of how many times the request is sent. The HTTP response code will differ (200 on the first call, 404 on subsequent calls), but RFC 7231 defines idempotency in terms of server state effect, not response equality, so DELETE correctly qualifies as idempotent.

### Part 3.1 — @Consumes and Content-Type Mismatch

When a method is annotated `@Consumes(MediaType.APPLICATION_JSON)` and a client sends a request with `Content-Type: text/plain` or `Content-Type: application/xml`, JAX-RS automatically returns **HTTP 415 Unsupported Media Type** before the resource method is ever invoked. Jersey inspects the `Content-Type` request header and attempts to find a registered `MessageBodyReader` capable of deserialising that media type into the target Java type. Finding no compatible reader for the non-JSON type, Jersey rejects the request at the framework level with 415, protecting the resource method from receiving malformed or unexpected input entirely.

### Part 3.2 — @QueryParam Filtering vs Path Segment Filtering

Using a query parameter (`GET /api/v1/sensors?type=CO2`) is the correct REST approach for filtering a collection because filters are optional modifiers on a resource, not part of its identity. The resource is always `/api/v1/sensors`; the `?type=` parameter narrows the result set without changing the resource being addressed. Using a path segment (`/api/v1/sensors/type/CO2`) is semantically incorrect because it implies `type/CO2` is a distinct sub-resource with its own permanent identity, when it is not. Query parameters are also easily composable — multiple filters can be chained (`?type=CO2&status=ACTIVE`) without new path definitions, and omitting the parameter returns the full unfiltered collection naturally without any special handling.

### Part 4.1 — Benefits of the Sub-Resource Locator Pattern

The sub-resource locator pattern delegates the handling of a nested path to a separate dedicated class rather than adding more methods to the parent class. In this project, `SensorResource` handles `/sensors` and `/sensors/{id}`, and delegates `/sensors/{id}/readings` to `SensorReadingResource` via a locator method. This provides two key benefits: first, each class has a single clearly defined responsibility making it easier to read, test, and modify independently; second, in a large API with many levels of nesting, a single monolithic controller would grow to hundreds of methods and become impossible to maintain. Sub-resource locators allow the API to scale in complexity while keeping each class small, focused, and independently testable.

### Part 5.2 — Why HTTP 422 is More Semantically Accurate Than 404

HTTP 404 Not Found means the URL that was requested does not exist. In the case of posting a sensor with an invalid `roomId`, the URL `/api/v1/sensors` is perfectly valid and does exist. The problem is not the URL it is a field inside the JSON request body (`roomId`) that references a room which does not exist in the system. HTTP 422 Unprocessable Entity is specifically designed for situations where the request is syntactically well-formed (valid JSON, correct Content-Type header) but cannot be processed due to a semantic error in its content. A broken internal reference inside a valid payload is precisely a semantic error, making 422 the accurate and informative choice that communicates the exact nature of the failure to the client.

### Part 5.4 — Cybersecurity Risks of Exposing Java Stack Traces

Exposing a raw Java stack trace to an external API consumer gives an attacker several useful pieces of information: internal package and class names reveal the project structure and technology stack; framework and library names with version numbers allow attackers to search for known CVEs targeting that exact version; method names and line numbers map the internal logic flow and can identify where to probe for injection attacks; and file system paths that sometimes appear in traces reveal the server's directory structure. Together this constitutes effective reconnaissance for a targeted attack. The `GlobalExceptionMapper` in this project ensures none of this information ever reaches the client — the full stack trace is logged server-side only and the client always receives a generic 500 JSON response with no implementation details.

### Part 5.5 — JAX-RS Filters vs Manual Logging in Every Method

Logging is a cross-cutting concern it must apply consistently to every endpoint regardless of what that endpoint does. Inserting `Logger.info()` statements manually into every resource method violates the DRY (Don't Repeat Yourself) principle, is error-prone because a developer can easily forget to add it to a new method, and couples logging logic to business logic making both harder to maintain. A JAX-RS filter applies automatically to every single request and response in the entire application through a single class with zero changes required to any resource method. Enabling or disabling logging, changing the log format, or redirecting output requires modifying only one place: the filter class itself. This is the correct architectural approach for any concern that must apply uniformly across all endpoints.
