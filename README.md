
# What is sample-webview?
This project is a sample for developing android applications using webview.


# Release Note
|    Date    |      Comment      |
|------------|-------------------|
| 2021-01-07 | Refactoring. |
| 2020-12-19 | Migrating apps to Android 11 (API 30). |
| 2020-12-17 | MediaStore and DownloadManager have been added for Android 10 (API 29). |
| 2020-11-16 | The version of targetSdkVersion has been changed.<br> [targetSdkVersion 28(9.0) -> 29(10.0)] |
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

2. Camera
```code
<script>
navigator.mediaDevices.getUserMedia({
    video: {
        facingMode: { exact: "environment" },
        zoom: true,
    },
})
.then(function (mediaStream) {
    var video = document.querySelector('#media-device-video');
    if (video) {
        video.srcObject = mediaStream;
        video.onloadedmetadata = function(e) {
            video.play();
        };
    }
})
</script>
```

3. Geolocation
```code
<script>
navigator.geolocation.watchPosition(function(Position) {
    console.log("watch position success.", Position);
})
</script>
```

4. Full-screen video playback
```code
<video width="100%" height="100%" controls>
    <source src="https://www.w3schools.com/html/movie.mp4" type="video/mp4">
    <source src="https://www.w3schools.com/html/movie.ogg" type="video/ogg">
    Your browser does not support the video tag.
</video>
```

5. Native Interface
```code
<script>

const NativeBridge = {

    callToNative: function (plugin, method, args, successCallback, errorCallback) {
        var cbId = _pushCallback(successCallback, errorCallback);

        let jsonObject = {
            "plugin": plugin,
            "method": method,
            "args": args,
            "cbId": cbId
        };

        let query = btoa(encodeURIComponent(JSON.stringify(jsonObject)));

        if (window.AndroidBridge) {
            AndroidBridge.callNativeMethod("native://callToNative?" + query);
        } else if (/iPhone|iPod|iPad/i.test(navigator.userAgent)) {
            if (window.webkit && window.webkit.callbackHandler) {
                window.webkit.messageHandlers.callbackHandler.postMessage("callToNative?" + query);
            } else {
                window.location.href = "native://callToNative?" + query;
            }
        } else {
            console.warn("Native calls are not supported.");
        }
    }
};

NativeBridge.callToNative(
    "api",
    "recommended",
    { a:"A", b:1, c:false, d:{ d1:"d1", d2:2 } },
    function(data) {
        console.log("response..." + JSON.stringify(data));
        alert(JSON.stringify(data));
        Progress.hide();
    },
    function(err) {
        console.error("error..." + err);
    }
);
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
