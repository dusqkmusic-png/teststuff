package game;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.environment.*;
import com.badlogic.gdx.graphics.g3d.attributes.*;
import com.badlogic.gdx.graphics.g3d.shaders.*;
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer.FrameBufferBuilder;
import com.badlogic.gdx.math.*;
/** Replaces direct-to-window rendering with a camera rig and a small toon render graph. */
public final class Presentation {
 public final PerspectiveCamera camera;public final Environment environment=new Environment();
 public final ModelBatch color,depth;public final DirectionalShadowLight sun;
 final SpriteBatch composite=new SpriteBatch();final ShaderProgram post;FrameBuffer scene,sceneDepth;
 final Vector3 follow=new Vector3(),look=new Vector3();boolean initialized;int width,height;
 public float daylight=-1;final PointLight lanternA=new PointLight(),lanternB=new PointLight();
 public Presentation(){camera=new PerspectiveCamera(39,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());camera.near=.15f;camera.far=75;
  String vertex=DefaultShader.getDefaultVertexShader().replace("void main()", "varying float v_rim;\nvoid main()").replace("void main() {", "void main() {\n v_rim=0.0;");
  vertex=vertex.replace("v_normal = normal;", "v_normal = normal;\n#ifdef cameraPositionFlag\nv_rim=pow(max(0.0,1.0-dot(normal,normalize(u_cameraPosition.xyz-pos.xyz))),3.0);\n#endif");
  vertex=vertex.replace("clamp(dot(normal, lightDir), 0.0, 1.0)","(0.16+0.84*smoothstep(-0.12,0.80,dot(normal, lightDir)))");
  String fragment=DefaultShader.getDefaultFragmentShader().replace("void main()", "varying float v_rim;\nvoid main()").replace("step(v_shadowMapUv.z,","step(v_shadowMapUv.z - 0.0010,");
  fragment=fragment.replace("#ifdef fogFlag\n\t\tgl_FragColor.rgb", "gl_FragColor.rgb += vec3(0.13,0.18,0.19)*v_rim;\n\t#ifdef fogFlag\n\t\tgl_FragColor.rgb");
  fragment=fragment.replace("vec2(u_shadowPCFOffset, u_shadowPCFOffset)","vec2(u_shadowPCFOffset*2.5, u_shadowPCFOffset*2.5)").replace("vec2(-u_shadowPCFOffset, u_shadowPCFOffset)","vec2(-u_shadowPCFOffset*2.5, u_shadowPCFOffset*2.5)").replace("vec2(u_shadowPCFOffset, -u_shadowPCFOffset)","vec2(u_shadowPCFOffset*2.5, -u_shadowPCFOffset*2.5)").replace("vec2(-u_shadowPCFOffset, -u_shadowPCFOffset)","vec2(-u_shadowPCFOffset*2.5, -u_shadowPCFOffset*2.5)");
  DefaultShader.Config config=new DefaultShader.Config(vertex,fragment);config.numPointLights=2;config.numDirectionalLights=1;color=new ModelBatch(new DefaultShaderProvider(config));
  DepthShader.Config dc=new DepthShader.Config();dc.depthBufferOnly=false;depth=new ModelBatch(new DepthShaderProvider(dc));
  sun=new DirectionalShadowLight(2048,2048,25,25,1,62);environment.add(sun);environment.shadowMap=sun;environment.add(lanternA);environment.add(lanternB);
  post=new ShaderProgram(POST_VERTEX,POST_FRAGMENT);if(!post.isCompiled())throw new IllegalStateException("Post shader: "+post.getLog());resize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
 }
 private FrameBuffer buffer(int width,int height){FrameBufferBuilder builder=new FrameBufferBuilder(width,height);builder.addBasicColorTextureAttachment(Pixmap.Format.RGBA8888);builder.addDepthRenderBuffer(GL30.GL_DEPTH_COMPONENT24);return builder.build();}
 public void resize(int w,int h){if(w<1||h<1)return;camera.viewportWidth=w;camera.viewportHeight=h;camera.update();width=Math.min(w,1440);height=Math.max(1,Math.round(h*width/(float)w));if(scene!=null)scene.dispose();if(sceneDepth!=null)sceneDepth.dispose();scene=buffer(width,height);sceneDepth=buffer(width,height);scene.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);sceneDepth.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest,Texture.TextureFilter.Nearest);}
 public void rig(Asterfall g,float dt){Vector3 desired=g.pos.cpy();desired.y+=.65f;desired.mulAdd(g.motor.velocity,.12f);if(g.combat.active())desired.mulAdd(g.combat.direction,.26f);if(!initialized){follow.set(desired);initialized=true;}else follow.lerp(desired,1-(float)Math.exp(-10*dt));look.set(follow);float shake=g.combat.shake;look.add(MathUtils.sin(g.time*107)*shake,MathUtils.sin(g.time*83)*shake*.35f,MathUtils.cos(g.time*93)*shake);float zoom=g.cameraZoom;camera.position.set(look.x+7.8f*zoom,look.y+9.0f*zoom,look.z+10.0f*zoom);camera.lookAt(look);camera.up.set(Vector3.Y);camera.update();}
 public void render(Asterfall g,float dt){rig(g,dt);float d=daylight>=0?daylight:g.weather.daylight();Color sky=new Color(.12f,.19f,.31f,1).lerp(new Color(.64f,.79f,.79f,1),d);environment.set(new ColorAttribute(ColorAttribute.AmbientLight,.15f+d*.20f,.22f+d*.17f,.34f+d*.10f,1));environment.set(ColorAttribute.createFog(sky));sun.set(.12f+d*.50f,.16f+d*.40f,.27f+d*.22f,-.5f,-1,-.35f);float warmth=2.0f+(1-d)*7;lanternA.set(1,.54f,.20f,-3.4f,1.9f,3.5f,warmth);lanternB.set(1,.61f,.29f,3.9f,1.8f,-2.7f,warmth);
  sun.begin(follow,camera.direction);depth.begin(sun.getCamera());g.drawGeometry(depth,true);depth.end();sun.end();
  sceneDepth.begin();Gdx.gl.glClearColor(1,1,1,1);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);depth.begin(camera);g.drawGeometry(depth,true);depth.end();sceneDepth.end();
  scene.begin();Gdx.gl.glClearColor(sky.r,sky.g,sky.b,1);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);color.begin(camera);g.drawGeometry(color,false);g.drawEffects(color);color.end();scene.end();
  Gdx.gl.glViewport(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);sceneDepth.getColorBufferTexture().bind(1);scene.getColorBufferTexture().bind(0);composite.setProjectionMatrix(new Matrix4().setToOrtho2D(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight()));composite.setShader(post);composite.begin();post.setUniformi("u_depth",1);post.setUniformf("u_pixel",1f/width,1f/height);post.setUniformf("u_day",d);post.setUniformf("u_near",camera.near);post.setUniformf("u_far",camera.far);composite.draw(scene.getColorBufferTexture(),0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),0,0,1,1);composite.end();composite.setShader(null);
 }
 public void dispose(){scene.dispose();sceneDepth.dispose();color.dispose();depth.dispose();sun.dispose();post.dispose();composite.dispose();}
 static final String POST_VERTEX="""
 attribute vec4 a_position; attribute vec4 a_color; attribute vec2 a_texCoord0;
 uniform mat4 u_projTrans; varying vec2 v_uv; varying vec4 v_color;
 void main(){v_uv=a_texCoord0;v_color=a_color;gl_Position=u_projTrans*a_position;}
 """;
 static final String POST_FRAGMENT="""
 #ifdef GL_ES
 precision mediump float;
 #endif
 varying vec2 v_uv; varying vec4 v_color;
 uniform sampler2D u_texture; uniform sampler2D u_depth;
 uniform vec2 u_pixel; uniform float u_near,u_far,u_day;
 float zAt(vec2 uv){float z=dot(texture2D(u_depth,uv),vec4(1.0,1.0/255.0,1.0/65025.0,1.0/16581375.0));return(2.0*u_near*u_far)/(u_far+u_near-(z*2.0-1.0)*(u_far-u_near));}
 float aoTap(vec2 off,float z){float dz=z-zAt(v_uv+off*u_pixel);return smoothstep(.035,.18,dz)*(1.0-smoothstep(.35,1.8,dz));}
 vec3 bloom(vec2 off){vec3 c=texture2D(u_texture,v_uv+off*u_pixel).rgb;return max(c-vec3(.65),vec3(0.0))*smoothstep(.68,.98,max(c.r,max(c.g,c.b)));}
 void main(){vec3 c=texture2D(u_texture,v_uv).rgb;float z=zAt(v_uv);float radius=clamp(70.0/max(1.0,z),2.0,9.0);float a=0.0;
 a+=aoTap(vec2(radius,0),z);a+=aoTap(vec2(-radius,0),z);a+=aoTap(vec2(0,radius),z);a+=aoTap(vec2(0,-radius),z);a+=aoTap(vec2(radius*.7,radius*.7),z);a+=aoTap(vec2(-radius*.7,radius*.7),z);a+=aoTap(vec2(radius*.7,-radius*.7),z);a+=aoTap(vec2(-radius*.7,-radius*.7),z);c*=1.0-a*.045;
 vec3 glow=bloom(vec2(0));glow+=bloom(vec2(5,0))+bloom(vec2(-5,0))+bloom(vec2(0,5))+bloom(vec2(0,-5));glow+=bloom(vec2(9,9))+bloom(vec2(-9,9))+bloom(vec2(9,-9))+bloom(vec2(-9,-9));c+=glow*.045;
 float l=dot(c,vec3(.2126,.7152,.0722));c=mix(vec3(l),c,1.10);c=(c-.5)*1.04+.5;c*=mix(vec3(.91,.96,1.10),vec3(1.04,1.025,.98),u_day);float vignette=smoothstep(.25,.85,length((v_uv-.5)*vec2(1.0,.75)));c*=1.0-vignette*.10;gl_FragColor=vec4(clamp(c,0.0,1.0),1.0);}
 """;
}
