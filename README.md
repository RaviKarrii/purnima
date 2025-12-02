# Purnima Vedic Astrology Library

A comprehensive Vedic Astrology library for Java that provides asthakoot (eight-fold compatibility), panchang (five elements), and chart generation capabilities using **accurate Swiss Ephemeris calculations** for precise planetary positions. This library is designed to be consumed by anyone who needs precise Vedic astrology calculations in their applications.

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
- Dasha charts (Vimshottari).

## Project Structure

```
purnima/
├── pom.xml                          # Maven configuration
├── Dockerfile                       # Docker configuration
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/purnima/
│   │   │       ├── PurnimaAstrology.java     # Main entry point
│   │   │       ├── api/                      # Core interfaces
│   │   │       ├── model/                    # Data models
│   │   │       ├── service/                  # Implementations (Accurate & Default)
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
- **Spring Boot**: For dependency injection and i18n support.
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
docker run -it purnima-astrology
```

### 3. Basic Usage (Java)

```java
import com.example.purnima.PurnimaAstrology;
import com.example.purnima.model.BirthData;
import java.time.LocalDateTime;

// Create the main astrology calculator (uses accurate Swiss Ephemeris calculations by default)
PurnimaAstrology astrology = new PurnimaAstrology();

// Create birth data
BirthData birthData = PurnimaAstrology.createBirthData(
    LocalDateTime.of(1990, 5, 15, 14, 30), // Date and time
    19.0760, 72.8777, "Mumbai"             // Latitude, longitude, place
);

// Generate birth chart with accurate planetary positions
String chartSummary = astrology.getBirthChartSummary(birthData);
System.out.println(chartSummary);
```

### 4. Command Line Usage

You can run the JAR file directly to perform calculations.

```bash
# Calculate Panchang (optional timezone)
java -jar target/purnima-1.0.0-jar-with-dependencies.jar panchang 2024-01-15 19.0760 72.8777 Mumbai [Asia/Kolkata]

# Generate Birth Chart
java -jar target/purnima-1.0.0-jar-with-dependencies.jar chart 1990-05-15T14:30:00 19.0760 72.8777 Mumbai

# Generate Chart in JSON format
java -jar target/purnima-1.0.0-jar-with-dependencies.jar json 1990-05-15T14:30:00 19.0760 72.8777 Mumbai

# Generate Chart in HTML format
java -jar target/purnima-1.0.0-jar-with-dependencies.jar html 1990-05-15T14:30:00 19.0760 72.8777 Mumbai
```

## API Documentation

The library exposes a REST-like API structure (though currently implemented as a library, it's structured for easy API adaptation).

### Endpoints

#### 1. Get Panchang
Calculate Tithi, Vara, Nakshatra, Yoga, Karana.
- **Method**: `GET /panchang` (Conceptual)
- **Parameters**: `date`, `latitude`, `longitude`, `placeName`, `timezone` (optional)
- **Output**: JSON with Panchang elements and end times.

#### 2. Get Birth Chart
Generate a Vedic birth chart.
- **Method**: `GET /chart` (Conceptual)
- **Parameters**: `birthTime`, `latitude`, `longitude`, `placeName`
- **Output**: JSON with planetary positions, ascendant, and house details.

#### 3. Check Compatibility
Calculate Asthakoot compatibility.
- **Method**: `POST /compatibility` (Conceptual)
- **Parameters**: Male and Female birth data.
- **Output**: JSON with compatibility score and breakdown.

### Internationalization Usage

To get localized output, you can set the `Locale` in your Java code or pass the `Accept-Language` header if wrapping this in a REST API.

**Example (Java):**
```java
LocaleContextHolder.setLocale(new Locale("hi")); // Set to Hindi
String chartJson = astrology.generateChartInFormat(birthData, ChartFormat.JSON);
// Output will contain Hindi names for Planets and Rashis
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
