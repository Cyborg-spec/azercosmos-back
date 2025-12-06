# Methane Leak Detection API

Real-time methane leak monitoring backend with satellite-based AI detection, REST API, and WebSocket notifications.

## Quick Start

```bash
./gradlew bootRun
```
Server runs on `http://localhost:8080`

---

## REST API Endpoints

### Get All Leaks
```http
GET /api/leaks
```
**Response:**
```json
[
  {
    "id": "LK-2024-001",
    "date": "2024-12-06",
    "locationName": "Shah Deniz Field (Offshore)",
    "coordinates": [[40.012, 50.200], [40.012, 50.250], [39.980, 50.250], [39.980, 50.200]],
    "severity": "HIGH",
    "status": "NEW",
    "detectedBy": "Sentinel-5P",
    "revenueLoss": 12500.00
  }
]
```

---

### Get Single Leak
```http
GET /api/leaks/{id}
```
**Example:** `GET /api/leaks/LK-2024-001`

---

### Create Leak Manually
```http
POST /api/leaks
Content-Type: application/json
```
**Body:**
```json
{
  "locationName": "Test Field",
  "severity": "HIGH",
  "detectedBy": "Manual Entry",
  "coordinates": [[40.0, 50.0], [40.0, 50.1], [39.9, 50.1], [39.9, 50.0]]
}
```

---

### Trigger AI Detection
```http
POST /api/leaks/run-detection
```
**Response (leak found):**
```json
{
  "leakDetected": true,
  "message": "Methane leak detected!",
  "leak": { ... }
}
```
**Response (no leak):**
```json
{
  "leakDetected": false,
  "message": "No methane leaks detected in this scan."
}
```

---

### Update Leak Status
```http
PATCH /api/leaks/{id}/status?status={NEW|VERIFIED|FALSE_POSITIVE}
```
**Example:**
```bash
curl -X PATCH "https://azercosmos-back-production.up.railway.app/api/leaks/LK-2025-001/status?status=VERIFIED"
```
**Response:**
```json
{
  "id": "LK-2025-001",
  "status": "VERIFIED",
  ...
}
```

---

## WebSocket (Real-time Updates)

### Connection
| Protocol | STOMP over SockJS |
|----------|-------------------|
| URL | `wss://azercosmos-back-production.up.railway.app/ws` |
| New Leaks | `/topic/leaks` |
| Status Updates | `/topic/leaks/updates` |

### JavaScript Example
```javascript
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const client = new Client({
    webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
    onConnect: () => {
        client.subscribe('/topic/leaks', (message) => {
            const leak = JSON.parse(message.body);
            // Draw polygon on map using leak.coordinates
        });
    }
});
client.activate();
```

---

## Data Models

### Leak Object
| Field | Type | Description |
|-------|------|-------------|
| id | String | Unique ID (LK-YYYY-XXX) |
| date | String | Detection date (YYYY-MM-DD) |
| locationName | String | Location name |
| coordinates | Array | Polygon points [[lat, lon], ...] |
| severity | Enum | LOW, MEDIUM, HIGH |
| status | Enum | NEW, VERIFIED, FALSE_POSITIVE |
| detectedBy | String | Detector satellite name |
| revenueLoss | Number | Estimated loss in USD |

---

## Scheduled Tasks

| Task | Schedule | Description |
|------|----------|-------------|
| Satellite Scan | Every 72 hours | Auto-downloads images, runs AI detection, broadcasts leaks |

---

## Tech Stack

- **Framework:** Spring Boot 4.0
- **Database:** SQLite
- **WebSocket:** STOMP + SockJS
- **Build:** Gradle

---

## Project Structure

```
src/main/java/com/azercosmos/back/
├── BackApplication.java
├── config/
│   └── WebSocketConfig.java
├── controller/
│   └── LeakController.java
├── dto/
│   ├── LeakDTO.java
│   └── LeakCoordinateDTO.java
├── entity/
│   ├── Leak.java
│   └── LeakCoordinate.java
├── enums/
│   ├── LeakStatus.java
│   └── Severity.java
├── repository/
│   ├── LeakRepository.java
│   └── LeakCoordinateRepository.java
├── scheduler/
│   └── SatelliteScanScheduler.java
└── service/
    ├── AIService.java
    └── LeakService.java
```
