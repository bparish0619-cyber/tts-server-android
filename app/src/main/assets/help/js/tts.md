# TTS Plugins
> Plugins are written in JavaScript, with Rhino as the JS parser

```javascript
let PluginJS = {
    "name": "Plugin name",
    "id": "Unique plugin ID", // Also used as the relative directory for fs file operations: /Android/data/com.github.jing332.tts_server_android/caches/<plugin ID>
    "author": "Author",
    "iconUrl": "https://cn.bing.com/favicon.ico",
    "version": 1, // Version number, must be an integer

    // Called when TTS stops
    "onStop": function () {
 
    },
    
    /**
    "getAudio": function (text, locale, voice, speed, volume, pitch) {
        // Supported return types:
        // A string starting with http:// or https://
        // InputStream (Java input stream)
        // ByteArray (Java byte array)
        // ArrayBuffer
        // Uint8Array
        
    }
    */

    // Use either this or getAudio, as needed
    "getAudioV2": function (request, callback) {
        let rate = (request.rate * 2) - 100
        let pitch = request.pitch - 50
        let voice = request.voice
        let volume = request.volume
        let text = request.text

        callback.write(bytes)   // Write a byte array
        callback.close()        // Call once everything has been written
        callback.error(string)  // Call when an error occurs
    },
}


let EditorJS = {
    // The audio sample rate; called when the TTS configuration is saved
    "getAudioSampleRate": function (locale, voice) {
        // return 24000
        
        // Automatically request a piece of audio to detect it
        let audio = PluginJS.getAudio('test', locale, voice, 50, 50, 50)
        return ttsrv.getAudioSampleRate(audio)
    },

    // Language dropdown
    "getLocales": function () {
        return ['zh-CN', 'en-US']
    },

    // Voice dropdown
    "getVoices": function (locale) {
        // Simple usage: the key is passed as the voice parameter of getAudio, the value is the display name
        return { 'xiaoxiao': 'Xiaoxiao' }
        
        // Advanced usage: specify an icon
        return { 
            'xiaoxiao': {
                name: 'Xiaoxiao',
                icon: 'male' // Allowed values: male / female / an icon URL
            }
        }
      
    },

    // Load voice data
    "onLoadData": function () {
        let jsonStr = ''
        if (fs.exists('voices.json')) {
            jsonStr = fs.readText('voices.json')
        } else {
            let url = 'https://speech.platform.bing.com/consumer/speech/synthesize/readaloud/voices/list?trustedclienttoken=' + token
            jsonStr = http.get(url).text()
            fs.writeFile('voices.json', jsonStr)
        }

        voices = JSON.parse(jsonStr)
    },

    "onLoadUI": function (ctx, linerLayout) {

    },

    "onVoiceChanged": function (locale, voiceCode) {

    }
}```

