#version 300 es
precision mediump float;

uniform sampler2D oldTexture;
uniform sampler2D newTexture;
uniform float progress;
uniform int mode;

in vec2 fragCoord;

out vec4 fragColor;

//modified zoom blur from http://transitions.glsl.io/transition/b86b90161503a0023231
const float strength = 0.3;
const float PI = 3.141592653589793;

float Linear_ease(in float begin, in float change, in float duration, in float time) {
    return change * time / duration + begin;
}

float Exponential_easeInOut(in float begin, in float change, in float duration, in float time) {
    if (time == 0.0)
    return begin;
    else if (time == duration)
    return begin + change;
    time = time / (duration / 2.0);
    if (time < 1.0)
    return change / 2.0 * pow(2.0, 10.0 * (time - 1.0)) + begin;
    return change / 2.0 * (-pow(2.0, -10.0 * (time - 1.0)) + 2.0) + begin;
}

float Sinusoidal_easeInOut(in float begin, in float change, in float duration, in float time) {
    return -change / 2.0 * (cos(PI * time / duration) - 1.0) + begin;
}

float random(in vec3 scale, in float seed) {
    return fract(sin(dot(gl_FragCoord.xyz + seed, scale)) * 43758.5453 + seed);
}

vec3 crossFade(in vec2 fragCoord, in float dissolve) {
    return mix(texture(oldTexture, fragCoord).rgb, texture(newTexture, fragCoord).rgb, dissolve);
}

void zoomBlur() {
    vec2 texCoord = fragCoord.xy;
    // Linear interpolate center across center half of the image
    vec2 center = vec2(Linear_ease(0.5, 0.0, 1.0, progress), 0.5);
    float dissolve = Exponential_easeInOut(0.0, 1.0, 1.0, progress);

    // Mirrored sinusoidal loop. 0->strength then strength->0
    float strength = Sinusoidal_easeInOut(0.0, strength, 0.5, progress);

    vec3 color = vec3(0.0);
    float total = 0.0;
    vec2 toCenter = center - texCoord;

    /* randomize the lookup values to hide the fixed number of samples */
    float offset = random(vec3(12.9898, 78.233, 151.7182), 0.0)*0.5;

    for (float t = 0.0; t <= 20.0; t++) {
        float percent = (t + offset) / 20.0;
        float weight = 1.0 * (percent - percent * percent);
        color += crossFade(texCoord + toCenter * percent * strength, dissolve) * weight;
        total += weight;
    }

    fragColor = vec4(color / total, 1.0);
}

vec3 TextureSource(vec2 uv)
{
    return texture(oldTexture, uv).rgb;
}

vec3 TextureTarget(vec2 uv)
{
    return texture(newTexture, uv).rrr;
}


float Hash(vec2 p)
{
    vec3 p2 = vec3(p.xy, 1.0);
    return fract(sin(dot(p2, vec3(37.1, 61.7, 12.4)))*3758.5453123);
}

float noise(in vec2 p)
{
    vec2 i = floor(p);
    vec2 f = fract(p);
    f *= f * (3.0-2.0*f);

    return mix(mix(Hash(i + vec2(0., 0.)), Hash(i + vec2(1., 0.)), f.x),
    mix(Hash(i + vec2(0., 1.)), Hash(i + vec2(1., 1.)), f.x),
    f.y);
}

float fbm(vec2 p)
{
    float v = 0.0;
    v += noise(p*1.)*.5;
    v += noise(p*2.)*.25;
    v += noise(p*4.)*.125;
    return v;
}

void brun()
{
    vec2 uv = fragCoord;

    vec3 src = TextureSource(uv);

    vec3 tgt = TextureTarget(uv);

    vec3 col = src;

    uv.x -= 1.5;

    float ctime = progress * 4.0f;

    // burn
    float d = uv.x+uv.y*0.5 + 0.5*fbm(uv*15.1) + ctime*1.3;
    if (d >0.35) col = clamp(col-(d-0.35)*10., 0.0, 1.0);
    if (d >0.47) {
        if (d < 0.5) col += (d-0.4)*33.0*0.5*(0.0+noise(100.*uv+vec2(-ctime*2., 0.)))*vec3(1.5, 0.5, 0.0);
        else col += tgt; }

    fragColor.rgb = col;
}

void slide() {
    if (fragCoord.x <= progress && fragCoord.y >= (1.0f - progress)) {
        float newX = 1.0f - progress + fragCoord.x;
        float newY = progress - 1.0f + fragCoord.y;
        fragColor = texture(newTexture, vec2(newX, newY));
    } else {
        fragColor = texture(oldTexture, fragCoord);
    }
}

void linearWipe() {
    float angle = 45.0f;
    float smoothness = 0.02f;
    vec2 dir = vec2(cos(radians(angle)), sin(radians(angle)));
    float gradient = dot(fragCoord, dir) / dot(vec2(1.0f), dir);
    float mask = smoothstep(progress, progress + smoothness, gradient);
    fragColor = mix(texture(newTexture, fragCoord), texture(oldTexture, fragCoord), mask);
}

void radialUnfold() {
    vec2 center = vec2(0.5f);
    float smoothness = 0.1f;
    vec2 uv = (fragCoord - center) * (sqrt(2.0f) - smoothness);
    float dist = length(uv);
    float mask = smoothstep(progress - smoothness, progress, dist);
    fragColor = mix(texture(newTexture, fragCoord), texture(oldTexture, fragCoord), mask);
}

void fade() {
    float midProgress = 2.0f * abs(progress - 0.5f);
    float fadeIn = progress;
    float fadeOut = (1.0f - progress);
    vec4 oldColor = texture(oldTexture, fragCoord) * fadeOut;
    vec4 newColor = texture(newTexture, fragCoord) * fadeIn;
    fragColor = oldColor + newColor;
}

void rotatingTilesTransition() {
    vec2 center = vec2(0.5f);
    float tileSize = 0.1f;
    float maxRotation = radians(360.0);
    float time = 0.4f;

    float maxDist = length(center - vec2(tileSize / 2.0f));
    vec2 tileID = floor(fragCoord / tileSize);
    // 局部坐标
    vec2 localUV = (fragCoord - tileID * tileSize) / tileSize;

    float distToCenter = length((tileID + 0.5f) * tileSize - center);
    float trigge = distToCenter * (1.0f - time) / maxDist;
    float tileProgress = smoothstep(trigge, trigge + time, progress);

    float angle = maxRotation * tileProgress;
    mat2 rotation = mat2(cos(angle), -sin(angle), sin(angle), cos(angle));
    // 局部坐标
    vec2 rotatedUV = rotation * (localUV - 0.5f) + 0.5f;

    vec4 oldColor = texture(oldTexture, tileID * tileSize + rotatedUV * tileSize);
    vec4 newColor = texture(newTexture, tileID * tileSize + rotatedUV * tileSize);
    float mixFactor = pow(tileProgress, 4.0);
    fragColor = mix(oldColor, newColor, mixFactor);
}

void main() {
    switch (mode) {
        case 0:
        slide();
        break;
        case 1:
        linearWipe();
        break;
        case 2:
        radialUnfold();
        break;
        case 3:
        fade();
        break;
        case 4:
        rotatingTilesTransition();
        break;
        case 5:
        zoomBlur();
        break;
        case 6:
        brun();
        break;
    }
}