# Reconstruction source map

The uploaded APK is a compiled release artifact. This project maps the recoverable app behavior back into editable source.

## Frontend

- `ui/auth` — Splash, Login, Register, password reset
- `ui/home` — dashboard + health summary + quick actions
- `ui/profile` — profile and edit-profile flows
- `ui/health` — Health Connect data, goals, insights, timeline
- `ui/reports` — report picker, ML Kit OCR, AI analysis, report details/history
- `ui/reminders` — daily medicine alarms
- `ui/hospitals` — nearby hospital/clinic discovery and navigation
- `ui/emergency` — emergency contacts and dialer actions
- `ui/ai` — AI health chat
- `navigation` — Compose route graph and bottom navigation

## Android/data layer

- `data/firebase` — Firebase Auth/Firestore singleton wiring
- `data/repository` — authentication, users, goals, reports, chat, timeline, emergency, hospitals, Health Connect
- `data/health` — Health Connect availability/permission helper
- `data/remote` — AI backend, Overpass, and optional Google Places Retrofit clients
- `data/api` — direct Gemini report-analysis compatibility layer
- `utils` — OCR, fallback report interpretation, medicine alarm scheduling, validation
- `viewmodel` — state and business logic for screens

## Backend

`backend/` is a compatible replacement for the remote AI service called by the app. The original server implementation cannot exist inside an APK, so it cannot be recovered from the client binary. This replacement verifies Firebase ID tokens and proxies AI chat to Gemini through a Cloudflare Worker secret.

## Secrets

No live credentials recovered from the supplied APK are reproduced here. Replace the placeholder Firebase config and set your own local/backend secrets.
