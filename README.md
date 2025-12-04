# Purnima Vedic Astrology Library

A comprehensive Vedic Astrology library for Java that provides asthakoot (eight-fold compatibility), panchang (five elements), chart generation, dasa (planetary periods), and muhurta (auspicious timings) capabilities using **accurate Swiss Ephemeris calculations** for precise planetary positions. This library is designed to be consumed by anyone who needs precise Vedic astrology calculations in their applications.

## 🌟 Key Features

### **Accurate Astronomical Calculations**
- **Swiss Ephemeris Integration**: Uses the industry-standard Swiss Ephemeris for high-precision planetary positions.
- **Real Astronomical Data**: Calculates actual planetary positions, not approximations.
- **Accurate House Calculations**: Proper ascendant and house cusp calculations using Local Sidereal Time.
- **Retrograde Detection**: Identifies when planets are in retrograde motion.
- **Exaltation Status**: Determines planetary exaltation, debilitation, own sign, etc.
- **Julian Day Calculations**: Accurate date/time conversions for astronomical calculations.

### **Core Functionality**
- **Asthakoot Calculator**: Eight-fold compatibility analysis for marriage matching.
- **Panchang Calculator**: Five elements (Tithi, Vara, Nakshatra, Yoga, Karana) calculations with accurate end times.
- **Chart Generator**: Birth charts, compatibility charts, transit charts, and more.
- **Dasa Calculator**: Vimshottari Dasa calculations (Mahadasa, Antardasa, Pratyantardasa).
- **Muhurta Calculator**: Choghadiya, Hora, Rahu Kalam, Yamagandam, Gulika Kalam.
- **Multiple Output Formats**: Text, JSON, XML, HTML, and CSV formats.
- **Internationalization (i18n)**: Support for multiple languages.

### 🌍 Internationalization (i18n)
The library supports localized output for astrological terms (Planets, Rashis, Panchang elements).
**Supported Languages:**
*   English (`en`) - Default
*   Hindi (`hi`)
*   Telugu (`te`)
*   Sanskrit (`sa`)
*   Tamil (`ta`)
*   Kannada (`kn`)

### 📊 Asthakoot (Eight-Fold Compatibility)
- Varna Koota (Caste compatibility)
- Vashya Koota (Control compatibility)
- Tara Koota (Star compatibility)
- Yoni Koota (Sexual compatibility)
- Graha Maitri Koota (Planetary friendship)
- Gana Koota (Temperament compatibility)
- Bhakoot Koota (Rashi compatibility)
- Nadi Koota (Health compatibility)

### 📅 Panchang (Five Elements)
- **Tithi**: Lunar day calculation with end time.
- **Vara**: Weekday and ruling planet.
- **Nakshatra**: Lunar mansion with ruling planet and end time.
- **Yoga**: Solar-lunar combination with end time.
- **Karana**: Half of tithi with end time.
- **Muhurta**: Auspicious timings (Brahma, Abhijit, Godhuli, Rahu Kaal, etc.).

### 🗺️ Chart Generation
- Birth charts (Janma Kundali) with accurate planetary positions.
- Compatibility charts.
- Transit charts.
- Horoscope charts (daily, weekly, monthly, yearly).
- Divisional charts (Vargas).
- Planetary position charts.

### ⏳ Dasa (Planetary Periods)
- **Vimshottari Dasa**: 120-year cycle based on Moon's Nakshatra.
- **Mahadasa**: Major planetary periods.
- **Antardasa**: Sub-periods.
- **Pratyantardasa**: Sub-sub-periods.

### ⏰ Muhurta (Auspicious Timings)
- **Choghadiya**: Day and Night Choghadiya (Good/Bad/Neutral periods).
- **Hora**: Planetary hours.
- **Inauspicious Times**: Rahu Kalam, Yamagandam, Gulika Kalam.

## Project Structure

```
purnima/
├── pom.xml                          # Maven configuration
├── Dockerfile                       # Docker configuration
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/purnima/
│   │   │       ├── PurnimaAstrology.java     # Facade (Library usage)
│   │   │       ├── api/                      # Core interfaces
│   │   │       ├── model/                    # Data models
│   │   │       ├── service/                  # Implementations (Accurate & Default)
│   │   │       ├── controller/               # REST Controllers
│   │   │       └── util/                     # Utilities (SwissEph, TimeUtil)
│   │   └── resources/
│   │       ├── messages.properties           # English translations
│   │       ├── messages_hi.properties        # Hindi translations
│   │       ├── messages_te.properties        # Telugu translations
│   │       ├── messages_sa.properties        # Sanskrit translations
│   │       ├── messages_ta.properties        # Tamil translations
│   │       └── messages_kn.properties        # Kannada translations
└── README.md                        # This file
```

## Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

## Dependencies

The library includes the following dependencies:
- **Swiss Ephemeris (swisseph)**: For high-precision astronomical calculations.
- **Spring Boot**: For dependency injection, REST API, and i18n support.
- **ThreeTen Extra**: Advanced date/time handling.
- **Jackson**: JSON processing.
- **SLF4J**: Logging framework.
- **JUnit 5**: Testing framework.

