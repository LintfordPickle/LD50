#version 150 core
precision mediump float;

uniform sampler2D textureSampler;

uniform vec2 screenDimensions = vec2(800, 100);
uniform float time = 0.;

const float pixSizeX = 4.0; //width of LED in pixel
const float pixSizeY = 4.0; //height of LED in pixel
vec3 dmdColor = vec3(0.9, 0.5, 0.3); //specify color of Dotmatrix LED

in vec2 passTexCoord;

out vec4 outColor;

void main() {
    float indexX = gl_FragCoord.x - .5;
    float indexY = gl_FragCoord.y - .5;
    float cellX = floor(indexX / pixSizeX)* pixSizeX;
    float cellY = floor(indexY / pixSizeY)* pixSizeY;

	float texAvg = 0.0;
	
	vec2 currUV = vec2(cellX/screenDimensions.x, cellY/screenDimensions.y);
    vec3 currTexVal = texture(textureSampler, passTexCoord).rgb; 
    texAvg = 0.3*currTexVal.r + 0.59*currTexVal.g + 0.11*currTexVal.b;

	vec3 col = dmdColor * texAvg;

	vec2 uvDots = vec2(fract(indexX / pixSizeX), fract(indexY / pixSizeY));
    float circle = 1. - step(.46, length(uvDots - .5));
    col = col * circle;

    float f = 0.0;
    f = sin(cellY + time * 2.) * 0.02;

	outColor = vec4(col + f, 1.);
}