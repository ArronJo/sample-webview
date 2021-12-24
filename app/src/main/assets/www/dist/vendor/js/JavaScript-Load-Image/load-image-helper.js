/******************************************************************
 *
 * JavaScript-Load-Image Helper
 * https://github.com/blueimp/JavaScript-Load-Image
 *
 * Copyright (c) 2019 Aaron Jo. All rights reserved.
 *
 *****************************************************************/

!(function() {

	var LoadImageHelper = {};

	LoadImageHelper.loadImage = function (fileOrBlobOrUrl, callback, options) {
        options = options || {};

        var loadingImage = loadImage(
            fileOrBlobOrUrl, // Source
            function (img, data) {
                if (img.type === "error") {
                    console.error("Error loading image " + target);
                    if ("function" === typeof callback) {
                        callback(false, img, data);
                    }
                    return;
                }

                if ("function" === typeof callback) {
                    var base64String;
                    if (img instanceof HTMLCanvasElement) {
                        base64String = img.toDataURL('image/jpg', 1);
                    }
                    callback(true, img, data, base64String);
                }
            },
            {   // Options
                orientation: true,
                //canvas: true,
                meta: true,
                minWidth: options.minWidth || 160,
                minHeight: options.minHeight || 120,
                maxWidth: options.maxWidth || 320
            }
        );
    };

	window.LoadImageHelper = window.LoadImageHelper || {};

    for (var m in LoadImageHelper) {
        if (typeof LoadImageHelper[m] === "function") {
            window.LoadImageHelper[m] = LoadImageHelper[m];
        }
    }

	if ( typeof define === "function" && define.amd ) {
		define( "LoadImageHelper", [], function () {
			return LoadImageHelper;
		} );
	}

})();
