package neighborhood;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.shaders.*;
import com.badlogic.gdx.graphics.g3d.utils.*;
/** libGDX material rendering with a wider, nine-tap soft shadow filter. */
public final class SceneRenderer {
    private SceneRenderer(){}
    public static ModelBatch create(){
        String fragment=DefaultShader.getDefaultFragmentShader();
        int start=fragment.indexOf("float getShadow()"),end=fragment.indexOf("#endif //shadowMapFlag",start);
        String filter="float getShadow() { float sum=0.0; for(int x=-1;x<=1;x++) { for(int y=-1;y<=1;y++) { sum += getShadowness(vec2(float(x),float(y))*u_shadowPCFOffset*5.0); } } return sum/9.0; }\n";
        DefaultShader.Config config=new DefaultShader.Config(DefaultShader.getDefaultVertexShader(),fragment.substring(0,start)+filter+fragment.substring(end));
        return new ModelBatch(new DefaultShaderProvider(config));
    }
}
