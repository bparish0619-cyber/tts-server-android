version-2

[![QQ Group](https://img.shields.io/badge/Q%E7%BE%A4-124841768-blue.svg)](https://jq.qq.com/?_wv=1027&k=y7WCDjEA)
[![Issue](https://img.shields.io/badge/Github-Issue-greeb.svg)](https://github.com/jing332/tts-server-android/issues)
[![Dev](https://img.shields.io/github/actions/workflow/status/jing332/tts-server-android/test.yml?label=%E5%BC%80%E5%8F%91%E7%89%88)](https://github.com/jing332/tts-server-android/actions/workflows/test.yml)

# TTS Server
This app does not provide speech synthesis itself. It is simply a carrier for online TTS services,
invoking TTS APIs on the internet through plugins.


## 1️⃣ System TTS
Each of the following 4 screens has its own import and export functions in the More options menu in the top-right corner. You can also back up and restore everything in Settings.

### Main Screen
The configuration list, used to manage TTS configurations. You can use the grouping feature to switch multiple configurations with one tap.
- You can swap the positions of the `Edit` and `Preview` buttons in Settings ( <i>long-press the Edit button to preview; conversely, long-press the Preview button to edit</i>)

### Speech Rules
Used to process the text to be read aloud, matching TTS configurations by the tags you configure (for example: narration/dialogue).
The app includes a built-in `Narration/Dialogue` speech rule based on Chinese double quotation marks, ready to use right away.

### Plugins
Used to extend TTS functionality, invoking TTS APIs on the internet with JS scripts, such as the built-in `Azure plugin`.

### Replacement Rules
Used to replace text before it is read aloud, to correct pronunciation and the like. For example: replacing an informal greeting with its polite form.

Advanced example:
- Replace the double quotation marks around dialogue of 5 characters or fewer with, so that the passage is read as narration.
```
(Enable regular expressions)
Rule: (“)(.{1,5})(”)
Replace with:
```

## 👨‍🏫 System TTS FAQ
### 1. Reading suddenly stops after a while once the screen is locked?
> In `System Settings -> Apps -> Battery optimization`, add this app and your reading app to the battery optimization whitelist.
>
> For this app, you can tap `Battery optimization whitelist` in the left slide-out menu for a quick shortcut.
>
> PS: On Chinese-market ROMs, you may also need to lock the app in the recent tasks list, enable background permissions, and so on.

### 2. Long pauses between paragraphs?
> This is usually caused by network latency. Because of technical limitations of the Android system TTS service, audio cannot be pre-cached, so every segment has to be fetched synchronously.



## 2️⃣ TTS Forwarder
Used to expose an Android system TTS engine as an HTTP network interface, so it can be called from web pages.
**Used together with a reading app's network TTS engine, it can effectively pre-cache a whole chapter of audio, improving fluency between paragraphs.**
