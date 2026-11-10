let txt = `Hello World! This is the content of a file named hello.txt`

let partBody = {
   'fileToUpload': { // File-type content
       'body': txt,
       'fileName': "hello.txt",
       'contentType': "text/plain"
   },
   'time': '1h',
   'reqtype': 'fileupload',
}
let headers = {
   'Content-Type': 'multipart/form-data',
   'User-Agent': 'PostmanRuntime-ApipostRuntime/1.1.0'
}
let resp = http.post('https://catbox.moe/user/api.php', partBody)
let ret = resp.text()


let url = 'https://sy.mgz6.com/shuyuan'
resp = upload(url, "config")
console.log(resp.text())

function upload(url, config, extra) {
    let form = {
        "file":{
            'body': config,
            'fileName': 'config.json',
            'contentType': 'application/json',
          }
    }

    return http.post(url, form)
}