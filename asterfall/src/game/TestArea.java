package game;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.*;
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.*;
/** One authored presentation courtyard: raised terrace, traversable ramp and garden. */
public final class TestArea {
 public static float height(float x,float z){if(z>=-3)return 0;if(Math.abs(x)<1.65f&&z>-6)return(-z-3)*.4f;return 1.2f;}
 public static boolean inside(float x,float z){return Math.abs(x)<10.7f&&z>-10.5f&&z<9.5f;}
 public static final Vector3 CRATE=new Vector3(-2.6f,0,.3f),CHEST=new Vector3(3.5f,0,2.1f);
 final Asterfall g;final Models m;final WorldBuilder props;
 public TestArea(Asterfall game){g=game;m=g.models;props=new WorldBuilder(game);}
 void add(Model model,float x,float y,float z,float a,float b,float c){g.add(model,x,y,z,a,b,c);}
 void bush(float x,float z,float size){float h=height(x,z);add(m.leafDark,x,h+size*.32f,z,size,.65f*size,size*.85f);add(m.leafLight,x-.25f*size,h+size*.48f,z-.1f,size*.65f,.60f*size,size*.7f);}
 public void build(){
  // A thick grassy island, with actual exposed cliff faces and an upper terrace.
  Model base=m.paintedBox("grass","ffffff");TextureAttribute uv=(TextureAttribute)base.materials.get(0).get(TextureAttribute.Diffuse);uv.scaleU=9;uv.scaleV=9;
  add(m.paintedBox("darkstone","b6b09b"),0,-1.0f,-.7f,24,1.9f,23);add(base,0,-.16f,-.7f,24,.30f,23);
  add(m.paintedBox("stone","bbc3a0"),-6.8f,.52f,-7.1f,10.3f,1.34f,8.2f);add(base,-6.8f,1.1f,-7.1f,10.3f,.20f,8.2f);
  add(m.paintedBox("stone","bbc3a0"),6.8f,.52f,-7.1f,10.3f,1.34f,8.2f);add(base,6.8f,1.1f,-7.1f,10.3f,.20f,8.2f);add(base,0,1.1f,-8.5f,3.4f,.2f,5);
  // Sloped mesh shares the exact height function used for movement and picking.
  ModelBuilder builder=new ModelBuilder();builder.begin();MeshPartBuilder p=builder.part("walkable-ramp",GL20.GL_TRIANGLES,Models.UV,m.paint("paving","ffffff"));p.rect(-1.65f,0,-3,1.65f,0,-3,1.65f,1.2f,-6,-1.65f,1.2f,-6,0,.93f,.37f);Model ramp=builder.end();m.owned.add(ramp);g.scenery.add(new ModelInstance(ramp));
  for(int i=0;i<12;i++){float x=-11+i*2;add(m.stone,x,-.9f,10.8f,1.95f,1.7f,.5f);add(m.leafDark,x,-.10f,10.8f,2,.22f,.7f);}
  // A little stone court set into the grass, deliberately leaving green edges.
  for(int x=-2;x<=2;x++)for(int z=-2;z<=2;z++)if(Math.abs(x)+Math.abs(z)<4)add(m.path,x*1.25f,-.02f,z*1.25f,1.22f,.09f,1.22f);
  for(int i=0;i<5;i++)add(m.path,0,.010f,3+i*1.1f,1.7f,.08f,1.08f);
  // Exactly one building on the raised ground. Its base is elevated with the terrain.
  int start=g.scenery.size();props.house(-5.8f,-6.4f);for(int i=start;i<g.scenery.size();i++)g.scenery.get(i).transform.trn(0,1.2f,0);
  props.column(5.1f,-6.8f,2.7f);props.column(8.7f,-6.8f,1.5f);props.ruinWall(6,-8.7f,4,2,true);
  props.lantern(-3.4f,3.5f,1.7f);props.lantern(3.9f,-2.7f,1.6f);
  // Low fences frame the path, while the play corridor stays clear.
  for(int side:new int[]{-1,1}){for(int i=0;i<4;i++){float z=3.2f+i*1.25f;add(m.trunk,side*5.6f,.56f,z,.17f,1.10f,.17f);if(i<3){add(m.boot,side*5.6f,.38f,z+.62f,.12f,.11f,1.35f);add(m.boot,side*5.6f,.83f,z+.62f,.12f,.11f,1.35f);}}}
  // Strong canopy silhouettes are kept to the perimeter so the hero stays visible.
  float[][] trees={{-8.5f,1.1f},{-8.8f,5.8f},{8.3f,4.9f},{8.7f,-1},{-9,-9},{8,-10}};
  for(float[] tree:trees){start=g.scenery.size();props.tree(tree[0],tree[1],.96f);float y=height(tree[0],tree[1]);for(int i=start;i<g.scenery.size();i++)g.scenery.get(i).transform.trn(0,y,0);}
  float[][] bushes={{-5,2},{5,5},{6.7f,2.7f},{-6.4f,5.3f},{4,-5.4f},{-9,-2},{9,7.6f}};for(float[] v:bushes)bush(v[0],v[1],1.5f);
  java.util.Random random=new java.util.Random(82);for(int i=0;i<145;i++){float x=random.nextFloat()*19-9.5f,z=random.nextFloat()*18-9;if(Math.abs(x)<3.4f&&z<6&&z>-6)continue;float y=height(x,z);for(int j=0;j<3;j++){ModelInstance tuft=m.instance(m.cone("79a64f"),x+j*.08f,y+.16f,z,.09f,.32f,.09f);tuft.transform.rotate(Vector3.Z,(j-1)*23);g.scenery.add(tuft);}if(i%3==0){add(m.sphere(i%2==0?"f1c66f":"c6a6d7"),x,y+.31f,z,.14f,.11f,.14f);add(m.sphere("e7d4a7"),x+.045f,y+.34f,z,.06f,.06f,.06f);}}
  // Stones and grass roots give the cliff edge a readable, volumetric profile.
  for(int side:new int[]{-1,1})for(int j=0;j<5;j++){float x=side*(2.6f+j*1.75f);add(m.stone,x,.37f,-3.02f,1.6f,.77f,.34f);add(m.leafDark,x,1.18f,-3.08f,1.65f,.17f,.55f);}
  g.crateBody=m.instance(m.paintedBox("wood","ffffff"),CRATE.x,.48f,CRATE.z,.92f,.96f,.92f);
  g.crateBraces.add(m.instance(m.gold,CRATE.x,.1f,CRATE.z+.47f,.96f,.10f,.06f));g.crateBraces.add(m.instance(m.gold,CRATE.x,.85f,CRATE.z+.47f,.96f,.10f,.06f));
  g.chestBase=m.instance(m.paintedBox("wood","ffffff"),CHEST.x,.37f,CHEST.z,1.25f,.70f,.78f);
  g.chestLid=m.instance(m.paintedBox("gold","c8d8cf"),CHEST.x,.83f,CHEST.z,1.32f,.24f,.85f);
 }
}
