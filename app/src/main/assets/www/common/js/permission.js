/**!
 * permission Javascript
 */

const Permission = {

    request : function (device) {
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
            console.log('request: then...');
        })
        .catch(function handleError(error) {
            console.error('request: error: ', error);
        });
    }

};

