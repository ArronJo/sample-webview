/**!
 * native bridge Javascript
 */

!(function (window) {

    const _callbackMap = {};

    function _createUUID () {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = Math.random()*16|0, v = c === 'x' ? r : (r&0x3|0x8);
            return v.toString(16);
        });
    }

    function _pushCallback (successCallback, errorCallback) {
        var _cbId = _createUUID();
        _callbackMap[_cbId] = {
            "successCallback": successCallback,
            "errorCallback": errorCallback
        };
        return _cbId;
    }

    function _popCallback (cbId) {
        var _callback = _callbackMap[cbId];
        delete _callbackMap[cbId];
        return _callback;
    }

    const NativeBridge = {

        // request (Web --> Native)
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
                hideProgress();
            }
        },

        // request (Native --> Web)
        callFromNative: function (cbId, resultCode, jsonString) {
            var cb = _popCallback(cbId);
            if ("00000" == resultCode) {
                var fn = cb['successCallback'];
                if ("function" == typeof fn) {
                    fn.apply(window, [ JSON.parse(jsonString) ]);
                } else if ("string" == typeof fn) {
                    eval(fn + "(" + jsonString + ")");
                }
            } else {
                var fn = cb['errorCallback'];
                if ("function" == typeof fn) {
                    fn.apply(window, [ JSON.parse(jsonString) ]);
                } else if ("string" == typeof fn) {
                    eval(fn + "(" + jsonString + ")");
                }
            }
        }

    };

    window.NativeBridge = NativeBridge;
})(window);