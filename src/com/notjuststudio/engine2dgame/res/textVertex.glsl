#version 330

in vec2 vertexPos;
in vec2 vertexUV;

uniform mat3 transformationMatrix;

out vec2 textureCoords;

void main() {

       gl_Position = vec4((transformationMatrix * vec3(vertexPos, 1.0)).xy, 0.0, 1.0);
       textureCoords = vertexUV;

}
