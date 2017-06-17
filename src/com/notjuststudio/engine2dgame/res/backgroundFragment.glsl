#version 330

in vec2 textureCoords;

uniform vec2 scale;
uniform sampler2D background;

out vec4 out_Color;

void main() {

    vec4 color = texture(background, vec2(textureCoords.x * scale.x, textureCoords.y * scale.y));

    out_Color = color;

}
