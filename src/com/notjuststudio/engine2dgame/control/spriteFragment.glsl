#version 330

in vec2 textureCoords;

uniform float alpha;
uniform sampler2D guiTexture;

uniform bool isFlipped;

out vec4 out_Color;

void main() {
    vec2 coords;
    if (isFlipped) {
        coords = vec2(textureCoords.x, 1.0 - textureCoords.y);
    } else {
        coords = textureCoords;
    }

    vec4 color = texture(guiTexture,coords);
    out_Color = vec4(color.rgb, color.a * alpha);

}
