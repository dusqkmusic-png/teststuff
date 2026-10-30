package game;
import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.*;
import com.badlogic.gdx.graphics.g3d.environment.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.*;
import java.time.*;

public class Asterfall extends ApplicationAdapter {
 public Content content;public SaveData save;public Weather weather;public Models models;public UI ui;
 public PerspectiveCamera camera;public ModelBatch batch;public Environment env;public DirectionalLight sun;
 public ArrayList<ModelInstance> scenery=new ArrayList<>();public ArrayList<Obstacle> obstacles=new ArrayList<>();
 public ArrayList<Enemy> enemies=new ArrayList<>();public ArrayList<Drop> drops=new ArrayList<>();public ArrayList<Effect> effects=new ArrayList<>();public ArrayList<Floating> floats=new ArrayList<>();
 public Models.Puppet hero;public Random rng=new Random();public Vector3 pos=new Vector3(0,0,7),target=new Vector3(0,0,7),aim=new Vector3(0,0,-1);
 public float hp,mana=100,time,attackCd,novaCd,boltCd,dashCd,invulnerable,autosave,weatherTimer,hitFlash,cameraZoom=.87f;public int kills=0;public boolean bossSpawned=false,victory=false,moving=false,paused=false;
 public static boolean smoke=false;public int smokeFrame=0;
 public String screen="title",notice="",zone="Lanternhaven",saveError="";public float noticeTime;
 public Sound slashS,hitS,lootS,castS;public Music music;
 public static class Obstacle {float x,z,r;Obstacle(float x,float z,float r){this.x=x;this.z=z;this.r=r;}}
 public static class Enemy {Models.Puppet body;float hp,maxHp,speed,cooldown,windup,stun;Vector3 locked=new Vector3();boolean boss,elite;Content.Zone zone;}
 public static class Drop {Vector3 pos;SaveData.Item item;float age;Drop(Vector3 p,SaveData.Item i){pos=p.cpy();item=i;}}
 public static class Effect {Vector3 pos,vel;float age,life,size;String color;boolean bolt,hit;Effect(Vector3 p,float life,float size,String col){pos=p.cpy();this.life=life;this.size=size;color=col;}}
 public static class Floating {Vector3 pos;String text,color;float age;Floating(Vector3 p,String t,String c){pos=p.cpy().add(0,1.8f,0);text=t;color=c;}}
 @Override public void create(){try{content=new Content();content.load();save=SaveData.load();weather=new Weather(save);models=new Models();hero=models.hero();hero.angle=180;hp=save.maxHp();camera=new PerspectiveCamera(42,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());camera.near=.1f;camera.far=145;batch=new ModelBatch();env=new Environment();sun=new DirectionalLight().set(.9f,.87f,.82f,-.4f,-1,-.3f);env.add(sun);env.set(new ColorAttribute(ColorAttribute.AmbientLight,.68f,.68f,.77f,1));env.set(new ColorAttribute(ColorAttribute.Fog,.61f,.75f,.79f,1));buildWorld();spawn();ui=new UI(this);Gdx.input.setInputProcessor(ui);slashS=Gdx.audio.newSound(Gdx.files.internal("assets/slash.wav"));hitS=Gdx.audio.newSound(Gdx.files.internal("assets/hit.wav"));lootS=Gdx.audio.newSound(Gdx.files.internal("assets/loot.wav"));castS=Gdx.audio.newSound(Gdx.files.internal("assets/cast.wav"));music=Gdx.audio.newMusic(Gdx.files.internal("assets/garden.wav"));music.setLooping(true);music.setVolume(.23f);if(save.music)music.play();}catch(Exception e){throw new RuntimeException("Could not load Asterfall: "+e.getMessage(),e);}}
 void add(Model m,float x,float y,float z,float sx,float sy,float sz){scenery.add(models.instance(m,x,y,z,sx,sy,sz));}
 void obstacle(float x,float z,float r){obstacles.add(new Obstacle(x,z,r));}
 void tree(float x,float z,boolean pink,float scale){add(models.trunk,x,scale*.85f,z,.45f*scale,1.7f*scale,.45f*scale);add(pink?models.leafPink:models.leaf,x,scale*2.1f,z,2.8f*scale,2.3f*scale,2.7f*scale);add(pink?models.leafPink:models.leaf,x+.65f*scale,scale*2.6f,z+.2f,1.7f*scale,1.5f*scale,1.7f*scale);add(models.shadow,x,.025f,z,2.9f*scale,.02f,2.4f*scale);obstacle(x,z,.65f*scale);}
 void house(float x,float z,float scale){add(models.wall,x,scale,z,3.5f*scale,2*scale,3*scale);add(models.roof,x,2.8f*scale,z,5.3f*scale,2*scale,4.7f*scale);add(models.dark,x,.7f*scale,z-1.51f*scale,.7f*scale,1.4f*scale,.05f);for(int side:new int[]{-1,1})add(models.gold,x+side*1.1f*scale,1.2f*scale,z-1.52f*scale,.5f*scale,.6f*scale,.06f);for(int side:new int[]{-1,1})add(models.gold,x+side*1.1f*scale,1.2f*scale,z+1.52f*scale,.5f*scale,.6f*scale,.06f);add(models.dark,x,.7f*scale,z+1.51f*scale,.7f*scale,1.4f*scale,.05f);add(models.shadow,x,.025f,z,5.4f*scale,.02f,4.7f*scale);obstacle(x,z,2.1f*scale);}
 void buildWorld(){Random r=new Random(814);add(models.grass,0,-.35f,-13,82,.6f,95);
  // Soft, polygonal tile variation, paths, river, plaza and distant boundaries.
  Model grass2=models.box("98c5a0"),grass3=models.box("92c19a");for(int i=0;i<210;i++){float x=r.nextFloat()*76-38,z=r.nextFloat()*89-57;add(i%2==0?grass2:grass3,x,-.035f,z,2+r.nextFloat()*3,.03f,2+r.nextFloat()*3);}
  for(int i=0;i<25;i++)add(models.path,0,-.003f,12-i*2,3.4f,.1f,2.05f);
  for(int side:new int[]{-1,1})for(int i=1;i<9;i++)add(models.path,side*i*1.8f,.012f,-10,1.95f,.11f,2.6f);
  add(models.water,24,-.015f,-11,9,.13f,70);for(int i=0;i<27;i++)add(models.stone,18.9f,-.02f,18-i*2.6f,1.1f,.3f,2.8f);
  for(int i=0;i<6;i++)add(models.trunk,20+i*1.05f,.12f,-10,1,.23f,3.1f);
  add(models.stone,0,.01f,7,10,.14f,8);add(models.rune,0,.17f,7,3.4f,.25f,3.4f);add(models.crystal,0,1.4f,9,1,2.3f,1);add(models.gold,0,.5f,9,1.7f,.35f,1.7f);
  house(-8,9,1);house(8,10,.9f);house(-8,17,.8f);
  for(int side:new int[]{-1,1})for(int j=0;j<6;j++){float x=side*4.2f,z=4-j*7;add(models.trunk,x,1.05f,z,.15f,2.1f,.15f);add(models.gold,x,2.1f,z,.48f,.65f,.48f);add(models.roof,x,2.53f,z,.75f,.3f,.75f);}
  for(int i=0;i<115;i++){float x=r.nextFloat()*74-37,z=r.nextFloat()*86-56;if(Math.abs(x)<5||Math.abs(z+10)<3||Math.abs(x-24)<6||new Vector2(x,z).dst(0,-32)<9||new Vector2(x,z).dst(0,9)<14)continue;boolean near=false;for(Content.Zone zone:content.zones)if(new Vector2(x,z).dst(zone.x,zone.z)<6)near=true;if(!near)tree(x,z,i%3!=0,.7f+r.nextFloat()*.6f);}
  Model petal=models.sphere("f4d5dc"),flower=models.sphere("f6dc9d");for(int i=0;i<250;i++){float x=r.nextFloat()*63-32,z=r.nextFloat()*76-52;if(Math.abs(x-24)<6)continue;add(i%2==0?petal:flower,x,.1f,z,.15f+r.nextFloat()*.2f,.16f,.2f);}
  for(Content.Zone z:content.zones){add(models.rune,z.x,.035f,z.z,6,.12f,6);for(int j=0;j<6;j++){float a=j*MathUtils.PI2/6;float x=z.x+MathUtils.cos(a)*7.5f,zz=z.z+MathUtils.sin(a)*7.5f;add(models.stone,x,.3f,zz,1.1f,.7f,1.2f);}}
  for(int side:new int[]{-1,1}){add(models.stone,side*5.5f,1.9f,-34,1.1f,3.8f,1.2f);add(models.gold,side*5.5f,3.8f,-34,1.45f,.3f,1.5f);obstacle(side*5.5f,-34,.8f);}add(models.stone,0,4.1f,-34,12,.65f,1.5f);
  for(int i=0;i<38;i++){tree(-37+i*2,-55,i%2==0,1.5f);}for(int i=0;i<24;i++){tree(-37,-53+i*3.2f,true,1.25f);tree(35,-53+i*3.2f,false,1.2f);}
 }
 void spawn(){kills=0;bossSpawned=false;victory=false;enemies.clear();drops.clear();effects.clear();for(Content.Zone z:content.zones){for(int j=0;j<z.enemies;j++){Enemy e=new Enemy();e.zone=z;e.elite=j==z.enemies-1;e.body=models.slime(z.color,false);if(e.elite)e.body.scale=1.4f;float a=j*MathUtils.PI2/z.enemies,rr=3+rng.nextFloat()*2;e.body.pos.set(z.x+MathUtils.cos(a)*rr,0,z.z+MathUtils.sin(a)*rr);e.maxHp=(e.elite?105:52)*(1+(save.expedition-1)*.18f);e.hp=e.maxHp;e.speed=e.elite?2.1f:1.8f;e.cooldown=rng.nextFloat();enemies.add(e);}}}
 public void begin(){screen="game";toast("Welcome to Lanternhaven. Follow the path north.");}
 public void toast(String s){notice=s;noticeTime=5;}
 public void safeSave(){try{save.save();saveError="";}catch(Exception e){saveError=e.getMessage();toast(saveError);}}
 public void expedition(){if(!victory){toast("Defeat the Crowned Bloom before opening a new trail.");return;}save.expedition++;pos.set(0,0,7);target.set(pos);hp=save.maxHp();mana=100;save.potions=Math.max(5,save.potions);spawn();safeSave();toast("Expedition "+save.expedition+" - a fresh trail, stronger spirits.");}
 public void shrine(){if(pos.dst2(0,0,7)<36){hp=save.maxHp();mana=100;save.potions=Math.max(save.potions,5);safeSave();toast("Restored at the lantern shrine. Progress saved.");}else toast("Return to Lanternhaven to rest at the shrine.");}
 void boss(){Enemy e=new Enemy();e.boss=true;e.body=models.slime("c4a5d9",true);e.body.pos.set(0,0,-34);e.maxHp=850*(1+(save.expedition-1)*.3f);e.hp=e.maxHp;e.speed=1.25f;enemies.add(e);bossSpawned=true;toast("The Crowned Bloom has awakened in Moonbell Ruins!");}
 public Vector3 ground(){Ray ray=camera.getPickRay(Gdx.input.getX(),Gdx.input.getY());Vector3 p=new Vector3();if(Intersector.intersectRayPlane(ray,new Plane(Vector3.Y,0),p))return p;return pos.cpy().add(aim);}
 public void attack(){if(!screen.equals("game")||paused||attackCd>0)return;attackCd=.36f;Vector3 a=ground().sub(pos);a.y=0;if(a.len2()>.01f)aim.set(a.nor());hero.angle=MathUtils.atan2(-aim.x,-aim.z)*MathUtils.radiansToDegrees;Effect fx=new Effect(pos,.24f,2.8f,"fff1c4");effects.add(fx);slashS.play(.25f,rng.nextFloat()*.2f+.9f,0);for(Enemy e:new ArrayList<>(enemies)){Vector3 delta=e.body.pos.cpy().sub(pos);float dist=delta.len();if(dist<(e.boss?4.3f:3.0f)&&(dist<1||delta.nor().dot(aim)>-.05f))damage(e,save.damage(),false);}}
 public void nova(){if(!screen.equals("game")||paused||novaCd>0||mana<30)return;mana-=30;novaCd=5;effects.add(new Effect(pos,.65f,6.8f,"b7efeb"));castS.play(.4f,.8f,0);for(Enemy e:new ArrayList<>(enemies))if(e.body.pos.dst(pos)<7.1f)damage(e,save.damage()*2.1f,true);}
 public void bolt(){if(!screen.equals("game")||paused||boltCd>0||mana<12)return;mana-=12;boltCd=.7f;Vector3 d=ground().sub(pos);d.y=0;if(d.len2()<.01f)d.set(aim);d.nor();aim.set(d);Effect e=new Effect(pos.cpy().add(0,.8f,0),1.8f,.45f,"b7dbff");e.vel=d.scl(19);e.bolt=true;effects.add(e);castS.play(.22f,1.4f,0);}
 public void dash(){if(!screen.equals("game")||paused||dashCd>0)return;dashCd=2.6f;invulnerable=.45f;Vector3 d=movement();if(d.len2()<.01f)d.set(aim);for(int i=0;i<12;i++)move(d.x*.43f,d.z*.43f);target.set(pos);effects.add(new Effect(pos,.3f,1.5f,"e5d2ff"));slashS.play(.16f,1.5f,0);}
 public void buyPotion(){if(pos.dst2(0,0,7)>=36){toast("Potions are sold at the town shrine.");return;}if(save.gold<15){toast("A potion costs 15 gold.");return;}save.gold-=15;save.potions++;safeSave();toast("Purchased a potion for 15 gold.");}
 public void potion(){if(!screen.equals("game")||paused||save.potions==0||hp>=save.maxHp())return;save.potions--;hp=Math.min(save.maxHp(),hp+65+save.level*8);floats.add(new Floating(pos,"Restored","b7efcb"));lootS.play(.2f,.75f,0);}
 void damage(Enemy e,float amount,boolean magic){boolean crit=rng.nextInt(100)<save.crit();float n=amount*(crit?1.7f:1);e.hp-=n;e.stun=.22f;floats.add(new Floating(e.body.pos,(crit?"CRIT ":"")+(int)n,crit?"ffe1a2":"fff6e8"));hitS.play(.14f,magic?1.2f:.95f,0);if(e.hp<=0){enemies.remove(e);kills++;save.gold+=e.boss?100:e.elite?14:4+rng.nextInt(6);save.xp+=e.boss?120:e.elite?25:12;if(rng.nextFloat()<.55f||e.elite||e.boss)drop(e);if(e.boss){victory=true;save.bosses++;save.potions+=2;for(int i=0;i<3;i++)drop(e);toast("The garden is free! Return to the shrine for a new expedition.");safeSave();}while(save.xp>=save.level*65){save.xp-=save.level*65;save.level++;hp=save.maxHp();mana=100;toast("Level "+save.level+"! Health restored and power increased.");effects.add(new Effect(pos,1,4,"ffe6a3"));}if(enemies.isEmpty()&&!bossSpawned)boss();}}
 void drop(Enemy e){Content.ItemDef def=content.roll(rng,weather.night(),weather.wet());float roll=rng.nextFloat();String rarity=def.weight<.2f?"Mythic":def.weight<=3||roll<.12f?"Rare":roll<.55f?"Enchanted":"Common";int bonus=rarity.equals("Mythic")?12:rarity.equals("Rare")?7:rarity.equals("Enchanted")?3:0;String[] aff={"of Blossoms","of Sparks","of the Fox","of Shelter"};int p=def.power+bonus+save.level+save.expedition;String af=aff[rng.nextInt(aff.length)];SaveData.Item item=new SaveData.Item(def.id,rarity,af,p,def.slot.equals("armor")?p*3:af.equals("of Shelter")?p:0,af.equals("of the Fox")?3+bonus/2:0);drops.add(new Drop(e.body.pos.cpy().add(rng.nextFloat()*2-1,0,rng.nextFloat()*2-1),item));}
 public void pickup(){int count=0;Iterator<Drop> it=drops.iterator();while(it.hasNext()){Drop d=it.next();if(d.pos.dst(pos)<3){if(save.bag.size()>=60){toast("Satchel full. Salvage some equipment with I.");break;}save.bag.add(d.item);it.remove();count++;}}if(count>0){lootS.play(.5f);toast("Collected "+count+" item"+(count==1?"":"s")+". Open your satchel with I.");safeSave();}}
 public void equip(int index){if(index<0||index>=save.bag.size())return;SaveData.Item item=save.bag.get(index),old;switch(content.find(item.id).slot){case "weapon":old=save.weapon;save.weapon=item;break;case "armor":old=save.armor;save.armor=item;break;default:old=save.charm;save.charm=item;}save.bag.remove(index);if(old!=null)save.bag.add(old);hp=Math.min(hp,save.maxHp());safeSave();lootS.play(.2f);}
 public void salvage(int index){if(index<0||index>=save.bag.size())return;save.gold+=save.bag.get(index).power*2;save.bag.remove(index);safeSave();}
 Vector3 movement(){Vector3 d=new Vector3();if(Gdx.input.isKeyPressed(Input.Keys.W)||Gdx.input.isKeyPressed(Input.Keys.UP))d.z--;if(Gdx.input.isKeyPressed(Input.Keys.S)||Gdx.input.isKeyPressed(Input.Keys.DOWN))d.z++;if(Gdx.input.isKeyPressed(Input.Keys.A)||Gdx.input.isKeyPressed(Input.Keys.LEFT))d.x--;if(Gdx.input.isKeyPressed(Input.Keys.D)||Gdx.input.isKeyPressed(Input.Keys.RIGHT))d.x++;return d.nor();}
 void move(float x,float z){if(walkable(pos.x+x,pos.z))pos.x+=x;if(walkable(pos.x,pos.z+z))pos.z+=z;}
 boolean walkable(float x,float z){if(x<-34||x>32||z<-51||z>23)return false;if(x>19&&x<29&&Math.abs(z+10)>1.5f)return false;for(Obstacle o:obstacles)if(Vector2.dst2(x,z,o.x,o.z)<(o.r+.33f)*(o.r+.33f))return false;return true;}
 void update(float dt){time+=dt;noticeTime=Math.max(0,noticeTime-dt);weatherTimer+=dt;if(weatherTimer>900){weatherTimer=0;weather.refresh(save);}if(!screen.equals("game")||paused)return;autosave+=dt;if(autosave>30){safeSave();autosave=0;}
 attackCd=Math.max(0,attackCd-dt);novaCd=Math.max(0,novaCd-dt);boltCd=Math.max(0,boltCd-dt);dashCd=Math.max(0,dashCd-dt);invulnerable=Math.max(0,invulnerable-dt);hitFlash=Math.max(0,hitFlash-dt);mana=Math.min(100,mana+dt*(9+save.flow*1.5f));
 Vector3 d=movement();if(d.len2()>0){target.set(pos);aim.set(d);}else if(target.dst2(pos)>.1f){d.set(target).sub(pos);d.y=0;d.nor();aim.set(d);}moving=d.len2()>0;if(moving){move(d.x*dt*5.4f,d.z*dt*5.4f);if(attackCd<.15f)hero.angle=MathUtils.atan2(-d.x,-d.z)*MathUtils.radiansToDegrees;}
 if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)&&!ui.blocksMouse())attack();if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT)&&!ui.blocksMouse()){target.set(ground());target.x=MathUtils.clamp(target.x,-34,32);target.z=MathUtils.clamp(target.z,-51,23);}
 zone="Lanternhaven";float nearest=99;for(Content.Zone z:content.zones){float dist=Vector2.dst(pos.x,pos.z,z.x,z.z);if(dist<nearest&&dist<15){zone=z.name;nearest=dist;}}if(pos.z<-25)zone="Moonbell Ruins";
 for(Enemy e:new ArrayList<>(enemies)){e.cooldown-=dt;e.stun=Math.max(0,e.stun-dt);float dist=e.body.pos.dst(pos);if(e.windup>0){e.windup-=dt;if(e.windup<=0){float radius=e.boss?4.3f:1.85f;if(pos.dst(e.locked)<radius&&invulnerable==0){float amount=(e.boss?28:e.elite?15:9)*(1+(save.expedition-1)*.1f);hp-=amount;invulnerable=.35f;hitFlash=.2f;floats.add(new Floating(pos,"-"+(int)amount,"ffb9b5"));hitS.play(.25f,.65f,0);}effects.add(new Effect(e.locked,.35f,radius,"f4a9bc"));e.cooldown=e.boss?1.8f:1.2f;}continue;}if(e.stun>0)continue;
  if(dist<13&&dist>1.45f+(e.boss?1:0)){Vector3 v=pos.cpy().sub(e.body.pos).nor();e.body.pos.add(v.x*e.speed*dt,0,v.z*e.speed*dt);e.body.angle=MathUtils.atan2(-v.x,-v.z)*MathUtils.radiansToDegrees;}if(dist<(e.boss?7:2.3f)&&e.cooldown<=0){e.windup=e.boss?1.05f:.65f;e.locked.set(pos);}}
 for(Iterator<Effect> it=effects.iterator();it.hasNext();){Effect e=it.next();e.age+=dt;if(e.bolt){e.pos.mulAdd(e.vel,dt);for(Enemy enemy:new ArrayList<>(enemies))if(Vector2.dst(e.pos.x,e.pos.z,enemy.body.pos.x,enemy.body.pos.z)<(enemy.boss?2:1)){damage(enemy,save.damage()*1.4f,true);e.age=e.life;break;}}if(e.age>=e.life)it.remove();}
 for(Iterator<Floating> it=floats.iterator();it.hasNext();){Floating f=it.next();f.age+=dt;f.pos.y+=dt*.8f;if(f.age>1.1f)it.remove();}for(Drop drop:drops)drop.age+=dt;
 if(hp<=0){hp=0;screen="death";safeSave();}
 }
 public void revive(){hp=save.maxHp();mana=100;pos.set(0,0,7);target.set(pos);screen="game";save.potions=Math.max(3,save.potions);for(Enemy e:enemies){e.windup=0;e.cooldown=1;}toast("The shrine welcomes you back. Your equipment is safe.");}
 @Override public void render(){float dt=Math.min(Gdx.graphics.getDeltaTime(),.05f);update(dt);boolean night=weather.night();Color sky=new Color(.27f,.32f,.48f,1).lerp(new Color(.66f,.81f,.84f,1),weather.daylight());if(weather.wet())sky.lerp(new Color(.49f,.57f,.68f,1),.3f);ScreenUtils.clear(sky,true);float daylight=weather.daylight();env.set(new ColorAttribute(ColorAttribute.AmbientLight,.27f+daylight*.10f,.30f+daylight*.10f,.43f,1));env.set(ColorAttribute.createFog(sky));sun.set(.22f+daylight*.40f,.25f+daylight*.32f,.40f+daylight*.12f,-.4f,-1,-.3f);
 Vector3 focus=screen.equals("title")?new Vector3(0,0,2):pos;float zoom=screen.equals("title")?1.12f:cameraZoom;camera.position.set(focus.x,20*zoom,focus.z+18*zoom);camera.lookAt(focus.x,0,focus.z-2);camera.up.set(Vector3.Y);camera.far=weather.linked&&weather.code>=45&&weather.code<=48?68:145;camera.update();Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);batch.begin(camera);for(ModelInstance s:scenery)batch.render(s,env);batch.render(models.instance(models.shadow,pos.x,.055f,pos.z,1.3f,.015f,1.0f),env);hero.pos.set(pos);hero.hit=attackCd;hero.draw(batch,env,time,moving);for(Enemy e:enemies){if(e.body.pos.dst2(pos)>2500&&!screen.equals("title"))continue;batch.render(models.instance(models.shadow,e.body.pos.x,.055f,e.body.pos.z,e.body.scale*1.25f,.015f,e.body.scale),env);e.body.draw(batch,env,time+e.body.pos.x,e.body.pos.dst(pos)<13&&e.windup==0);}
 for(Drop drop:drops){ModelInstance jewel=models.instance(models.crystal,drop.pos.x,.65f+MathUtils.sin(time*2+drop.pos.x)*.12f,drop.pos.z,.4f,.65f,.4f);jewel.transform.rotate(Vector3.Y,time*70);batch.render(jewel,env);}
 for(Effect e:effects){if(e.bolt){batch.render(models.instance(models.rune,e.pos.x,e.pos.y,e.pos.z,e.size,e.size,e.size),env);}else{float r=e.size*e.age/e.life;for(int i=0;i<24;i++){float a=i*MathUtils.PI2/24;batch.render(models.instance(models.rune,e.pos.x+MathUtils.cos(a)*r,.24f,e.pos.z+MathUtils.sin(a)*r,.18f,.13f,.18f),env);}}}
 for(Enemy e:enemies)if(e.windup>0){float radius=e.boss?4.3f:1.85f;for(int i=0;i<24;i++){float a=i*MathUtils.PI2/24;batch.render(models.instance(models.gold,e.locked.x+MathUtils.cos(a)*radius,.12f,e.locked.z+MathUtils.sin(a)*radius,.17f,.1f,.17f),env);}}
 batch.end();ui.render(dt);if(smoke)smokeStep();}

 void capture(String name){com.badlogic.gdx.graphics.Pixmap p=com.badlogic.gdx.utils.ScreenUtils.getFrameBufferPixmap(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());com.badlogic.gdx.graphics.PixmapIO.writePNG(Gdx.files.local("../"+name+".png"),p,6,true);p.dispose();}
 void require(boolean value,String message){if(!value)throw new AssertionError(message);}
 void smokeStep(){smokeFrame++;
  if(smokeFrame==12)capture("preview-title");
  if(smokeFrame==14){begin();pos.set(-13,0,-7);target.set(pos);}
  if(smokeFrame==28)capture("preview-garden");
  if(smokeFrame==30){int before=enemies.size();Enemy e=enemies.get(0);damage(e,999,false);require(enemies.size()==before-1,"Enemy dies");require(save.xp>0,"Kill grants XP");nova();require(novaCd>0&&mana<100,"Nova consumes mana and starts cooldown");dash();require(invulnerable>0,"Dodge grants invulnerability");hp=20;potion();require(hp>20,"Potion restores health");}
  if(smokeFrame==34){for(Enemy e:new ArrayList<>(enemies))damage(e,9999,false);require(bossSpawned,"Boss unlocks after gardens cleared");require(enemies.size()==1&&enemies.get(0).boss,"Boss exists");damage(enemies.get(0),99999,false);require(victory&&save.bosses==1,"Boss victory");for(Drop d:new ArrayList<>(drops)){pos.set(d.pos);pickup();}require(save.bag.size()>0,"Loot pickup");equip(0);require(save.weapon!=null||save.armor!=null||save.charm!=null,"Equipment equipped");ui.open("inventory");}
  if(smokeFrame==39)capture("preview-equipment");
  if(smokeFrame==40){ui.modal="talents";}
  if(smokeFrame==41){ui.close();expedition();require(save.expedition==2&&enemies.size()==24&&!victory,"Replay resets gardens and scales expedition");ui.open("settings");}
  if(smokeFrame==45)capture("preview-settings");
  if(smokeFrame==47){ui.close();safeSave();require(SaveData.load().level==save.level,"Saved progress loads");System.out.println("PASS: rendered title/world/inventory/settings; combat, abilities, boss, loot, equipment, replay, saves.");Gdx.app.exit();}
 }
 @Override public void resize(int w,int h){if(camera!=null){camera.viewportWidth=w;camera.viewportHeight=h;camera.update();}if(ui!=null)ui.resize(w,h);}
 @Override public void pause(){if(screen.equals("game"))paused=true;if(save!=null)safeSave();}
 @Override public void dispose(){if(save!=null)safeSave();if(weather!=null)weather.close();if(batch!=null)batch.dispose();if(models!=null)models.dispose();if(ui!=null)ui.dispose();if(music!=null)music.dispose();for(Sound s:new Sound[]{slashS,hitS,lootS,castS})if(s!=null)s.dispose();}
}
