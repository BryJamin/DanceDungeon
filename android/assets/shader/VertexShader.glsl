attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;

varying vec4 v_color;
varying vec2 v_texCoords;

void main() {
    v_color = a_color;
    v_texCoords = a_texCoord0;
    gl_Position = u_projTrans * a_position;
}


/* THIS WAS FOR THE HIGHLIGHT THINGIE THAT DIDN'T REALLY PAN OUT GREAT
uniform mat4 u_projTrans;

attribute vec4 a_position;
attribute vec2 a_texCoord0;
attribute vec4 a_color;

varying vec4 v_color;
varying vec2 v_texCoord;

uniform vec2 u_viewportInverse;

void main() {
    gl_Position = u_projTrans * a_position;
    v_texCoord = a_texCoord0;
    v_color = a_color;
}*/
