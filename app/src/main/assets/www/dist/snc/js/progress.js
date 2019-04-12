/**!
 * progress Javascript
 * @import [anim.css]
 */

// show progress
function showProgress() {
    var e = document.createElement('div');
    e.className = "anim-progress-layer";

    var elem = document.getElementsByTagName('body')[0];
    elem.parentNode.appendChild(e);
}

// hide progress
function hideProgress() {
    var elem = document.getElementsByClassName('anim-progress-layer')[0];
    elem.parentNode.removeChild(elem);
}
