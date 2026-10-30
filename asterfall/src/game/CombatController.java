package game;
import com.badlogic.gdx.math.*;
/** Explicit anticipation, strike and recovery phases; render code never advances gameplay. */
public final class CombatController {
 public static final float DURATION=.48f,STRIKE=.14f;
 public float age=DURATION,hitStop,shake,shakeTime;public boolean struck;public int combo;
 public final Vector3 direction=new Vector3(0,0,-1);
 public boolean active(){return age<DURATION;}
 public boolean start(Vector3 aim){if(active())return false;direction.set(aim).nor();age=0;struck=false;combo=(combo+1)%2;return true;}
 public boolean advance(float dt){float previous=age;age=Math.min(DURATION,age+dt);return !struck&&previous<STRIKE&&age>=STRIKE;}
 public float lunge(float dt){float a=MathUtils.clamp((age-.10f)/.15f,0,1),b=MathUtils.clamp((age+dt-.10f)/.15f,0,1);return .52f*(Interpolation.smooth.apply(b)-Interpolation.smooth.apply(a));}
 public float phase(){return MathUtils.clamp(age/DURATION,0,1);}
 public void impact(boolean heavy){hitStop=Math.max(hitStop,heavy?.065f:.042f);shake=Math.max(shake,heavy?.16f:.095f);shakeTime=.16f;}
 public float tickReal(float dt){shakeTime=Math.max(0,shakeTime-dt);shake*=Math.exp(-17*dt);if(hitStop>0){hitStop=Math.max(0,hitStop-dt);return 0;}return dt;}
}
