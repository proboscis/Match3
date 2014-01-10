#ifdef GL_ES
//precision mediump float;
#endif
varying vec2 v_texCoords;
uniform sampler2D u_sampler0;
uniform float u_dt;
uniform bool u_init;
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

void main(){
    if(u_init){
        vec2 r = rand(vec2(0.1,0.4));
        gl_FragColor = vec4(0,0,0,0);
    }else{
        vec4 t = texture2D(u_sampler0,v_texCoords);
        vec2 a = vec2(1,1);
        //TODO it seems that the texture has only 8bits for each rgba values.
        //glTexImage2D(GL_TEXURE_2D, level, GL_RGBA width, height, border, GL_RGBA, GL_FLOAT, data);this must be done
        vec2 v = t.xy + a * u_dt;
        vec2 p = t.zw + v * u_dt;
        gl_FragColor = vec4(v,p);
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