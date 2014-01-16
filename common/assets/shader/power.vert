#define PI 3.1415926535897932384626433832795
attribute vec2 a_position;
uniform mat4 u_projModelView;
uniform sampler2D u_sampler0;
uniform sampler2D u_sampler1;
uniform float u_pointSize;
varying vec4 v_velPos;
void main(){
    vec4 t = texture2D(u_sampler0,a_position);
    v_velPos = t;
    gl_Position = u_projModelView * vec4(t.zw,0,1);
    gl_PointSize = u_pointSize;
}