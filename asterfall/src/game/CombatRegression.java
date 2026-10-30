package game;
import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
/** Actual gameplay update path: a projectile kill that levels up adds a VFX. */
public class CombatRegression extends Asterfall {
 @Override public void create(){super.create();screen="game";Enemy victim=enemies.get(0);victim.hp=1;pos.set(victim.body.pos);target.set(pos);save.xp=save.level*65-1;int before=save.level;
 Effect projectile=new Effect(victim.body.pos.cpy().add(0,.8f,0),2,.4f,"b7dbff");projectile.bolt=true;projectile.vel=new Vector3();effects.add(projectile);update(.016f);
 require(save.level==before+1,"Projectile kill must level up");require(effects.size()>0,"Level-up VFX must survive");
 spawn();Enemy melee=enemies.get(0);melee.hp=1;pos.set(melee.body.pos);target.set(pos);attackCd=0;attack();require(melee.hp==1,"Sword damage waits for the swing");update(.18f);require(!enemies.contains(melee),"Sword connects during the swing");
 int modelCount=models.owned.size();for(int round=0;round<60;round++){spawn();Enemy e=enemies.get(0);e.hp=1;pos.set(e.body.pos);target.set(pos);save.xp=save.level*65-1;int level=save.level;Effect bolt=new Effect(e.body.pos.cpy().add(0,.8f,0),1,.4f,"b7dbff");bolt.bolt=true;bolt.vel=new Vector3();effects.add(bolt);update(.016f);require(save.level==level+1,"Stress level-up "+round);require(models.owned.size()==modelCount,"Repeated encounters must reuse mesh assets");}System.out.println("PASS: 60 repeated projectile level-ups and stable mesh ownership.");
 System.out.println("PASS: projectile kill + level-up through real update loop, no concurrent modification.");Gdx.app.exit();}
}
