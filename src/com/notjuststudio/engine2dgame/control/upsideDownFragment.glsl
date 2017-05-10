#version 330

in vec2 textureCoords;

uniform sampler2D guiTexture;

out vec4 out_Color;

void main() {

    out_Color = texture(guiTexture,vec2(textureCoords.x, 1 - textureCoords.y));

}
