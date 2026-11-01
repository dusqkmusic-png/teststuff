package neighborhood;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
/** Coordinates lifecycle only; world, movement, camera and lighting remain independent. */
public final class NeighborhoodGame extends ApplicationAdapter {
    private Materials materials;private NeighborhoodScene scene;private Player player;private CameraController camera;
    private LightingController lighting;private ModelBatch renderer,shadowRenderer;private SpriteBatch overlay;private BitmapFont font;
    private final InputController input=new InputController();private boolean hud=true;
    private final boolean capture,smoke,yard;private int frames;private float elapsed;
    public NeighborhoodGame(String[] args){capture=java.util.Arrays.asList(args).contains("--capture");smoke=java.util.Arrays.asList(args).contains("--smoke");yard=java.util.Arrays.asList(args).contains("--yard");}
    @Override public void create(){
        materials=new Materials();scene=new NeighborhoodScene(materials);player=new Player(materials);if(yard)player.position.set(5,9);camera=new CameraController(player);lighting=new LightingController();
        renderer=SceneRenderer.create();shadowRenderer=new ModelBatch(new DepthShaderProvider());overlay=new SpriteBatch();font=new BitmapFont(Gdx.files.internal("assets/type.fnt"));
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        Gdx.app.log("Neighborhood","Loaded "+scene.instance.nodes.size+" scene nodes; GL "+Gdx.gl.glGetString(GL20.GL_RENDERER));
    }
    @Override public void resize(int width,int height){if(camera!=null)camera.resize(width,height);}
    @Override public void render(){
        float dt=Math.min(Gdx.graphics.getDeltaTime(),.10f);elapsed+=dt;frames++;
        input.poll();
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))Gdx.app.exit();
        if(Gdx.input.isKeyJustPressed(Input.Keys.H))hud=!hud;
        if(Gdx.input.isKeyJustPressed(Input.Keys.R)){player.reset();camera.snap(player);}
        if(smoke){input.direction.set(frames<80?1:0,frames<80?0:-1);}
        player.update(input.direction,dt,scene.collision);camera.update(player,dt);
        lighting.sun.begin(new Vector3(0,0,0),camera.camera.direction);
        shadowRenderer.begin(lighting.sun.getCamera());shadowRenderer.render(scene.instance);for(ModelInstance i:player.instances)shadowRenderer.render(i);shadowRenderer.end();lighting.sun.end();
        Gdx.gl.glViewport(0,0,Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getBackBufferHeight());
        Color c=lighting.sky;Gdx.gl.glClearColor(c.r,c.g,c.b,1);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);
        renderer.begin(camera.camera);renderer.render(scene.instance,lighting.environment);for(ModelInstance i:player.instances)renderer.render(i,lighting.environment);renderer.end();
        if(hud)drawOverlay();
        if(Gdx.input.isKeyJustPressed(Input.Keys.F12) || (capture&&frames==8)){
            Pixmap pix=Pixmap.createFromFrameBuffer(0,0,Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getBackBufferHeight());
            PixmapIO.writePNG(Gdx.files.local("screenshots/"+(yard?"garden":"neighborhood")+".png"),pix,-1,true);pix.dispose();Gdx.app.log("Neighborhood","Saved screenshot");
            if(capture)Gdx.app.exit();
        }
        if(smoke&&frames==180){Gdx.app.log("Smoke","180 frames completed; position="+player.position+"; GL error="+Gdx.gl.glGetError());Gdx.app.exit();}
    }
    private void drawOverlay(){
        int w=Gdx.graphics.getWidth(),h=Gdx.graphics.getHeight();overlay.getProjectionMatrix().setToOrtho2D(0,0,w,h);overlay.begin();
        font.getData().setScale(.7f);font.setColor(.20f,.29f,.25f,.9f);font.draw(overlay,"K O M O R E B I",30,h-25);
        font.getData().setScale(.48f);font.setColor(.25f,.32f,.28f,.85f);font.draw(overlay,"NEIGHBORHOOD STUDY  /  AFTERNOON",31,h-52);
        font.setColor(.97f,.94f,.82f,.95f);font.draw(overlay,"WASD / ARROWS  Walk     R  Return home     H  Hide text     F12  Screenshot",30,34);
        overlay.end();
    }
    @Override public void dispose(){font.dispose();overlay.dispose();renderer.dispose();shadowRenderer.dispose();scene.dispose();player.dispose();materials.dispose();lighting.dispose();}
}
