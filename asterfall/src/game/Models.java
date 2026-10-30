package game;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.*;
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.*;
import java.util.*;

/** All characters and scenery are polygon meshes; there are no character sprites. */
public class Models {
 public final ArrayList<Model> owned=new ArrayList<>();
 public final ModelBuilder b=new ModelBuilder();
 public static Color col(String s){return Color.valueOf(s);}
 private final HashMap<String,Model> spheres=new HashMap<>();
 public Model sphere(String c){if(spheres.containsKey(c))return spheres.get(c);Model m=b.createSphere(1,1,1,12,8,new Material(ColorAttribute.createDiffuse(col(c))),Usage.Position|Usage.Normal);owned.add(m);spheres.put(c,m);return m;}
 public Model box(String c){Model m=b.createBox(1,1,1,new Material(ColorAttribute.createDiffuse(col(c))),Usage.Position|Usage.Normal);owned.add(m);return m;}
 public Model cone(String c){Model m=b.createCone(1,1,1,8,new Material(ColorAttribute.createDiffuse(col(c))),Usage.Position|Usage.Normal);owned.add(m);return m;}
 public Model cylinder(String c){Model m=b.createCylinder(1,1,1,16,new Material(ColorAttribute.createDiffuse(col(c))),Usage.Position|Usage.Normal);owned.add(m);return m;}
 public Model glow(String c){Model m=sphere(c);m.materials.get(0).set(ColorAttribute.createEmissive(col(c).mul(.6f)));return m;}
 public Model shadow=cylinder("456171");
 {shadow.materials.get(0).set(new BlendingAttribute(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA,.18f));}
 public Model skin=sphere("ffe2cb"),hair=sphere("e9b5d5"),dress=cone("6878c9"),boot=box("40455f"),white=sphere("fff6ed"),eye=sphere("41466b"),gold=box("f4ce7c"),blade=box("c8f1f1"),leaf=sphere("88c8a2"),leafPink=sphere("efb4c5"),trunk=cylinder("967d77"),stone=box("c3c5ce"),grass=box("8dbc99"),path=box("ded3b8"),water=box("75bac9"),roof=cone("bd789d"),wall=box("f4e5d0"),crystal=cone("b9a6ef"),rune=cylinder("a8e9df"),dark=box("707a9d");
 public static class Part {ModelInstance mesh;Vector3 offset,scale;float spin;Part(Model m,float x,float y,float z,float sx,float sy,float sz){mesh=new ModelInstance(m);offset=new Vector3(x,y,z);scale=new Vector3(sx,sy,sz);}}
 public static class Puppet {
  public final ArrayList<Part> parts=new ArrayList<>();public Vector3 pos=new Vector3();public float angle=0,scale=1,hit=0;public String kind;
  public void add(Model m,float x,float y,float z,float sx,float sy,float sz){parts.add(new Part(m,x,y,z,sx,sy,sz));}
  public void draw(ModelBatch batch,Environment env,float time,boolean moving){int n=0;for(Part p:parts){float bob=moving?(float)Math.sin(time*11)*.065f:0;float swing=(moving&&n<2)?(float)Math.sin(time*11+n*Math.PI)*24:0;p.mesh.transform.idt().translate(pos.x,pos.y+bob,pos.z).rotate(Vector3.Y,angle).scale(scale,scale,scale).rotate(Vector3.Y,kind.equals("hero")&&n>=parts.size()-2?hit*300:0).translate(p.offset).rotate(Vector3.X,swing).scale(p.scale.x,p.scale.y,p.scale.z);batch.render(p.mesh,env);n++;}}
 }
 public Puppet hero(){Puppet p=new Puppet();p.kind="hero";
 p.add(boot,-.22f,.19f,0,.25f,.4f,.34f);p.add(boot,.22f,.19f,0,.25f,.4f,.34f);
 p.add(dress,0,.65f,0,1.05f,.85f,.85f);p.add(skin,0,1.45f,0,.92f,.92f,.85f);p.add(hair,0,1.72f,.10f,1.04f,.66f,.94f);
 for(int side:new int[]{-1,1}){p.add(hair,side*.53f,1.25f,.08f,.42f,.91f,.42f);p.add(hair,side*.48f,1.74f,.03f,.44f,.43f,.5f);p.add(eye,side*.2f,1.47f,-.397f,.16f,.23f,.07f);p.add(white,side*.18f,1.52f,-.435f,.055f,.075f,.025f);p.add(sphere("f1a9b0"),side*.31f,1.32f,-.36f,.15f,.07f,.025f);p.add(skin,side*.49f,.87f,0,.23f,.45f,.25f);p.add(white,side*.5f,1.04f,0,.35f,.3f,.35f);}
 p.add(white,0,.97f,-.34f,.35f,.24f,.12f);p.add(gold,0,1.04f,-.43f,.12f,.13f,.08f);
 p.add(gold,0,.75f,-.39f,.27f,.13f,.06f);p.add(blade,.68f,.86f,-.2f,.11f,1.15f,.12f);p.add(gold,.68f,.49f,-.2f,.42f,.09f,.17f);return p;}
 public Puppet slime(String color,boolean boss){Puppet p=new Puppet();p.kind="slime";Model m=sphere(color);p.add(m,0,.46f,0,1.25f,.95f,1.15f);for(int side:new int[]{-1,1}){p.add(eye,side*.24f,.55f,-.49f,.13f,.18f,.08f);p.add(white,side*.22f,.59f,-.53f,.04f,.05f,.025f);}p.add(eye,0,.34f,-.52f,.17f,.05f,.025f);if(boss){p.scale=2.7f;for(int i=-1;i<=1;i++)p.add(cone("f4ce7c"),i*.3f,1.1f,0,.24f,.55f,.3f);p.add(crystal,0,.93f,.1f,.6f,.9f,.6f);}else {p.add(leaf,0,1.0f,0,.65f,.14f,.4f);p.add(crystal,.1f,1.15f,0,.17f,.34f,.17f);}return p;}
 public ModelInstance instance(Model m,float x,float y,float z,float sx,float sy,float sz){ModelInstance i=new ModelInstance(m);i.transform.setToTranslation(x,y,z).scale(sx,sy,sz);return i;}
 public void dispose(){for(Model m:owned)m.dispose();}
}
