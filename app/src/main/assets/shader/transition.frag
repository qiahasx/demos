#version 300 es
precision mediump float;

uniform sampler2D oldTexture;
uniform sampler2D newTexture;
uniform float progress;
uniform vec2 iResolution;
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
    return texture(oldTexture, uv).rgb;;
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
    //    float angle = 45.0;        // 擦除角度（单位：度）
    //    float smoothness = 0.02;   // 边缘羽化范围
    //    vec2 uv = fragCoord;
    //
    //    // 计算标准化方向向量
    //    vec2 direction = normalize(vec2(cos(radians(angle)), sin(radians(angle))));
    //
    //    // 计算梯度范围（适配任意角度）
    //    float min_gradient = dot(vec2(0.0), direction);  // 左下角投影值
    //    float max_gradient = dot(vec2(1.0), direction);  // 右上角投影值
    //    float gradient = dot(uv, direction);
    //
    //    // 将梯度值映射到0-1范围
    //    float normalized_gradient = (gradient - min_gradient) / (max_gradient - min_gradient);
    //
    //    // 计算遮罩（确保progress=1时完全覆盖）
    //    float mask = smoothstep(
    //    progress - smoothness,
    //    progress + smoothness,
    //    normalized_gradient * (1.0 + smoothness) // 扩展末端边界
    //    );
    //
    //    fragColor = mix(
    //    texture(newTexture, uv),
    //    texture(oldTexture, uv),
    //    clamp(mask, 0.0, 1.0)
    //    );

    float angle = 45.0;// 擦除角度（单位：度）
    float smoothness = 0.02;// 边缘羽化
    vec2 uv = fragCoord;
    float gradient = dot(uv, vec2(cos(radians(angle)), sin(radians(angle))));
    float mask = smoothstep(progress - smoothness, progress + smoothness, gradient);
    fragColor = mix(texture(oldTexture, uv), texture(newTexture, uv), mask);
}

void radialUnfold() {
    vec2 center = vec2(0.5);// 中心点
    float speed = 2.0;// 展开速度
    vec2 uv = fragCoord;
    float dist = length(uv - center);
    float mask = smoothstep(progress - 0.1, progress, dist);
    fragColor = mix(texture(newTexture, uv), texture(oldTexture, uv), mask);
}

void fade() {
    float fadePower = 1.0f;
    float scaleRatio = 0.2f;
    vec2 uv = fragCoord;
    float midProgress = 2.0 * abs(progress - 0.5);
    float fadeIn = pow(progress, fadePower);
    float fadeOut = pow(1.0 - progress, fadePower);
    vec4 oldColor = texture(oldTexture, uv) * fadeOut;
    vec4 newColor = texture(newTexture, uv) * fadeIn;
    vec4 fallback = (progress < 0.5) ? texture(oldTexture, fragCoord) : texture(newTexture, fragCoord);
    fragColor = (uv.x >=0.0 && uv.x <=1.0 && uv.y >=0.0 && uv.y <=1.0) ? (oldColor + newColor) : fallback;
}

void warpFade() {
    float waveFreq = 8.0;// 波形频率
    float waveAmp = 0.04;// 波形幅度
    float distortSpeed = 2.0;// 扭曲速度

    vec2 uv = fragCoord;

    // 动态扭曲
    float wave = sin(uv.y * waveFreq + progress * distortSpeed * 10.0) * waveAmp;
    vec2 warpedUV = uv + vec2(wave, 0.0);

    // 渐变混合
    float fade = smoothstep(0.2, 0.8, progress);
    vec4 oldColor = texture(oldTexture, uv) * (1.0 - fade);
    vec4 newColor = texture(newTexture, uv) * fade;

    fragColor = oldColor + newColor;
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
        warpFade();
        break;
        case 5:
        zoomBlur();
        break;
        case 6:
        brun();
        break;
    }
}