package neighborhood;
import com.badlogic.gdx.math.*;
/** Deterministic checks of actual movement/collision behavior; no window required. */
public final class MovementChecks {
    private static int checks;
    private static void expect(boolean ok,String why){checks++;if(!ok)throw new AssertionError(why);}
    public static void main(String[] args){
        Vector2 direction=new Vector2();
        for(int x=-1;x<=1;x++)for(int y=-1;y<=1;y++){
            InputController.directionFor(x,y,direction);
            expect(Math.abs(direction.len()-(x==0&&y==0?0:1))<.0001f,"Eight-direction normalization");
        }
        CollisionWorld empty=new CollisionWorld();
        PlayerMotor straight=new PlayerMotor(),diagonal=new PlayerMotor();straight.position.setZero();diagonal.position.setZero();
        for(int i=0;i<120;i++){straight.update(new Vector2(1,0),1f/60,empty);diagonal.update(new Vector2(1,1),1f/60,empty);}
        expect(Math.abs(straight.position.len()-diagonal.position.len())<.001f,"Diagonal travel must not be faster");
        float stopX=straight.position.x;for(int i=0;i<20;i++)straight.update(Vector2.Zero,1f/60,empty);
        expect(straight.velocity.isZero(),"Stops promptly after key release");
        expect(straight.position.x-stopX<.14f,"Braking drift remains below fourteen centimeters");
        CollisionWorld walls=new CollisionWorld();walls.box(2,0,1,8);walls.circle(-2,0,.6f);
        PlayerMotor wall=new PlayerMotor();wall.position.set(0,0);
        for(int i=0;i<150;i++)wall.update(new Vector2(1,.3f),1f/60,walls);
        expect(wall.position.x<=1.5f-PlayerMotor.RADIUS+.001f,"Cannot penetrate house wall");
        expect(wall.position.y>1.2f,"Slides along a wall while moving diagonally");
        Vector2 fast=new Vector2(0,0);walls.move(fast,new Vector2(12,0),.27f);
        expect(fast.x<1.24f,"Substeps prevent tunneling through a wall");
        expect(walls.blocked(-1.3f,0,.27f),"Circle colliders include character radius");
        expect(!walls.blocked(-1.0f,0,.27f),"Open ground beside tree remains walkable");
        expect(walls.blocked(28.9f,0,.27f),"Scene boundary retains the player");
        PlayerMotor a=new PlayerMotor(),b=new PlayerMotor();a.position.setZero();b.position.setZero();
        for(int i=0;i<60;i++)a.update(new Vector2(1,0),1f/30,empty);
        for(int i=0;i<240;i++)b.update(new Vector2(1,0),1f/120,empty);
        expect(a.position.dst(b.position)<.05f,"Comparable movement over different frame rates");
        System.out.println("PASS: "+checks+" movement and collision checks.");
    }
}
