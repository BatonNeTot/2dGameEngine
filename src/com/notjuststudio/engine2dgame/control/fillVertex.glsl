#version 330

in vec2 vertexPos;

out vec2 textureCoords;

void main() {

       gl_Position = vec4(vertexPos, 0.0, 1.0);
       textureCoords = vec2(vertexPos.x / 2 + 0.5, 0.5 - vertexPos.y / 2);

}
