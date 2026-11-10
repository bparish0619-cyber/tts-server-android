# Speech Rules
> Speech rules split the original text into segments and mark each segment with a tag; TTS Server then matches a configuration by tag to synthesize the speech.

## Example of the rule built into the app:

```javascript 
let SpeechRuleJS = {
    name: "Narration/Dialogue",
    id: "ttsrv.multi_voice",
    author: "TTS Server",
    version: 4,
    
    // Map type: the key is the tag id, the value is the display name shown in the tag dropdown
    tags: {narration: "Narration", dialogue: "Dialogue"},

    // text is the original, unprocessed text
    // Returns a List:
    // [
    //     {text: "This is a narration text", tag: "narration"}, 
    //     {text: "This is a dialogue text", tag: "dialogue"}
    // ]
    handleText(text) {
        const list = [];
        let tmpStr = "";
        let endTag = "narration";

        text.split("").forEach((char, index) => {
            tmpStr += char;

            if (char === '“') {
                endTag = "dialogue";
                list.push({text: tmpStr, tag: "narration"});
                tmpStr = "";
            } else if (char === '”') {
                endTag = "narration";
                tmpStr = tmpStr.slice(0, -1)
                list.push({text: tmpStr, tag: "dialogue"});
                tmpStr = "";
            } else if (index === text.length - 1) {
                list.push({text: tmpStr, tag: endTag});
            }
        });

        return list;
    },

    // After handleText returns, split the text of each element in the returned list into sentences;
    // Returns a List of strings
    splitText(text) {
        let separatorStr = "。？?！!;；"

        let list = []
        let tmpStr = ""
        text.split("").forEach((char, index) => {
            tmpStr += char

            if (separatorStr.includes(char)) {
                list.push(tmpStr)
                tmpStr = ""
            } else if (index === text.length - 1) {
                list.push(tmpStr);
            }
        })

        return list.filter(item =>  item.replace(/[“”]/g, '').trim().length > 0);
    }

};


```

