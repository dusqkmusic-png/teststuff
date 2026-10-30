package game;
/** Developer check: actual local forecast and offline cache round-trip. */
public class WeatherCheck {
 public static void main(String[] args)throws Exception {SaveData s=new SaveData();s.lat=40.7505;s.lon=-73.8205;s.place="Flushing, Queens, NY 11355";Weather w=new Weather(s);long deadline=System.currentTimeMillis()+25000;while(w.busy&&System.currentTimeMillis()<deadline)Thread.sleep(100);if(!w.linked)throw new AssertionError("Live weather failed: "+w.status+" "+w.error);System.out.println("PASS: live 11355 weather, "+w.label+", sunrise "+w.sunrise+", sunset "+w.sunset);w.close();Weather cached=new Weather(s);if(!cached.linked)throw new AssertionError("Offline cache missing");cached.close();System.out.println("PASS: cached local conditions load immediately.");}
}
