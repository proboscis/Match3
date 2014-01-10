#define PI 3.1415926535897932384626433832795
attribute vec2 a_position;
uniform mat4 u_projModelView;
uniform sampler2D u_sampler0;
varying vec4 v_color;
varying vec2 v_texCoords;
void main(){
    v_color = vec4(0,1,0,1);
    vec2 p = texture2D(u_sampler0,a_position/16.0).zw*100;//TODO pass resolution to get the right index
    gl_Position = u_projModelView * vec4(p,0,1);
    gl_PointSize = 10.0;
}