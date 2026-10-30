package game;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.*;
import com.badlogic.gdx.math.*;
import java.util.*;
/** Reusable original volumetric props. Scene layout and terrain belong to TestArea. */
public class WorldBuilder {
 final Asterfall g;final Models m;final Random r=new Random(9948);float floorY=-.04f;Model timber,roof,soil,moss,pavers,brick,trim,iron;
 WorldBuilder(Asterfall game){g=game;m=g.models;timber=m.paintedBox("wood","ffffff");roof=m.paintedBox("wood","c29675");soil=m.paintedBox("darkstone","a69476");moss=m.paintedBox("grass","81936b");pavers=m.path;brick=m.stone;trim=m.paintedBox("stone","d6d0b0");iron=m.box("39494b");}
 void add(Model model,float x,float y,float z,float a,float b,float c){g.add(model,x,y,z,a,b,c);}
 void block(float x,float y,float z,float a,float b,float c){add(brick,x,y,z,a,b,c);}
 ModelInstance rotated(Model model,float x,float y,float z,float a,float b,float c,float angle,Vector3 axis){ModelInstance i=m.instance(model,x,y,z,a,b,c);i.transform.setToTranslation(x,y,z).rotate(axis,angle).scale(a,b,c);g.scenery.add(i);return i;}
 void tile(float x,float z,float width){add(pavers,x,floorY,z,width-.035f,.12f,width-.035f);}
 void column(float x,float z,float h){block(x,.17f,z,1.25f,.34f,1.25f);block(x,.43f,z,1.05f,.18f,1.05f);add(m.paintedCylinder("stone","e2dbbc"),x,.62f+h*.45f,z,.78f,h*.9f,.78f);for(int j=0;j<8;j++){float a=j*MathUtils.PI2/8;add(trim,x+MathUtils.cos(a)*.38f,.63f+h*.45f,z+MathUtils.sin(a)*.38f,.055f,h*.82f,.055f);}block(x,h+.48f,z,1.05f,.22f,1.05f);block(x,h+.68f,z,1.23f,.21f,1.23f);g.obstacle(x,z,.64f);}
 void ruinWall(float x,float z,int count,int height,boolean alongX){for(int k=0;k<count;k++){int h=height;if(k==0||k==count-1)h=Math.max(1,height-1);for(int j=0;j<h;j++){float xx=x+(alongX?k*1.0f:0),zz=z+(alongX?0:k*1.0f);block(xx,.36f+j*.66f,zz,.98f,.63f,.9f);if(j==h-1)add(moss,xx,.69f+j*.66f,zz,1,.08f,.94f);}if(h>=2)g.obstacle(x+(alongX?k:0),z+(alongX?0:k),.48f);}}
 void arch(float x,float z){for(int side:new int[]{-1,1}){column(x+side*3.5f,z,3.5f);for(int j=0;j<3;j++)block(x+side*(2.8f-j*.43f),4.15f+j*.30f,z,.72f,.61f,.93f);}block(x,4.98f,z,3.1f,.54f,1.03f);add(moss,x,5.28f,z,3.15f,.07f,1.05f);for(int side:new int[]{-1,1}){block(x+side*3.5f,4.35f,z,1.4f,.25f,1.45f);}}
 void lantern(float x,float z,float height){add(iron,x,height/2,z,.10f,height,.10f);add(iron,x,height-.10f,z,.51f,.08f,.51f);add(m.flame,x,height+.18f,z,.24f,.42f,.24f);for(int dx:new int[]{-1,1})for(int dz:new int[]{-1,1})add(iron,x+dx*.19f,height+.15f,z+dz*.19f,.055f,.48f,.055f);add(m.cone("39494b"),x,height+.5f,z,.68f,.27f,.68f);add(m.rim,x,.1f,z,.5f,.15f,.5f);}
 void brazier(float x,float z){add(m.dark,x,.26f,z,.8f,.5f,.8f);add(m.paintedCylinder("gold","8c8970"),x,.64f,z,.62f,.25f,.62f);add(iron,x,.79f,z,.94f,.18f,.94f);for(int j=0;j<5;j++){float a=j*MathUtils.PI2/5;rotated(m.cone(j%2==0?"ffc37c":"e99748"),x+MathUtils.cos(a)*.15f,1.05f,z+MathUtils.sin(a)*.15f,.27f,.75f,.25f,MathUtils.cos(a)*16,Vector3.Z);}add(m.flame,x,.95f,z,.35f,.30f,.35f);}
 void crate(float x,float z,float size){add(timber,x,size*.5f,z,size,size,size);for(int side:new int[]{-1,1}){add(trim,x+side*size*.44f,size*.5f,z+size*.51f,size*.10f,size,.07f);add(trim,x,size*(side<0?.07f:.93f),z+size*.51f,size,size*.1f,.07f);}rotated(timber,x,size*.5f,z+size*.55f,size*1.21f,.10f,.08f,43,Vector3.Z);g.obstacle(x,z,size*.55f);}
 void house(float x,float z){for(int j=0;j<3;j++)for(int k=-2;k<=2;k++)block(x+k*.9f,.35f+j*.69f,z,.87f,.66f,3.5f);
  for(int side:new int[]{-1,1}){add(timber,x+side*2.25f,1.2f,z,.16f,2.5f,3.75f);rotated(roof,x+side*1.2f,3.0f,z,2.9f,.20f,4.4f,side*34,Vector3.Z);add(timber,x+side*1.15f,2.2f,z+1.81f,2.2f,.15f,.14f);}
  add(timber,x,1.07f,z+1.79f,.96f,1.9f,.13f);for(int side:new int[]{-1,1})add(trim,x+side*.58f,1.09f,z+1.85f,.15f,2.1f,.21f);add(trim,x,2.18f,z+1.84f,1.35f,.22f,.24f);add(m.sphere("ddbe75"),x+.30f,1.1f,z+1.91f,.12f,.12f,.10f);
  for(int side:new int[]{-1,1}){add(m.box("628e97"),x+side*1.52f,1.49f,z+1.79f,.68f,.76f,.08f);add(timber,x+side*1.52f,1.49f,z+1.86f,.055f,.76f,.07f);add(timber,x+side*1.52f,1.49f,z+1.86f,.68f,.055f,.07f);add(trim,x+side*1.52f,1.03f,z+1.89f,.89f,.18f,.28f);}
  for(int side:new int[]{-1,1})block(x+side*1.1f,.14f,z+2.0f,1.6f,.23f,.7f);lantern(x+2.8f,z+2,1.6f);g.obstacle(x,z,2.35f);
 }
 void tree(float x,float z,float scale){rotated(m.trunk,x,1.12f*scale,z,.40f*scale,2.25f*scale,.42f*scale,7,Vector3.Z);for(int j=0;j<4;j++){float a=j*MathUtils.PI2/4;rotated(m.trunk,x+MathUtils.cos(a)*.35f*scale,1.60f*scale,z+MathUtils.sin(a)*.35f*scale,.19f*scale,1.1f*scale,.2f*scale,MathUtils.cos(a)*38,Vector3.Z);}
  for(int j=0;j<9;j++){float a=j*2.39996f,rr=j==0?0:.65f;Model crown=j%3==0?m.leafDark:j%3==1?m.leaf:m.leafLight;add(crown,x+MathUtils.cos(a)*rr*scale,(2.25f+(j%3)*.28f)*scale,z+MathUtils.sin(a)*rr*scale,1.6f*scale,1.20f*scale,1.5f*scale);}g.obstacle(x,z,.64f*scale);}
 void planter(float x,float z){block(x,.18f,z,1.2f,.36f,1.2f);for(int j=0;j<5;j++){float a=j*MathUtils.PI2/5;add(m.leaf,x+MathUtils.cos(a)*.28f,.5f,z+MathUtils.sin(a)*.28f,.65f,.56f,.65f);add(m.sphere(j%2==0?"dcae78":"d5b8b3"),x+MathUtils.cos(a)*.34f,.77f,z+MathUtils.sin(a)*.34f,.18f,.16f,.18f);}}

}
