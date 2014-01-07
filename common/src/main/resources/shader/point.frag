#ifdef GL_ES
precision mediump float;
#endif
varying vec4 v_color;
//varying vec2 v_texCoords;
uniform sampler2D u_sampler0;
void main(){
    gl_FragColor = v_color * texture2D(u_sampler0, gl_PointCoord);
    //gl_FragColor = v_color * texture2D(u_sampler0, vec2(0.3,0.3));
}