#version 120

uniform sampler2D DiffuseSampler;
varying vec2 texCoord;


//width of steps (higher numbers make quality worse)
const float blurWidth=0.002;

//number of steps (higher numbers cause more lag)
const float blurSteps=0.1;

//bloom brightness
const float amount=5.0;

void main() {
    vec4 color=texture2D(DiffuseSampler, texCoord);
    float brightness=(color.r+color.g+color.b)/3.0;

    vec4 sum = vec4(0);
    float i;
    const float foo = 10.0/(   ((blurSteps*2.0)+1.0)*4  );

    for (i = -blurSteps; i < blurSteps; i++){
        sum += texture2D(DiffuseSampler, texCoord + vec2(i, i)*blurWidth*0.7);
        sum += texture2D(DiffuseSampler, texCoord + vec2(i, -i)*blurWidth*0.7);
        sum += texture2D(DiffuseSampler, texCoord + vec2(0.0,i)*blurWidth);
        sum += texture2D(DiffuseSampler, texCoord + vec2(i,0.0)*blurWidth);
    }


    sum*=foo;
    vec4 modifier = sum*sum*(0.015-(brightness*0.01));
    vec4 outputColor = modifier*amount + color;
    
    gl_FragColor = outputColor*0.9;
}
