# Websocket

WebSocket requests are supported since the 2025 version; internally they are implemented on top of OkHttp.

``` javascript
let ws = Websocket("wss://echo.websocket.org", {"User-Agent": "TTS Server"})
ws.on('close', function(code, reason){ // Int, String
    
})

ws.on('error', function(err, response){ // String, NativeResponse
    
})

ws.on('binary', function(buf){ // Buffer
    
})

ws.on('text', function(msg){ // String
    
})

ws.on('open', function(){
    ws.send("Hello world!")
})

// Close
// ws.close(1007, "close~~")

// Force disconnect
// ws.cancel()

```

## Using it together with a plugin's TTS getAudioV2

```javascript
var ws = null
let PluginJS = {
    // ...
    
    "onStop": function () {
        if (ws != null) {
            ws.cancel()
        }
    },

    "getAudioV2": function (request, callback) {
        // request is a JSON object
        // request.text /voice / locale / rate / volume / pitch
        // e.g. request.voice 
                
    
        callback.write(bytes)   // Write a byte array
        callback.close()        // Call once everything has been written
        callback.error(string)  // Call when an error occurs
    }
    
}
// ...
    

```