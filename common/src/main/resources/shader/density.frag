#ifdef GL_ES
//precision mediump float;
#endif
varying vec4 v_velPos;
uniform sampler2D u_sampler0; // position and velocity
uniform sampler2D u_sampler1; // force texture
void main(){
    vec4 force = texture2D(u_sampler1, gl_PointCoord);
    gl_FragColor =vec4(force.z,1,0,0);
}