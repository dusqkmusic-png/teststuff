package game;
import java.nio.file.*;
import java.util.*;
public class Checks {
 static int n=0;static void check(boolean ok,String message){n++;if(!ok)throw new AssertionError(message);}
 public static void run()throws Exception{
  Content c=new Content();c.load();check(c.items.size()>=9,"Item catalog");check(c.zones.size()==3,"Three zones");
  for(Content.ItemDef d:c.items){check(c.weight(d,true,true)>0,"Every item obtainable");if(d.condition.equals("night"))check(Math.abs(c.weight(d,true,false)/c.weight(d,false,false)-1.25)<.0001,"Night bonus");}
  Random rng=new Random(41);Set<String> found=new HashSet<>();for(int i=0;i<100000;i++)found.add(c.roll(rng,false,false).id);check(found.size()==c.items.size(),"All items drop outside preferred conditions");
  SaveData s=new SaveData();s.level=5;s.gold=123;s.weapon=new SaveData.Item("moonthread","Mythic","of the Fox",25,0,9);s.bag.add(new SaveData.Item("raincoat","Rare","of Shelter",18,54,0));s.city="Test town";s.lat=42.36;s.lon=-71.06;s.save();SaveData r=SaveData.load();check(r.level==5&&r.gold==123,"Progress round trip");check(r.bag.size()==1&&r.bag.get(0).hp==54,"Loot round trip");check(r.weapon.id.equals("moonthread"),"Stable equipped item ID");check(r.damage()==56,"Damage includes equipment");r.gold=124;r.save();check(Files.exists(SaveData.DIR.resolve("save-v1.backup.json")),"Save backup");check(SaveData.load().gold==124,"Atomic save replaces old data");
  check(r.points()==4,"Talent points follow level");r.edge=1;check(r.points()==3&&r.damage()==59,"Talent spending affects damage");
  Weather w=new Weather(new SaveData());w.apply(new com.badlogic.gdx.utils.JsonReader().parse("{\"current\":{\"temperature_2m\":12.5,\"weather_code\":63},\"daily\":{\"sunrise\":[\"2026-10-30T07:15\"],\"sunset\":[\"2026-10-30T17:52\"]}}"));check(w.linked&&w.wet()&&!w.snow()&&w.sunrise.getHour()==7,"Weather response parsing");w.close();
  check(Weather.describe(95).equals("Thunderstorm"),"Storm classification");check(Weather.describe(73).equals("Snow"),"Snow classification");check(Weather.describe(63).equals("Rain"),"Rain classification");
  Files.writeString(SaveData.file(),"broken");boolean rejected=false;try{SaveData.load();}catch(IllegalStateException e){rejected=true;}check(rejected,"Corrupt save is preserved rather than overwritten");check(Files.readString(SaveData.file()).equals("broken"),"Corrupt save preserved");
  System.out.println("PASS: "+n+" checks. Loot, content, equipment, saves, weather classification.");
 }
}
