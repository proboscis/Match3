#ifdef GL_ES
//precision mediump float;
#endif
varying vec4 v_color;
//varying vec2 v_texCoords;
uniform sampler2D u_sampler0;
uniform sampler2D u_sampler1;
void main(){
    gl_FragColor = v_color;//* texture2D(u_sampler1, gl_PointCoord);
}