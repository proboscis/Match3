#define PI 3.1415926535897932384626433832795
#define ROT90DEG mat2(0,-1,1,0)
attribute vec2 a_position;
attribute vec2 a_normal;
attribute float a_width;
uniform mat4 u_projTrans;
varying vec4 v_color;
varying vec2 v_texCoords;
mat2 rot2(float rad){
    float s = sin(rad);
    float c = cos(rad);
    return mat2( c, -s, s ,c);
}
float rand(vec2 n)
{
  return 0.5 + 0.5 *
     fract(sin(dot(n.xy, vec2(12.9898, 78.233)))* 43758.5453);
}
void main(){
    v_color = vec4(a_normal,1,1);
    vec2 nor = normalize(a_normal);
    float w = a_width;
    nor *= w;
    float tmp = nor.x;
    nor.x = -nor.y;
    nor.y = tmp;
    nor += a_position;
    gl_Position =  u_projTrans * vec4(nor,0,1);
}