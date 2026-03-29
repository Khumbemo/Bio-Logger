# Bio Logger - Scientific Research Suite

Bio Logger is a high-fidelity, scientific data logging and analysis application designed for fieldwork in ecology, agriculture, and horticulture. It combines a sleek, futuristic UI with robust research-grade tools and calculations.

## 🚀 Overview

Bio Logger is a hybrid application built with modern web technologies and integrated with Android via a specialized bridge. It is designed to replace traditional field notebooks with an intelligent, real-time analytics suite.

### Core Modules

#### 🌲 Forest Capture (Ecology & Forestry)
- **Quadrat Sampling:** Log species binomials, DBH, and abundance.
- **Diversity Analytics:** Real-time calculation of **Shannon-Wiener (H')** and **Simpson's Diversity (D)** indices.
- **Importance Value Index (IVI):** Automatic generation of species dominance charts.
- **Composite Burn Index (CBI):** 3-strata severity scoring for post-fire ecology studies.
- **Belt Transect Sampling:** Linear species distribution logging.

#### 🧪 Agroclimatic Lab (Experimental Agriculture)
- **Germination Studies:** Track seed germination over time with **Mean Germination Time (MGT)** calculations and cumulative growth curves.
- **Growth Monitoring:** Systematic tracking of plant height and leaf count.
- **Treatment Comparison:** Visual bar charts comparing different experimental groups (e.g., control vs. fertilizer).
- **Environmental Logging:** Record CO₂, Lux, and Soil Moisture levels.

#### 🏡 Garden Scape (Precision Horticulture)
- **Crop Rotation Guide:** Standardized 4-year rotation planning (Legumes → Brassicas → Alliums → Solanaceous).
- **Yield Prediction:** Estimating harvest weights based on area and crop-specific constants.
- **Spacing Optimizer:** Maximizing plant density based on row and individual plant spacing.
- **Soil Profiling:** Tracking pH and soil texture.

## 📱 Platform Features

### Android Integration
The application features a deep bridge to Android OS, enabling:
- **GPS Telemetry:** Real-time coordinate and elevation tracking.
- **Hardware Access:** Camera integration for herbarium vouchers.
- **Voice-to-Text:** Neural capture of field notes via voice recognition (Android only).
- **Native Toasts:** System-level notifications.

### Data Management
- **Unified Archive:** Filter and search through all research records.
- **Excel Export:** Export entire datasets to XLSX format for further analysis in professional GIS or statistics software.

## 🛠️ Tech Stack

- **Frontend:** HTML5, CSS3 (Glassmorphism, Neon UI), Vanilla JavaScript (ES6+).
- **Visualization:** [Chart.js](https://www.chartjs.org/) for real-time scientific graphing.
- **Mapping:** [Leaflet.js](https://leafletjs.com/) for spatial waypoints.
- **Data Export:** [SheetJS](https://sheetjs.com/) for XLSX generation.
- **Mobile Wrapper:** Android WebView with Custom Java Bridge.

## 🏗️ Getting Started

### View in Web Browser
1. Navigate to `app/src/main/assets/www/index.html`.
2. For full functionality (e.g., mapping and telemetry simulation), serve it via a local server:
   ```bash
   python3 -m http.server 8080 --directory app/src/main/assets/www
   ```
3. Open `http://localhost:8080` in your browser.

### View in Android Studio
1. Open Android Studio.
2. Select **"Open an Existing Project"** and choose the root directory of this repository.
3. Sync Gradle and run the project on an emulator or physical device.

---
*Developed for research-grade biological logging.*
