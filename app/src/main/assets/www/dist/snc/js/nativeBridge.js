/**!
 * native bridge Javascript
 */

// request (Web --> Native)
function callNative(command, args, callback) {
    let jsonObject = {
        command: command,
        args: encodeURIComponent(JSON.stringify(args)),
        callback: callback
    };

    if (window.AndroidBridge) {
        window.AndroidBridge.callNativeMethod("native://callNative?" + JSON.stringify(jsonObject));
    } else {
        console.warn("Native calls are not supported.");
        hideProgress();
    }
}
