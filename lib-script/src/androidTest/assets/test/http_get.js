let resp = http.get("https://reqres.in/api/users")
console.log("url", resp.url)
console.log("redirected", resp.redirected)
console.log("isOK", resp.ok)
console.log("status", resp.status)
console.log("statusText", resp.statusText)
console.log("headers", resp.headers)

// Pick one of the three; each can only be called once
console.log("json", resp.json(true/* do not check the status code */).data[0].avatar) // https://reqres.in/img/faces/1-image.jpg
// console.log("bytes", resp.bytes())
// console.log("text", resp.text())


// https://reqres.in/api/users
// Example output:
//{
//  "page": 1,
//  "per_page": 6,
//  "total": 12,
//  "total_pages": 2,
//  "data": [
//    {
//      "id": 1,
//      "email": "george.bluth@reqres.in",
//      "first_name": "George",
//      "last_name": "Bluth",
//      "avatar": "https://reqres.in/img/faces/1-image.jpg"
//    },
//  ],
//  "support": {
//    "url": "https://contentcaddy.io?utm_source=reqres&utm_medium=json&utm_campaign=referral",
//    "text": "Tired of writing endless social media content? Let Content Caddy generate it for you."
//  }
//}
