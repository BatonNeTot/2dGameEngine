#version 330

in vec2 textureCoords;

uniform sampler2D guiTexture;

out vec4 out_Color;

void main() {

    vec4 color = texture(guiTexture,vec2(textureCoords.x, 1 - textureCoords.y));

    out_Color = color;

}
