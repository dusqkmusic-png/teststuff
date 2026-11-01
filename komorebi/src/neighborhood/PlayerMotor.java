package neighborhood;
import com.badlogic.gdx.math.*;
/** Simulation without rendering dependencies, shared by the playable character and checks. */
public final class PlayerMotor {
    public static final float RADIUS=.27f,SPEED=3.15f;
    public final Vector2 position=new Vector2(-2,3.5f),velocity=new Vector2();
    private final Vector2 desired=new Vector2(),delta=new Vector2();
    public float update(Vector2 direction,float dt,CollisionWorld collision){
        desired.set(direction);if(desired.len2()>1)desired.nor();desired.scl(SPEED);
        float response=direction.isZero()?24:19;
        velocity.lerp(desired,1-(float)Math.exp(-response*dt));
        if(direction.isZero()&&velocity.len2()<.0004f)velocity.setZero();
        delta.set(velocity).scl(dt);float beforeX=position.x,beforeZ=position.y;
        collision.move(position,delta,RADIUS);
        return Vector2.dst(beforeX,beforeZ,position.x,position.y);
    }
}
