/**!
 * progress Javascript
 * @import [anim.css]
 */

const Progress = {

    show : function () {
        let els = document.querySelectorAll(".anim-progress-layer");
        Array.prototype.forEach.call(els, function(node) {
            node.parentNode.removeChild(elem);
        });

        var e = document.createElement('div');
        e.className = "anim-progress-layer";

        var elem = document.getElementsByTagName('body')[0];
        elem.parentNode.appendChild(e);
    },

    hide : function () {
        var elem = document.getElementsByClassName('anim-progress-layer')[0];
        elem.parentNode.removeChild(elem);
    }

};