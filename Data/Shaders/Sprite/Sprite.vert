varying vec2 texCoord; 
uniform float tx;
uniform float ty;

void main(){
    texCoord = vec2(gl_Vertex.x+tx,gl_Vertex.y+ty);
    gl_Position = gl_ModelViewProjectionMatrix*gl_Vertex;
}