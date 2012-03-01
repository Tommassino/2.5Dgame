varying vec2 texCoord;
uniform sampler2D colorMap;  

void main(){             
     vec4 c = texture2D(colorMap, texCoord);
     gl_FragColor = c;
}