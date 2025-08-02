# Purnima Vedic Astrology Library

A comprehensive Vedic Astrology library for Java that provides asthakoot (eight-fold compatibility), panchang (five elements), and chart generation capabilities using **mathematical astronomical calculations** for accurate planetary positions. This library is designed to be consumed by anyone who needs precise Vedic astrology calculations in their applications.

## 🌟 Key Features

### **Accurate Astronomical Calculations**
- **Mathematical Formulas**: Uses proven astronomical formulas for precise planetary positions
- **Real Astronomical Data**: Calculates actual planetary positions using mathematical algorithms
- **Accurate House Calculations**: Proper ascendant and house cusp calculations using Local Sidereal Time
- **Retrograde Detection**: Identifies when planets are in retrograde motion
- **Exaltation Status**: Determines planetary exaltation and debilitation
- **Julian Day Calculations**: Accurate date/time conversions for astronomical calculations

### **Core Functionality**
- **Asthakoot Calculator**: Eight-fold compatibility analysis for marriage matching
- **Panchang Calculator**: Five elements (Tithi, Vara, Nakshatra, Yoga, Karana) calculations
- **Chart Generator**: Birth charts, compatibility charts, transit charts, and more
- **Multiple Output Formats**: Text, JSON, XML, HTML, and CSV formats

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
- **Tithi**: Lunar day calculation
- **Vara**: Weekday and ruling planet
- **Nakshatra**: Lunar mansion with ruling planet
- **Yoga**: Solar-lunar combination
- **Karana**: Half of tithi
- **Muhurta**: Auspicious timings (Brahma, Abhijit, Godhuli, Rahu Kaal, etc.)

### 🗺️ Chart Generation
- Birth charts (Janma Kundali) with accurate planetary positions
- Compatibility charts
- Transit charts
- Horoscope charts (daily, weekly, monthly, yearly)
- Divisional charts (Vargas)
- Planetary position charts
- Dasha charts (Vimshottari, Ashtottari, Yogini, Kalachakra)

## Project Structure

```
purnima/
├── pom.xml                          # Maven configuration with mathematical calculation dependencies
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── purnima/
│   │   │               ├── PurnimaAstrology.java     # Main entry point
│   │   │               ├── api/                      # Core interfaces
│   │   │               │   ├── AsthakootCalculator.java
│   │   │               │   ├── PanchangCalculator.java
│   │   │               │   └── ChartGenerator.java
│   │   │               ├── model/                    # Data models
│   │   │               │   ├── BirthData.java
│   │   │               │   ├── Planet.java
│   │   │               │   ├── Rashi.java
│   │   │               │   ├── AsthakootResult.java
│   │   │               │   ├── PanchangResult.java
│   │   │               │   └── ChartResult.java
│   │   │               ├── service/                  # Implementations
│   │   │               │   ├── DefaultAsthakootCalculator.java
│   │   │               │   ├── DefaultPanchangCalculator.java
│   │   │               │   ├── DefaultChartGenerator.java
│   │   │               │   └── AccurateChartGenerator.java  # Mathematical calculations
│   │   │               └── util/                     # Utilities
│   │   │                   └── SwissEphCalculator.java      # Astronomical calculations
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── purnima/
│                       └── AppTest.java
├── target/                          # Build output directory
└── README.md                        # This file
```

## Prerequisites

- Java 24 or higher
- Maven 3.6 or higher

## Dependencies

The library includes the following dependencies:
- **ThreeTen Extra**: Advanced date/time handling
- **Jackson**: JSON processing
- **Apache Commons Math**: Mathematical calculations
- **SLF4J**: Logging framework
- **JUnit & Mockito**: Testing framework

## Quick Start

### 1. Build the Project

```bash
mvn clean package
```

This creates two JAR files:
- `purnima-1.0.0.jar` - Standard JAR file
- `purnima-1.0.0-jar-with-dependencies.jar` - Executable JAR with all dependencies

### 2. Basic Usage

