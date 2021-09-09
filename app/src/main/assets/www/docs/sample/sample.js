/**!
 * Page Javascript
 */

/////////////////////////////////////////////////
// Global
/////////////////////////////////////////////////

// response (Native --> Web)
function callbackNativeResponse(data) {
    console.log("callbackNativeResponse(): data = " + data);
    alert(data);
    Progress.hide();
}

// response (Native --> Web)
function callbackTakePicture(data) {
    console.log("callbackTakePicture(): data = " + data);
    alert(data);
}


/////////////////////////////////////////////////
// Immediately
/////////////////////////////////////////////////
(function() {
    window.onerror = function (err) {
        //console.log(arguments);
        console.log(err);
        for (var i=0; i < arguments.length; i++) {
            console.log("[" + i + "] : " + arguments[i]);
        }
    };

    var ro = new ResizeObserver(function (entries) {
        entries[0].target.classList.add('big');
    });

    if (ro && "function" === typeof ro.observe ) {
        ro.observe(window.video);
    }
})();


/////////////////////////////////////////////////
// Document Ready
/////////////////////////////////////////////////
$(document).ready(function() {
    console.info('document.ready ...');

    $('#call-android-methods-recommended').on('click', function () {
        Progress.show();
        NativeBridge.call("apiRecommended", { a:"A", b:1, c:false, d:{ d1:"d1", d2:2 } }, "callbackNativeResponse");
    });

    $('#call-android-methods-recommended-2').on('click', function () {
        Progress.show();
        NativeBridge.callToNative(
            "apiRecommended2",
            { a:"A", b:1, c:false, d:{ d1:"d1", d2:2 } },
            function(data) {
                console.log("response..." + JSON.stringify(data));
                alert(data);
                Progress.hide();
            },
            function(data) {
                console.log("error..." + data);
                alert(data);
                Progress.hide();
            }
        );
    });

    $('#call-android-methods-recommended-3').on('click', function () {
        Progress.show();
        NativeBridge.callToNative(
            "apiRecommended2",
            { a:"A", b:1, c:false, d:{ d1:"d1", d2:2 } },
            "callbackNativeResponse"
        );
    });

    $('#call-android-methods-not-recommended').on('click', function () {
        Progress.show();
        NativeBridge.call("apiNotRecommended", { a:"A", b:1, c:false, d:{ d1:"d1", d2:2 } }, "callbackNativeResponse");
    });

    $('#native-take-a-picture').on('click', function () {
        NativeBridge.call("apiTakePicture", { a:"A", b:1, c:false, d:{ d1:"d1", d2:2 } }, "callbackTakePicture");
    });

    $('#req-microphone').on('click', function () {
        navigator.mediaDevices.getUserMedia("microphone")
        .then(function (mediaStream) {
            console.log('request: then...', mediaStream);

            var video = document.querySelector('#media-device-video');
            if (video) {
                video.srcObject = mediaStream;
                video.onloadedmetadata = function(e) {
                    video.play();
                };
            }
        })
        .catch(function (err) {
            console.error('request: error: ' + err.toString(), err);
        });
    });

    $('#req-camera').on('click', function () {
        var constraints = navigator.mediaDevices.getSupportedConstraints();
        console.log(constraints);

        navigator.mediaDevices.getUserMedia({
            audio: false,
            video: {
                facingMode: { exact: "environment" },
                zoom: true,
            },
        })
        .then(function (mediaStream) {
            console.log('request: then...', mediaStream);

            var video = document.querySelector('#media-device-video');
            if (video) {
                video.srcObject = mediaStream;
                video.onloadedmetadata = function(e) {
                    video.play();
                };
            }
        })
        .catch(function (err) {
            console.error('request: error: ' + err.toString(), err);
        });
    });

});
