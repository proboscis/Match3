#ifdef GL_ES
precision mediump float;
#endif
uniform sampler2D u_sampler0;
uniform float u_delta;
uniform int u_horizontal;
varying vec4 v_color;
varying vec2 v_texCoords;
void main(){

    float step = 5.0;
    float t = 1.0/(step*2.0+1.0);
    vec4 sum = texture2D(u_sampler0, v_texCoords)*t;
    float i = 0.0;
    if(u_horizontal > 0){
        for(i = 0.0; i <= step; i+=1.0){
            sum += texture2D(u_sampler0, v_texCoords+vec2(u_delta*i,0))*t;
            sum += texture2D(u_sampler0, v_texCoords+vec2(-u_delta*i,0))*t;
        }
    }else{
        for(i = 0.0; i <= step; i+= 1.0){
            sum += texture2D(u_sampler0, v_texCoords+vec2(0,u_delta*i))*t;
            sum += texture2D(u_sampler0, v_texCoords+vec2(0,-u_delta*i))*t;
        }
    }
    gl_FragColor = sum;
}