package game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.*;
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.*;
import java.util.*;

/** Original articulated polygon meshes with painted surface materials. */
public class Models {
 public final ArrayList<Model> owned=new ArrayList<>();public final ArrayList<Texture> textures=new ArrayList<>();
 private final HashMap<String,Model> cache=new HashMap<>();private final HashMap<String,Texture> tex=new HashMap<>();
 public final ModelBuilder b=new ModelBuilder();public static final long UV=Usage.Position|Usage.Normal|Usage.TextureCoordinates;
 public static Color col(String s){return Color.valueOf(s);}
 Texture texture(String name){if(tex.containsKey(name))return tex.get(name);Texture t=new Texture(Gdx.files.internal("assets/materials/"+name+".png"),true);t.setFilter(Texture.TextureFilter.MipMapLinearLinear,Texture.TextureFilter.Linear);t.setWrap(Texture.TextureWrap.Repeat,Texture.TextureWrap.Repeat);textures.add(t);tex.put(name,t);return t;}
 Material mat(String color){return new Material(ColorAttribute.createDiffuse(col(color)));}
 Material paint(String name,String tint){return new Material(TextureAttribute.createDiffuse(texture(name)),ColorAttribute.createDiffuse(col(tint)));}
 Model remember(String key,Model m){owned.add(m);cache.put(key,m);return m;}
 public Model sphere(String c){String k="s"+c;if(cache.containsKey(k))return cache.get(k);return remember(k,b.createSphere(1,1,1,24,16,mat(c),Usage.Position|Usage.Normal));}
 public Model box(String c){String k="b"+c;if(cache.containsKey(k))return cache.get(k);return remember(k,b.createBox(1,1,1,mat(c),Usage.Position|Usage.Normal));}
 public Model cone(String c){String k="c"+c;if(cache.containsKey(k))return cache.get(k);return remember(k,b.createCone(1,1,1,7,mat(c),Usage.Position|Usage.Normal));}
 public Model cylinder(String c){String k="y"+c;if(cache.containsKey(k))return cache.get(k);return remember(k,b.createCylinder(1,1,1,24,mat(c),Usage.Position|Usage.Normal));}
 public Model paintedSphere(String name,String tint){String k="ps"+name+tint;if(cache.containsKey(k))return cache.get(k);return remember(k,b.createSphere(1,1,1,18,12,paint(name,tint),UV));}
 public Model paintedBox(String texture,String tint){String k="pb"+texture+tint;if(cache.containsKey(k))return cache.get(k);return remember(k,b.createBox(1,1,1,paint(texture,tint),UV));}
 public Model paintedCylinder(String texture,String tint){String k="pc"+texture+tint;if(cache.containsKey(k))return cache.get(k);return remember(k,b.createCylinder(1,1,1,24,paint(texture,tint),UV));}
 public Model glow(String c){String k="g"+c;if(cache.containsKey(k))return cache.get(k);Material m=mat(c);m.set(ColorAttribute.createEmissive(col(c).mul(.65f)));return remember(k,b.createSphere(1,1,1,12,8,m,Usage.Position|Usage.Normal));}
 public Model surface(String name,float[] r,float[] h,float depth,Material material,int segments){b.begin();MeshPartBuilder p=b.part(name,GL20.GL_TRIANGLES,UV,material);short[][] ids=new short[r.length][segments+1];for(int j=0;j<r.length;j++){int lo=Math.max(0,j-1),hi=Math.min(r.length-1,j+1);float slope=(r[hi]-r[lo])/Math.max(.01f,h[hi]-h[lo]);for(int i=0;i<=segments;i++){float a=i*MathUtils.PI2/segments,cs=MathUtils.cos(a),sn=MathUtils.sin(a);Vector3 normal=new Vector3(cs,-slope,sn/depth).nor();ids[j][i]=p.vertex(new VertexInfo().setPos(cs*r[j],h[j],sn*r[j]*depth).setNor(normal).setUV(i/(float)segments,j/(float)(r.length-1)));}}for(int j=0;j<r.length-1;j++)for(int i=0;i<segments;i++){p.triangle(ids[j][i],ids[j+1][i],ids[j+1][i+1]);p.triangle(ids[j][i],ids[j+1][i+1],ids[j][i+1]);}Model m=b.end();owned.add(m);return m;}
 public Model eyePatch(){String k="eyePatch";if(cache.containsKey(k))return cache.get(k);Material m=new Material(TextureAttribute.createDiffuse(texture("eye")),new BlendingAttribute(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA,1),FloatAttribute.createAlphaTest(.15f));b.begin();MeshPartBuilder p=b.part(k,GL20.GL_TRIANGLES,UV,m);p.rect(new VertexInfo().setPos(-.5f,-.5f,0).setNor(0,0,-1).setUV(0,1),new VertexInfo().setPos(-.5f,.5f,0).setNor(0,0,-1).setUV(0,0),new VertexInfo().setPos(.5f,.5f,0).setNor(0,0,-1).setUV(1,0),new VertexInfo().setPos(.5f,-.5f,0).setNor(0,0,-1).setUV(1,1));return remember(k,b.end());}
 public Model bladeMesh(){b.begin();Material silver=new Material(ColorAttribute.createDiffuse(col("d5eced")),ColorAttribute.createSpecular(col("ffffff")),FloatAttribute.createShininess(50));MeshPartBuilder p=b.part("forged-blade",GL20.GL_TRIANGLES,Usage.Position|Usage.Normal,silver);Vector3 tip=new Vector3(0,1.9f,0),l=new Vector3(-.19f,.38f,0),r=new Vector3(.19f,.38f,0),front=new Vector3(0,.45f,-.07f),back=new Vector3(0,.45f,.07f);p.triangle(l,tip,front);p.triangle(front,tip,r);p.triangle(r,tip,back);p.triangle(back,tip,l);p.box(0,.27f,0,.31f,.23f,.1f);Model m=b.end();owned.add(m);return m;}
 public Model trail(){String key="sword-ribbon";if(cache.containsKey(key))return cache.get(key);b.begin();Material material=new Material(ColorAttribute.createDiffuse(col("ffe1ad")),ColorAttribute.createEmissive(col("f9d7a0")),new BlendingAttribute(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA,.5f));MeshPartBuilder p=b.part(key,GL20.GL_TRIANGLES,Usage.Position|Usage.Normal,material);for(int j=0;j<30;j++){float a=-MathUtils.PI/2-1.2f+j*2.4f/30,next=a+2.4f/30;p.rect(MathUtils.cos(a)*1.35f,0,MathUtils.sin(a)*1.35f,MathUtils.cos(next)*1.35f,0,MathUtils.sin(next)*1.35f,MathUtils.cos(next)*2.15f,0,MathUtils.sin(next)*2.15f,MathUtils.cos(a)*2.15f,0,MathUtils.sin(a)*2.15f,0,1,0);}return remember(key,b.end());}
 public Model skin=sphere("f3ca9d"),hair=sphere("284d68"),dress=cone("2b7680"),boot=paintedBox("leather","ffffff"),white=sphere("f6e8c3"),eye=sphere("172b3c"),gold=paintedBox("gold","ffffff"),blade=bladeMesh(),leaf=paintedSphere("leaf","ffffff"),leafPink=paintedSphere("leaf","d0cba0"),trunk=paintedCylinder("wood","ffffff"),stone=paintedBox("stone","ffffff"),grass=paintedBox("grass","ffffff"),path=paintedBox("paving","ffffff"),water=paintedBox("water","ffffff"),roof=cone("9e5e3e"),wall=paintedBox("stone","e0d8bb"),crystal=cone("86d6d1"),rune=cylinder("80a7a0"),dark=paintedBox("darkstone","ffffff"),leafDark=paintedSphere("leaf","b6c394"),leafLight=paintedSphere("leaf","c1d2a0"),cloth=paintedBox("cloth","ffffff"),ivory=paintedBox("ivory","ffffff"),flame=glow("ffc475"),rim=cylinder("c4a66a"),flesh=skin;
 public Model shadow=cylinder("23333c");{shadow.materials.get(0).set(new BlendingAttribute(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA,.20f));}
 public static class Part {ModelInstance mesh;Vector3 offset,scale;float spin,tilt,roll;int motion;Part(Model m,float x,float y,float z,float sx,float sy,float sz){mesh=new ModelInstance(m);for(Material material:mesh.materials)if(!material.has(ColorAttribute.Emissive))material.set(ColorAttribute.createEmissive(Color.BLACK));offset=new Vector3(x,y,z);scale=new Vector3(sx,sy,sz);}}
 public static class Puppet {
  public final ArrayList<Part> parts=new ArrayList<>();public Vector3 pos=new Vector3();public float angle=0,scale=1,hit=0;public String kind;public int swordStart=999;
  public Part add(Model m,float x,float y,float z,float sx,float sy,float sz){Part p=new Part(m,x,y,z,sx,sy,sz);parts.add(p);return p;}
  public float runBlend,walkPhase,attackPhase=1,anticipation,lean,dodgeBlend,hurt,flash;private float lastFlash=-1;
  public void prepare(){if(Math.abs(flash-lastFlash)>.025f){for(Part part:parts)for(Material material:part.mesh.materials){ColorAttribute emissive=(ColorAttribute)material.get(ColorAttribute.Emissive);emissive.color.set(flash*.8f,flash*.7f,flash*.5f,1);}lastFlash=flash;}}
  public void draw(ModelBatch batch,Environment env,float time,boolean moving){int n=0;boolean isHero=kind.equals("hero");float breath=MathUtils.sin(time*2)*.012f;float bob=isHero?Math.abs(MathUtils.sin(walkPhase))*runBlend*.065f:MathUtils.sin(time*5)*.035f;
   float phase=attackPhase;float bladeAngle=phase>=1?-15:phase<.25f?MathUtils.lerp(-15,-105,Interpolation.smooth.apply(phase*4)):phase<.56f?MathUtils.lerp(-105,125,Interpolation.smooth.apply((phase-.25f)/.31f)):MathUtils.lerp(125,-15,Interpolation.smooth.apply((phase-.56f)/.44f));
   for(Part p:parts){p.mesh.transform.idt().translate(pos.x,pos.y+bob+breath-dodgeBlend*.22f,pos.z).rotate(Vector3.Y,angle).rotate(Vector3.X,isHero?-lean*45-dodgeBlend*29+hurt*13:-hurt*18).scale(scale,scale*(1-dodgeBlend*.14f),scale);
    if(isHero&&n>=swordStart){p.mesh.transform.translate(.62f,1.02f,-.1f).rotate(Vector3.Y,bladeAngle).rotate(Vector3.X,phase<1?-58:-17).rotate(Vector3.Z,-23);}
    else if(isHero&&Math.abs(p.motion)==1){float swing=MathUtils.sin(walkPhase+(p.motion<0?MathUtils.PI:0))*29*runBlend;p.mesh.transform.translate(0,.78f,0).rotate(Vector3.X,swing).translate(0,-.78f,0);}
    else if(isHero&&Math.abs(p.motion)==2){float swing=MathUtils.sin(walkPhase+(p.motion<0?0:MathUtils.PI))*12*runBlend;p.mesh.transform.translate(0,1.4f,0).rotate(Vector3.X,swing).translate(0,-1.4f,0);}
    p.mesh.transform.translate(p.offset).rotate(Vector3.Y,p.spin).rotate(Vector3.X,p.tilt).rotate(Vector3.Z,p.roll).scale(p.scale.x,p.scale.y,p.scale.z);batch.render(p.mesh,env);n++;}}

 }
 public Puppet hero(){Puppet p=new Puppet();p.kind="hero";
  Model head=surface("sculpted-head",new float[]{0,.23f,.39f,.47f,.49f,.47f,.36f,.15f,0},new float[]{-.5f,-.45f,-.32f,-.12f,.10f,.30f,.45f,.52f,.53f},.9f,mat("f4cda6"),32);
  Model tunic=surface("tailored-tunic",new float[]{.45f,.46f,.33f,.35f,.44f,.34f},new float[]{-.38f,-.28f,-.08f,.18f,.4f,.46f},.77f,paint("cloth","ffffff"),12);
  Model silver=sphere("b6c6c3"),brow=box("284654"),grip=paintedCylinder("leather","ffffff");
  // Articulated boots, trousers, tunic, shoulder armor and cuffs.
  for(int side:new int[]{-1,1}){Part a=p.add(boot,side*.23f,.23f,-.045f,.34f,.43f,.53f);a.motion=side;p.add(ivory,side*.23f,.52f,0,.31f,.5f,.35f).motion=side;p.add(gold,side*.23f,.36f,-.26f,.23f,.12f,.04f).motion=side;
   Part upper=p.add(cloth,side*.48f,1.25f,0,.29f,.48f,.35f);upper.roll=side*12;upper.motion=side*2;p.add(silver,side*.51f,1.43f,0,.48f,.27f,.46f);p.add(gold,side*.55f,1.4f,-.17f,.36f,.065f,.08f);p.add(ivory,side*.57f,1.03f,-.03f,.29f,.18f,.31f);p.add(boot,side*.59f,.90f,-.04f,.27f,.25f,.29f).motion=side*2;}
  p.add(tunic,0,1.00f,0,1,1,1);p.add(ivory,0,1.3f,-.28f,.5f,.29f,.08f);p.add(boot,0,.93f,-.01f,.76f,.15f,.61f);p.add(gold,0,.94f,-.335f,.22f,.19f,.065f);p.add(dark,0,.94f,-.37f,.12f,.09f,.03f);
  p.add(skin,0,1.57f,0,.29f,.25f,.29f);p.add(head,0,2.02f,0,1.14f,1.12f,1.14f);
  // Original layered anime hair: fitted cap and tapered locks with pointed ends.
  p.add(hair,0,2.44f,.11f,1.16f,.39f,1.03f);Model lock=cone("284d68"),shine=cone("38657a");
  for(int j=0;j<11;j++){float a=j*MathUtils.PI2/11;if(MathUtils.sin(a)<-.2f)continue;Part h=p.add(j%3==0?shine:lock,MathUtils.cos(a)*.45f,2.38f,MathUtils.sin(a)*.40f,.30f,.48f,.30f);h.roll=MathUtils.cos(a)*58;h.tilt=-MathUtils.sin(a)*58;}
  for(int j=-2;j<=2;j++){Part h=p.add(j%2==0?shine:lock,j*.19f,2.39f,-.43f,.27f,.30f,.22f);h.roll=165+j*13;h.tilt=-12;}
  for(int j=-2;j<=2;j++){Part spike=p.add(j%2==0?shine:lock,j*.21f,2.57f,.09f,.31f,.55f,.29f);spike.roll=-j*19;spike.tilt=-18;}
  for(int side:new int[]{-1,1}){p.add(skin,side*.56f,2.01f,0,.18f,.27f,.17f);p.add(eyePatch(),side*.235f,2.02f,-.475f,.33f,.30f,1);Part br=p.add(brow,side*.23f,2.23f,-.45f,.27f,.035f,.035f);br.roll=side*-9;p.add(sphere("dc9c86"),side*.37f,1.89f,-.37f,.14f,.055f,.025f);}
  p.add(skin,0,1.92f,-.49f,.11f,.12f,.12f);p.add(sphere("9c6556"),0,1.81f,-.40f,.15f,.025f,.024f);
  // Amber scarf, stitched cape and a round shield on the left arm.
  p.add(sphere("dca246"),0,1.55f,-.03f,.56f,.18f,.47f);Part scarf=p.add(box("dca246"),-.36f,1.25f,.18f,.24f,.59f,.08f);scarf.roll=-23;
  Part cape=p.add(cloth,0,1.04f,.33f,.75f,.92f,.08f);cape.tilt=-12;p.add(gold,0,.65f,.43f,.72f,.045f,.08f);
  Part sh=p.add(paintedCylinder("wood","ffffff"),-.72f,1.01f,-.13f,.73f,.15f,.84f);sh.tilt=78;sh.roll=-12;
  Part border=p.add(rim,-.72f,1.01f,-.23f,.77f,.045f,.88f);border.tilt=78;border.roll=-12;
  Part shield=p.add(paintedCylinder("cloth","ffffff"),-.72f,1.01f,-.27f,.62f,.03f,.72f);shield.tilt=78;shield.roll=-12;p.add(sphere("e4c378"),-.72f,1.01f,-.32f,.21f,.21f,.13f);
  // A full-length forged sword; these final parts swing together at the wrist.
  p.swordStart=p.parts.size();p.add(grip,0,.06f,0,.13f,.40f,.13f);p.add(gold,0,.30f,0,.61f,.12f,.18f);p.add(blade,0,0,0,1,1,1);p.add(sphere("d5b26c"),0,-.15f,0,.19f,.16f,.19f);p.add(glow("83cfd4"),0,.34f,-.075f,.11f,.12f,.05f);
  return p;
 }
 public Puppet slime(String color,boolean boss){Puppet p=new Puppet();p.kind=boss?"boss":"spirit";
  String bodyKey="spirit-body-"+color;Model body=cache.get(bodyKey);if(body==null){body=surface("spirit-body",new float[]{0,.35f,.58f,.64f,.59f,.43f,.20f,0},new float[]{0,.08f,.18f,.4f,.65f,.84f,.96f,1.02f},.86f,new Material(ColorAttribute.createDiffuse(col(color)),ColorAttribute.createSpecular(col("b4c9c5")),FloatAttribute.createShininess(24)),24);cache.put(bodyKey,body);}
  p.add(body,0,.04f,0,1,1,1);for(int side:new int[]{-1,1}){p.add(eyePatch(),side*.22f,.54f,-.525f,.23f,.21f,1);p.add(sphere("e4b6a1"),side*.38f,.37f,-.39f,.12f,.05f,.04f);}p.add(eye,0,.31f,-.51f,.14f,.035f,.03f);
  if(boss){p.scale=2.45f;Model crown=paintedCylinder("gold","ffffff");p.add(crown,0,.98f,0,.84f,.16f,.75f);for(int j=0;j<5;j++){float a=j*MathUtils.PI2/5;Part pr=p.add(cone("d3af62"),MathUtils.cos(a)*.33f,1.22f,MathUtils.sin(a)*.3f,.2f,.54f,.2f);p.add(glow("80c7c4"),MathUtils.cos(a)*.33f,1.49f,MathUtils.sin(a)*.3f,.12f,.12f,.12f);}p.add(crystal,0,1.22f,0,.36f,.7f,.36f);}else{Part a=p.add(leaf,0,1.02f,0,.61f,.12f,.31f);a.roll=12;p.add(cone("e4be6c"),.04f,1.18f,0,.22f,.37f,.19f);}
  return p;
 }
 public ModelInstance instance(Model m,float x,float y,float z,float sx,float sy,float sz){ModelInstance i=new ModelInstance(m);i.transform.setToTranslation(x,y,z).scale(sx,sy,sz);return i;}
 public void dispose(){for(Model m:owned)m.dispose();for(Texture t:textures)t.dispose();}
}
