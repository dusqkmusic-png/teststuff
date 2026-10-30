package game;
import com.badlogic.gdx.utils.*;
import java.nio.file.*;
import java.util.*;

public class SaveData {
 public static class Item {public String id,rarity,affix;public int power,hp,crit;public Item(){} public Item(String i,String r,String a,int p,int h,int c){id=i;rarity=r;affix=a;power=p;hp=h;crit=c;} }
 public int edge=0,vigor=0,flow=0;
 public int version=1,level=1,xp=0,gold=0,potions=5,expedition=1,bosses=0;
 public ArrayList<Item> bag=new ArrayList<>();
 public Item weapon,armor,charm;
 public String city="",place="",timezone="";
 public double lat=999,lon=999;
 public boolean music=true;
 public static final Path DIR=Path.of(System.getProperty("user.home"),".asterfall");
 public static Path file(){return DIR.resolve("save-v1.json");}
 public static SaveData load(){try{if(!Files.exists(file())){SaveData fresh=new SaveData();Path defaults=Path.of("content/settings.json");if(Files.exists(defaults)){JsonValue v=new JsonReader().parse(Files.readString(defaults));fresh.city=v.getString("location","");fresh.place=v.getString("place","");fresh.lat=v.getDouble("latitude",999);fresh.lon=v.getDouble("longitude",999);}return fresh;}SaveData s=new Json().fromJson(SaveData.class,Files.readString(file()));if(s.version!=1)throw new Exception("Unsupported save version");return s;}catch(Exception e){throw new IllegalStateException("Cannot read save. It has been kept at "+file()+". "+e.getMessage(),e);} }
 public void save(){try{Files.createDirectories(DIR);Path tmp=DIR.resolve("save.tmp");Files.writeString(tmp,new Json().prettyPrint(this));if(Files.exists(file()))Files.copy(file(),DIR.resolve("save-v1.backup.json"),StandardCopyOption.REPLACE_EXISTING);try{Files.move(tmp,file(),StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);}catch(AtomicMoveNotSupportedException e){Files.move(tmp,file(),StandardCopyOption.REPLACE_EXISTING);}}catch(Exception e){throw new IllegalStateException("Could not save: "+e.getMessage(),e);} }
 public int points(){return Math.max(0,level-1-edge-vigor-flow);}
 public int damage(){return 16+level*3+edge*3+(weapon==null?0:weapon.power)+(charm==null?0:charm.power/2);}
 public int maxHp(){return 110+level*12+vigor*15+(armor==null?0:armor.hp)+(charm==null?0:charm.hp);}
 public int crit(){return 8+(armor==null?0:armor.crit)+(weapon==null?0:weapon.crit)+(charm==null?0:charm.crit);}
}
