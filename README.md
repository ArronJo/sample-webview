
# What is webview-test?
This project is a sample for developing android applications using webview.


# FEATURES
1. File Chooser
```code
<input type="file" accept="image/*" />
```

2. GeoLocation
```code
<script>
navigator.geolocation.watchPosition(function(Position) {
    console.log("watch position success.", Position);
})
</script>
```

3. Native Interface
```code
<script>
function callNative(command, args, callback) {
    var param = {
        command: command,
        args: args,
        callback: callback
    };

    if (window.AndroidBridge) {
        window.AndroidBridge.callNativeMethod("native://callNative?" + JSON.stringify(param));
    } else {
        console.warn("Native calls are not supported.");
    }
}

function callbackNativeResponse(data) {
    alert(data);
}

callNative("apiSample", {}, "callbackNativeResponse");
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
