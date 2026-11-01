package neighborhood;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Disposable;
import java.util.Random;
/** Hand-composed neighborhood. All placements are in meters, with Y up. */
public final class NeighborhoodScene implements Disposable {
    public final CollisionWorld collision=new CollisionWorld();
    public final ModelInstance instance;
    private final Model model;
    private final Materials m;
    private final Geometry g=new Geometry();
    private final Random random=new Random(2804);
    public NeighborhoodScene(Materials materials){m=materials;compose();model=g.finish();instance=new ModelInstance(model);}
    private float rand(float a,float b){return a+random.nextFloat()*(b-a);}
    public static float roadZ(float x){return (float)Math.sin(x*.075f)*1.7f;}
    private void compose(){
        // One continuous painted lawn, not a terrain tile grid.
        g.box(m.grass,0,-.17f,0,76,.32f,64);
        for(float x=-36;x<36;x+=1.5f){
            float z=roadZ(x),yaw=-(float)Math.toDegrees(Math.atan(.1275f*Math.cos(x*.075f)));
            g.box(m.asphalt,x,.005f,z,1.58f,.08f,6.1f,yaw,0);
            for(int side:new int[]{-1,1}){
                g.box(m.color("b5b6a0"),x,.075f,z+side*3.18f,1.57f,.20f,.27f,yaw,0);
                g.box(m.paving,x,.105f,z+side*3.94f,1.56f,.18f,1.30f,yaw,0);
                g.box(m.color("aaa997"),x+.7f,.199f,z+side*3.94f,.017f,.004f,1.28f,yaw,0);
            }
        }
        // Faded road markings are intentionally sparse in this quiet residential lane.
        for(float x=-27;x<28;x+=6){g.box(m.color("d9d1a4"),x,.05f,roadZ(x),2,.008f,.10f);}
        house(-17,-12.2f,0,"d8bf9a","b2c7bf","b87d65",17);
        house(-5.4f,-12.8f,0,"efe0b4","719a9b","8aabb2",19);
        house(7.2f,-12.0f,0,"c6d3ba","e5d9b7","b39686",21);
        house(20,-12.6f,0,"ead1b7","9cafa9","aaa994",23);
        house(-20,13.9f,180,"d1d8bb","ded3aa","ac7c61",12);
        house(19,14.2f,180,"e2c3b4","a4bdb3","857f78",16);
        // Shared green with a small loop path, a flowering tree, benches and a noticeboard.
        g.ring(m.tint(m.paving,"dec99f"),1,.055f,13,7.4f,5.4f,.60f);
        g.box(m.tint(m.paving,"dec99f"),-2,.025f,6.4f,1.6f,.05f,4.0f);
        tree(2,13.5f,1.05f,true); bench(-3.1f,11.4f,15);bench(5.7f,14.7f,-70);
        // Layered perennial beds frame the green rather than scattering isolated ornaments.
        for(int i=0;i<20;i++){
            float t=i*MathUtils.PI2/20,xx=1+MathUtils.cos(t)*8.8f,zz=13+MathUtils.sin(t)*6.9f;
            if(zz<8.4f&&xx<0)continue;
            bush(xx,zz,.55f);flowers(xx-.3f,zz-.4f,.55f);tuft(xx+.5f,zz+.4f,.42f);
        }
        g.frame(-5.9f,8.4f,15);
        g.cylinder(m.bark,-.8f,1.0f,0,.12f,2,.12f);g.cylinder(m.bark,.8f,1,0,.12f,2,.12f);
        g.box(m.color("eee0b8"),0,1.65f,0,1.9f,1.2f,.16f);g.roof(m.roof,m.plaster,0,2.26f,0,2.2f,.38f,.65f);
        for(int i=0;i<5;i++)g.box(m.color(i%2==0?"91b0a5":"d7bb84"),-.60f+i*.29f,1.63f,.09f,.22f,.55f,.01f,0,rand(-7,7));
        g.reset();collision.box(-5.9f,8.4f,2,.5f);
        // Boundary landscaping closes the composition naturally.
        for(float x=-29;x<=29;x+=3.5f){tree(x,-21.7f,rand(.85f,1.15f),false);bush(x+1,-20,1.2f);}
        for(float z=-18;z<=21;z+=5){tree(-29,z,rand(.85f,1.2f),false);tree(29.5f,z,rand(.9f,1.2f),false);}
        for(float x=-28;x<29;x+=3.4f){bush(x,22,1.4f);}
        // Garden clumps follow edges and leave routes genuinely walkable.
        for(int i=0;i<300;i++){
            float x=rand(-28,28),z=rand(-21,21);
            if(Math.abs(z-roadZ(x))<5.6f || collision.blocked(x,z,.5f))continue;
            if(z>7 && x> -7 && x<10 && random.nextFloat()<.75f)continue;
            tuft(x,z,rand(.18f,.32f));
            if(random.nextFloat()<.17f)flowers(x,z,.45f);
        }
        for(float x:new float[]{-24,-11,3,14,26}){
            float z=roadZ(x)-4.9f;streetlight(x,z);tree(x+1.8f,z-1.3f,.75f,false);
        }
        // A modest suburban utility pole and crossbar at the edge of the green.
        g.cylinder(m.bark,12,3.6f,6,.18f,7.2f,.18f);g.box(m.bark,12,6.5f,6,2.4f,.15f,.18f);
        for(float x:new float[]{11.2f,12.8f})g.cylinder(m.color("ddd4b5"),x,6.7f,6,.18f,.27f,.18f);
        collision.circle(12,6,.24f);
        // Subtle curb drains.
        for(float x:new float[]{-13,9,25}){
            float z=roadZ(x)+2.9f;g.box(m.color("69766e"),x,.054f,z,.65f,.02f,.33f);
            for(int i=0;i<6;i++)g.box(m.color("a0a796"),x-.27f+i*.105f,.069f,z,.035f,.01f,.29f);
        }
    }
    private void house(float x,float z,float angle,String wall,String trim,String roof,int number){
        g.frame(x,z,angle);
        Material plaster=m.tint(m.plaster,wall),wood=m.color(trim),roofMat=m.tint(m.roof,roof),ivory=m.color("f3e6c6"),glass=m.color("769b9f");
        g.box(m.color("a6a594"),0,.28f,0,7.6f,.55f,6.5f);
        g.box(plaster,0,2.1f,0,7.25f,3.65f,6.05f);
        // Clapboard courses, corner boards and generous roof fascia.
        for(float y=.7f;y<3.8f;y+=.38f)g.box(m.tint(m.plaster,"e1d2b7"),0,y,3.04f,7.15f,.028f,.025f);
        for(float xx:new float[]{-3.52f,3.52f}){g.box(ivory,xx,2.1f,3.08f,.15f,3.65f,.1f);g.box(ivory,xx,2.1f,-3.08f,.15f,3.65f,.1f);}
        float roofHeight=number%3==0?2.1f:2.65f;
        g.roof(roofMat,plaster,0,3.93f,0,8.15f,roofHeight,7.1f);
        for(float xx:new float[]{-4.06f,4.06f})g.box(wood,xx,3.94f,0,.15f,.18f,7.2f);
        // Front gable trim follows the roofline rather than an unadorned box silhouette.
        for(float zz:new float[]{-3.56f,3.56f}){
            g.beam(ivory,new Vector3(-4.1f,3.95f,zz),new Vector3(0,3.97f+roofHeight,zz),.12f);
            g.beam(ivory,new Vector3(0,3.97f+roofHeight,zz),new Vector3(4.1f,3.95f,zz),.12f);
        }
        g.box(m.color("ad9881"),2.2f,5.65f,-.8f,.8f,2.0f,.85f);g.box(ivory,2.2f,6.69f,-.8f,1,.16f,1.02f);
        // Attic window, with dimensional surround and crossed mullions.
        g.ball(ivory,0,4.95f,3.12f,1.18f,1.18f,.13f);g.ball(glass,0,4.95f,3.20f,.88f,.88f,.08f);
        g.box(ivory,0,4.95f,3.26f,.07f,.9f,.05f);g.box(ivory,0,4.95f,3.26f,.9f,.07f,.05f);
        for(float xx:new float[]{-2.25f,2.25f}){
            window(xx,2.25f,3.09f,wood,glass,ivory);
            g.box(m.bark,xx,1.18f,3.3f,1.8f,.27f,.48f);
            for(int j=0;j<5;j++){g.ball(m.leaves,xx-.64f+j*.32f,1.41f,3.3f,.48f,.35f,.44f);g.ball(m.color(j%2==0?"e9b8a5":"eee2af"),xx-.60f+j*.30f,1.60f,3.35f,.16f,.14f,.16f);}
        }
        // Door recess, porch posts, canopy, steps and handrails.
        g.box(wood,0,1.55f,3.09f,1.32f,2.52f,.18f);g.box(m.color("586f6e"),0,1.60f,3.20f,1.02f,2.23f,.07f);
        g.box(glass,0,2.18f,3.26f,.65f,.66f,.02f);g.box(ivory,0,2.18f,3.29f,.045f,.7f,.03f);g.ball(m.color("d9b972"),.36f,1.5f,3.3f,.10f,.10f,.10f);
        g.box(m.paving,0,.24f,3.9f,2.8f,.48f,1.5f);g.box(m.paving,0,.13f,4.88f,2.4f,.26f,.65f);
        g.box(roofMat,0,2.99f,3.85f,3.1f,.18f,2.0f,0,-3);
        for(float xx:new float[]{-1.25f,1.25f}){g.box(ivory,xx,1.75f,4.5f,.13f,2.55f,.13f);}
        for(float xx:new float[]{-3.68f,3.68f}){
            g.box(wood,xx,2.2f,0,.10f,1.55f,2.2f);
            g.box(glass,xx*1.016f,2.2f,0,.06f,1.28f,1.9f);
            g.box(ivory,xx*1.03f,2.2f,0,.07f,1.35f,.08f);
        }
        // Path connects each porch to the public sidewalk.
        
        float sidewalk=roadZ(x)+(angle==0?-1:1)*4.45f;
        float end=Math.abs(sidewalk-z);
        g.box(m.paving,0,.045f,(5+end)*.5f,1.45f,.08f,Math.max(.5f,end-5));
        for(float xx:new float[]{-4.75f,4.75f}){
            for(float zz=1;zz<6.5f;zz+=1.05f){g.box(m.color("dbd8b5"),xx,.64f,zz,.11f,1.15f,.14f);g.cone(m.color("dbd8b5"),xx,1.26f,zz,.18f,.18f,.18f);}
            g.box(m.color("dbd8b5"),xx,.86f,3.65f,.08f,.10f,6.5f);g.box(m.color("dbd8b5"),xx,.43f,3.65f,.08f,.10f,6.5f);
        }
        // Front fences have a real opening aligned to the house path.
        for(float xx=-4.75f;xx<=4.76f;xx+=.45f){if(Math.abs(xx)<1.15f)continue;g.box(m.color("dbd8b5"),xx,.57f,6.75f,.13f,.99f,.14f);g.cone(m.color("dbd8b5"),xx,1.1f,6.75f,.17f,.15f,.17f);}
        for(float xx:new float[]{-3,3})for(float yy:new float[]{.35f,.77f})g.box(m.color("dbd8b5"),xx,yy,6.75f,3.5f,.10f,.12f);
        // Mailbox, flag, garden pot, porch light and doormat.
        g.cylinder(m.bark,1.7f,.65f,6.45f,.13f,1.3f,.13f);g.box(wood,1.7f,1.35f,6.45f,.58f,.37f,.8f);g.ball(wood,1.7f,1.53f,6.45f,.58f,.35f,.8f);
        g.box(m.color("ba7958"),2.02f,1.65f,6.45f,.06f,.34f,.25f);
        g.box(m.color("8b8c69"),0,.49f,3.66f,.8f,.025f,.45f);
        g.box(m.color("505d51"),.92f,2.3f,3.18f,.20f,.36f,.22f);g.box(m.color("f4de9e"),.92f,2.32f,3.31f,.12f,.22f,.02f);
        for(float xx:new float[]{-1.92f,1.92f}){g.cylinder(m.color("b98464"),xx,.44f,4.3f,.45f,.48f,.45f);g.ball(m.leaves,xx,.83f,4.3f,.65f,.62f,.65f);}
        g.reset();
        collision.box(x,z,7.7f,6.5f);
        collision.box(x,z+(angle==0?1:-1)*3.9f,2.9f,1.5f); // Facades are exterior-only in this milestone.
        for(float xx:new float[]{-4.75f,4.75f})collision.box(x+xx,z+(angle==0?1:-1)*3.7f,.22f,6.5f);
        for(float xx:new float[]{-3,3})collision.box(x+xx,z+(angle==0?1:-1)*6.75f,3.55f,.22f);
        collision.box(x+(angle==0?1:-1)*1.7f,z+(angle==0?1:-1)*6.45f,.7f,.9f);
        for(float xx:new float[]{-3.2f,3.2f}){
            float zz=z+(angle==0?1:-1)*5.1f;
            bush(x+xx,zz,.85f);flowers(x+xx,zz+1.0f,.7f);
        }
    }
    private void window(float x,float y,float z,Material wood,Material glass,Material ivory){
        g.box(wood,x,y,z,1.70f,1.65f,.20f);g.box(glass,x,y,z+.12f,1.35f,1.35f,.08f);
        g.box(ivory,x,y,z+.18f,.07f,1.42f,.05f);g.box(ivory,x,y,z+.18f,1.42f,.07f,.05f);
        for(float side:new float[]{-1,1}){g.box(wood,x+side*1.01f,y,z+.04f,.28f,1.65f,.12f);for(float yy=-.6f;yy<.7f;yy+=.18f)g.box(ivory,x+side*1.01f,y+yy,z+.11f,.22f,.035f,.04f);}
        g.box(ivory,x,y-.87f,z+.1f,1.95f,.13f,.38f);
    }
    private void tree(float x,float z,float scale,boolean blossom){
        g.frame(x,z,0);
        g.cylinder(m.bark,0,1.75f*scale,0,.48f*scale,3.5f*scale,.48f*scale);
        g.beam(m.bark,new Vector3(0,1.9f*scale,0),new Vector3(.85f*scale,3.4f*scale,.3f),.26f*scale);
        g.beam(m.bark,new Vector3(0,2.2f*scale,0),new Vector3(-.8f*scale,3.5f*scale,-.3f),.25f*scale);
        Material crown=blossom?m.blossom:m.leaves;
        g.canopy(crown,0,3.9f*scale,0,4.2f*scale,3.0f*scale,3.8f*scale,x*.13f+z*.21f);
        for(int i=0;i<7;i++){
            float t=i*MathUtils.PI2/7,r=1.32f*scale;
            Material leaf=blossom?m.tint(m.blossom,i%2==0?"f3d1cc":"e2b0b6"):m.tint(m.leaves,i%2==0?"c3d49c":"afc78e");
            g.canopy(leaf,MathUtils.cos(t)*r,(3.55f+(i%3)*.22f)*scale,MathUtils.sin(t)*r,1.8f*scale,1.5f*scale,1.8f*scale,t);
        }
        g.reset();collision.circle(x,z,.36f*scale);
        // Rounded root mound and small fern-like clumps anchor trunks to the ground.
        g.ball(m.tint(m.grass,"b4b48b"),x,.08f,z,1.1f*scale,.3f,1.1f*scale);
        for(int i=0;i<3;i++)tuft(x+rand(-.8f,.8f),z+rand(-.7f,.7f),.30f);
    }
    private void bush(float x,float z,float scale){for(int i=0;i<4;i++)g.ball(m.tint(m.leaves,i%2==0?"acc787":"bcca86"),x+(i-1.5f)*.34f*scale,.46f*scale,z+(i%2)*.15f,.85f*scale,.95f*scale,.85f*scale);collision.circle(x,z,.55f*scale);}
    private void tuft(float x,float z,float s){for(int i=0;i<3;i++)g.cone(m.color(i%2==0?"83a565":"94b16b"),x+(i-1)*s*.45f,s*.55f,z+(i%2)*s*.4f,s*.34f,s*1.6f,s*.33f);}
    private void flowers(float x,float z,float spread){for(int i=0;i<7;i++){float xx=x+rand(-spread,spread),zz=z+rand(-spread*.4f,spread*.4f),h=rand(.23f,.40f);g.cylinder(m.color("688857"),xx,h*.5f,zz,.025f,h,.025f);g.ball(m.color(i%3==0?"e8c577":i%3==1?"edd9bd":"c8afbf"),xx,h,zz,.15f,.09f,.15f);}}
    private void streetlight(float x,float z){
        Material metal=m.color("536c64");g.cylinder(metal,x,2.25f,z,.10f,4.5f,.10f);g.cylinder(metal,x,.18f,z,.28f,.36f,.28f);
        g.ball(metal,x,4.53f,z,.18f,.18f,.18f);g.box(metal,x,4.4f,z+.40f,.10f,.10f,.8f);
        g.cone(metal,x,4.27f,z+.78f,.64f,.28f,.64f);g.ball(m.color("ecdfb2"),x,4.1f,z+.78f,.40f,.12f,.40f);collision.circle(x,z,.20f);
    }
    private void bench(float x,float z,float angle){
        g.frame(x,z,angle);Material wood=m.color("b2956d"),metal=m.color("586d61");
        for(float zz:new float[]{-.30f,-.1f,.1f,.3f})g.box(wood,0,.54f,zz,2,.12f,.16f);
        for(float yy:new float[]{.9f,1.15f})g.box(wood,0,yy,-.4f,2,.18f,.12f);
        for(float xx:new float[]{-.75f,.75f}){g.box(metal,xx,.3f,0,.10f,.6f,.65f);g.box(metal,xx,.85f,-.4f,.09f,.8f,.09f);}
        g.reset();collision.box(x,z,2.1f,1.1f);
    }
    public void dispose(){model.dispose();}
}
