/**!
 * progress Javascript
 * @import [anim.css]
 */

const Progress = {

    show : function () {
        this.hide();

        var e = document.createElement('div');
        e.className = "anim-progress-layer";

        var elem = document.getElementsByTagName('body')[0];
        elem.parentNode.appendChild(e);
    },

    hide : function () {
        let els = document.querySelectorAll(".anim-progress-layer");
        Array.prototype.forEach.call(els, function(node) {
            if (node) {
                node.parentNode.removeChild(node);
            }
        });
    }

};