```java
import com.example.purnima.PurnimaAstrology;
import com.example.purnima.model.BirthData;
import java.time.LocalDateTime;

// Create the main astrology calculator (uses mathematical calculations by default)
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

### 3. Command Line Usage

```bash
# Calculate Panchang
java -jar target/purnima-1.0.0-jar-with-dependencies.jar panchang 2024-01-15 19.0760 72.8777 Mumbai

# Generate Birth Chart (with mathematical calculations)
java -jar target/purnima-1.0.0-jar-with-dependencies.jar chart 1990-05-15T14:30:00 19.0760 72.8777 Mumbai

# Generate Chart in JSON format
java -jar target/purnima-1.0.0-jar-with-dependencies.jar json 1990-05-15T14:30:00 19.0760 72.8777 Mumbai

# Generate Chart in HTML format
java -jar target/purnima-1.0.0-jar-with-dependencies.jar html 1990-05-15T14:30:00 19.0760 72.8777 Mumbai
```

## API Examples

### Accurate Birth Chart Generation

```java
// Create birth data
BirthData birthData = PurnimaAstrology.createBirthData(
    LocalDateTime.of(1990, 5, 15, 14, 30), 19.0760, 72.8777, "Mumbai"
);

// Generate accurate birth chart using mathematical calculations
ChartResult birthChart = astrology.generateBirthChart(birthData);

// Get detailed information including retrograde status and exaltation
String details = astrology.getBirthChartDetails(birthData);
System.out.println(details);

// Generate in different formats
String jsonChart = astrology.generateChartInFormat(birthData, ChartFormat.JSON);
String htmlChart = astrology.generateChartInFormat(birthData, ChartFormat.HTML);
```

### Asthakoot Compatibility

```java
// Create birth data for two individuals
BirthData maleData = PurnimaAstrology.createBirthData(
    LocalDateTime.of(1985, 3, 15, 10, 30), 19.0760, 72.8777, "Mumbai"
);
BirthData femaleData = PurnimaAstrology.createBirthData(
    LocalDateTime.of(1988, 7, 22, 16, 45), 28.6139, 77.2090, "Delhi"
);

// Calculate compatibility
AsthakootResult result = astrology.calculateAsthakoot(maleData, femaleData);

// Get detailed analysis
String analysis = astrology.getAsthakootAnalysis(maleData, femaleData);
System.out.println(analysis);
```

### Panchang Calculation

```java
// Calculate Panchang for a specific date and location
PanchangResult panchang = astrology.calculatePanchang(
    LocalDate.of(2024, 1, 15), 19.0760, 72.8777, "Mumbai"
);

// Get summary
String summary = astrology.getPanchangSummary(
    LocalDate.of(2024, 1, 15), 19.0760, 72.8777, "Mumbai"
);
System.out.println(summary);

// Get detailed information
String details = astrology.getPanchangDetails(
    LocalDate.of(2024, 1, 15), 19.0760, 72.8777, "Mumbai"
);
System.out.println(details);
```

## Advanced Features

### Choosing Calculation Accuracy

```java
// Use accurate calculations with mathematical formulas (default)
PurnimaAstrology accurateAstrology = new PurnimaAstrology(true);

// Use simplified calculations (for testing or when mathematical calculations are unavailable)
PurnimaAstrology simpleAstrology = new PurnimaAstrology(false);
```

### Custom Implementations

```java
// Create custom implementations
AsthakootCalculator customAsthakoot = new CustomAsthakootCalculator();
PanchangCalculator customPanchang = new CustomPanchangCalculator();
ChartGenerator customChart = new CustomChartGenerator();

// Use custom implementations
PurnimaAstrology customAstrology = new PurnimaAstrology(
    customAsthakoot, customPanchang, customChart
);
```

### Direct Astronomical Calculations

```java
import com.example.purnima.util.SwissEphCalculator;

// Calculate planetary position directly
SwissEphCalculator.PlanetaryPosition sunPos = SwissEphCalculator.calculatePlanetPosition(
    LocalDateTime.now(), 19.0760, 72.8777, "Sun"
);

