#define PI 3.1415926535897932384626433832795
attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
uniform mat4 u_projModelView;
varying vec4 v_color;
varying vec2 v_texCoords;
void main(){
    v_color = vec4(0,1,1,1);//a_color;
    v_texCoords = a_texCoord0;
    float s = sin(PI/4);
    float c = cos(PI/4);
    vec4 p = a_position;
    mat4 rot = mat4(
    c,-s,0,0,
    s,c,0,0,
    0,0,1,0,
    0,0,0,1);
    vec4 p2 = p - vec4(p.x,p.y,0,0);
    p2 *= rot;
    p2 += vec4(p.x,p.y,0,0);
    gl_Position = u_projModelView *p;
    gl_PointSize = 10;
}