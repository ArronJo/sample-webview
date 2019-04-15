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

});