// Calculate ascendant
double ascendant = SwissEphCalculator.calculateAscendant(
    LocalDateTime.now(), 19.0760, 72.8777
);

// Calculate lunar phase (Tithi)
SwissEphCalculator.LunarPhase lunarPhase = SwissEphCalculator.calculateLunarPhase(
    LocalDateTime.now(), 19.0760, 72.8777
);
```

## Output Formats

The library supports multiple output formats with accurate data:

### Text Format (with mathematical calculation data)
```
Birth Chart Summary (Mathematical Calculations)
===============================================

Name: Mumbai
Date & Time: 1990-05-15T14:30
Location: 19.0760, 72.8777

ASCENDANT (LAGNA)
Rashi: Cancer (कर्क)
Degree: 15.25°

PLANETARY POSITIONS
==================
Sun: Aries 12.50° (House 1, Retrograde: No, Exaltation: Neutral)
Moon: Taurus 8.75° (House 2, Retrograde: No, Exaltation: Neutral)
Mars: Gemini 22.30° (House 3, Retrograde: No, Exaltation: Neutral)
...
```

### JSON Format (with mathematical calculation data)
```json
{
  "birthData": {
    "dateTime": "1990-05-15T14:30:00",
    "latitude": 19.076,
    "longitude": 72.8777,
    "placeName": "Mumbai"
  },
  "ascendant": "Cancer",
  "planets": [
    {
      "planet": "Sun",
      "rashi": "Aries",
      "degree": 12.5,
      "house": 1,
      "retrograde": false,
      "exaltation": "Neutral"
    }
  ]
}
```

### HTML Format
Generates a complete HTML page with styled tables, including retrograde status and exaltation information.

### XML Format
Generates structured XML data for integration with other systems.

### CSV Format
Generates comma-separated values for spreadsheet applications.

## Mathematical Calculation Features

### Accurate Planetary Positions
- **Real Astronomical Data**: Uses proven mathematical formulas for planetary positions
- **Retrograde Detection**: Identifies when planets appear to move backwards
- **Exaltation Status**: Determines planetary strength based on position
- **House Calculations**: Accurate ascendant and house cusp calculations using Local Sidereal Time

### Supported Planets
- Sun, Moon, Mercury, Venus, Mars, Jupiter, Saturn
- Rahu (North Node) and Ketu (South Node)

### Calculation Methods
- **Planetary Positions**: Uses astronomical formulas based on J2000 epoch
- **House Calculations**: Placidus house system using Local Sidereal Time
- **Lunar Phase**: Calculated from Sun-Moon longitude difference
- **Nakshatra**: Calculated from Moon's longitude
- **Julian Day**: Accurate date/time conversions for astronomical calculations

### Mathematical Formulas Used
- **Sun Position**: Mean longitude calculation with corrections
- **Moon Position**: Mean longitude with lunar corrections
- **Planetary Positions**: Mean longitude calculations for each planet
- **Ascendant**: Using Local Sidereal Time and latitude
- **House Cusps**: Simplified Placidus system

## Contributing

This library provides a solid foundation for Vedic astrology calculations with mathematical astronomical formulas. For production use, you may want to:

1. **Enhance Panchang Calculations**: Implement more accurate Tithi, Nakshatra calculations
2. **Improve Asthakoot Algorithms**: Add more sophisticated compatibility algorithms
3. **Add More Chart Types**: Implement additional divisional charts and special charts
4. **Enhance Output Formats**: Add more visualization options
5. **Add More Mathematical Formulas**: Include more precise planetary position calculations

## License

This project is open source and available under the [MIT License](LICENSE).

## Disclaimer

This library is provided for educational and research purposes. The astrological calculations are based on traditional Vedic astrology principles and use mathematical formulas for astronomical data. Users should exercise their own judgment and consult qualified astrologers for important life decisions.

## Support

For questions, issues, or contributions, please refer to the project documentation or create an issue in the repository.
