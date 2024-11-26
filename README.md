# Tic Tac Toe Project Report

## a. Basic Information
**Project Name:** Tic Tac Toe

**Participants:**
- Ameya Kasture (2021A7PS2058G, f20212058@goa.bits-pilani.ac.in)
- Sharwin Neema (2021A7PS1442G, f20211442@goa.bits-pilani.ac.in)

---

## b. Overview of the Application and Its Current State
The Tic Tac Toe application simulates the classic game, supporting both single-player and two-player modes. It integrates with Firebase for user authentication and real-time database management. The application currently has no reported bugs and is in a fully functional state.

---

## c. Summary of Completed Tasks and Methods Used
### Task 1: Development of Sign-in Screen
- **Firebase Integration:** Established connection with Firebase for user authentication and database management.
- **Sign-in/Registration Functionality:** Enabled user login and registration using email and password. Once logged in, users can view active games stored in the database.

### Task 2: Single-Player Mode Implementation
- After logging in, users can choose a single-player mode.
- **Game Logic:** The app simulates a computer opponent making random moves after the player's turn.

### Task 3: Two-Player Mode Development
- Enabled a two-player mode post-login.
- **Game Synchronization:** Created new game entries in the Firebase database, allowing two users to interact with a synchronized tic tac toe grid.

### Task 4: Enhancing Accessibility
- **TalkBack Support:** Integrated TalkBack service for users requiring audible guidance, ensuring all elements on the screen are correctly identified.
- **Accessibility Scanner:** Utilized the Accessibility Scanner to identify issues such as:
    - Insufficient text contrast
    - Image color contrast problems
    - Missing speakable text
- Resolved these issues, particularly by adjusting `android:textColor` values for better contrast and visibility.

---

## d. Testing Strategy and Outcomes
### Firebase Setup for the Application:
1. **Firebase Console Configuration:**
    - Created a new project or used an existing Firebase project.
    - Added the app package name to the project.
    - Downloaded the `google-services.json` file and placed it in the app's directory (`app/`).

2. **Android Studio Configuration:**
    - Integrated Firebase SDK by modifying the project-level `build.gradle` file:
      ```gradle
      buildscript {
          dependencies {
              classpath 'com.google.gms:google-services:4.3.10' // Latest version
          }
      }
      ```
    - Applied the Firebase plugin in the app-level `build.gradle`:
      ```gradle
      apply plugin: 'com.google.gms.google-services'
      ```
    - Added dependencies for Firebase Authentication and Realtime Database:
      ```gradle
      dependencies {
          implementation 'com.google.firebase:firebase-auth:21.0.1' // Latest version
          implementation 'com.google.firebase:firebase-database:20.0.3' // Latest version
      }
      ```

### Testing Strategy:
- **Test-Driven Development:** Adopted a TDD approach, focusing on edge cases for game logic and user interactions.
- **UI and Instrumented Tests:** Conducted regular UI tests to ensure stable navigation, interaction, and error handling.
- **Monkey Testing:** Performed stress-testing with 10,000 random interactions to validate app stability and confirm no crashes under random user input.

---

## e. Time Investment for Completion
- **Coding and Development:** 15 hours
- **Testing and Accessibility Fixing:** 4 hours
- **Documentation Preparation:** 1 hour  
  **Total Time Spent:** 20 hours

---

## f. Assignment Difficulty Assessment
- **Difficulty Level:** 9.5/10  
  The assignment posed significant challenges, including Firebase integration, game logic synchronization for two players, and implementing accessibility features.

---
