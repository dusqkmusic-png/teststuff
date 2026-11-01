package neighborhood;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.*;
/** Fixed heading and restrained perspective; damped follow with a small velocity lead. */
public final class CameraController {
    public final PerspectiveCamera camera=new PerspectiveCamera(36,1440,900);
    private final Vector3 focus=new Vector3();
    private final Vector3 offset=new Vector3(12.6f,21.6f,25.2f);
    public CameraController(Player p){camera.near=.2f;camera.far=150;snap(p);}
    public void snap(Player p){focus.set(p.position.x,.6f,p.position.y);apply();}
    public void update(Player p,float dt){Vector3 desired=new Vector3(p.position.x+p.velocity.x*.17f,.6f,p.position.y+p.velocity.y*.17f);focus.lerp(desired,1-(float)Math.exp(-6.8f*dt));apply();}
    private void apply(){camera.position.set(focus).add(offset);camera.up.set(Vector3.Y);camera.lookAt(focus);camera.update();}
    public void resize(int w,int h){camera.viewportWidth=w;camera.viewportHeight=h;camera.update();}
}
