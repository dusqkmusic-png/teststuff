package game;

import com.badlogic.gdx.utils.*;
import java.nio.file.*;
import java.util.*;

public class Content {
 public static class ItemDef { public String id,name,slot,condition; public int power; public float weight; }
 public static class Zone { public String id,name,subtitle; public float x,z; public int enemies; public String color; }
 public final List<ItemDef> items=new ArrayList<>();
 public final List<Zone> zones=new ArrayList<>();
 public void load() throws Exception {
  JsonReader reader=new JsonReader();
  JsonValue root=reader.parse(Files.readString(Path.of("content/items.json")));
  Set<String> ids=new HashSet<>();
  for(JsonValue n:root){ItemDef i=new ItemDef();i.id=n.getString("id");i.name=n.getString("name");i.slot=n.getString("slot");i.condition=n.getString("condition","any");i.power=n.getInt("power");i.weight=n.getFloat("weight"); if(!ids.add(i.id)||i.weight<=0||!Set.of("weapon","charm","armor").contains(i.slot))throw new IllegalArgumentException("Invalid item: "+i.id);items.add(i);}
  for(JsonValue n:reader.parse(Files.readString(Path.of("content/zones.json")))){Zone z=new Zone();z.id=n.getString("id");z.name=n.getString("name");z.subtitle=n.getString("subtitle");z.x=n.getFloat("x");z.z=n.getFloat("z");z.enemies=n.getInt("enemies");z.color=n.getString("color");zones.add(z);}
  if(items.isEmpty()||zones.isEmpty())throw new IllegalArgumentException("Content cannot be empty");
 }
 public ItemDef roll(Random rng, boolean night, boolean wet) {
  double total=0;for(ItemDef i:items)total+=weight(i,night,wet);
  double r=rng.nextDouble()*total;for(ItemDef i:items){r-=weight(i,night,wet);if(r<=0)return i;}return items.get(0);
 }
 public double weight(ItemDef i,boolean night,boolean wet){return i.weight*((i.condition.equals("night")&&night||i.condition.equals("rain")&&wet||i.condition.equals("day")&&!night)?1.25:1);}
 public ItemDef find(String id){return items.stream().filter(i->i.id.equals(id)).findFirst().orElse(items.get(0));}
}
