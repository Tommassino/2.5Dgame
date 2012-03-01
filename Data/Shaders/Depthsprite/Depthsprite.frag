uniform sampler2D colorMap;
uniform float height;
 
varying vec2 texturep; 

void main(){
     vec4 c = texture2D(colorMap, (texturep.st));
     gl_FragDepth = height*c.a;
     gl_FragColor = vec4(c.rgb,1.0); 
}