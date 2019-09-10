/**!
 * Page Javascript
 */

/////////////////////////////////////////////////
// Global
/////////////////////////////////////////////////

// response (Native --> Web)
function callbackNativeResponse(data) {
    Progress.hide();

    alert(data);
    console.log("callbackNativeResponse(): data = " + data);
}

// response (Native --> Web)
function callbackTakePicture(data) {
    alert(data);
    console.log("callbackTakePicture(): data = " + data);
}


/////////////////////////////////////////////////
// Error
/////////////////////////////////////////////////
(function() {
    window.onerror = function (err) {
        console.log(err);
        for (var i=0; i < arguments.length; i++) {
            console.log("[" + i + "] : " + arguments[i]);
        }
    };
})();


/////////////////////////////////////////////////
// Document Ready
/////////////////////////////////////////////////
(function() {
    function DOMContentLoaded () {
        console.log('document.ready ...');

        document.querySelector('#call-android-methods-recommended').addEventListener('click', function (e) {
            Progress.show();

            let args = {
                a: "A",
                b: 1,
                c: false,
                d: {
                    d1:"d1",
                    d2:2
                }
            };
            NativeBridge.call("apiRecommended", args, "callbackNativeResponse");
        });

        document.querySelector('#call-android-methods-not-recommended').addEventListener('click', function (e) {
            Progress.show();

            let args = {
                a: "A",
                b: 1,
                c: false,
                d: {
                    d1:"d1",
                    d2:2
                }
            };
            NativeBridge.call("apiNotRecommended", args, "callbackNativeResponse");
        });

        document.querySelector('#native-take-a-picture').addEventListener('click', function (e) {
            let args = {
                a: "A",
                b: 1,
                c: false,
                d: {
                    d1:"d1",
                    d2:2
                }
            };
            NativeBridge.call("apiTakePicture", args, "callbackTakePicture");
        });

        document.querySelector('#req-microphone').addEventListener('click', function (e) {
            Permission.request("microphone");
        });

        document.querySelector('#req-camera').addEventListener('click', function (e) {
            Permission.request("camera");
        });
    }

	if ( document.readyState !== 'loading' ) {
		console.log( 'document is already ready, just execute code here' );
		DOMContentLoaded();
	} else {
		document.addEventListener('DOMContentLoaded', function () {
			console.log( 'document was not ready, place code here' );
			DOMContentLoaded();
		});
	}

})();