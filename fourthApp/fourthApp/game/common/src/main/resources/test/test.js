var out=java.lang.System.out
values = {
    yAmount:10,
    i:"aaa",
    interpolation:new Packages.com.badlogic.gdx.math.Interpolation{
        apply:function(alpha){
            var sin = java.lang.Math.sin
            var abs = java.lang.Math.abs
            var pi = java.lang.Math.PI
            var x = alpha*2*pi
            var result = abs(sin(x)/(x))*100
            return result
        }
    }
}