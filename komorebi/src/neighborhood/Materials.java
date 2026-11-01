package neighborhood;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.*;
import com.badlogic.gdx.utils.*;
import java.util.*;
/** Owns authored textures and palette. No global asset singleton. */
public final class Materials implements Disposable {
    private final Array<Texture> textures=new Array<>();
    private final Map<String,Material> colors=new HashMap<>();
    public Material grass, asphalt, paving, plaster, bark, roof, leaves, blossom;
    public Materials(){
        grass=surface("grass",Color.WHITE,22);asphalt=surface("asphalt",Color.WHITE,1);
        paving=surface("paving",Color.WHITE,1);plaster=surface("plaster",Color.WHITE,2);
        bark=surface("bark",Color.WHITE,1);roof=surface("roof",Color.WHITE,2);
        leaves=surface("leaves",Color.WHITE,1);blossom=surface("blossom",Color.WHITE,1);
    }
    private Material surface(String file,Color tint,float repeat){
        Texture t=new Texture(Gdx.files.internal("assets/"+file+".png"),true);
        t.setFilter(Texture.TextureFilter.MipMapLinearLinear,Texture.TextureFilter.Linear);
        t.setWrap(Texture.TextureWrap.Repeat,Texture.TextureWrap.Repeat);textures.add(t);
        TextureAttribute a=TextureAttribute.createDiffuse(t);a.scaleU=repeat;a.scaleV=repeat;
        return new Material(file,a,ColorAttribute.createDiffuse(tint));
    }
    public Material color(String hex){return colors.computeIfAbsent(hex,h->new Material(h,ColorAttribute.createDiffuse(Color.valueOf(h))));}
    public Material tint(Material source,String hex){String key=source.id+hex;return colors.computeIfAbsent(key,k->{Material m=source.copy();m.id=k;m.set(ColorAttribute.createDiffuse(Color.valueOf(hex)));return m;});}
    public void dispose(){for(Texture t:textures)t.dispose();}
}
