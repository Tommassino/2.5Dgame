varying vec2 texCoord;
uniform sampler2D colorMap; 
uniform sampler2D lightMap;

void main(){             
     vec4 c = texture2D(colorMap, texCoord);
     vec4 l = texture2D(lightMap, texCoord);
     gl_FragColor = c*(vec4(1,1,1,1)-l);
}