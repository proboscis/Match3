#define PI 3.1415926535897932384626433832795
#define ROT90DEG mat2(0,-1,1,0)
attribute vec2 a_position;
attribute vec4 a_color;
attribute vec2 a_normal;
attribute float a_width;
attribute float a_length;
uniform mat4 u_projTrans;
varying vec4 v_color;
varying vec2 v_texCoords;
void main(){
    vec2 nor = normalize(a_normal);
    v_color = a_color * vec4(1,1,1,0.5);
    //v_color = vec4(1,1,1,1);
    //v_color = vec4(1,1,0,1);
    float w = a_width;
    nor *= w * 10.0;
    float tmp = nor.x;
    nor.x = -nor.y;
    nor.y = tmp;
    nor += a_position;
    float len = a_length;
    if(w > 0.0){
        v_texCoords = vec2(0,len);
    }else{
        v_texCoords = vec2(1,len);
    }
    gl_Position =  u_projTrans * vec4(nor,0,1);
}