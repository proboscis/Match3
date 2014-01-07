#define PI 3.1415926535897932384626433832795
attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
uniform mat4 u_projModelView;
varying vec4 v_color;
varying vec2 v_texCoords;
void main(){
    v_color = a_color;
    //v_color = vec4(0,1,0,1);
    v_texCoords = a_texCoord0;
    gl_Position = u_projModelView *a_position;
    gl_PointSize = 1;
}