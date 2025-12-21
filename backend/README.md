# PhysicsHub Translation API

Spring Boot (Kotlin) backend for managing translations.

## Requirements

- Java 17+
- Gradle 8.5+

## Run Locally

```bash
cd backend
./gradlew bootRun
```

Server will start at `http://localhost:8080`

## API Endpoints

### Get All Translations
```
GET /api/translations
```

Response:
```json
{
  "en": { "hello": "Hello,", ... },
  "vn": { "hello": "Xin chào,", ... }
}
```

### Get Translations by Language
```
GET /api/translations/en
GET /api/translations/vn
```

### Update All Translations
```
PUT /api/translations
Content-Type: application/json

{
  "en": { ... },
  "vn": { ... }
}
```

### Update Single Language
```
PUT /api/translations/en
Content-Type: application/json

{
  "hello": "Hello,",
  "noticeBoard": "NOTICE BOARD",
  ...
}
```

### Update Single Translation
```
PATCH /api/translations
Content-Type: application/json

{
  "language": "en",
  "key": "hello",
  "value": "Hi there,"
}
```

### Health Check
```
GET /api/health
```

## Deploy

### Railway
1. Push to GitHub
2. Connect to Railway
3. Deploy

### Render
1. Push to GitHub
2. Create Web Service on Render
3. Set build command: `./gradlew build`
4. Set start command: `java -jar build/libs/physicshub-backend-1.0.0.jar`

### Docker
```dockerfile
FROM eclipse-temurin:17-jdk-alpine
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Update Android App

After deploying, update the URL in your Android app:

`TranslationRepository.kt`:
```kotlin
private const val TRANSLATIONS_URL = "https://your-deployed-url.com/api/translations"
```
