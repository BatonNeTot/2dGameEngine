#version 330

in vec2 textureCoords;

uniform sampler2D guiTexture;

uniform vec2 point;
uniform vec2 size;

out vec4 out_Color;

void main() {

    out_Color = texture(guiTexture,  vec2(point.x + size.x * textureCoords.x, point.y + size.y * textureCoords.y));

}
