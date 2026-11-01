package neighborhood;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
/** Original articulated temporary character; continuous motion, never grid coordinates. */
public final class Player implements Disposable {
    private final PlayerMotor motor=new PlayerMotor();
    public final Vector2 position=motor.position,velocity=motor.velocity;
    public final Array<ModelInstance> instances=new Array<>();
    private final Array<Model> models=new Array<>();
    private ModelInstance body,leftLeg,rightLeg,leftArm,rightArm;
    
    private float heading=155,phase;
    public static final float RADIUS=PlayerMotor.RADIUS,SPEED=PlayerMotor.SPEED;
    public Player(Materials m){
        Material skin=m.color("efd0ac"),hair=m.color("354f59"),highlight=m.color("46616b"),coat=m.color("c1b582"),pants=m.color("425c73"),boots=m.color("665d53"),ivory=m.color("ecdfbb"),pack=m.color("9a7858");
        Geometry g=new Geometry();
        // Cloth torso, hem and contrasting scarf; oversized head follows reference proportions.
        g.ball(coat,0,.86f,0,.56f,.63f,.36f);g.box(coat,0,.69f,0,.47f,.18f,.32f);
        g.box(m.color("7c7056"),0,.63f,0,.48f,.07f,.33f);
        g.cylinder(skin,0,1.17f,0,.14f,.20f,.14f);
        g.ball(skin,0,1.43f,.015f,.66f,.65f,.58f);
        g.ball(hair,0,1.58f,-.07f,.73f,.56f,.64f);
        // Side locks, asymmetrical fringe and swept crown.
        g.ball(hair,-.28f,1.40f,.015f,.18f,.39f,.31f);g.ball(hair,.29f,1.43f,0,.16f,.40f,.30f);
        for(int i=0;i<5;i++){float x=-.25f+i*.115f;g.ball(i%2==0?highlight:hair,x,1.60f-Math.abs(x)*.18f,.255f,.20f,.27f+(i%2)*.09f,.16f);}
        g.cone(hair,-.12f,1.85f,-.12f,.31f,.28f,.25f);g.ball(highlight,.15f,1.75f,-.11f,.3f,.14f,.28f);
        // Readable face including eyes, brows, nose and warm cheek accents.
        for(float x:new float[]{-.13f,.13f}){
            g.ball(ivory,x,1.43f,.281f,.105f,.135f,.035f);g.ball(m.color("314c59"),x+.008f,1.43f,.304f,.060f,.093f,.025f);
            g.ball(m.color("fff1ce"),x+.017f,1.45f,.32f,.023f,.027f,.01f);
            g.box(hair,x,1.54f,.295f,.11f,.025f,.025f);
            g.ball(m.color("dfac96"),x*1.45f,1.34f,.265f,.095f,.045f,.02f);
        }
        g.ball(skin,0,1.36f,.32f,.065f,.075f,.07f);g.box(m.color("ae8b79"),0,1.28f,.285f,.07f,.012f,.018f);
        g.ball(m.color("bd775c"),0,1.12f,.03f,.43f,.16f,.39f);g.box(m.color("bd775c"),.17f,.94f,.20f,.14f,.34f,.065f,0,-12);
        // Backpack, pocket, top flap, straps, buckles and coat fastenings.
        g.box(pack,0,.89f,-.27f,.43f,.48f,.22f);g.ball(pack,0,1.12f,-.28f,.45f,.19f,.23f);
        g.box(m.color("b08b63"),0,.77f,-.405f,.29f,.20f,.06f);
        for(float x:new float[]{-.17f,.17f}){g.box(m.color("7c654b"),x,.9f,-.405f,.055f,.40f,.03f);g.box(m.color("dec89b"),x,.87f,-.43f,.075f,.07f,.015f);g.box(pack,x,1.0f,.18f,.055f,.29f,.045f);}
        for(float y:new float[]{.75f,.89f,1.02f})g.ball(m.color("e0cc94"),0,y,.187f,.035f,.035f,.02f);
        body=add(g.finish());
        leftLeg=leg(m,pants,boots,ivory);rightLeg=leg(m,pants,boots,ivory);
        leftArm=arm(m,coat,skin,ivory);rightArm=arm(m,coat,skin,ivory);
        animate(0);
    }
    private ModelInstance add(Model model){models.add(model);ModelInstance i=new ModelInstance(model);instances.add(i);return i;}
    private ModelInstance leg(Materials m,Material pants,Material boots,Material ivory){
        Geometry g=new Geometry();g.cylinder(pants,0,-.19f,0,.15f,.38f,.16f);g.ball(boots,0,-.46f,.06f,.23f,.26f,.36f);g.box(ivory,0,-.55f,.07f,.23f,.045f,.34f);return add(g.finish());
    }
    private ModelInstance arm(Materials m,Material coat,Material skin,Material ivory){Geometry g=new Geometry();g.ball(coat,0,-.13f,0,.19f,.34f,.19f);g.cylinder(ivory,0,-.29f,0,.17f,.08f,.17f);g.ball(skin,0,-.38f,0,.15f,.19f,.16f);return add(g.finish());}
    public void update(Vector2 input,float dt,CollisionWorld collision){
        float distance=motor.update(input,dt,collision);phase+=distance*3.8f;
        if(velocity.len2()>.04f){float target=MathUtils.atan2(velocity.x,velocity.y)*MathUtils.radiansToDegrees;heading+=MathUtils.atan2(MathUtils.sinDeg(target-heading),MathUtils.cosDeg(target-heading))*MathUtils.radiansToDegrees*(1-(float)Math.exp(-18*dt));}
        animate(distance/Math.max(dt,.0001f));
    }
    private void animate(float speed){
        float amount=MathUtils.clamp(speed/SPEED,0,1),swing=MathUtils.sin(phase)*27*amount,bob=Math.abs(MathUtils.sin(phase))*.025f*amount;
        Matrix4 base=new Matrix4().translate(position.x,bob,position.y).rotate(Vector3.Y,heading);
        body.transform.set(base);
        leftLeg.transform.set(base).translate(-.13f,.57f,0).rotate(Vector3.X,swing);rightLeg.transform.set(base).translate(.13f,.57f,0).rotate(Vector3.X,-swing);
        leftArm.transform.set(base).translate(-.34f,1.06f,0).rotate(Vector3.X,-swing*.7f).rotate(Vector3.Z,-6);
        rightArm.transform.set(base).translate(.34f,1.06f,0).rotate(Vector3.X,swing*.7f).rotate(Vector3.Z,6);
    }
    public void reset(){position.set(-2,3.5f);velocity.setZero();heading=155;phase=0;animate(0);}
    public void dispose(){for(Model model:models)model.dispose();}
}
