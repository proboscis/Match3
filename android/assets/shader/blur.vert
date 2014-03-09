#define PI 3.1415926535897932384626433832795
attribute vec2 a_position;
attribute vec2 a_texCoord0;
uniform mat4 u_projModelView;
uniform sampler2D u_sampler0;
varying vec2 v_texCoords;
void main(){
    v_texCoords = a_texCoord0;
    gl_Position = u_projModelView * vec4(a_position,0.0,1.0);
    gl_PointSize = 1.0;
}