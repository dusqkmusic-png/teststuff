package game;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import java.util.*;
import java.time.*;

public class UI extends InputAdapter {
 final Asterfall g;final SpriteBatch b=new SpriteBatch();final OrthographicCamera camera=new OrthographicCamera();final BitmapFont font;
 Texture pixel,circle;ArrayList<Button> buttons=new ArrayList<>();String modal="",location="";int page=0,selected=-1;float mx,my;boolean typing=false;String tooltip="";boolean questOpen=false;public final ArrayList<Rectangle> lootLabels=new ArrayList<>();
 final Color ink=Color.valueOf("eef0f6"),muted=Color.valueOf("a8b4c9"),gold=Color.valueOf("edc788"),mint=Color.valueOf("a3dfd2"),panel=Color.valueOf("172738ee"),line=Color.valueOf("516174");
 static class Button {Rectangle r;Runnable action;Button(Rectangle r,Runnable a){this.r=r;action=a;}}
 public UI(Asterfall game){g=game;font=new BitmapFont(Gdx.files.internal("assets/ui.fnt"));font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);Pixmap p=new Pixmap(1,1,Pixmap.Format.RGBA8888);p.setColor(Color.WHITE);p.fill();pixel=new Texture(p);p.dispose();p=new Pixmap(64,64,Pixmap.Format.RGBA8888);p.setColor(Color.WHITE);p.fillCircle(32,32,31);circle=new Texture(p);p.dispose();resize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());location=g.save.city;}
 void rect(float x,float y,float w,float h,Color c){b.setColor(c);b.draw(pixel,x,y,w,h);b.setColor(Color.WHITE);}
 void rect(float x,float y,float w,float h,String c){rect(x,y,w,h,Color.valueOf(c));}
 void circle(float x,float y,float r,Color c){b.setColor(c);b.draw(circle,x-r,y-r,r*2,r*2);b.setColor(Color.WHITE);}
 void text(String t,float x,float y,float size,Color color){font.getData().setScale(size/32f);font.setColor(color);font.draw(b,t,x,y);}
 void text(String t,float x,float y,float size,String c){text(t,x,y,size,Color.valueOf(c));}
 void center(String t,float x,float y,float size,Color c){font.getData().setScale(size/32);GlyphLayout l=new GlyphLayout(font,t);text(t,x-l.width/2,y,size,c);}
 void panel(float x,float y,float w,float h){rect(x+5,y-6,w,h,"0c172877");rect(x,y,w,h,panel);rect(x,y+h-1,w,1,line);rect(x,y,w,1,"2c4055");}
 void button(String label,float x,float y,float w,float h,Runnable action,boolean accent){boolean hover=new Rectangle(x,y,w,h).contains(mx,my);Color base=accent?(hover?mint:Color.valueOf("c1decf")):(hover?Color.valueOf("41556b"):Color.valueOf("2d4055"));rect(x,y,w,h,base);rect(x,y+h-1,w,1,accent?Color.valueOf("f0f9df"):line);center(label,x+w/2,y+h/2+8,18,accent?Color.valueOf("203c42"):ink);buttons.add(new Button(new Rectangle(x,y,w,h),action));}
 void bar(float x,float y,float w,float h,float frac,Color color){rect(x,y,w,h,"0c1724");rect(x+2,y+2,(w-4)*MathUtils.clamp(frac,0,1),h-4,color);}
 void divider(float x,float y,float w){rect(x,y,w,1,"415268");}
 public void resize(int w,int h){camera.setToOrtho(false,1440,900);}
 public void render(float dt){mx=Gdx.input.getX()*1440f/Gdx.graphics.getWidth();my=900-Gdx.input.getY()*900f/Gdx.graphics.getHeight();buttons.clear();b.setProjectionMatrix(camera.combined);Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);b.begin();rain();worldLabels();
 if(g.screen.equals("title")){title();}else{hud();if(g.screen.equals("death"))death();else if(!modal.isEmpty())modal();else if(g.paused)pause();}
 if(g.noticeTime>0&&!g.screen.equals("title")&&modal.isEmpty())center(g.notice,720,172,16,ink);
 b.end();}
 void rain(){if(!g.weather.linked||!g.weather.wet())return;boolean snow=g.weather.snow();Random r=new Random(42);for(int i=0;i<(snow?95:140);i++){float x=r.nextFloat()*1600-80,y=(r.nextFloat()*1100-g.time*(snow?35:490))%1100;if(y<0)y+=1100;x+=MathUtils.sin(g.time+i)*8;if(snow)circle(x,y,2.4f,Color.valueOf("eaf3f8aa"));else{rect(x,y,1.2f,16,"c0e2ef70");}}if(g.weather.code>=95&&MathUtils.sin(g.time*1.7f)>.998f)rect(0,0,1440,900,"f1edff44");}
 Vector3 project(Vector3 p){Vector3 v=g.camera.project(p.cpy());v.x*=1440f/Gdx.graphics.getWidth();v.y*=900f/Gdx.graphics.getHeight();return v;}
 void worldLabels(){lootLabels.clear();if(g.screen.equals("title"))return;for(Asterfall.Enemy enemy:g.enemies)if(enemy.hp<enemy.maxHp){Vector3 p=project(enemy.body.pos.cpy().add(0,1.55f,0));bar(p.x-31,p.y,62,5,enemy.hp/enemy.maxHp,Color.valueOf("d8ad84"));}
 ArrayList<Rectangle> occupied=new ArrayList<>();Vector3 feet=project(g.pos),head=project(g.pos.cpy().add(0,2.9f,0));occupied.add(new Rectangle(feet.x-66,Math.min(feet.y,head.y)-10,132,Math.abs(head.y-feet.y)+25));
 ArrayList<Asterfall.Drop> sorted=new ArrayList<>(g.drops);sorted.sort(Comparator.comparingDouble(d->d.pos.dst2(g.pos)));for(Asterfall.Drop d:sorted){float distance=d.pos.dst(g.pos);if(distance>5.5f)continue;Vector3 p=project(d.pos.cpy().add(0,.85f,0));if(p.z<0||p.z>1)continue;String name=g.content.find(d.item.id).name;font.getData().setScale((distance<3?16:13)/32f);GlyphLayout layout=new GlyphLayout(font,name);Rectangle r=LootLayout.place(p.x,p.y,layout.width+18,occupied);if(r==null)continue;lootLabels.add(r);Color tint=rarity(d.item.rarity);rect(r.x,r.y,r.width,r.height,"132d32b0");rect(r.x,r.y,2,r.height,tint);center(name,r.x+r.width/2,r.y+17,distance<3?16:13,tint);if(Math.abs(r.y-p.y)>5){float x=r.x+r.width/2,y=r.y;float dx=p.x-x,dy=p.y-y;float len=(float)Math.sqrt(dx*dx+dy*dy);b.setColor(tint.r,tint.g,tint.b,.35f);b.draw(pixel,x,y,0,0,len,1,1,1,MathUtils.atan2(dy,dx)*MathUtils.radiansToDegrees,0,0,1,1,false,false);b.setColor(Color.WHITE);}}
 for(Asterfall.Floating f:g.floats){Vector3 p=project(f.pos);center(f.text,p.x,p.y,19,Color.valueOf(f.color));}if(!g.chestOpened&&g.pos.dst(TestArea.CHEST)<2.5f){Vector3 p=project(TestArea.CHEST.cpy().add(0,1.25f,0));center("F  /  OPEN",p.x,p.y,14,gold);}
 }
 void title(){rect(0,0,690,900,"102137e8");rect(0,0,1440,12,"dcbfa1");text("A LITTLE LIGHT. A LITTLE ADVENTURE.",80,784,15,mint);text("ASTERFALL",73,718,69,ink);text("The gardens remember your world.",80,653,24,gold);divider(80,621,476);
 text("A bright little action RPG, beneath your sky.",80,583,19,ink);text("A little clearing beneath your own sky.",80,550,17,muted);text("Local time and weather leave a little magic in the loot.",80,521,17,muted);
 button(g.save.level>1||g.save.bag.size()>0?"CONTINUE YOUR ADVENTURE":"ENTER THE GARDEN",80,410,390,57,()->g.begin(),true);
 button("YOUR LOCAL SKY  /  SETTINGS",80,338,390,50,()->{g.screen="game";modal="settings";g.paused=true;},false);
 text("REAL 3D  /  JAVA DESKTOP  /  SINGLE PLAYER",80,278,13,muted);
 text("WASD to move   /   Left mouse to slash",80,196,17,ink);text("Q: sword shockwave    E: whirlwind    Space: dodge",80,165,17,muted);text("R: potion    F: rest / collect    I: equipment    Esc: pause",80,134,16,muted);
 panel(1020,50,370,112);text("YOUR WORLD, REFLECTED",1042,136,13,mint);text(g.weather.clock()+"  /  "+(g.weather.night()?"Moonlit hours":"Daylight hours"),1042,107,22,ink);text(g.weather.linked?g.weather.label:"Set your town to link local weather",1042,76,15,muted);
 text("LANTERN GARDEN  /  0.3",80,47,12,muted);
 }
 void hud(){
 // Quiet, cohesive gold-and-teal treatment; the scene occupies the screen.
 text(g.zone,29,859,24,ink);button(questOpen?"-":"+",27,797,28,27,()->questOpen=!questOpen,false);text("GARDEN NOTES",66,817,12,gold);
 if(questOpen){rect(27,660,300,125,"183737b0");text("A quiet clearing",43,761,19,ink);text("Practice your sword and dodge.",43,731,14,muted);text("The ramp leads to the cottage.",43,705,14,muted);text("N resets this practice scene.",43,680,13,gold);}
 boolean night=g.presentation.daylight>=0?g.presentation.daylight<.5f:g.weather.night();String sky=g.weather.clock()+"  /  "+(night?"NIGHT":"DAY");text(sky,1204,859,16,ink);text(g.weather.linked?g.weather.label:"Weather connecting...",1204,833,12,muted);
 float x=425,y=28,w=590;rect(x-3,y-3,w+6,89,"c6ab7255");rect(x,y,w,83,"142e35c8");rect(x,y+81,w,2,gold);circle(x+37,y+40,25,Color.valueOf("bda477"));circle(x+37,y+40,22,Color.valueOf("284a50"));center("L"+g.save.level,x+37,y+48,17,ink);
 bar(x+77,y+51,386,14,g.hp/g.save.maxHp(),Color.valueOf("c87979"));text((int)g.hp+" / "+g.save.maxHp(),x+84,y+62,10,ink);bar(x+77,y+34,386,8,g.mana/100,Color.valueOf("70b8bc"));
 text("Q  WAVE",x+79,y+21,11,g.boltCd>0?muted:gold);text("E  WHIRL",x+178,y+21,11,g.novaCd>0?muted:gold);text("SPACE  DODGE",x+284,y+21,11,g.dashCd>0?muted:gold);text("R  "+g.save.potions,x+408,y+21,11,ink);
 button("I",x+489,y+43,33,26,()->open("inventory"),false);button("ESC",x+532,y+43,45,26,()->g.paused=true,false);text(g.save.gold+" gold",x+490,y+25,11,gold);
 text("WASD / RMB Move    LMB Approach / Attack    Shift Stand    F Interact",28,28,12,ink);
 }
 void open(String s){modal=s;g.paused=true;typing=false;}
 void close(){modal="";g.paused=false;typing=false;}
 void modal(){rect(0,0,1440,900,"0a1328aa");if(modal.equals("inventory"))inventory();else if(modal.equals("settings"))settings();else if(modal.equals("talents"))talents();else guide();}
 void pause(){rect(0,0,1440,900,"0a1328aa");panel(490,207,460,497);text("A MOMENT TO BREATHE",529,660,14,mint);text("Lanternlight & rest",529,619,30,ink);button("RETURN TO THE GARDENS",530,519,380,49,()->g.paused=false,true);button("EQUIPMENT & LOOT",530,456,380,46,()->open("inventory"),false);button("YOUR LOCAL SKY / SETTINGS",530,395,380,46,()->open("settings"),false);button("FIELD GUIDE",530,334,380,46,()->open("guide"),false);button("SAVE & QUIT",530,260,380,44,()->{g.safeSave();Gdx.app.exit();},false);}
 void death(){rect(0,0,1440,900,"152236cc");panel(435,280,570,330);center("EVERY FLOWER BENDS SOMETIMES",720,562,15,gold);center("The lantern calls you home.",720,507,30,ink);center("Keep your levels, loot, and gold. Try another approach.",720,458,17,muted);button("RETURN TO THE SHRINE",530,334,380,54,()->g.revive(),true);}
 public static Color rarity(String r){return Color.valueOf(r.equals("Mythic")?"f0b3df":r.equals("Rare")?"efd399":r.equals("Enchanted")?"a4dcd6":"d0d6de");}
 String itemName(SaveData.Item i){return g.content.find(i.id).name;}
 void equipped(String title,SaveData.Item item,float x,float y){text(title,x,y,12,muted);text(item==null?"Traveler's starting gear":itemName(item),x,y-25,18,item==null?ink:rarity(item.rarity));if(item!=null)text("Power "+item.power+"  /  Health +"+item.hp+"  /  Crit +"+item.crit+"%",x,y-51,12,muted);}
 void inventory(){panel(150,118,1140,665);text("YOUR SATCHEL",183,744,14,mint);text("Little treasures. Bigger adventures.",183,704,30,ink);button("CLOSE  [I / ESC]",1080,715,175,40,()->close(),false);divider(183,677,1070);
 equipped("WEAPON",g.save.weapon,185,645);equipped("ARMOR",g.save.armor,545,645);equipped("CHARM",g.save.charm,905,645);divider(183,565,1070);text("DAMAGE "+g.save.damage()+"    /    HEALTH "+g.save.maxHp()+"    /    CRITICAL "+g.save.crit()+"%",185,543,15,gold);text(g.save.gold+" GOLD  /  "+g.save.bag.size()+" / 60 ITEMS",965,543,14,muted);
 int pages=Math.max(1,(g.save.bag.size()+6)/7);page=Math.min(page,pages-1);int start=page*7;
 if(g.save.bag.isEmpty()){center("Your next favorite item is still out there.",720,389,24,ink);center("Defeat spirits, approach their drops, and press F to collect.",720,347,17,muted);}
 for(int j=start;j<Math.min(start+7,g.save.bag.size());j++){final int idx=j;SaveData.Item i=g.save.bag.get(j);float y=485-(j-start)*43;rect(182,y-13,1072,39,j%2==0?"23364a":"1c3043");text(itemName(i),195,y+12,18,rarity(i.rarity));text(i.rarity+" "+i.affix+"  /  "+g.content.find(i.id).slot,470,y+11,13,muted);text("PWR "+i.power+"  HP +"+i.hp+"  CRIT +"+i.crit+"%",800,y+11,12,ink);button("EQUIP",1031,y-7,101,27,()->g.equip(idx),false);button("SALVAGE",1141,y-7,101,27,()->g.salvage(idx),false);}
 button("PREVIOUS",183,142,130,33,()->page=Math.max(0,page-1),false);text("PAGE "+(page+1)+" / "+pages,340,164,13,muted);button("NEXT",480,142,100,33,()->page=Math.min(pages-1,page+1),false);text("Salvage converts an item into gold. Equipped items stay safe.",640,163,13,muted);
 }
 void talents(){panel(265,180,910,540);text("A LITTLE STRONGER, EVERY DAY",305,675,14,mint);text("Your sword techniques",305,629,34,ink);text(g.save.points()+" UN spent talent points".replace("UN spent","UNSPENT"),305,590,16,gold);button("CLOSE [ESC]",970,637,165,40,()->close(),false);divider(305,565,830);
 String[] names={"Sword Mastery","Vital Bloom","Battle Rhythm"},descs={"+3 attack damage per rank","+15 maximum health per rank","+1.5 focus per second per rank"};int[] ranks={g.save.edge,g.save.vigor,g.save.flow};for(int i=0;i<3;i++){final int n=i;float y=510-i*90;text(names[i]+"  /  RANK "+ranks[i],305,y,23,ink);text(descs[i],305,y-32,16,muted);button(g.save.points()>0?"SPEND POINT":"LEVEL UP TO UNLOCK",878,y-35,255,45,()->{if(g.save.points()==0)return;if(n==0)g.save.edge++;else if(n==1){g.save.vigor++;g.hp+=15;}else g.save.flow++;g.safeSave();},g.save.points()>0);}text("Earn one point each level. All three combat abilities are available from the start.",305,222,15,muted);
 }
 void settings(){panel(260,126,920,650);text("YOUR LOCAL SKY",300,733,14,mint);text("A world that follows yours.",300,691,33,ink);button("CLOSE  [ESC]",985,705,155,39,()->close(),false);divider(300,662,840);
 text("LOCAL TIME",300,627,13,gold);text(g.weather.clock()+"  /  "+java.time.ZoneId.systemDefault().getId(),300,598,23,ink);text("Uses your computer clock. Sunrise and sunset follow your weather location.",300,563,16,muted);
 text("YOUR TOWN OR LATITUDE, LONGITUDE",300,517,13,gold);boolean hover=mx>300&&mx<940&&my>449&&my<492;rect(300,449,640,43,typing?"415b70":"2a4055");text(location+(typing&&((int)(g.time*2))%2==0?"|":""),314,478,19,ink);buttons.add(new Button(new Rectangle(300,449,640,43),()->typing=true));button(g.weather.busy?"CONNECTING...":"LINK WEATHER",956,449,184,43,()->{typing=false;if(!location.isBlank())g.weather.locate(location,g.save);},true);
 text("Example: Boston, or 42.36, -71.06. Enter submits. No GPS access needed.",300,427,14,muted);text(g.weather.status.length()>95?g.weather.status.substring(0,95):g.weather.status,300,391,14,mint);
 text(g.weather.linked?g.weather.place+"  /  "+g.weather.label+"  /  "+Math.round(g.weather.temperature)+" C":"No weather selected. No simulated weather is presented as local weather.",300,357,17,ink);
 if(g.weather.linked)text("Sunrise "+g.weather.sunrise+"  /  Sunset "+g.weather.sunset+"  /  Updated "+java.time.Duration.between(g.weather.updated,java.time.Instant.now()).toMinutes()+" min ago",300,326,14,muted);
 divider(300,300,840);text("Weather refreshes every 15 minutes while open. Offline: last known conditions.",300,274,15,muted);text("Only your chosen coordinates go to Open-Meteo. Your save stays on this computer.",300,245,15,muted);
 button(g.save.music?"MUSIC: ON":"MUSIC: OFF",300,170,190,43,()->{g.save.music=!g.save.music;if(g.save.music)g.music.play();else g.music.pause();g.safeSave();},false);button("REFRESH WEATHER",510,170,210,43,()->g.weather.refresh(g.save),false);text("Open-Meteo / CC BY 4.0",878,193,14,muted);
 }
 void guide(){panel(265,142,910,615);text("THE LANTERNKEEPER'S FIELD GUIDE",305,714,14,mint);text("A few things before you wander.",305,670,31,ink);String[] lines={"WASD / arrows - move. Right mouse - walk toward the cursor.","Left click - approach / attack. Shift + left click - stand and slash.","Q - ranged sword shockwave. E - whirlwind. Both use regenerating focus.","Space - dodge in your movement direction; briefly avoids damage.","R - drink a potion. F - collect loot, open the chest, or rest near the garden entrance.","I - equipment. K - talents. B - buy potion at shrine. Mouse wheel - zoom.","Warm ground rings warn of enemy attacks. Move out before they land.","N resets the small practice garden. This build focuses on presentation.","Your computer clock drives day / night. Set your town for actual weather.","Condition-matching items get +25% selection weight, never exclusive drops.","Progress saves every 30 seconds, on equipment changes, and on exit.","Returning to the game starts a fresh garden; your character progress persists."};for(int i=0;i<lines.length;i++)text(lines[i],305,623-i*31,16,i%2==0?ink:muted);button("BACK TO THE GARDEN",305,177,290,43,()->close(),true);}
 public boolean blocksMouse(){if(!modal.isEmpty()||g.paused||!g.screen.equals("game"))return true;for(Button button:buttons)if(button.r.contains(mx,my))return true;return mx>415&&mx<1025&&my<120;}
 @Override public boolean touchDown(int sx,int sy,int pointer,int button){if(button!=Input.Buttons.LEFT)return false;float x=sx*1440f/Gdx.graphics.getWidth(),y=900-sy*900f/Gdx.graphics.getHeight();for(int i=buttons.size()-1;i>=0;i--){Button p=buttons.get(i);if(p.r.contains(x,y)){p.action.run();return true;}}typing=false;return false;}
 @Override public boolean keyDown(int key){if(typing){if(key==Input.Keys.BACKSPACE&&!location.isEmpty())location=location.substring(0,location.length()-1);if(key==Input.Keys.ENTER){typing=false;if(!location.isBlank())g.weather.locate(location,g.save);}if(key==Input.Keys.ESCAPE)typing=false;return true;}if(key==Input.Keys.ESCAPE){if(!modal.isEmpty())close();else if(g.screen.equals("game"))g.paused=!g.paused;return true;}if(key==Input.Keys.K&&g.screen.equals("game")){if(modal.equals("talents"))close();else open("talents");return true;}if(key==Input.Keys.I&&g.screen.equals("game")){if(modal.equals("inventory"))close();else open("inventory");return true;}if(!g.screen.equals("game")||g.paused)return false;switch(key){case Input.Keys.Q:g.bolt();break;case Input.Keys.E:g.nova();break;case Input.Keys.SPACE:g.dash();break;case Input.Keys.R:g.potion();break;case Input.Keys.F:g.interact();break;case Input.Keys.B:g.buyPotion();break;case Input.Keys.N:g.expedition();break;case Input.Keys.F1:open("guide");break;default:return false;}return true;}
 @Override public boolean keyTyped(char c){if(typing&&c>=32&&c!=127&&location.length()<68){location+=c;return true;}return false;}
 @Override public boolean scrolled(float x,float y){if(modal.isEmpty())g.cameraZoom=MathUtils.clamp(g.cameraZoom+y*.07f,.7f,1.5f);return true;}
 public void dispose(){b.dispose();font.dispose();pixel.dispose();circle.dispose();}
}
