# FleetTrack

Spring Boot 4 / Java 21 backend for managing a vehicle fleet: vehicles, drivers, maintenance history,
live GPS tracking over WebSocket, Redis caching + pub/sub notifications, JWT security and PDF reports.

## Run it

1. Start PostgreSQL and Redis (needs `DB_PASSWORD` set):
   `docker compose up -d`
2. Set environment variables (see `.env.example`): `DB_PASSWORD`, `JWT_SECRET` (32+ chars).
3. Create the first admin **once**: run the app with program argument `setup-admin` and the extra variables
   `ADMIN_USERNAME`, `ADMIN_PASSWORD` (12+ chars). It prints `Admin created successfully.` and exits.
4. Run the app normally. Swagger UI: http://localhost:2424/swagger-ui/index.html
5. `POST /api/auth/login`, copy the token, click **Authorize** in Swagger and paste it.

Flyway applies `V1`, `V2`, `V3` automatically on startup (do not edit V1/V2 once applied).

## Roles

| Role | Can do |
|------|--------|
| `ADMIN` | everything |
| `FLEET_MANAGER` | read everything, send vehicle locations, create/update/complete maintenance records |

Missing/invalid token -> `401`. Wrong role -> `403`.

## Endpoints

| Method + path | Description |
|---|---|
| `POST /api/auth/login` | get a JWT |
| `GET /api/vehicles` | list. Filters: `search`, `status`, `modelYear`, `driverId`. Sort: `sort=modelYear,desc`. Page: `page`, `size` |
| `GET/POST/PUT/DELETE /api/vehicles[/{id}]` | vehicle CRUD (write = ADMIN) |
| `PUT /api/vehicles/{id}/location` | save GPS position + broadcast over WebSocket |
| `PUT /api/vehicles/{id}/driver/{driverId}` / `DELETE /api/vehicles/{id}/driver` | assign / unassign driver |
| `GET /api/drivers` | list. Filters: `search`, `active`. Sort: `sort=lastName,asc` |
| `GET/POST/PUT/DELETE /api/drivers[/{id}]` | driver CRUD (write = ADMIN) |
| `GET /api/drivers/{id}/vehicles` | vehicles assigned to a driver |
| `GET /api/maintenance` | history. Filters: `vehicleId`, `completed`, `overdue` |
| `POST/PUT/DELETE /api/maintenance[/{id}]`, `PATCH /api/maintenance/{id}/complete` | maintenance records (delete = ADMIN) |
| `GET /api/summary` | dashboard numbers (cached 1 min) |
| `GET /api/notifications` | latest notifications |
| `GET /api/reports/fleet` | PDF report (fleet status, vehicles, maintenance, driver activity) |

Errors use RFC 7807 JSON (`title`, `status`, `detail`, and `errors` for validation failures).

## Real-time tracking (WebSocket / STOMP)

- Endpoint: `ws://localhost:2424/ws`
- Send header `Authorization: Bearer <jwt>` in the STOMP `CONNECT` frame (same token as REST).
- Subscribe to:
  - `/topic/vehicles/locations` - every vehicle
  - `/topic/vehicles/{id}/location` - one vehicle
  - `/topic/notifications` - alerts
- Messages are JSON text, e.g. `{"vehicleId":1,"licensePlate":"10-AB-123","latitude":40.4,"longitude":49.8,"status":"IN_USE","timestamp":"..."}`
- Try it: open http://localhost:2424/tracking.html, paste a token, **Connect**, then send a location update.

## Redis

- **Cache** (`CacheConfig`): `vehicles` and `drivers` 10 min, `fleet-summary` 1 min. Values are stored as JSON.
  Updates/deletes evict the entry.
- **Pub/Sub**: alerts are published to channel `fleet.notifications` and the last 100 are kept in a Redis list.
  A subscriber forwards each one to WebSocket clients on `/topic/notifications`.

## Scheduled jobs

| Job | Default | What it does |
|---|---|---|
| `MaintenanceReminderJob` | daily 08:00 | notifies for maintenance due within 3 days and for overdue maintenance (once each) |
| `CleanupJob` | daily 02:30 | deletes completed maintenance records older than 90 days |

Configure in `application.properties` (`app.maintenance.*`, `app.cleanup.*`). To test quickly set
`app.maintenance.reminder-cron=*/30 * * * * *` (every 30 seconds).
Notification publishing runs `@Async` so API calls never wait for Redis.

## Notes

- With several app instances, protect the scheduled jobs with a lock (e.g. ShedLock).
- `app.websocket.allowed-origins=*` is for development; restrict it in production.
- `logging.level.org.springframework.security=TRACE` is very noisy; lower it when you are done debugging.
