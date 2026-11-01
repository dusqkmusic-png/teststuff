package neighborhood;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.math.*;
import java.util.*;
/** Batches original environment meshes by material. Arbitrary continuous placement. */
public final class Geometry {
    private final ModelBuilder builder=new ModelBuilder();
    private final Map<Material,MeshBuilder> parts=new LinkedHashMap<>();
    private final Matrix4 frame=new Matrix4();
    private int id;
    public Geometry(){builder.begin();}
    public void frame(float x,float z,float angle){frame.idt().translate(x,0,z).rotate(Vector3.Y,angle);}
    public void reset(){frame.idt();}
    private void flush(Material m,MeshBuilder p){builder.part("surface"+(id++),p.end(),GL20.GL_TRIANGLES,m);}
    private MeshPartBuilder part(Material m,Matrix4 local){
        MeshBuilder p=parts.get(m);
        if(p==null || p.lastIndex()>48000){
            if(p!=null)flush(m,p);
            p=new MeshBuilder();p.begin(VertexAttributes.Usage.Position|VertexAttributes.Usage.Normal|VertexAttributes.Usage.TextureCoordinates|VertexAttributes.Usage.ColorUnpacked,GL20.GL_TRIANGLES);parts.put(m,p);
        }
        p.setVertexTransform(new Matrix4(frame).mul(local));return p;
    }
    public void box(Material m,float x,float y,float z,float w,float h,float d){box(m,x,y,z,w,h,d,0,0);}
    public void box(Material m,float x,float y,float z,float w,float h,float d,float yaw,float roll){part(m,new Matrix4().translate(x,y,z).rotate(Vector3.Y,yaw).rotate(Vector3.Z,roll)).box(w,h,d);}
    public void ball(Material m,float x,float y,float z,float w,float h,float d){part(m,new Matrix4().translate(x,y,z)).sphere(w,h,d,16,10);}
    public void cylinder(Material m,float x,float y,float z,float w,float h,float d){part(m,new Matrix4().translate(x,y,z)).cylinder(w,h,d,12);}
    public void cone(Material m,float x,float y,float z,float w,float h,float d){part(m,new Matrix4().translate(x,y,z)).cone(w,h,d,12);}
    public void beam(Material m,Vector3 a,Vector3 b,float radius){Vector3 delta=new Vector3(b).sub(a);Matrix4 tr=new Matrix4().translate(new Vector3(a).add(b).scl(.5f)).rotate(new Quaternion().setFromCross(Vector3.Y,new Vector3(delta).nor()));part(m,tr).cylinder(radius,delta.len(),radius,10);}
    public void roof(Material m,Material gable,float x,float y,float z,float w,float h,float d){
        MeshPartBuilder p=part(m,new Matrix4().translate(x,y,z));
        float a=w/2,b=d/2;Vector3 n1=new Vector3(-h,a,0).nor(),n2=new Vector3(h,a,0).nor();
        p.rect(-a,0,b,0,h,b,0,h,-b,-a,0,-b,n1.x,n1.y,0);
        p.rect(0,h,b,a,0,b,a,0,-b,0,h,-b,n2.x,n2.y,0);
        p=part(gable,new Matrix4().translate(x,y,z));
        p.triangle(new MeshPartBuilder.VertexInfo().setPos(-a,0,b).setNor(0,0,1).setUV(0,0),new MeshPartBuilder.VertexInfo().setPos(a,0,b).setNor(0,0,1).setUV(1,0),new MeshPartBuilder.VertexInfo().setPos(0,h,b).setNor(0,0,1).setUV(.5f,1));
        p.triangle(new MeshPartBuilder.VertexInfo().setPos(a,0,-b).setNor(0,0,-1).setUV(0,0),new MeshPartBuilder.VertexInfo().setPos(-a,0,-b).setNor(0,0,-1).setUV(1,0),new MeshPartBuilder.VertexInfo().setPos(0,h,-b).setNor(0,0,-1).setUV(.5f,1));
    }
    /** Continuous irregular crown with shaded leaf masses, avoiding a stack of primitive spheres. */
    public void canopy(Material m,float x,float y,float z,float w,float h,float d,float seed){
        MeshPartBuilder p=part(m,new Matrix4().translate(x,y,z));
        final int longitude=20,latitude=12;
        for(int row=0;row<latitude;row++)for(int col=0;col<longitude;col++){
            MeshPartBuilder.VertexInfo a=crownVertex(col,row,longitude,latitude,w,h,d,seed);
            MeshPartBuilder.VertexInfo b=crownVertex(col,row+1,longitude,latitude,w,h,d,seed);
            MeshPartBuilder.VertexInfo c=crownVertex(col+1,row+1,longitude,latitude,w,h,d,seed);
            MeshPartBuilder.VertexInfo e=crownVertex(col+1,row,longitude,latitude,w,h,d,seed);
            p.rect(a,e,c,b);
        }
    }
    private MeshPartBuilder.VertexInfo crownVertex(int col,int row,int lon,int lat,float w,float h,float d,float seed){
        float u=(float)col/lon,v=(float)row/lat,t=v*MathUtils.PI,phi=u*MathUtils.PI2;
        float st=MathUtils.sin(t),ct=MathUtils.cos(t);
        float r=1+st*st*(.085f*MathUtils.sin(phi*5+ct*4+seed)+.045f*MathUtils.cos(phi*9+t*7+seed));
        float xx=MathUtils.cos(phi)*st*w*.5f*r,yy=ct*h*.5f,zz=MathUtils.sin(phi)*st*d*.5f*r;
        Vector3 n=new Vector3(xx/(w*w),yy/(h*h),zz/(d*d)).nor();
        float shade=.88f+.12f*ct+.025f*MathUtils.sin(phi*13+t*17+seed);
        return new MeshPartBuilder.VertexInfo().setPos(xx,yy,zz).setNor(n).setUV(u*2,v).setCol(shade,shade,shade,1);
    }
    public void ring(Material m,float x,float y,float z,float rx,float rz,float width){
        MeshPartBuilder p=part(m,new Matrix4().translate(x,y,z));
        for(int i=0;i<96;i++){
            float a=i*MathUtils.PI2/96,b=(i+1)*MathUtils.PI2/96;
            float ca=MathUtils.cos(a),sa=MathUtils.sin(a),cb=MathUtils.cos(b),sb=MathUtils.sin(b);
            float wa=width*(1+.08f*MathUtils.sin(a*7)),wb=width*(1+.08f*MathUtils.sin(b*7));
            p.rect(new MeshPartBuilder.VertexInfo().setPos(ca*(rx-wa),0,sa*(rz-wa)).setNor(0,1,0).setUV(ca*3,sa*3),
                new MeshPartBuilder.VertexInfo().setPos(cb*(rx-wb),0,sb*(rz-wb)).setNor(0,1,0).setUV(cb*3,sb*3),
                new MeshPartBuilder.VertexInfo().setPos(cb*(rx+wb),0,sb*(rz+wb)).setNor(0,1,0).setUV(cb*3,sb*3),
                new MeshPartBuilder.VertexInfo().setPos(ca*(rx+wa),0,sa*(rz+wa)).setNor(0,1,0).setUV(ca*3,sa*3));
        }
    }
    public Model finish(){for(Map.Entry<Material,MeshBuilder> e:parts.entrySet())flush(e.getKey(),e.getValue());return builder.end();}
}
