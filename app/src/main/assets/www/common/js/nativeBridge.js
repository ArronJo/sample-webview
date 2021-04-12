/**!
 * native bridge Javascript
 */

const NativeBridge = {

    // request (Web --> Native)
    call : function (command, args, callback) {
        let jsonObject = {
            command: command,
            args: args,
            callback: callback
        };

        let query = btoa(encodeURIComponent(JSON.stringify(jsonObject)));

        if (window.AndroidBridge) {
            AndroidBridge.callNativeMethod("native://callNative?" + query);
        } else if (/iPhone|iPod|iPad/i.test(navigator.userAgent)) {
            if (window.webkit && window.webkit.callbackHandler) {
                window.webkit.messageHandlers.callbackHandler.postMessage("callNative?" + query);
            } else {
                window.location.href = "native://callNative?" + query;
            }
        } else {
            console.warn("Native calls are not supported.");
            hideProgress();
        }
    }

};
