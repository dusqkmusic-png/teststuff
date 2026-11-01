package neighborhood;
import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
/** Camera-relative eight-direction input, normalized before applying speed. */
public final class InputController {
    public final Vector2 direction=new Vector2();
    public void poll(){
        float x=(down(Input.Keys.D,Input.Keys.RIGHT)?1:0)-(down(Input.Keys.A,Input.Keys.LEFT)?1:0);
        float y=(down(Input.Keys.W,Input.Keys.UP)?1:0)-(down(Input.Keys.S,Input.Keys.DOWN)?1:0);
        directionFor(x,y,direction);
    }
    public static Vector2 directionFor(float x,float y,Vector2 out){
        out.set(x*.894427f-y*.447214f,-x*.447214f-y*.894427f);
        if(out.len2()>0)out.nor();return out;
    }
    private boolean down(int a,int b){return Gdx.input.isKeyPressed(a)||Gdx.input.isKeyPressed(b);}
}
