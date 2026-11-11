// --- ElevenLabs TTS Plugin for TTS Server ---
// Please set your ElevenLabs API Key in "More options" -> "Variables" after clicking save.

let apiKey = ttsrv.userVars['apiKey'] || ''

// Default model - 'eleven_multilingual_v2' supports many languages.
// You could make this a variable if needed: ttsrv.userVars['modelId'] || 'eleven_multilingual_v2'
let defaultModelId = 'eleven_multilingual_v2'
// Define the desired output format for ElevenLabs (affects sample rate, bitrate, codec)
// See ElevenLabs docs for options. 'mp3_44100_128' is a common high-quality default.
let outputFormat = "mp3_44100_128" // Example: MP3, 44.1kHz, 128kbps
let sampleRate = 44100; // Set based on the outputFormat requested

let PluginJS = {
    "name": "ElevenLabs",
    "id": "io.elevenlabs.api",
    "author": "Generated AI",
    "description": "Provides Text-to-Speech using the ElevenLabs API.",
    "version": 1,
    "vars": {
        apiKey: {label: "ElevenLabs API Key"},
        // Optionally add modelId: {label: "Model ID", hint:"Default: eleven_multilingual_v2"}
    },

    "onLoad": function () {
      checkApiKey() // Check if API key is set on load
    },

    "getAudio": function (text, locale, voice, rate, volume, pitch) {
        // NOTE: ElevenLabs API (v1 standard endpoint) doesn't directly use rate, volume, pitch
        // like Azure's SSML prosody tags. Basic SSML is supported for some features.
        // We will use Stability and Similarity Boost instead, controlled by UI sliders.
        checkApiKey(); // Ensure key is present before making call

        let stability = parseFloat(ttsrv.tts.data['stability']);
        let similarity = parseFloat(ttsrv.tts.data['similarity_boost']);

        // Set defaults if values are missing or invalid
        if (isNaN(stability)) {
            stability = 0.75; // Default stability
            ttsrv.tts.data['stability'] = stability.toFixed(2);
         }
        if (isNaN(similarity)) {
            similarity = 0.75; // Default similarity boost
            ttsrv.tts.data['similarity_boost'] = similarity.toFixed(2);
        }

        let requestUrl = `https://api.elevenlabs.io/v1/text-to-speech/${voice}?output_format=${outputFormat}`;
        // If not using output_format parameter, use the basic endpoint and Accept header:
        // let requestUrl = `https://api.elevenlabs.io/v1/text-to-speech/${voice}`;
        // let headers = {
        //     'Accept': 'audio/mpeg', // Request MP3 audio
        //     'xi-api-key': apiKey,
        //     'Content-Type': 'application/json',
        // };


        let headers = {
            'xi-api-key': apiKey,
            'Content-Type': 'application/json',
        };

        let payload = {
            "text": text,
            "model_id": defaultModelId, // Use the configured default or variable
            "voice_settings": {
                "stability": stability,
                "similarity_boost": similarity,
                // "style": 0.5, // Optional, requires v2 models, might add slider later
                // "use_speaker_boost": true // Optional, requires v2 models, might add toggle later
            }
        };

        let jsonPayload = JSON.stringify(payload);
        let resp = ttsrv.httpPost(requestUrl, jsonPayload, headers);

        if (resp.code() !== 200) {
            let errorBody = resp.body().string(); // Try to get error details
             if (resp.code() === 401) {
                 throw `401 Unauthorized. Please check your ElevenLabs API Key. Details: ${errorBody}`;
             }
             // Add more specific error handling if needed (e.g., 400 Bad Request, 422 Unprocessable Entity)
            throw `Audio generation failed: HTTP-${resp.code()}. ${errorBody}`;
        }

        return resp.body().byteStream();
    },
};

function checkApiKey() {
    apiKey = (apiKey + '').trim();
    if (apiKey === '') {
        throw "Please set your ElevenLabs API Key in the plugin variables.";
    }
}

// Cache for voice list
let voicesData = null;
// Map to store current voices {voice_id: voice_name}
let currentVoicesMap = new Map();

