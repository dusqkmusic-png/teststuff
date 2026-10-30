package game;
import com.badlogic.gdx.utils.*;
import java.net.*;
import java.net.http.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.concurrent.*;

public class Weather {
 public volatile String status="Choose your town in Settings",label="Weather not linked",place="",error="";
 public volatile int code=0;public volatile float temperature=0;public volatile boolean linked=false,busy=false;
 public volatile LocalTime sunrise=LocalTime.of(6,0),sunset=LocalTime.of(18,0);
 public volatile Instant updated=Instant.EPOCH;
 private final ExecutorService worker=Executors.newSingleThreadExecutor(r->{Thread t=new Thread(r,"Local weather");t.setDaemon(true);return t;});
 private final HttpClient client=makeClient();
 private static HttpClient makeClient(){HttpClient.Builder b=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10));String proxy=System.getenv("HTTPS_PROXY");if(proxy!=null&&!proxy.isBlank()){try{URI u=URI.create(proxy);b.proxy(ProxySelector.of(new InetSocketAddress(u.getHost(),u.getPort()>0?u.getPort():8080)));}catch(Exception ignored){}}return b.build();}
 private String fetch(String url)throws Exception{return client.send(HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(15)).header("User-Agent","Asterfall/0.1 personal game").build(),HttpResponse.BodyHandlers.ofString()).body();}
 public Weather(SaveData save){place=save.place;try{Path p=SaveData.DIR.resolve("weather.json");if(Files.exists(p)){JsonValue v=new JsonReader().parse(Files.readString(p));if(v.getDouble("lat")==save.lat&&v.getDouble("lon")==save.lon){apply(v.get("weather"));updated=Instant.parse(v.getString("updated"));status="Cached local weather";}}}catch(Exception ignored){}if(save.lat!=999)refresh(save);}
 public void locate(String query,SaveData save){if(busy)return;busy=true;status="Finding your town...";worker.submit(()->{try{
  double lat,lon;String name;
  if(query.trim().equals("11355")){lat=40.7505;lon=-73.8205;name="Flushing, Queens, NY 11355";}
  else if(query.matches("\\s*-?\\d+(\\.\\d+)?\\s*,\\s*-?\\d+(\\.\\d+)?\\s*")){String[] a=query.split(",");lat=Double.parseDouble(a[0].trim());lon=Double.parseDouble(a[1].trim());if(Math.abs(lat)>90||Math.abs(lon)>180)throw new Exception("Coordinates out of range");name=String.format("%.2f, %.2f",lat,lon);}
  else {JsonValue r=new JsonReader().parse(fetch("https://geocoding-api.open-meteo.com/v1/search?count=1&language=en&format=json&name="+URLEncoder.encode(query,java.nio.charset.StandardCharsets.UTF_8)));JsonValue a=r.get("results");if(a==null||a.size==0)throw new Exception("Town not found. Try a nearby city or latitude, longitude.");JsonValue n=a.get(0);lat=n.getDouble("latitude");lon=n.getDouble("longitude");name=n.getString("name")+", "+n.getString("admin1",n.getString("country",""));}
  String raw=fetchWeather(lat,lon);JsonValue weather=new JsonReader().parse(raw);apply(weather);
  save.lat=lat;save.lon=lon;save.city=query;save.place=name;place=name;if(com.badlogic.gdx.Gdx.app!=null)com.badlogic.gdx.Gdx.app.postRunnable(()->save.save());else save.save();cache(raw,save);status="Live local weather";error="";
 }catch(Exception e){error=e.getMessage();status="Connection failed - "+error;}finally{busy=false;}});}
 private String fetchWeather(double lat,double lon)throws Exception{return fetch("https://api.open-meteo.com/v1/forecast?latitude="+lat+"&longitude="+lon+"&current=temperature_2m,weather_code&daily=sunrise,sunset&timezone=auto&forecast_days=1");}
 public void refresh(SaveData save){if(busy||save.lat==999)return;busy=true;worker.submit(()->{try{String raw=fetchWeather(save.lat,save.lon);apply(new JsonReader().parse(raw));cache(raw,save);status="Live local weather";error="";}catch(Exception e){status=linked?"Offline - last known weather":"Weather unavailable";error=e.getMessage();}finally{busy=false;}});}
 void apply(JsonValue root){JsonValue cur=root.get("current");if(cur==null)throw new IllegalArgumentException("Weather service returned no current conditions");code=cur.getInt("weather_code");temperature=cur.getFloat("temperature_2m");JsonValue daily=root.get("daily");if(daily!=null){sunrise=LocalDateTime.parse(daily.get("sunrise").getString(0)).toLocalTime();sunset=LocalDateTime.parse(daily.get("sunset").getString(0)).toLocalTime();}label=describe(code);linked=true;}
 private void cache(String raw,SaveData s)throws Exception{updated=Instant.now();Files.createDirectories(SaveData.DIR);Files.writeString(SaveData.DIR.resolve("weather.json"),"{\"lat\":"+s.lat+",\"lon\":"+s.lon+",\"updated\":\""+updated+"\",\"weather\":"+raw+"}");}
 public float daylight(){float t=LocalTime.now().toSecondOfDay()/60f,r=sunrise.toSecondOfDay()/60f,s=sunset.toSecondOfDay()/60f;return Math.max(0,Math.min(1,Math.min((t-r+35)/70,(s-t+35)/70)));}
 public boolean night(){LocalTime t=LocalTime.now();return t.isBefore(sunrise)||t.isAfter(sunset);}
 public boolean wet(){return linked&&code>=51;}
 public boolean snow(){return code==71||code==73||code==75||code==77||code==85||code==86;}
 public String clock(){return LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a"));}
 public static String describe(int c){if(c==0)return "Clear skies";if(c<=3)return "Cloudy";if(c<=48)return "Fog";if(c>=95)return "Thunderstorm";if(c==71||c==73||c==75||c==77||c==85||c==86)return "Snow";if(c>=51)return "Rain";return "Clear skies";}
 public void close(){worker.shutdownNow();}
}
