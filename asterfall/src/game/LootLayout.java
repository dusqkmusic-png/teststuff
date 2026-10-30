package game;
import com.badlogic.gdx.math.*;
import java.util.*;
/** Stable screen-space collision layout; far loot is represented by its world glow only. */
public final class LootLayout {
 public static Rectangle place(float cx,float y,float width,List<Rectangle> occupied){float x=MathUtils.clamp(cx-width/2,22,1418-width);for(int step=0;step<32;step++){float dy=step==0?0:((step+1)/2)*26*(step%2==1?1:-1);Rectangle r=new Rectangle(x,MathUtils.clamp(y+dy,145,815),width,23);boolean clear=true;for(Rectangle other:occupied)if(new Rectangle(r.x-3,r.y-3,r.width+6,r.height+6).overlaps(other)){clear=false;break;}if(clear){occupied.add(r);return r;}}return null;}
}
