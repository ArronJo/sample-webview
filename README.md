
# What is sample-webview?
This project is a sample for developing android applications using webview.


# Release Note
|    Date    |      Comment      |
|------------|-------------------|
| 2020-07-15 | Added file download function in webview. (setDownloadListener()) |
| 2019-08-02 | Added support for the "audio / video recording" feature. |
| 2019-06-14 | Added support for the "multiple windows" features. |
| 2019-06-12 | Added Full-screen video playback on the web. |
| 2019-04-12 | The first commit. |


# Support Features
1. File Chooser
```code
<input type="file" accept="image/*" />
<input type="file" accept="audio/*" />
<input type="file" accept="video/*" />
<input type="file" accept="*/*" />
```

2. Geolocation
```code
<script>
navigator.geolocation.watchPosition(function(Position) {
    console.log("watch position success.", Position);
})
</script>
```

3. Full-screen video playback
```code
<video width="100%" height="100%" controls>
    <source src="https://www.w3schools.com/html/movie.mp4" type="video/mp4">
    <source src="https://www.w3schools.com/html/movie.ogg" type="video/ogg">
    Your browser does not support the video tag.
</video>
```

4. Native Interface
```code
<script>
function callNative(command, args, callback) {
    let jsonObject = {
        command: command,
        args: args,
        callback: callback
    };
    
    let query = btoa(encodeURIComponent(JSON.stringify(jsonObject)));

    if (window.AndroidBridge) {
        AndroidBridge.callNativeMethod("native://callNative?" + query);
    } else if (/iPhone|iPod|iPad/i.test(navigator.userAgent)) {
        window.location.href = "native://callNative?" + query;
    } else {
        console.warn("Native calls are not supported.");
    }
}

function callbackNativeResponse(data) {
    alert(data);
}

callNative("apiSample", { num:10, str:"string", bool:true }, "callbackNativeResponse");
</script>
```


# License 
```code
Copyright (C) 2018 Aaron Jo (mcharima5@gmail.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0
      
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
