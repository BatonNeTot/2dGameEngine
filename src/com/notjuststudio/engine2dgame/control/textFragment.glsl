#version 330

in vec2 textureCoords;

uniform vec4 color;
uniform vec4 outline;
uniform sampler2D guiTexture;

uniform float thickness = 0.50;
uniform float edge = 0.10;

const float borderThickness = 0.40;
const float borderEdge = 0.10;

out vec4 out_Color;

void main() {

    float distance = 1.0 - texture(guiTexture, textureCoords.xy).a;

    float alpha = (1.0 - smoothstep(thickness, thickness + edge, distance));
    float borderAlpha = (1.0 - smoothstep(thickness + borderThickness, thickness + borderThickness + borderEdge, distance));

    if (distance < thickness)
    {
        out_Color = vec4(color.rgb, color.a * alpha);
    }
    else if (distance > thickness + edge)
    {
        out_Color = vec4(outline.rgb, outline.a * borderAlpha);
    }
    else
    {
        out_Color = mix(vec4(color.rgb, color.a * alpha), vec4(outline.rgb, outline.a * borderAlpha), 0.5);
    }

}
