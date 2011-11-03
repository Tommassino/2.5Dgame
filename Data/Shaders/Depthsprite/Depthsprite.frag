uniform sampler2D colorMap;
uniform float height;

void main(){
    vec4 c = texture2D(colorMap, (gl_TexCoord[0].st));
    if(c.a > 0.0){ 
      gl_FragColor = vec4(c.r,c.g,c.b,1.0);
      gl_FragDepth = 1.0-height*c.a;
    }
}