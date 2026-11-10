![MIT](https://img.shields.io/badge/license-MIT-green)
[![Crowdin](https://img.shields.io/badge/Localization-Crowdin-blueviolet?logo=Crowdin)](https://crowdin.com/project/tts-server)

[![CI](https://github.com/jing332/tts-server-android/actions/workflows/release.yml/badge.svg)](https://github.com/jing332/tts-server-android/actions/workflows/release.yml)
[![CI](https://github.com/jing332/tts-server-android/actions/workflows/test.yml/badge.svg)](https://github.com/jing332/tts-server-android/actions/workflows/test.yml)

![GitHub release](https://img.shields.io/github/downloads/jing332/tts-server-android/total)
![GitHub release (latest by date)](https://img.shields.io/github/downloads/jing332/tts-server-android/latest/total)

# TTS Server [![](https://img.shields.io/badge/Q%E7%BE%A4-124841768-blue)](https://jq.qq.com/?_wv=1027&k=y7WCDjEA)

<details>
  <summary>View screenshots</summary>

  <img src="./images/1.jpg" height="150px">
  <img src="./images/2.jpg" height="150px">
  <img src="./images/3.jpg" height="150px">
  <img src="./images/4.jpg" height="150px">

</details>

# Download

* [Stable release (Releases)](https://github.com/jing332/tts-server-android/releases)

* [Dev build (Actions, requires logging in to a GitHub account)](https://github.com/jing332/tts-server-android/actions)

## Actions mirror

app: https://jing332.lanzn.com/b09jpjd2d

dev: https://jing332.lanzn.com/b09ig9qla

Password: 1234


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
| [zhanghai/AndroidFastScroll](https://github.com/zhanghai/AndroidFastScroll)                                     | Fast scroll for Android RecyclerView and more                                                                                                                 |
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

* [酷安@沉默_9520](http://www.coolapk.com/u/25956307) author of this app's icon

# Build

### Android Studio:
Create a new file named `local.properties` in the project root directory and write the following into it:
```
KEY_PATH=E\:\\Android\\key\\sign.jks (signing file)
KEY_PASSWORD= password
ALIAS_NAME= alias
ALIAS_PASSWORD= alias password
```



### Github Actions:
> See https://www.cnblogs.com/jing332/p/17452492.html for details

Use Git Bash to Base64-encode the signing file without line breaks: `openssl base64 < key.jks | tr -d '\r\n' | tee key.jks.base64.txt`

Add the following four secrets (Repository secrets):
> Go to the following link: https://github.com/your-username/tts-server-android/settings/secrets/actions
* `ALIAS_NAME` alias
* `ALIAS_PASSWORD` alias password
* `KEY_PASSWORD` password
* `KEY_STORE` the contents of the sign.jks.base64.txt generated above
