package game;
import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
/** Exercises actual simulation and render-resource contracts in a real graphics context. */
public final class CombatRegression extends Asterfall {
 @Override public void create(){super.create();screen="game";Enemy victim=enemies.get(0);victim.hp=1;pos.set(victim.body.pos);target.set(pos);save.xp=save.level*65-1;int before=save.level;Effect projectile=new Effect(victim.body.pos.cpy().add(0,.8f,0),2,.4f,"b7dbff");projectile.bolt=true;projectile.vel.setZero();effects.add(projectile);update(.016f);require(save.level==before+1,"Projectile kill must level up");require(!effects.isEmpty(),"Level-up VFX survives list mutation");
 spawn();Enemy melee=enemies.get(0);melee.hp=1;pos.set(melee.body.pos);target.set(pos);attack();update(.10f);require(melee.hp==1,"Sword anticipates before contact");update(.06f);require(!enemies.contains(melee),"Sword strikes at contact");require(combat.hitStop>0,"Contact causes hit-stop");
 int owned=models.owned.size();for(int round=0;round<60;round++){spawn();require(enemies.size()==1,"Only one practice enemy");Enemy e=enemies.get(0);e.hp=1;pos.set(e.body.pos);target.set(pos);motor.stop();save.xp=save.level*65-1;int level=save.level;Effect bolt=new Effect(e.body.pos.cpy().add(0,.8f,0),1,.4f,"b7dbff");bolt.bolt=true;effects.add(bolt);update(.016f);require(save.level==level+1,"Stress level-up "+round);require(models.owned.size()==owned,"Mesh resources reused across resets");}
 pos.set(0,0,-2.8f);move(0,-3.5f);require(Math.abs(pos.y-1.2f)<.01f,"Player climbs physical ramp");pos.set(4,0,-2.8f);move(0,-1);require(pos.z>=-3&&pos.y==0,"Player cannot walk up cliff face");
 spawn();Enemy enemy=enemies.get(0);Vector3 old=enemy.body.pos.cpy();pos.set(old.cpy().add(0,0,1));damage(enemy,1,false);require(enemy.knockback.len()>3&&enemy.stun>0,"Enemy reacts with recoil and knockback");combat.hitStop=0;update(.05f);require(enemy.body.pos.dst(old)>.10f,"Knockback moves enemy");
 pos.set(0,0,3.1f);presentation.rig(this,1);Vector3 base=camera.project(pos.cpy()),head=camera.project(pos.cpy().add(0,2.8f,0));require(Math.abs(head.y-base.y)>150,"Player reads substantially larger on screen");breakCrate();require(crateBroken,"One destructible prop");pos.set(TestArea.CHEST);target.set(pos);interact();require(chestOpened,"One chest opens");
 System.out.println("PASS: 60 projectile level-ups, timed melee, hit-stop, knockback, elevated collision, camera framing, prop, chest, and stable meshes.");Gdx.app.exit();}
}
