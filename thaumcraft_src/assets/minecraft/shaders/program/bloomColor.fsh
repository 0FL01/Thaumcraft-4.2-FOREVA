#version 120

uniform sampler2D DiffuseSampler;
uniform vec2 OutSize;
varying vec2 texCoord;

vec4 RGBtoHSL( vec4 col )
{
        float red   = col.r;
        float green = col.g;
        float blue  = col.b;
        float minc  = min(min( col.r, col.g),col.b) ;
        float maxc  = max(max( col.r, col.g),col.b);
        float delta = maxc - minc;
        float lum = (minc + maxc) * 0.5;
        float sat = 0.0;
        float hue = 0.0;
        if (lum > 0.0 && lum < 1.0) {
                float mul = (lum < 0.5)  ?  (lum)  :  (1.0-lum);
                sat = delta / (mul * 2.0);
        }
        vec3 masks = vec3(
                (maxc == red   && maxc != green) ? 1.0 : 0.0,
                (maxc == green && maxc != blue)  ? 1.0 : 0.0,
                (maxc == blue  && maxc != red)   ? 1.0 : 0.0
        );
        vec3 adds = vec3(
                          ((green - blue ) / delta),
                2.0 + ((blue  - red  ) / delta),
                4.0 + ((red   - green) / delta)
        );
        float deltaGtz = (delta > 0.0) ? 1.0 : 0.0;
        hue += dot( adds, masks );
        hue *= deltaGtz;
        hue /= 6.0;
        if (hue < 0.0)
                hue += 1.0;
        return vec4( hue, sat, lum, col.a );
}

vec4 HSLtoRGB( vec4 col )
{
    const float onethird = 1.0 / 3.0;
    const float twothird = 2.0 / 3.0;
    const float rcpsixth = 6.0;

    float hue = col.x;
    float sat = col.y;
    float lum = col.z;

    vec3 xt = vec3(
        rcpsixth * (hue - twothird),
        0.0,
        rcpsixth * (1.0 - hue)
    );

    if (hue < twothird) {
        xt.r = 0.0;
        xt.g = rcpsixth * (twothird - hue);
        xt.b = rcpsixth * (hue      - onethird);
    } 

    if (hue < onethird) {
        xt.r = rcpsixth * (onethird - hue);
        xt.g = rcpsixth * hue;
        xt.b = 0.0;
    }

    xt = min( xt, 1.0 );

    float sat2   =  2.0 * sat;
    float satinv =  1.0 - sat;
    float luminv =  1.0 - lum;
    float lum2m1 = (2.0 * lum) - 1.0;
    vec3  ct     = (sat2 * xt) + satinv;

    vec3 rgb;
    if (lum >= 0.5)
         rgb = (luminv * ct) + lum2m1;
    else rgb =  lum    * ct;

    return vec4( rgb, col.a );
}


