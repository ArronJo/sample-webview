/**!
 * Page Javascript
 */

// response (Native --> Web)
function callbackNativeResponse(data) {
    alert(data);
    hideProgress();
    console.log("callbackNativeResponse(): data = " + data);
}

// response (Native --> Web)
function callbackTakePicture(data) {
    alert(data);
    console.log("callbackTakePicture(): data = " + data);
}

// request permission
function requirePermission (device) {
    var audioSource = "";
    var videoSource = "";

    var constraints = {};

    if ("microphone" == device) {
        constraints.audio = {deviceId: audioSource ? {exact: audioSource} : undefined};
    } else if ("camera" == device) {
        constraints.video = {deviceId: videoSource ? {exact: videoSource} : undefined};
    }

    navigator.mediaDevices.getUserMedia(constraints)
    .then(function next(error) {
        console.log('then...');
    })
    .catch(function handleError(error) {
        console.error('Error: ', error);
    });
}

// ready
$(document).ready(function() {
    console.info('document.ready ...');

    $('#call-android-methods-recommended').on('click', function () {
        showProgress();
        callNative("apiRecommended", { a:"A", b:1, c:false, d:{ d1:"d1", d2:2 } }, "callbackNativeResponse");
    });

    $('#call-android-methods-not-recommended').on('click', function () {
        showProgress();
        callNative("apiNotRecommended", { a:"A", b:1, c:false, d:{ d1:"d1", d2:2 } }, "callbackNativeResponse");
    });

    $('#native-take-a-picture').on('click', function () {
        callNative("apiTakePicture", { a:"A", b:1, c:false, d:{ d1:"d1", d2:2 } }, "callbackTakePicture");
    });

    $('#req-microphone').on('click', function () {
        requirePermission("microphone");
    });

    $('#req-camera').on('click', function () {
        requirePermission("camera");
    });

});
