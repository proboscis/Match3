#ifdef GL_ES
//precision mediump float;
#endif
varying vec2 v_texCoords;
uniform sampler2D u_sampler0;// velocity and position
uniform sampler2D u_sampler1;// power and viscosity map
uniform sampler2D u_sampler2;// density map
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
        gl_FragColor = vec4(0,0,tex.x*960.0,tex.y*540.0);
    }else{
        vec4 t = texture2D(u_sampler0,v_texCoords);
        vec2 v = t.xy;
        vec2 p = t.zw;
        vec2 d = vec2(480,270)-p;//mouse - p;
        vec2 index = vec2(p.x/(960.0),p.y/540.0);
        vec4 powerMap = texture2D(u_sampler1,index);
        vec2 density = texture2D(u_sampler2,index).xy;
        vec2 power = powerMap.xy * 1000;
        power += (powerMap.zw - v * density.x * density.y)*0.3;
        vec2 interact = p - mouse;
        float interactPower = length(interact);
        interactPower *= interactPower;
        interactPower = 1.0/interactPower;
        vec2 gravity = vec2(0,-400);
        vec2 a = gravity + power + normalize(interact)*interactPower*100000;
        vec2 nv = v + a * u_dt;

        vec2 np = p + nv * u_dt;
        float penalty = 100.0;
        if(np.y < 0){
            nv.y += -np.y * penalty;
        }
        if(np.y > 540){
            nv.y += (540 - np.y) * penalty;
        }
        if(np.x < 0){
            nv.x += -np.x* penalty;
        }
        if(np.x > 960){
            nv.x += (960-np.x) * penalty ;
        }
        if(length(nv) >= 1000){
            nv = normalize(nv)*1000;
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