//samples color from 81 places on the screen and averages them
vec4 averageColor=(
    texture2D(DiffuseSampler, vec2(0.1,0.1))+
    texture2D(DiffuseSampler, vec2(0.1,0.2))+
    texture2D(DiffuseSampler, vec2(0.1,0.3))+
    texture2D(DiffuseSampler, vec2(0.1,0.4))+
    texture2D(DiffuseSampler, vec2(0.1,0.5))+
    texture2D(DiffuseSampler, vec2(0.1,0.6))+
    texture2D(DiffuseSampler, vec2(0.1,0.7))+
    texture2D(DiffuseSampler, vec2(0.1,0.8))+
    texture2D(DiffuseSampler, vec2(0.1,0.9))+

    texture2D(DiffuseSampler, vec2(0.2,0.1))+
    texture2D(DiffuseSampler, vec2(0.2,0.2))+
    texture2D(DiffuseSampler, vec2(0.2,0.3))+
    texture2D(DiffuseSampler, vec2(0.2,0.4))+
    texture2D(DiffuseSampler, vec2(0.2,0.5))+
    texture2D(DiffuseSampler, vec2(0.2,0.6))+
    texture2D(DiffuseSampler, vec2(0.2,0.7))+
    texture2D(DiffuseSampler, vec2(0.2,0.8))+
    texture2D(DiffuseSampler, vec2(0.2,0.9))+

    texture2D(DiffuseSampler, vec2(0.3,0.1))+
    texture2D(DiffuseSampler, vec2(0.3,0.2))+
    texture2D(DiffuseSampler, vec2(0.3,0.3))+
    texture2D(DiffuseSampler, vec2(0.3,0.4))+
    texture2D(DiffuseSampler, vec2(0.3,0.5))+
    texture2D(DiffuseSampler, vec2(0.3,0.6))+
    texture2D(DiffuseSampler, vec2(0.3,0.7))+
    texture2D(DiffuseSampler, vec2(0.3,0.8))+
    texture2D(DiffuseSampler, vec2(0.3,0.9))+

    texture2D(DiffuseSampler, vec2(0.4,0.1))+
    texture2D(DiffuseSampler, vec2(0.4,0.2))+
    texture2D(DiffuseSampler, vec2(0.4,0.3))+
    texture2D(DiffuseSampler, vec2(0.4,0.4))+
    texture2D(DiffuseSampler, vec2(0.4,0.5))+
    texture2D(DiffuseSampler, vec2(0.4,0.6))+
    texture2D(DiffuseSampler, vec2(0.4,0.7))+
    texture2D(DiffuseSampler, vec2(0.4,0.8))+
    texture2D(DiffuseSampler, vec2(0.4,0.9))+

    texture2D(DiffuseSampler, vec2(0.5,0.1))+
    texture2D(DiffuseSampler, vec2(0.5,0.2))+
    texture2D(DiffuseSampler, vec2(0.5,0.3))+
    texture2D(DiffuseSampler, vec2(0.5,0.4))+
    texture2D(DiffuseSampler, vec2(0.5,0.5))+
    texture2D(DiffuseSampler, vec2(0.5,0.6))+
    texture2D(DiffuseSampler, vec2(0.5,0.7))+
    texture2D(DiffuseSampler, vec2(0.5,0.8))+
    texture2D(DiffuseSampler, vec2(0.5,0.9))+

    texture2D(DiffuseSampler, vec2(0.6,0.1))+
    texture2D(DiffuseSampler, vec2(0.6,0.2))+
    texture2D(DiffuseSampler, vec2(0.6,0.3))+
    texture2D(DiffuseSampler, vec2(0.6,0.4))+
    texture2D(DiffuseSampler, vec2(0.6,0.5))+
    texture2D(DiffuseSampler, vec2(0.6,0.6))+
    texture2D(DiffuseSampler, vec2(0.6,0.7))+
    texture2D(DiffuseSampler, vec2(0.6,0.8))+
    texture2D(DiffuseSampler, vec2(0.6,0.9))+

    texture2D(DiffuseSampler, vec2(0.7,0.1))+
    texture2D(DiffuseSampler, vec2(0.7,0.2))+
    texture2D(DiffuseSampler, vec2(0.7,0.3))+
    texture2D(DiffuseSampler, vec2(0.7,0.4))+
    texture2D(DiffuseSampler, vec2(0.7,0.5))+
    texture2D(DiffuseSampler, vec2(0.7,0.6))+
    texture2D(DiffuseSampler, vec2(0.7,0.7))+
    texture2D(DiffuseSampler, vec2(0.7,0.8))+
    texture2D(DiffuseSampler, vec2(0.7,0.9))+

    texture2D(DiffuseSampler, vec2(0.8,0.1))+
    texture2D(DiffuseSampler, vec2(0.8,0.2))+
    texture2D(DiffuseSampler, vec2(0.8,0.3))+
    texture2D(DiffuseSampler, vec2(0.8,0.4))+
    texture2D(DiffuseSampler, vec2(0.8,0.5))+
    texture2D(DiffuseSampler, vec2(0.8,0.6))+
    texture2D(DiffuseSampler, vec2(0.8,0.7))+
    texture2D(DiffuseSampler, vec2(0.8,0.8))+
    texture2D(DiffuseSampler, vec2(0.8,0.9))+

    texture2D(DiffuseSampler, vec2(0.9,0.1))+
    texture2D(DiffuseSampler, vec2(0.9,0.2))+
    texture2D(DiffuseSampler, vec2(0.9,0.3))+
    texture2D(DiffuseSampler, vec2(0.9,0.4))+
    texture2D(DiffuseSampler, vec2(0.9,0.5))+
    texture2D(DiffuseSampler, vec2(0.9,0.6))+
    texture2D(DiffuseSampler, vec2(0.9,0.7))+
    texture2D(DiffuseSampler, vec2(0.9,0.8))+
    texture2D(DiffuseSampler, vec2(0.9,0.9))
)/81.0;


void main() {
    vec4 inputColor = texture2D(DiffuseSampler, texCoord);
    vec4 inputHSL = RGBtoHSL(inputColor);
    
    //desaturate dark things
    float brightness = min(0.2,inputHSL.b)*5.0;
    vec4 finalHSL = inputHSL*vec4(1.0,0.5+(brightness*0.5),1.0,1.0);
    vec4 satAjustedColor=HSLtoRGB(finalHSL);
    
    //HDR
    float averageBrightness=RGBtoHSL(averageColor).b;
    //vec4 averagedColor=satAjustedColor*max((1.0-averageBrightness)*1.5,0.8);
    vec4 averagedColor=satAjustedColor*min(max(0.3/averageBrightness,0.7),4.0);

    //Ajust for stronger or subtler HDR
    gl_FragColor = (averagedColor*0.4+satAjustedColor*0.6);

    
}
