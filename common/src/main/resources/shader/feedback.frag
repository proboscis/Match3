#ifdef GL_ES
//precision mediump float;
#endif
varying vec2 v_texCoords;
uniform sampler2D u_sampler0;
uniform float u_dt;
uniform bool u_init;
uniform vec2 mouse;
vec2 rand(vec2 pos)
{
  return
    fract(
      (
        pow(
          pos+2.0,
          pos.yx+2.0
        )*22222.0
      )
    );
}
/*using an internal format:RGBA32F_ARB 0x8814 made this work!*/
void main(){
    if(u_init){
        vec2 r = rand(vec2(0.1,0.4));
        vec2 tex = v_texCoords;
        gl_FragColor = vec4(0,0,tex.x*960,tex.y*540);
    }else{
        vec4 t = texture2D(u_sampler0,v_texCoords);
        vec2 v = t.xy;
        vec2 p = t.zw;
        vec2 d = mouse - p;
        float l = length(d);
        vec2 a = normalize(d) *l * l *0.001;
        vec2 nv = v + a * u_dt;
        vec2 np = p + v * u_dt;
        if(np.x >=960 || np.x < 0 || np.y >= 540 || np.y < 0){
            //normalize(rand(nv))*100;
            np =vec2(480,270);
        }
        gl_FragColor = vec4(nv,np);
    }
}
/*
// Fill the texture with the initial particle data
        gl.texImage2D(
            // target, level, internal format, width, height
            gl.TEXTURE_2D, 0, gl.RGBA, PARTICLE_TEXTURE_WIDTH, PARTICLE_TEXTURE_HEIGHT,
            // border, data format, data type, pixels
            0, gl.RGBA, gl.FLOAT, particleData
        );
*/