## Quick Start

### 1. Build the Project

```bash
mvn clean package
```

This creates two JAR files in the `target` directory:
- `purnima-1.0.0.jar` - Standard JAR file
- `purnima-1.0.0-jar-with-dependencies.jar` - Executable JAR with all dependencies

### 2. Docker Support

You can build and run the application using Docker.

**Build the Docker Image:**
```bash
docker build -t purnima-astrology .
```

**Run the Docker Container:**
```bash
docker run -p 8080:8080 purnima-astrology
```

### 3. API Usage (REST)

The application exposes a REST API for all astrological calculations.

#### Endpoints

#### 1. Get Panchang
Calculate Tithi, Vara, Nakshatra, Yoga, Karana.
- **Method**: `GET /api/panchang`
- **Parameters**: `date`, `latitude`, `longitude`, `placeName`, `timezone` (optional)
- **Output**: JSON with Panchang elements and end times.

**1. Get Panchang**
- **URL**: `GET /api/panchang`
- **Parameters**: `date`, `latitude`, `longitude`, `placeName`, `timezone`

**2. Get Birth Chart**
- **URL**: `GET /api/chart`
- **Parameters**: `birthTime`, `latitude`, `longitude`, `placeName`

**3. Get Vimshottari Dasa**
- **URL**: `GET /api/dasa/vimshottari`
- **Parameters**: `birthTime`, `latitude`, `longitude`, `placeName`, `zoneId`
- **Example**: `GET /api/dasa/vimshottari?birthTime=1990-05-15T14:30:00&latitude=19.076&longitude=72.877&placeName=Mumbai`

**4. Get Current Dasa**
- **URL**: `GET /api/dasa/current`
- **Parameters**: `birthTime`, `latitude`, `longitude`, `placeName`, `zoneId`

**5. Get Muhurta**
- **URL**: `GET /api/muhurta/calculate`
- **Parameters**: `date`, `latitude`, `longitude`, `zoneId`
- `GET /api/muhurta/vehicle`: Find auspicious slots for vehicle purchase.
- `GET /api/muhurta/marriage`: Find auspicious slots for marriage.
- `GET /api/muhurta/griha-pravesh`: Find auspicious slots for house warming.
- `GET /api/muhurta/business`: Find auspicious slots for starting a new business.
- `GET /api/muhurta/namakarana`: Find auspicious slots for naming ceremony.
- `GET /api/muhurta/property`: Find auspicious slots for property purchase.

**Parameters:**
- `start`: Start date-time (ISO format, e.g., `2023-01-01T00:00:00`)
- `end`: End date-time (ISO format)
- `latitude`: Latitude of the location
- `longitude`: Longitude of the location
- `zoneId`: (Optional) Timezone ID (e.g., `Asia/Kolkata`)
- **Example**: `GET /api/muhurta/calculate?date=2024-01-15&latitude=19.076&longitude=72.877`

### 4. Library Usage (Java)

You can also use the library directly in your Java code.

```java
import com.example.purnima.PurnimaAstrology;
import com.example.purnima.model.BirthData;
import com.example.purnima.model.DasaResult;
import com.example.purnima.model.MuhurtaResult;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

// Create the main astrology calculator
PurnimaAstrology astrology = new PurnimaAstrology(true);

// 1. Dasa Calculation
BirthData birthData = PurnimaAstrology.createBirthData(
    LocalDateTime.of(1990, 5, 15, 14, 30),
    19.0760, 72.8777, "Mumbai"
);
List<DasaResult> dasas = astrology.calculateVimshottariDasa(birthData);

// 2. Muhurta Calculation
MuhurtaResult muhurta = astrology.calculateMuhurta(
    LocalDate.of(2024, 1, 15),
    19.0760, 72.8777, ZoneId.of("Asia/Kolkata")
);
```

## Implementation Details

### Swiss Ephemeris
The core of the astronomical calculations is powered by the **Swiss Ephemeris** library (via the `swisseph` Java wrapper). This ensures that planetary positions are calculated with high precision (up to 0.001 arcseconds).

### Time Formatting
Panchang end times are formatted as `HH:mm` strings for better readability. The `TimeUtil` class handles the conversion from decimal hours to this format.

### Localization
The project uses Spring's `MessageSource` for internationalization.
- **Resource Bundles**: `messages_*.properties` files contain translations.
- **Dynamic Switching**: The `AccurateChartGenerator` and `DefaultPanchangCalculator` dynamically fetch translations based on the current `Locale`.

## Building Tips

- **Maven Wrapper**: Use `./mvnw` (if available) or installed `mvn` to ensure consistent build environment.
- **Skip Tests**: If you want to build quickly without running tests, use `mvn clean package -DskipTests`.
- **Docker**: Ensure Docker is running before building the image.

## License

This project is open source and available under the [MIT License](LICENSE).

## Disclaimer

This library is provided for educational and research purposes. The astrological calculations are based on traditional Vedic astrology principles and use mathematical formulas for astronomical data. Users should exercise their own judgment and consult qualified astrologers for important life decisions.
