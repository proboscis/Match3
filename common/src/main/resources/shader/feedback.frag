#ifdef GL_ES
precision mediump float;
#endif
varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_sampler0;
uniform int u_state;
void main(){
    //gl_FlagColor = vec4(u_state,0,0,1);
    //gl_FlagColor = v_color;
    gl_FragColor = v_color * texture2D(u_sampler0, v_texCoords);
}