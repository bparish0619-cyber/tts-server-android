// Please set the key and region in "More options" -> "Variables" after clicking save.

let key = ttsrv.userVars['key'] || 'Default_KEY'
let region = ttsrv.userVars['region'] || 'eastus'

let format = "audio-24khz-48kbitrate-mono-mp3"
let sampleRate = 24000 // Corresponds to 24 kHz. Formats with "opus" in the name actually use twice this sample rate
let isNeedDecode = true // Whether the audio needs decoding; set to false if format is raw

let PluginJS = {
    "name": "Azure",
    "id": "com.microsoft.azure",
    "author": "TTS Server",
    "description": "",
    "version": 3,
    "vars": { // Declare variables here; the user sets their values.
        key: {label: "Key"},
        region: {label: "Region", hint: "Uses the default 'eastus' when empty"},
    },

    "onLoad": function () {
      checkKeyRegion()
    },

    "getAudio": function (text, locale, voice, rate, volume, pitch) {
        rate = (rate * 2) - 100
        pitch = pitch - 50

        let styleDegree = ttsrv.tts.data['styleDegree']
        if (!styleDegree || Number(styleDegree) < 0.01) {
            styleDegree = '1.0'
        }

        let style = ttsrv.tts.data['style']
        let role = ttsrv.tts.data['role']
        if (!style || style === "") {
            style = 'general'
        }
        if (!role || role === "") {
            role = 'default'
        }

        let textSsml = ''
        let langSkill = ttsrv.tts.data['languageSkill']
        if (langSkill === "" || langSkill == null) {
            textSsml = escapeXml(text)
        } else {
            textSsml = `<lang xml:lang="${langSkill}">${escapeXml(text)}</lang>`
        }

        let ssml = `
        <speak xmlns="http://www.w3.org/2001/10/synthesis" xmlns:mstts="http://www.w3.org/2001/mstts" xmlns:emo="http://www.w3.org/2009/10/emotionml" version="1.0" xml:lang="zh-CN">
            <voice name="${voice}">
                <mstts:express-as style="${style}" styledegree="${styleDegree}" role="${role}">
                    <prosody rate="${rate}%" pitch="${pitch}%" volume="${volume}">${textSsml}</prosody>
                </mstts:express-as>
            </voice >
         </speak >
        `

        return getAudioInternal(ssml, format)
    },
}

