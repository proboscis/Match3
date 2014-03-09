#define PI 3.1415926535897932384626433832795
#ifdef GL_ES
precision mediump float;
#endif
uniform sampler2D u_sampler0;
uniform float u_delta;
uniform int u_horizontal;
varying vec4 v_color;
varying vec2 v_texCoords;
void main(){

    float step = 10.0 ;
    float t = 1.0/(step*2.0+1.0);
    vec4 sum = texture2D(u_sampler0, v_texCoords)*t;
    float i = 0.0;
    float v;
    if(u_horizontal > 0){
        for(i = 0.0; i <= step; i+=1.0){
            v = (cos(i/(step+1)*PI)+1)*0.5;
            sum += texture2D(u_sampler0, v_texCoords+vec2(u_delta*i,0))*t*v;
            sum += texture2D(u_sampler0, v_texCoords+vec2(-u_delta*i,0))*t*v;
        }
    }else{
        for(i = 0.0; i <= step; i+= 1.0){
            v = (cos(i/(step+1)*PI)+1)*0.5;
            sum += texture2D(u_sampler0, v_texCoords+vec2(0,u_delta*i))*t*v;
            sum += texture2D(u_sampler0, v_texCoords+vec2(0,-u_delta*i))*t*v;
        }
    }
    gl_FragColor = sum;
}