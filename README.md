### **README.md**  

#### **Music Player App - Jetpack Compose UI**  
This is a modern, professional **Music Player App UI** built using **Jetpack Compose**. The app currently includes:  
- **Login and Registration Screens**  
- **Modern UI Design with Dark Theme & Gradients**  
- **Navigation between Screens**  
- **A Welcome Page for the Music Player**  

Firebase integration for authentication and data storage will be added later.  

---

## **🛠️ Tech Stack**
- **Language:** Kotlin  
- **Framework:** Jetpack Compose  
- **Architecture:** MVVM (to be implemented)  
- **Navigation:** Jetpack Navigation  

---

## **📂 Project Structure**
```
MusicPlayerApp/
│── app/
│   ├── src/main/java/com/example/musicplayerapp/
│   │   ├── ui/theme/screens/  
│   │   │   ├── LoginScreen.kt  
│   │   │   ├── RegisterScreen.kt  
│   │   │   ├── MusicScreen.kt  
│   │   │   ├── WelcomeScreen.kt  
│   ├── src/main/res/  
│   │   ├── drawable/ (for icons & images)  
│   │   ├── values/ (colors.xml, themes.xml)  
│── README.md  
│── build.gradle.kts  
│── settings.gradle.kts  
```
---

## **💻 How to Import & Run the Project**

### **For Windows (Android Studio)**
1. Open **Android Studio**  
2. Click **"Open"** and select the project folder  
3. Wait for **Gradle Sync** to complete  
4. Run the project using **Shift + F10** or click ▶️  

### **For Mac (Android Studio)**
1. Open **Android Studio**  
2. Click **File → Open…** and select the project folder  
3. Wait for **Gradle Sync** to complete  
4. Run the project using **⌘ + R** or click ▶️  

---

## **🔧 Common Issues & Fixes**
1. **Gradle Sync Issues?**  
   - Run: `File → Invalidate Caches & Restart`  
   - Ensure **Java 17+** is installed  

2. **Jetpack Compose Preview Not Working?**  
   - Switch to **"Design" mode** in the preview tab  
   - Click **"Refresh"** 🔄  

3. **App Crashes on Launch?**  
   - Check **Logcat** (`View → Tool Windows → Logcat`)  
   - Fix any missing imports or dependencies  

---

## **🚀 Future Updates**
✔ Firebase Authentication (Login & Signup)  
✔ Music Playback Integration  
✔ Save & Load Playlists  


---
