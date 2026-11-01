package neighborhood;
import com.badlogic.gdx.math.*;
import java.util.*;
/** Circle against solid rectangles/circles, substepped movement with wall sliding. */
public final class CollisionWorld {
    private final List<Rectangle> boxes=new ArrayList<>();
    private final List<Vector3> circles=new ArrayList<>();
    public void box(float x,float z,float w,float d){boxes.add(new Rectangle(x-w/2,z-d/2,w,d));}
    public void circle(float x,float z,float radius){circles.add(new Vector3(x,z,radius));}
    public boolean blocked(float x,float z,float r){
        if(Math.abs(x)>29-r || z< -23+r || z>23-r)return true;
        for(Rectangle b:boxes){float dx=x-MathUtils.clamp(x,b.x,b.x+b.width),dz=z-MathUtils.clamp(z,b.y,b.y+b.height);if(dx*dx+dz*dz<r*r)return true;}
        for(Vector3 c:circles)if(Vector2.dst2(x,z,c.x,c.y)<(r+c.z)*(r+c.z))return true;
        return false;
    }
    public void move(Vector2 p,Vector2 delta,float r){
        int steps=Math.max(1,(int)Math.ceil(delta.len()/(r*.4f)));float dx=delta.x/steps,dz=delta.y/steps;
        for(int i=0;i<steps;i++){if(!blocked(p.x+dx,p.y,r))p.x+=dx;if(!blocked(p.x,p.y+dz,r))p.y+=dz;}
    }
}
