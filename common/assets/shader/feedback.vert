#define PI 3.1415926535897932384626433832795
attribute vec2 a_position;
attribute vec2 a_texCoord0;
uniform mat4 u_projModelView;
varying vec2 v_texCoords;
void main(){
    v_texCoords = a_texCoord0;
    gl_Position = u_projModelView * vec4(a_position,0,1);
    gl_PointSize = 1;
}
/*
1)Liskovの置換
2)テスト
3a)ポリもーふぃずむ
4)仕様、紙と鉛筆による不変条件のチェックなど
5)ソフトウェア開発(UML読む)
*/