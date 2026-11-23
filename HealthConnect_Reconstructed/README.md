# HealthConnect — reconstructed Android Studio project

This project was reconstructed from the supplied `HealthConnect` APK. An APK contains compiled DEX bytecode and resources, not the original Kotlin project, so this is a source-level reconstruction rather than a byte-for-byte recovery of the developer's original files.

## What was recovered from the APK

- Package: `com.healthconnect.app`
- Version: `1.0.0` (versionCode 1)
- minSdk 26, target/compile SDK 35
- Jetpack Compose UI + Material 3 + Navigation Compose
- Firebase Authentication and Firestore
- Android Health Connect
- ML Kit on-device text recognition for report scanning
- Gemini-based medical report interpretation
- AI health chat through a Cloudflare Worker-style HTTP backend
- OpenStreetMap Overpass API for nearby hospitals/clinics
- Daily medicine alarms using AlarmManager/BroadcastReceiver

Recovered screen/routes include: Splash, Login, Register, Home, Reports, AI, Medicine Reminders, Profile, Edit Profile, Goals, Timeline, Insights, Health Data, Hospitals, and Emergency.

Recovered Firestore structure:

```
users/{uid}
users/{uid}/goals/{goalId}
users/{uid}/reports/{reportId}
users/{uid}/chatMessages/{messageId}
users/{uid}/emergencyContacts/{contactId}
users/{uid}/timelineEvents/{eventId}
```

The uploaded APK contains live service configuration/API-key material. Those secrets are intentionally **not copied into this project**. All sensitive values are placeholders.

## Android setup

1. Open this folder in Android Studio.
2. Use JDK 17.
3. Create a Firebase project and add Android package `com.healthconnect.app`.
4. Enable **Authentication → Email/Password** and **Cloud Firestore**.
5. Download your own `google-services.json` and replace `app/google-services.json`.
6. Copy values from `local.properties.example` into the existing root `local.properties` (do not delete the `sdk.dir` line Android Studio creates).
7. Set `AI_BACKEND_BASE_URL` to your deployed Worker URL.
8. Set `GEMINI_API_KEY` only if you want direct report analysis from the Android app. For production, move report analysis to the backend instead of embedding an API key in an APK.
9. Paste `firestore.rules` into Firebase Console → Firestore → Rules and publish.
10. Run on Android 8+; Health Connect features require a device where Health Connect is available and permissions have been granted.

## Backend setup (Cloudflare Worker replacement)

The APK only contains the backend URL and client contract; server source cannot be recovered from the APK. A compatible replacement is included in `/backend`.

```bash
cd backend
npm install
cp wrangler.toml.example wrangler.toml
# edit FIREBASE_PROJECT_ID in wrangler.toml
npx wrangler secret put GEMINI_API_KEY
npm run deploy
```

Then put the Worker URL in root `local.properties`:

```
AI_BACKEND_BASE_URL=https://YOUR-WORKER.workers.dev/
```

The Worker verifies the Firebase ID token, accepts `POST /chat`, detects urgent phrases, and calls Gemini without exposing the Gemini key to the mobile client.

## Important reconstruction differences / fixes

The original APK requests location permissions in the Hospitals screen, but the decoded manifest did not include `ACCESS_FINE_LOCATION` or `ACCESS_COARSE_LOCATION`; this reconstructed project adds them so hospital search can actually work.

The original APK included direct Gemini client configuration for report analysis. This reconstruction keeps the same feature but reads the key from `local.properties`; the included Worker demonstrates the safer server-side pattern for AI chat.

The exact original source formatting, comments, variable names, and some UI dimensions cannot be recovered from compiled APK bytecode. The navigation routes, screen wording, data models, Firestore collection names, service roles, report pipeline, health metrics, and alarm behavior were reconstructed from the APK's DEX/resources/manifest.
