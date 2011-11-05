uniform sampler2D colorMap;
uniform float height; 
varying vec2 texturep; 

void main(){
     vec4 c = texture2D(colorMap, (texturep.st));
     if(c.a > 0.0){ 
       gl_FragColor = vec4(c.rgb,1.0);
       gl_FragDepth = 1.0-height*c.a;
     }
}