## 2026-11-10: First release of the English-only fork

- First release of the English-only fork of TTS Server
- All interface text, help documentation, built-in plugins, and the built-in web UI converted to English, with the other language resources removed
- Removed the last remaining Chinese characters from code and tests, including renaming the default voice identifier to DEFAULT
- Updated for Android 16 (API 36), with AGP 8.13.2, Gradle 8.13, Kotlin 2.2.21, and Room 2.7.2
- Minimum Android version unchanged (minSdk 21)
- README and repository description refreshed

- Added a helpful prompt when importing configurations
- Added three theme colors
- A default configuration is now added automatically for new users
- Added help documentation, which is shown automatically
- Speech rules can now be exported one at a time
- Callers can now specify the voice configuration through the API
- Fixed speech rule exports having no file extension
- Fixed local TTS not being previewable on the edit screen
- Fixed plugin TTS extra data not updating (fixes Azure plugin style and role changes not taking effect)
- Fixed backup issues on Android 8 and below