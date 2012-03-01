attribute vec3 position;
attribute vec2 texture;

varying vec2 texturep;
void main(){
    texturep = texture;
    gl_Position = gl_ModelViewProjectionMatrix*vec4(position,1.0);
}           