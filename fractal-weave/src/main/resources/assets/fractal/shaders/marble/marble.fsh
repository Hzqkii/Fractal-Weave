#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 resolution;
uniform float time;
varying vec2 v_texCoord;

const int complexity = 25;
const float fluid_speed = 108.0;

void main()
{
  vec2 p = v_texCoord;
  for(int i = 1; i <complexity; i++)
  {
    vec2 newp = p + 0.001;
    newp.x += 0.6 / float(i) * sin(float(i) * p.y + 1 / fluid_speed + 0.3 * float(i)) + 0.5;
    newp.y += 0.6 / float(i) * sin(float(i) * p.x + 1 / fluid_speed + 0.3 * float(i + 10)) - 0.5;
    p=newp;
  }

  float mix_ratio = 0.5 * sin(4.0 * p.x) + 0.6;
  vec3 col = mix(vec3(0.0, 0.0, 1.0), vec3(0.6, 0.9, 1.0), mix_ratio);
  gl_FragColor = vec4(col, 1.0);
}