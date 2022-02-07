/**!
 * Page Javascript
 */

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

    $('#call-android-methods-recommended-1').on('click', function () {
        Progress.show();

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
                alert(err);
                Progress.hide();
            }
        );
    });

    $('#call-android-methods-recommended-2').on('click', function () {
        Progress.show();

        // response (Native --> Web)
        function callbackNativeResponse (data) {
            console.log("callbackNativeResponse(): data = " + JSON.stringify(data));
            alert(JSON.stringify(data));
            Progress.hide();
        }
        window['callbackNativeResponse'] = callbackNativeResponse;

        NativeBridge.callToNative(
            "api",
            "recommended",
            { a:"A", b:1, c:false, d:{ d1:"d1", d2:2 } },
            "callbackNativeResponse"
        );
    });

    $('#native-take-a-picture').on('click', function () {
        NativeBridge.callToNative(
            "camera",
            "takePicture",
            { a:"A", b:1, c:false, d:{ d1:"d1", d2:2 } },
            function (data) {
                 console.log("callback : data = " + data);
                 alert(data);
             }
         );
    });

    $('#req-microphone').on('click', function () {
        //navigator.mediaDevices.getUserMedia("microphone")
        navigator.mediaDevices.getUserMedia({
            audio: true
        })
        .then(function (mediaStream) {
            console.log('request: then...', mediaStream);
        })
        .catch(function (err) {
            console.error('request: error: ' + err.toString(), err);
            alert(err);
        });
    });

    // https://www.html5rocks.com/en/tutorials/getusermedia/intro/
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

            var $video = document.querySelector('#media-device-video');
            if ($video) {
                $video.srcObject = mediaStream;
                $video.onloadedmetadata = function(e) {
                    $video.play();
                };
            }
        })
        .catch(function (err) {
            console.error('request: error: ' + err.toString(), err);
            alert(err);
        });
    });


    var $video = document.querySelector('#media-device-video');
    var $canvas = document.querySelector('#canvas');
    var $img = document.querySelector('#captured-img');

    $('#media-device-video').on('click', function() {
        const $canvas = document.createElement("canvas");
        $canvas.width = $video.videoWidth;
        $canvas.height = $video.videoHeight;
        $canvas.getContext("2d").drawImage($video, 0, 0);
        // Other browsers will fall back to image/png
        var src = $canvas.toDataURL("image/webp");
        $img.src = src;
    });

    $('#btn-capture').on('click', function() {
        $canvas.width = $video.videoWidth;
        $canvas.height = $video.videoHeight;
        $canvas.getContext("2d").drawImage($video, 0, 0);
        // Other browsers will fall back to image/png
        var src = $canvas.toDataURL("image/webp");
        $img.src = src;
    });

});
