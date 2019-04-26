/**!
 * native bridge Javascript
 */

// request (Web --> Native)
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
        hideProgress();
    }
}
