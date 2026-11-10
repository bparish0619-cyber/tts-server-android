![MIT](https://img.shields.io/badge/license-MIT-green)
[![Build Release](https://github.com/bparish0619-cyber/tts-server-android/actions/workflows/release.yml/badge.svg)](https://github.com/bparish0619-cyber/tts-server-android/actions/workflows/release.yml)
[![Build Debug](https://github.com/bparish0619-cyber/tts-server-android/actions/workflows/debug.yml/badge.svg)](https://github.com/bparish0619-cyber/tts-server-android/actions/workflows/debug.yml)

# TTS Server (English fork)

This repository is Brandon's English-only fork of [jing332's TTS Server](https://github.com/jing332/tts-server-android), updated for Android 16 (API 36). The app is the upstream project; this fork changes the language resources and the build setup, as described below.

## Features

- Registers as an Android system text-to-speech engine, so apps that use the system TTS voice can speak through it
- Built-in Microsoft demo interface, for trying voices without your own service
- Custom HTTP TTS requests: send speech requests to an API with your own URL, headers, and request body
- Importable local TTS engines and JavaScript plugins
- Narration and dialogue detection for reading mixed text aloud
- Auto retry with backup configurations, so a failed request can fall back to another setup
- Text replacement rules, for adjusting wording and pronunciation before speech

## Language

English is the only language in this fork. The interface text, the help documentation, the built-in plugins, and the built-in web control page are all in English, and the other language resources from upstream were removed.

<details>
  <summary>View screenshots</summary>

  <img src="./images/1.jpg" height="150px">
  <img src="./images/2.jpg" height="150px">
  <img src="./images/3.jpg" height="150px">
  <img src="./images/4.jpg" height="150px">

</details>

## Download

* [Releases of this fork](https://github.com/bparish0619-cyber/tts-server-android/releases)
* Debug builds: run the "Build Debug" workflow from the Actions tab and download the `TTS-Server_debug` artifact (requires a GitHub account). Debug builds carry the standard Android debug signature: fine for sideloading and testing, but they cannot update an install that was signed with a private key.

# Grateful

<details>
  <summary>Open source projects</summary>

| Application                                                                     | Microsoft TTS                                                         |
|---------------------------------------------------------------------------------|-----------------------------------------------------------------------|
| [gedoor/legado](https://github.com/gedoor/legado)                               | [wxxxcxx/ms-ra-forwarder](https://github.com/wxxxcxx/ms-ra-forwarder) |
| [ag2s20150909/TTS](https://github.com/ag2s20150909/TTS)                         | [litcc/tts-server](https://github.com/litcc/tts-server)               |
| [benjaminwan/ChineseTtsTflite](https://github.com/benjaminwan/ChineseTtsTflite) | [asters1/tts](https://github.com/asters1/tts)                         |
| [yellowgreatsun/MXTtsEngine](https://github.com/yellowgreatsun/MXTtsEngine)     |
| [2dust/v2rayNG](https://github.com/2dust/v2rayNG)                               |

| Library                                                                                                         | Description                                                                                                                                                   |
|-----------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [dromara/hutool](https://github.com/dromara/hutool/)                                                            | 🍬A set of tools that keep Java sweet.                                                                                                                        |
| [LouisCAD/Splitties](https://github.com/LouisCAD/Splitties)                                                     | A collection of hand-crafted extensions for your Kotlin projects.                                                                                             |
| [getactivity/logcat](https://github.com/getactivity/logcat)                                                     | An Android logging framework; view Logcat logs right on your phone                                                                                                                          |
| [rosuH/AndroidFilePicker](https://github.com/rosuH/AndroidFilePicker)                                           | FilePicker is a small and fast file selector library that is constantly evolving with the goal of rapid integration, high customization, and configurability~ |
| [androidbroadcast/ViewBindingPropertyDelegate](https://github.com/androidbroadcast/ViewBindingPropertyDelegate) | Make work with Android View Binding simpler                                                                                                                   |
| [zhanghai/AndroidFastScroll](https://github.com/zhanghai/AndroidFastScroll)                                             | Fast scroll for Android RecyclerView and more                                                                                                                 |
| [Rosemoe/sora-editor](https://github.com/Rosemoe/sora-editor)                                                   | sora-editor is a cool and optimized code editor on Android platform                                                                                           |
| [gedoor/rhino-android](https://github.com/gedoor/rhino-android)                                                 | Give access to RhinoScriptEngine from the JSR223 interfaces on Android JRE.                                                                                   |
| [liangjingkanji/BRV](https://github.com/liangjingkanji/BRV)                                                     | The best RecyclerView framework on Android, simpler and more powerful than BRVAH                                                                                                                      |
| [liangjingkanji/Net](https://github.com/liangjingkanji/Net)                                                     | The best networking tool on Android, simpler and easier to use than Retrofit/OkGo                                                                                                                       |
| [chibatching/kotpref](https://github.com/chibatching/kotpref)                                                   | Android SharedPreferences delegation library for Kotlin                                                                                                       |
| [google/ExoPlayer](https://github.com/google/ExoPlayer)                                                         | An extensible media player for Android                                                                                                                        |
| [material-components-android](https://github.com/material-components/material-components-android)               | Modular and customizable Material Design UI components for Android                                                                                            |
| [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization/)                                       | Kotlin multiplatform / multi-format serialization                                                                                                             |
| [kotlinx.coroutine](https://github.com/Kotlin/kotlinx.coroutines)                                               | Library support for Kotlin coroutines                                                                                                                         |

</details>

Other resources:

* <a href="https://www.flaticon.com/free-icons/female" title="female icons">Female icons created by popcornarts - Flaticon</a>

* [Alibaba IconFont](https://www.iconfont.cn/)

* [Coolapk user](http://www.coolapk.com/u/25956307), author of this app's icon

# Build

### GitHub Actions:

The "Build Debug" workflow (`.github/workflows/debug.yml`) builds a debug APK on demand: open the Actions tab, choose "Build Debug", run it on the `compose` branch, and download the `TTS-Server_debug` artifact. The "Build Release" workflow (`.github/workflows/release.yml`) runs when `CHANGELOG.md` changes on the `compose` branch and publishes a GitHub Release with the release APK attached.

This fork has no signing secrets configured. Without them, debug builds use the standard Android debug key. Signed release builds need the four repository secrets listed below.

### Android Studio:
Create a new file named `local.properties` in the project root directory and write the following into it:
```
KEY_PATH=E\:\\Android\\key\\sign.jks (signing file)
KEY_PASSWORD= password
ALIAS_NAME= alias
ALIAS_PASSWORD= alias password
```



### Github Actions signing secrets:

Use Git Bash to Base64-encode the signing file without line breaks: `openssl base64 < key.jks | tr -d '\r\n' | tee key.jks.base64.txt`

Add the following four secrets (Repository secrets):
> Go to the following link: https://github.com/your-username/tts-server-android/settings/secrets/actions
* `ALIAS_NAME` alias
* `ALIAS_PASSWORD` alias password
* `KEY_PASSWORD` password
* `KEY_STORE` the contents of the sign.jks.base64.txt generated above
