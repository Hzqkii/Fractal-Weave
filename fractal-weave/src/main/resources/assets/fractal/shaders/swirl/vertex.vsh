#version 120

varying vec2 v_texCoord;

mat4 resetRotation(mat4 m) {
    float s = length(vec3(m[0][0], m[1][0], m[2][0]));
    return mat4(s, 0.0, 0.0, 0.0, 0.0, s, 0.0, 0.0, 0.0, 0.0, s, 0.0, m[3]);
}

void main() {
    gl_Position = gl_ProjectionMatrix * resetRotation(gl_ModelViewMatrix) * gl_Vertex;

    v_texCoord = gl_Vertex.xy;
}