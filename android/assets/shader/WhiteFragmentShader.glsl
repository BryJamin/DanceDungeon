#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {

  vec4 texColor = texture2D(u_texture, v_texCoords);

  //Tutorial used 255,255 but it can be 1,1,1 apparently
  vec3 white = texColor.rgb + vec3(255, 255, 255);

  texColor.rgb = white;

    //red flash
  //gl_FragColor = vec4(1 ,0,0, texColor.a);

  //gl_FragColor = v_color * texColor;

  gl_FragColor = vec4(1,1,1, texColor.a);
}