# Chroma News

## Description
We read, watch, discuss and hear a lot of information in the form of news and that information impacts our mood, our emotional state. This application is an attempt to give you a chromatic representation of the impact on the information acquired through news reading habits. Are you in a state of cool / happy blue or angry / sad red? Or somewhere in between? An insight into what you tend to read and how it impacts your emotional state. 

## Installation Instructions
- Create Virtual Device via Device Manager with following values:
    - Pixel 4 or 5
    - API 32
    - Google Play Service enabled
    - Location permission enabled
- Google-service.json is added to project repo for accessing Google MAP API services
- SHA1 key is generated and added to Google Credentials. If a new signing report key is generated, a new SHA1 key needs to be added to your google console to load Google map in application
- The keys required to make the API calls are hardcoded into the application
- Click on “Run App” from the Run menu in Android Studio to build and run the  application.

## ThirdParty APIs
- NewsAPI (newsapi.org)
- ACLED API 
- Twinword Sentiment Analysis API

## Systems
- Android SDK
- Glide
- Retrofit
- Firebase
- Firestore
- GoogleMaps