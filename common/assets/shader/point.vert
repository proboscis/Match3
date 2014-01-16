#define PI 3.1415926535897932384626433832795
attribute vec2 a_position;
uniform mat4 u_projModelView;
uniform sampler2D u_sampler0;
uniform sampler2D u_sampler1;
uniform sampler2D u_sampler2;
uniform float u_pointSize;
uniform float u_et;
varying vec4 v_color;
varying vec2 v_texCoords;
void main(){
    vec4 t = texture2D(u_sampler0,a_position);
    gl_Position = u_projModelView * vec4(t.z,t.w,0,1);
    //v_color = vec4(t.xy,t.zw*0.001);
    float age = texture2D(u_sampler2,a_position);
    v_color = texture2D(u_sampler1,vec2(a_position.x,1-a_position.y))*(10.0-u_et)/10.0;
    //v_color=vec4(a_position,0,1);
    gl_PointSize = u_pointSize;
}