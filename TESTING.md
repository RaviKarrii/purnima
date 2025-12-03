# API Testing Examples

This document provides `curl` commands to test the Purnima Astrology API endpoints.

## Prerequisites

Ensure the application is running locally on port 8080.

```bash
mvn spring-boot:run
```

## 1. Panchang API

Calculate Panchang (Tithi, Vara, Nakshatra, Yoga, Karana) for a specific date and location.

**Request:**
```bash
curl "http://localhost:8080/api/panchang?date=2024-01-15&latitude=19.0760&longitude=72.8777&placeName=Mumbai&timezone=Asia/Kolkata"
```

**Expected Response (Snippet):**
```json
{
  "tithi": { "name": "Shukla Panchami", "number": 5, "endTime": "23:45" },
  "vara": "Monday",
  "nakshatra": { "name": "Purva Bhadrapada", "number": 25, "endTime": "08:12" },
  ...
}
```

## 2. Birth Chart API

Generate a Vedic birth chart with planetary positions.

**Request:**
```bash
curl "http://localhost:8080/api/chart?birthTime=1990-05-15T14:30:00&latitude=19.0760&longitude=72.8777&placeName=Mumbai"
```

**Expected Response (Snippet):**
```json
{
  "ascendant": "Virgo",
  "planets": {
    "Sun": { "name": "Sun", "longitude": 30.12, "rashi": "Taurus", ... },
    "Moon": { "name": "Moon", "longitude": 280.45, "rashi": "Capricorn", ... },
    ...
  }
}
```

## 3. Vimshottari Dasa API

Calculate Vimshottari Dasa periods (Mahadasa, Antardasa, Pratyantardasa).

**Request:**
```bash
curl "http://localhost:8080/api/dasa/vimshottari?birthTime=1990-05-15T14:30:00&latitude=19.0760&longitude=72.8777&placeName=Mumbai&zoneId=Asia/Kolkata"
```

**Expected Response (Snippet):**
```json
[
  {
    "planet": "Sun",
    "startDate": "1990-05-15T14:30:00",
    "endDate": "1993-02-20T05:45:00",
    "level": 1,
    "subPeriods": [...]
  },
  ...
]
```

## 4. Current Dasa API

Get the currently active Dasa period hierarchy.

**Request:**
```bash
curl "http://localhost:8080/api/dasa/current?birthTime=1990-05-15T14:30:00&latitude=19.0760&longitude=72.8777&placeName=Mumbai&zoneId=Asia/Kolkata"
```

**Expected Response (Snippet):**
```json
{
  "planet": "Rahu",
  "startDate": "2010-02-20T00:00",
  "endDate": "2028-02-20T00:00",
  "level": 1,
  "subPeriods": [
    {
      "planet": "Jupiter",
      "level": 2,
      ...
    }
  ]
}
```

## 5. Muhurta API

Calculate Choghadiya, Hora, and inauspicious times.

**Request:**
```bash
curl "http://localhost:8080/api/muhurta/calculate?date=2024-01-15&latitude=19.0760&longitude=72.8777&zoneId=Asia/Kolkata"
```

**Expected Response (Snippet):**
```json
{
  "dayChoghadiya": [
    { "name": "Amrit", "nature": "GOOD", "startTime": "07:15", "endTime": "08:38" },
    ...
  ],
  "rahuKalam": { "startTime": "08:38", "endTime": "10:01" },
  ...
}
```

## 6. Compatibility API (Asthakoot)

Check compatibility between two birth charts.

**Request:**
```bash
curl -X POST "http://localhost:8080/api/compatibility" \
     -H "Content-Type: application/json" \
     -d '{
           "maleBirthData": {
             "birthDateTime": "1990-05-15T14:30:00",
             "latitude": 19.0760,
             "longitude": 72.8777,
             "placeName": "Mumbai"
           },
           "femaleBirthData": {
             "birthDateTime": "1992-08-20T10:15:00",
             "latitude": 28.6139,
             "longitude": 77.2090,
             "placeName": "New Delhi"
           }
         }'
```

**Expected Response (Snippet):**
```json
{
  "totalPoints": 24.5,
  "areas": {
    "Varna": { "score": 1.0, "maxScore": 1.0, "description": "..." },
    ...
  }
}
```