function escapeXml(s) {
    return s.replace(/'/g, '&apos;').replace(/"/g, '&quot;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/&/g, '&amp;').replace(/\//g, '').replace(/\\/g, '');
}

function checkKeyRegion() {
    key = (key + '').trim()
    region = (region + '').trim()
    if (key === '' || region === '') {
        throw "Please set the variables: Key and Region."
    }
}

let ttsUrl = 'https://' + region + '.tts.speech.microsoft.com/cognitiveservices/v1'

function getAudioInternal(ssml, format) {
    let headers = {
        'Ocp-Apim-Subscription-Key': key,
        "X-Microsoft-OutputFormat": format,
        "Content-Type": "application/ssml+xml",
    }
    let resp = ttsrv.httpPost(ttsUrl, ssml, headers)
    if (resp.code() !== 200) {
        if (resp.code() === 401) {
            throw "401 Unauthorized. Please check that your key and region are correct."
        }else if (resp.code() === 403) {
            throw "403 Forbidden. Your Azure account may have been disabled."
        }

        throw "Failed to fetch audio: HTTP-" + resp.code()
    }

    return resp.body().byteStream()
}

// All voice data
let voices = {}
// Voices under the current language
let currentVoices = new Map()

// Language skill (secondary language)
let skillSpinner

let styleSpinner
let roleSpinner
let seekStyle

let EditorJS = {
    // The audio sample rate; called when saving from the Edit TTS screen
    "getAudioSampleRate": function (locale, voice) {
        return sampleRate
    },

    "isNeedDecode": function (locale, voice) {
        return isNeedDecode
    },

    "getLocales": function () {
        let locales = new Array()

        voices.forEach(function (v) {
            let loc = v["Locale"]
            if (!locales.includes(loc)) {
                locales.push(loc)
            }
        })

        return locales
    },

    // Called when the language changes
    "getVoices": function (locale) {
        currentVoices = new Map()
        voices.forEach(function (v) {
            if (v['Locale'] === locale) {
                currentVoices.set(v['ShortName'], v)
            }
        })

        let mm = {}
        for (let [key, value] of currentVoices.entries()) {
            mm[key] = new java.lang.String(value['LocalName'] + ' (' + key + ')')
        }
        return mm
    },

    // Load local or network data; runs on the IO thread.
    "onLoadData": function () {
        // Fetch the data and cache it for reuse
        let jsonStr = ''
        if (ttsrv.fileExist('voices.json')) {
            jsonStr = ttsrv.readTxtFile('voices.json')
        } else {
            checkKeyRegion()
            let url = 'https://' + region + '.tts.speech.microsoft.com/cognitiveservices/voices/list'
            let header = {
                "Ocp-Apim-Subscription-Key": key,
                "Content-Type": "application/json",
            }
            jsonStr = ttsrv.httpGetString(url, header)


            ttsrv.writeTxtFile('voices.json', jsonStr)
        }

        voices = JSON.parse(jsonStr)
    },

    "onLoadUI": function (ctx, linerLayout) {
        let layout = new LinearLayout(ctx)
        layout.orientation = LinearLayout.HORIZONTAL // Horizontal layout
        let params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1)

        skillSpinner = JSpinner(ctx, "Language skill")
        linerLayout.addView(skillSpinner)
        ttsrv.setMargins(skillSpinner, 2, 4, 0, 0)
        skillSpinner.setOnItemSelected(function (spinner, pos, item) {
            ttsrv.tts.data['languageSkill'] = item.value + ''
        })

        styleSpinner = JSpinner(ctx, "Style")
        styleSpinner.layoutParams = params
        layout.addView(styleSpinner)
        ttsrv.setMargins(styleSpinner, 2, 4, 0, 0)
        styleSpinner.setOnItemSelected(function (spinner, pos, item) {
            ttsrv.tts.data['style'] = item.value
            // Default || no value || empty-string value
            if (pos === 0 || !item.value || item.value === "") {
                seekStyle.visibility = View.GONE // Hide the style degree
            } else {
                seekStyle.visibility = View.VISIBLE // Show it
            }
        })

        roleSpinner = JSpinner(ctx, "Role")
        roleSpinner.layoutParams = params
        layout.addView(roleSpinner)
        ttsrv.setMargins(roleSpinner, 0, 4, 2, 0)
        roleSpinner.setOnItemSelected(function (spinner, pos, item) {
            ttsrv.tts.data['role'] = item.value
        })
        linerLayout.addView(layout)

        seekStyle = JSeekBar(ctx, "Style degree:")
        linerLayout.addView(seekStyle)
        ttsrv.setMargins(seekStyle, 0, 4, 0, -4)
        seekStyle.setFloatType(2) // Two decimal places
        seekStyle.max = 200 // Maximum of 200 ticks

        let styleDegree = Number(ttsrv.tts.data['styleDegree'])
        if (!styleDegree || isNaN(styleDegree)) {
            styleDegree = 1.0
        }
        seekStyle.value = new java.lang.Float(styleDegree)

        seekStyle.setOnChangeListener(
            {
                // When dragging starts
                onStartTrackingTouch: function (seek) {

                },
                // While the progress is being dragged
                onProgressChanged: function (seek, progress, fromUser) {

                },
                // When dragging stops
                onStopTrackingTouch: function (seek) {
                    ttsrv.tts.data['styleDegree'] = Number(seek.value).toFixed(2)
                },
            }
        )
    },

    "onVoiceChanged": function (locale, voiceCode) {
        let vic = currentVoices.get(voiceCode)

        let locale2List = vic['SecondaryLocaleList']
        let locale2Items = []
        let locale2Pos = 0

        if (locale2List) {
            locale2Items.push(Item("Default", ""))
            locale2List.map(function (v, i) {
                let loc = java.util.Locale.forLanguageTag(v)
                let name = loc.getDisplayName(loc)
                locale2Items.push(Item(name, v))
                if (v === ttsrv.tts.data['languageSkill'] + '') {
                    locale2Pos = i + 1
                }
            })
        }
        skillSpinner.items = locale2Items
        skillSpinner.selectedPosition = locale2Pos

        if (locale2Items.length === 0) {
            skillSpinner.visibility = View.GONE
        } else {
            skillSpinner.visibility = View.VISIBLE
        }

        let styles = vic['StyleList']
        let styleItems = []
        let stylePos = 0
        if (styles) {
            styleItems.push(Item("Default (general)", ""))
            styles.map(function (v, i) {
                styleItems.push(Item(getString(v), v))
                if (v === ttsrv.tts.data['style'] + '') {
                    stylePos = i + 1 // +1 to account for the default item
                }
            })
        } else {
            seekStyle.visibility = View.GONE
        }
        styleSpinner.items = styleItems
        styleSpinner.selectedPosition = stylePos

        let roles = vic['RolePlayList']
        let roleItems = []
        let rolePos = 0
        if (roles) {
            roleItems.push(Item("Default", ""))
            roles.map(function (v, i) {
                roleItems.push(Item(getString(v), v))
                if (v === ttsrv.tts.data['role'] + '') {
                    rolePos = i + 1 // +1 to account for the default item
                }
            })
        }
        roleSpinner.items = roleItems
        roleSpinner.selectedPosition = rolePos
    }
}

let cnLocales = {
    "narrator": "Narration",
    "girl": "Girl",
    "boy": "Boy",
    "youngadultfemale": "Young adult female",
    "youngadultmale": "Young adult male",
    "olderadultfemale": "Older adult female",
    "olderadultmale": "Older adult male",
    "seniorfemale": "Senior female",
    "seniormale": "Senior male",

    "advertisement_upbeat": "Upbeat advertisement",
    "affectionate": "Affectionate",
    "angry": "Angry",
    "assistant": "Digital assistant",
    "calm": "Calm",
    "chat": "Chat",
    "cheerful": "Cheerful",
    "customerservice": "Customer service",
    "depressed": "Depressed",
    "disgruntled": "Disgruntled",
    "documentary-narration": "Documentary narration",
    "embarrassed": "Embarrassed",
    "empathetic": "Empathetic",
    "envious": "Envious",
    "excited": "Excited",
    "fearful": "Fearful",
    "friendly": "Friendly",
    "gentle": "Gentle",
    "hopeful": "Hopeful",
    "lyrical": "Lyrical",
    "narration-professional": "Professional narration",
    "narration-relaxed": "Relaxed narration",
    "newscast": "Newscast",
    "newscast-casual": "Casual newscast",
    "newscast-formal": "Formal newscast",
    "poetry-reading": "Poetry reading",
    "sad": "Sad",
    "serious": "Serious",
    "shouting": "Shouting",
    "sports_commentary": "Sports commentary",
    "sports_commentary_excited": "Excited sports commentary",
    "whispering": "Whispering",
    "terrified": "Terrified",
    "unfriendly": "Unfriendly",
}

let isZh = java.util.Locale.getDefault().getLanguage() == 'zh'

function getString(key) {
    if (isZh) {
        return cnLocales[key.toLowerCase()] || key
    } else {
        return key
    }
}