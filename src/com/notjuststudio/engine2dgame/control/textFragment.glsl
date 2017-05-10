#version 330

in vec2 textureCoords;

uniform vec4 color;
uniform sampler2D guiTexture;

const float width = 0.70;
const float edge = 0.05;

out vec4 out_Color;

void main() {

    float distance = 1.0 - texture(guiTexture,vec2(textureCoords.x, 1 - textureCoords.y)).a;

    out_Color = vec4(color.xyz, color.a * (1.0 - smoothstep(width, width + edge, distance)));

}
