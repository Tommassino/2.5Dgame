varying vec2 texCoord; 
uniform float tx;
uniform float ty;
uniform float meshScale;

void main(){
    texCoord = vec2(gl_Vertex.x*meshScale+tx,gl_Vertex.y*meshScale+ty);
    gl_Position = gl_ModelViewProjectionMatrix*gl_Vertex;
}