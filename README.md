# Security Camera
### Mobile application 
[English version](#english-version)

## Główna idea projektu
Celem projektu jest wykorzystanie starego, nieużywanego telefonu jako inteligentnej kamery bezpieczeństwa, która:
- Powiadamia wybranych użytkowników, gdy zostanie wykryty człowiek.
- Robi zdjęcie potencjalnemu intruzowi, które w każdej można przejrzeć w galerii na innym telefonie.

### Przykładowe zastosowanie
1. Ustawiasz telefon tak, aby obserwował podwórko i wyjeżdżasz do pracy.
2. Podczas Twojej nieobecności, jeśli ktoś pojawi się na podwórku:
   - Aplikacja powiadomi Cię o tym zdarzeniu.
   - Zrobi zdjęcie, dzięki czemu możesz sprawdzić, czy to listonosz czy też podejrzana osoba.  
   *(Możliwe problemy z ochroną prywatności listonosza)*

---

## Dodatkowe funkcjonalności
- **Bezpieczeństwo konta użytkownika**:  
  - Aplikacja wymaga założenia konta podczas pierwszego użycia.  
  - Logowanie jest wymagane przy każdym kolejnym dostępie.

- **Monitorowanie pozycji telefonu**:  
  - Możliwość aktywacji opcji monitorowania położenia urządzenia podczas korzystania z kamery.  
  - W przypadku zmiany lokalizacji telefonu, wybrani użytkownicy otrzymają powiadomienie.  
  - Funkcja ma na celu zapobieganie nieautoryzowanemu demontażowi urządzenia.

- **Wykres przestępczości**:  
  - Aplikacja generuje wykres dla podanej lokalizacji w Wielkiej Brytanii (z wykorzystaniem darmowego API).  
  - Wykres przedstawia zgłoszone przestępstwa dla wybranego miesiąca.

---

## Wykorzystane technologie
- **Firebase**:  
  - Authentication -> logowanie
  - Firestore -> przechowywanie danych o użytkownikach 
  - Storage -> przetrzymywanie zdjęć
  - FCM (Firebase Cloud Messaging) -> zarządzanie powiadomieniami

- **Android Jetpack Compose**:  
  - Tworzenie UI w sposób rekomendowany przez Google.  
  - Aplikacja napisana w architekturze MVVM.

- **CameraX i MLKit**:  
  - Obsługa kamery oraz wykrywanie człowieka.

- **Retrofit, Moshi i Room database**:  
  - Pobieranie danych z API.  
  - Przekształcanie JSON do obiektów Kotlina.  
  - Zapisywanie danych do lokalnej bazy danych.

- **WorkManager**:  
  - Wykonywanie zadań w tle (np. wysyłanie zdjęć na serwer po uzyskaniu dostępu do internetu).

---

## Co się nie udało zrobić
- Początkowy zamysł zakładał wygenerowanie wykresu na podstawie lokalizacji użytkownika uzyskanej z GPS.  
  Niestety, nie udało się znaleźć API dostarczającego dane o przestępczości w Polsce.

---

## Ścieżki rozwoju
- Dodanie wykresu przedstawiającego dni i godziny, w których zostaje wykryty człowiek.

---

## Prezentacja działania
### Uwierzytelnianie
<img width="800" alt="Uwierzytelnianie" src="https://github.com/user-attachments/assets/8ed10fb2-388e-44db-8ba7-9efbd84e9ecb" />

  - Walidacja poprawności emaila, długości hasła
  - Możliwość zresetowania hasła

### Strona główna & Kamera bezpieczeństwa & Galeria
  <img width="800" alt="glowna" src="https://github.com/user-attachments/assets/5d1629e6-0a4b-4e7b-979d-c71b7b7c45fc" />

  - Aplikacja na bieżąco analizuje klatki z kamery i przy pomocy ML Kit Pose detection wykrywa sylwetkę ludzką
  - Po wykryciu robi zdjęcie, zapisuje je w podręcznej pamięci telefonu i gdy jest internet, działając w tle, wysyła je do Firebase
  - Po wysłaniu czyści pamięć podręczną telefonu
  - Zdjęcia w galerii pobierane są z Firebase Storage w czasie rzeczywistym 

### Wykres & Ustawienia powiadomień
  <img width="800" alt="wykres" src="https://github.com/user-attachments/assets/b09957b2-1085-4777-844f-d95599daaa5a" />
 
  - **Wykres**
  - Po wybraniu przez użytkowanika daty oraz wpisaniu lokalizacji, aplikacja wysyła zapytanie do Google Geolocation API aby uzyskać informację na temat szerokości geograficznej wybranego miejsca
  - Po przekształceniu nazwy lokalizacji na szerokość geograficzną, aplikacja wykonuje kolejne zapytanie, tym razem do data.police.uk w celu zdobycia danych potrzebnych do wykresu
  - Wszystkie dane pobrane z obu API zapisywane są w lokalnej bazie danych aby w przypadku ponownej próby tworzenia wykresu dla tych samych danych wejściowych nie trzeba było wykonywać od nowa zapytań do API
  -  **Ustawienia powiadomień**
  -  Możliwość spersonalizowania do kogo mają być wysyłane powiadomienia
  -  Włączenie/wyłączenie powiadomień w przypadku wykrycia ruchu telefonu podczas działania kamery
  
 

---

<a id="english-version"></a>
# English Version

## Project Overview
The main idea of the project is to repurpose an old, unused phone as an intelligent security camera that:
- Notifies selected users when a person is detected.
- Takes a photo of potential intruders, which can be viewed in a gallery at any time.

### Example Use Case
1. You position the phone to observe your backyard and go to work.
2. During your absence:
   - The app notifies you if someone appears in the yard.
   - It takes a photo so you can check whether it's the mailman or a suspicious person.  
   *(Potential privacy concerns for the mailman)*

---

## Additional Functionalities
- **Account Security**:  
  - Users are required to create an account during first use.  
  - Logging in is mandatory for each subsequent access.

- **Phone Position Monitoring**:  
  - An option can be activated to monitor the phone's position while using the camera.  
  - If a change in device location is detected, selected users will receive a notification.  
  - This aims to prevent unauthorized removal of the device.

- **Crime Graph**:  
  - The app generates a graph for a given location in the UK (using free API).  
  - The graph shows reported crimes for a specific month.

---

## Technologies Used
- **Firebase**:  
  - Authentication -> user login  
  - Firestore -> storing user data  
  - Storage -> storing photos  
  - FCM (Firebase Cloud Messaging) -> managing notifications
    
- **Android Jetpack Compose**:  
  - Creating UI in the Google-recommended way.  
  - App written in MVVM architecture.

- **CameraX and MLKit**:  
  - Camera handling and human detection.

- **Retrofit, Moshi and Room Database**:  
  - Fetching data from API.  
  - Converting JSON to Kotlin objects.  
  - Saving data to local database.

- **WorkManager**:  
  Performing background tasks (e.g., uploading photos to the server when internet is available).

---

## What Couldn't Be Implemented
- The initial idea was to generate a graph based on the user's location obtained from GPS. However, I couldn't find an API providing crime information for Poland.

---

## Development Paths
- Adding a graph showing on which days and hours a person is detected.

---

## Functionality Presentation
### Authentication
<img width="800" alt="Authentication" src="https://github.com/user-attachments/assets/a6d6481c-394f-43c7-a735-d59ae19d1192" />

  - Validation of email correctness and password length
  - Password reset option

### Home Page & Security Camera & Gallery
  <img width="800" alt="home" src="https://github.com/user-attachments/assets/14b266a2-707c-4150-95f6-fa2baba8855d" />

  - The application continuously analyzes camera frames and detects human silhouettes using ML Kit Pose detection
  - Upon detection, it takes a photo, saves it in the phone's cache memory, and when internet is available, sends it to Firebase in the background
  - After sending, it clears the phone's cache memory
  - Photos in the gallery are retrieved from Firebase Storage in real-time

### Chart & Notification Settings
  <img width="800" alt="chart" src="https://github.com/user-attachments/assets/435f6fb3-9fe2-4f98-9446-0a3b209a41f1" />

  - **Chart**
  - After the user selects a date and enters a location, the application sends a request to Google Geolocation API to obtain information about the latitude of the selected place
  - After converting the location name to latitude, the application makes another request, this time to data.police.uk to obtain data needed for the chart
  - All data retrieved from both APIs is stored in a local database so that in case of repeated attempts to create a chart for the same input data, API requests don't need to be made again
  -  **Notification Settings**
  -  Ability to personalize to whom notifications should be sent
  -  Enable/disable notifications in case of phone movement detection during camera operation
  
---
