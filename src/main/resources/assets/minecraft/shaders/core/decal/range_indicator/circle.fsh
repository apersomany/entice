#version 150

uniform sampler2D DepthSampler;

uniform mat4 MvpInvMat;
uniform float Radius;

out vec4 fragColor;

void main() {
    vec4 modelPos = MvpInvMat * vec4(gl_FragCoord.xy, texture2D(DepthSampler, gl_FragCoord.xy + 0.5).x, 0);
    modelPos /= modelPos.w;
    if (length(modelPos.xz) < 2) {
        fragColor = vec4(1.0);
    } else {
        discard;
    }
}