// UI Definition
let EditorJS = {
    // Return the sample rate based on the selected output format
    "getAudioSampleRate": function (locale, voice) {
         // This should ideally match the sample rate dictated by the 'outputFormat' setting
         // Or, could dynamically fetch detailed voice info if needed, but hardcoding is simpler
         return sampleRate;
    },

    "getLocales": function () {
        // Load data if needed (might happen before onLoadData in some flows)
        if (!voicesData) {
           try {
               this.onLoadData();
           } catch(e) {
               // Return empty or a placeholder to avoid breaking UI
               return ["Error - Check API Key?"];
           }
        }
        return ["ElevenLabs"]; // Single pseudo-locale
    },

    // Returns voices for the selected "locale" (which will always be "ElevenLabs")
    "getVoices": function (locale) {
         if (locale !== "ElevenLabs" || !voicesData || !voicesData.voices) {
             return {}; // Return empty map if locale isn't ours or data failed load
         }

         currentVoicesMap = new Map();
         let mm = {}; // Use standard JS object for the map expected by TTS Server
         voicesData.voices.forEach(function (v) {
            if (v.voice_id && v.name) {
                 currentVoicesMap.set(v.voice_id, v); // Store full voice info
                 // Use java.lang.String to ensure compatibility with the Java environment if needed
                 // Adding labels (like accent) to the display name for better identification
                 let displayName = v.name;
                 if (v.labels) {
                     let accent = v.labels.accent;
                     let description = v.labels.description;
                     let age = v.labels.age;
                     let gender = v.labels.gender;
                     let use_case = v.labels['use case']; // Access 'use case' label if it exists

                     let labelsStr = [accent, description, age, gender, use_case].filter(Boolean).join(', '); // Filter out null/empty labels and join
                     if (labelsStr) {
                         displayName += ' (' + labelsStr + ')';
                     }
                 }
                 mm[v.voice_id] = new java.lang.String(displayName);
            }
         });
        return mm;
    },

    // Load voice list from ElevenLabs API or cache
    "onLoadData": function () {
         let cacheFileName = 'elevenlabs_voices.json';
         let jsonStr = '';

        if (ttsrv.fileExist(cacheFileName)) {
             jsonStr = ttsrv.readTxtFile(cacheFileName);
             // ttsrv.log("Loaded ElevenLabs voices from cache."); // Removed log call
         } else {
             checkApiKey(); // Need key to fetch voices
            let url = 'https://api.elevenlabs.io/v1/voices';
             let header = { "xi-api-key": apiKey };
            try {
                 jsonStr = ttsrv.httpGetString(url, header);
                 if (jsonStr) {
                    // Validate if response seems like JSON before writing
                    if (jsonStr.trim().startsWith('{')) {
                        ttsrv.writeTxtFile(cacheFileName, jsonStr);
                    } else {
                         throw "Received non-JSON response from voices API: " + jsonStr.substring(0, 100);
                    }
                 } else {
                     throw "Received empty response from voices API.";
                 }
            } catch (e) {
                 throw "Failed to fetch voice list from ElevenLabs. Check API Key and network. " + e;
             }
         }

        try {
             voicesData = JSON.parse(jsonStr);
        } catch (e) {
            voicesData = null; // Ensure data is null on error
            // Optionally delete corrupted cache file
             // if (ttsrv.fileExist(cacheFileName)) { ttsrv.deleteFile(cacheFileName); }
            throw "Failed to parse cached or downloaded voice list.";
         }
    },

    // Create the UI elements (Sliders for Stability and Similarity)
    "onLoadUI": function (ctx, linerLayout) {
         // Stability Slider
        let seekStability = JSeekBar(ctx, "Stability:");
         linerLayout.addView(seekStability);
         ttsrv.setMargins(seekStability, 0, 4, 0, 0);
         seekStability.setFloatType(2); // Two decimal places
         seekStability.max = 100; // Represents 0.00 to 1.00
         let stability = parseFloat(ttsrv.tts.data['stability']);
        seekStability.value = new java.lang.Float(isNaN(stability) ? 0.75 * 100 : stability * 100); // Default 0.75
         seekStability.setOnChangeListener({
             onProgressChanged: function(seek, progress, fromUser){ }, // Added empty placeholders if needed
             onStartTrackingTouch: function(seek){ }, // Added empty placeholders if needed
            onStopTrackingTouch: function (seek) {
                ttsrv.tts.data['stability'] = (seek.value / 100).toFixed(2);
             }
         });

        // Similarity Boost Slider
        let seekSimilarity = JSeekBar(ctx, "Similarity Boost:");
         linerLayout.addView(seekSimilarity);
         ttsrv.setMargins(seekSimilarity, 0, 4, 0, -4); // Adjust margins as needed
         seekSimilarity.setFloatType(2); // Two decimal places
         seekSimilarity.max = 100; // Represents 0.00 to 1.00
        let similarity = parseFloat(ttsrv.tts.data['similarity_boost']);
        seekSimilarity.value = new java.lang.Float(isNaN(similarity) ? 0.75 * 100 : similarity * 100); // Default 0.75
         seekSimilarity.setOnChangeListener({
             onProgressChanged: function(seek, progress, fromUser){ }, // Added empty placeholders if needed
             onStartTrackingTouch: function(seek){ }, // Added empty placeholders if needed
            onStopTrackingTouch: function (seek) {
                ttsrv.tts.data['similarity_boost'] = (seek.value / 100).toFixed(2);
             }
         });
     },

    // Called when the selected voice changes
    "onVoiceChanged": function (locale, voiceCode) {
         // Currently, stability/similarity are user-global settings.
    }
};
