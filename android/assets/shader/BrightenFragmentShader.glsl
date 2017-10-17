#version 120


varying vec2 v_texCoords;

uniform sampler2D u_texture;

void main()
{

        vec4 color = texture2D(u_texture, v_texCoords).rgba;
        float gray = ((color.r + 0.05) + (color.g + 0.05) + (color.b + 0.05)) / 3.0;
        vec3 grayscale = vec3(gray);

        gl_FragColor = vec4(grayscale, color.a);


}
