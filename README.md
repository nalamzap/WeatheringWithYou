# WeatherApp

## Project Overview
**WeatherWithYou** is a modern Android application built with Kotlin and Jetpack Compose.  
It allows users to view real-time weather updates for their preferred city, even when offline.  
The app fetches weather data from the OpenWeatherMap API, caches it locally, and provides periodic notifications with the latest updates.

---

## Features
- **User Preferences:**  
  Prompt for preferred city on first launch and allow updates later.

- **Real-Time Weather:**  
  Fetches current temperature, weather condition, and timestamp.

- **Offline Access:**  
  Stores weather data locally using Room database for seamless offline experience.

- **Periodic Updates:**  
  Automatically refreshes weather data every 6 hours using WorkManager and sends a notification.

- **Change City Anytime:**  
  Easily update your preferred city and fetch new weather data immediately.

- **Modern Android Stack:**  
  - Jetpack Compose for UI  
  - Retrofit for API calls  
  - Room for local caching  
  - WorkManager for background updates  
  - SharedPreferences for settings

---

## Setup and Build Instructions

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/nalamzap/WeatheringWithYou.git

2. **Get API key from weatherapi.com**
   Go to weatherapi.com > sign up and get an API key for free
   
4. **Add the API key to the project** 
   In the project `gradle.properties` directory, replace value for `WEATHER_API_KEY` with the key found on weatherapi.com

5. **(Re)build/sync gradle.**
   
6. **You're all set! Now the Project is ready to be build/run**


