#version 120

uniform sampler2D texture;
uniform int time;

uniform float xpos;
uniform float zpos;

void main() {
	vec2 texcoord = vec2(gl_TexCoord[0]);
    vec4 color = texture2D(texture, texcoord);
    
    float ix = xpos * 1000.0;
    float iy = zpos * 1000.0;

    vec4 col = vec4(0,0,0,1);
    
    for(int i=0; i<16; i++) {
    	float mult = 1.0/(16-i);
    	float imult = 1.0/(i+1);
    	
    	float angle = (i * i * 4321 + i * 8) * 2.0F;
    	float dx = sin(angle);
    	float dy = cos(angle);
    	
    	float ox = gl_FragCoord.x + ix * (1.5-mult*0.5) - (time * dx * 0.5) * mult;
    	float oy = gl_FragCoord.y + iy * (1.5-mult*0.5) + (time * dy * 0.5) * mult;
    	
    	float tx = (ox / 48) * imult + dx;
    	float ty = (oy / 48) * imult + dy;
    	
    	vec2 tex = vec2(tx*dy + ty*dx, ty*dy - tx*dx);
    	
    	vec4 tcol = texture2D(texture, tex);
    	
    	float a = tcol.r * (0.1 + mult * 0.9);
    	
    	float r = (mod(angle, 29.0)/29.0) * 0.5 + 0.1;
    	float g = (mod(angle, 35.0)/35.0) * 0.5 + 0.4;
    	float b = (mod(angle, 17.0)/17.0) * 0.5 + 0.5;
    	
    	col = col*(1-a) + vec4(r,g,b,1)*a;
    }
    
    gl_FragColor = col;
}