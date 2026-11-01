package neighborhood;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.*;
import com.badlogic.gdx.graphics.g3d.environment.*;
import com.badlogic.gdx.utils.Disposable;
/** One reference-driven afternoon preset. Rendering is ready for later dynamic light changes. */
@SuppressWarnings("deprecation")
public final class LightingController implements Disposable {
    public final Environment environment=new Environment();
    public final DirectionalShadowLight sun=new DirectionalShadowLight(2048,2048,68,60,1,140);
    public final Color sky=new Color();
    public LightingController(){environment.add(sun);environment.shadowMap=sun;setAfternoon();}
    public void setAfternoon(){
        sky.set(Color.valueOf("b5cbbb"));
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight,.62f,.66f,.59f,1));
        sun.set(.64f,.59f,.46f,-.48f,-1f,-.36f);
    }
    public void dispose(){sun.dispose();}
}
