package game;
import com.badlogic.gdx.math.*;
/** Owns continuous movement, turning, anticipation and eased dodge displacement. */
public final class PlayerMotor {
 public final Vector3 velocity=new Vector3(),dashDirection=new Vector3(),recoil=new Vector3();
 public float heading=215,runBlend,lean,dodgeTime=-1,walkPhase,travelled;
 public static final float DODGE_DURATION=.34f,DODGE_DISTANCE=3.7f;
 public boolean dodging(){return dodgeTime>=0;}
 public void dodge(Vector3 direction){dashDirection.set(direction).nor();dodgeTime=0;velocity.setZero();}
 public void stop(){velocity.setZero();recoil.setZero();dodgeTime=-1;runBlend=0;}
 public Vector3 step(Vector3 intent,float dt,boolean attacking){
  Vector3 out=new Vector3();
  if(dodging()){float a=dodgeTime/DODGE_DURATION;dodgeTime=Math.min(DODGE_DURATION,dodgeTime+dt);float b=dodgeTime/DODGE_DURATION;out.set(dashDirection).scl(DODGE_DISTANCE*(MathUtils.cos(a*MathUtils.PI)-MathUtils.cos(b*MathUtils.PI))*.5f);if(dodgeTime>=DODGE_DURATION-.000001f)dodgeTime=-1;}
  else {Vector3 wanted=intent.cpy().limit(1).scl(attacking?1.25f:4.6f);Vector3 change=wanted.sub(velocity);float max=(intent.len2()>.01f?29:36)*dt;change.limit(max);lean=MathUtils.lerp(lean,intent.len2()>.01f?Math.min(.13f,change.len()*.13f):-.035f,1-(float)Math.exp(-14*dt));velocity.add(change);out.set(velocity).scl(dt);}
  out.mulAdd(recoil,dt);recoil.scl((float)Math.exp(-13*dt));runBlend=MathUtils.lerp(runBlend,MathUtils.clamp(velocity.len()/4.6f,0,1),1-(float)Math.exp(-12*dt));float dist=out.len();travelled+=dist;walkPhase+=dist*3.4f;return out;
 }
 public void turn(Vector3 direction,float dt,float speed){if(direction.len2()<.001f)return;float target=MathUtils.atan2(-direction.x,-direction.z)*MathUtils.radiansToDegrees;heading=MathUtils.lerpAngleDeg(heading,target,1-(float)Math.exp(-speed*dt));}
}
