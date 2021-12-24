/**!
 * Page Javascript
 */

/////////////////////////////////////////////////
// Utilities
/////////////////////////////////////////////////

function removeAllChild ($el) {
    if ($el.children.length > 0) {
        while ($el.firstChild) {
            $el.removeChild($el.firstChild);
        }
    }
}


/////////////////////////////////////////////////
// Global
/////////////////////////////////////////////////

// response (Native --> Web)
function callbackTakePicture(data, base64String) {
    console.log("callbackTakePicture(): data = " + data);

    var $preview = document.querySelector('#img-preview');
    removeAllChild($preview);

    var $img = document.createElement('img');
    $img.src = 'data:image/png;base64,' + base64String;
    $img.width = '320';

    $preview.appendChild($img);
}


/////////////////////////////////////////////////
// Document Ready
/////////////////////////////////////////////////

$(document).ready(function() {
    console.info('document.ready ...');

    $('#native-take-a-picture').on('click', function () {
        NativeBridge.call("apiTakePicture", { a:"A", b:1, c:false, d:{ d1:"d1", d2:2 } }, "callbackTakePicture");
    });

    $('#file-chooser-image').on('change', function (e) {
        var $preview = document.querySelector('#img-preview');
        removeAllChild($preview);

        var file = e.target.files[0];
        LoadImageHelper.loadImage(file, function (result, img, data, base64String) {
            if (result) {
                img.width = '320';
                $preview.appendChild(img);
            }
        });
    });

});
