'use strict';
const canvas=document.querySelector('#world'),ctx=canvas.getContext('2d');
canvas.width=480;canvas.height=270;
ctx.scale(.5,.5); ctx.imageSmoothingEnabled=false;
const W=960,H=540,T=16,MW=100,MH=76;
let seed=71;function rand(){seed=(seed*1664525+1013904223)>>>0;return seed/4294967296;}
const noise=Array.from({length:MH},()=>Array.from({length:MW},()=>rand()));
const keys=new Set(),player={x:768,y:660,dir:0,walk:0,moving:false,charge:0,dashing:false,dashBlocked:false,dashDir:0},camera={x:0,y:0};
let weather={code:0,temp:null,wind:0,live:false},last=0,time=0,noticeTimer,muted=true,audio;
const trees=[],flowers=[],rocks=[];
function river(y){return 1110+Math.sin(y/145)*47;}
function isPath(x,y){return Math.abs(x-760)<27 || Math.abs(y-650)<25&&x>350&&x<1190 || Math.abs(y-390)<22&&x>450&&x<800;}
const houses=[{x:490,y:450,w:112,h:96,roof:'#af6550',name:'Juniper Cottage'},{x:845,y:410,w:112,h:96,roof:'#6b8191',name:'The Riverside House'},{x:535,y:245,w:112,h:96,roof:'#9c7259',name:'The Gardener’s Home'}];
for(let i=0;i<600;i++){let x=32+rand()*(MW*T-64),y=32+rand()*(MH*T-64);if(!isPath(x,y)&&Math.abs(x-river(y))>100&&!houses.some(h=>x>h.x-40&&x<h.x+h.w+40&&y>h.y-60&&y<h.y+h.h+40)&&Math.hypot(x-765,y-650)>110)trees.push({x,y,s:0.8+rand()*.5});}
for(let i=0;i<65;i++)rocks.push({x:60+rand()*1460,y:80+rand()*1050});
for(let i=0;i<520;i++)flowers.push({x:rand()*MW*T,y:rand()*MH*T,c:rand()>.5?'#e2c66e':'#cba6b0'});
const objects=[{x:700,y:650,text:'Willowmere Village • North: gardens. East: river. Everywhere: a little peace.'},{x:675,y:740,text:'You sit for a moment. The world can wait.'},{x:1000,y:650,text:'The water carries the day slowly downstream.'},{x:590,y:570,text:'Someone has been tending these flowers with care.'}];
function rect(x,y,w,h,c){ctx.fillStyle=c;ctx.fillRect(Math.round(x),Math.round(y),w,h);}
function msg(s){document.querySelector('#message').textContent=s;document.querySelector('#message').style.opacity=1;clearTimeout(noticeTimer);noticeTimer=setTimeout(()=>document.querySelector('#message').style.opacity=0,6500);}
function nyParts(){let p=new Intl.DateTimeFormat('en-US',{timeZone:'America/New_York',hourCycle:'h23',hour:'2-digit',minute:'2-digit',second:'2-digit'}).formatToParts(new Date());return Object.fromEntries(p.map(x=>[x.type,x.value]));}
function dayState(){let p=nyParts(),hour=+p.hour+(+p.minute)/60;return {hour,dark:hour<5||hour>=21?.68:hour<7?.68*(7-hour)/2:hour<18?0:hour<21?.68*(hour-18)/3:0,phase:hour<5||hour>=21?'Moonlit night':hour<7?'First light':hour<17?'Daylight':hour<21?'Evening glow':'Night'};}
function updateClock(){document.querySelector('#clock').textContent=new Intl.DateTimeFormat('en-US',{timeZone:'America/New_York',hour:'numeric',minute:'2-digit'}).format(new Date());document.querySelector('#date').textContent=new Intl.DateTimeFormat('en-US',{timeZone:'America/New_York',month:'short',day:'numeric',timeZoneName:'short'}).format(new Date()).toUpperCase();document.querySelector('#phase').textContent=dayState().phase+' · New York';}
function description(c){return c===0?'Clear skies':c<=3?'Cloudy':c<=48?'Misty':c<=57?'Drizzle':c<=67?'Rain':c<=77?'Snow':c<=82?'Rain showers':c<=86?'Snow showers':'Thunderstorms';}
function wet(){return weather.live&&weather.code>=51&&!(weather.code>=71&&weather.code<=77)&&!(weather.code>=85&&weather.code<=86);}
function snowy(){return weather.live&&(weather.code>=71&&weather.code<=77||weather.code>=85&&weather.code<=86);}
async function updateWeather(){try{const controller=new AbortController(),timer=setTimeout(()=>controller.abort(),12000);let r;try{r=await fetch('https://api.open-meteo.com/v1/forecast?latitude=40.7654&longitude=-73.8174&current=temperature_2m,weather_code,wind_speed_10m&temperature_unit=fahrenheit&wind_speed_unit=mph&timezone=America%2FNew_York',{signal:controller.signal});}finally{clearTimeout(timer);}if(!r.ok)throw Error('Weather unavailable');const d=await r.json();if(!d.current||!Number.isFinite(d.current.weather_code)||!Number.isFinite(d.current.temperature_2m))throw Error('Invalid weather');weather={code:d.current.weather_code,temp:d.current.temperature_2m,wind:d.current.wind_speed_10m,live:true};document.querySelector('#weather').textContent=`${description(weather.code)} · ${Math.round(weather.temp)}°F`;document.querySelector('#weather-icon').textContent=snowy()?'❄':wet()?'☂':weather.code===0?'☼':'☁';document.querySelector('#weather-detail').textContent='Flushing · updated '+new Intl.DateTimeFormat('en-US',{timeZone:'America/New_York',hour:'numeric',minute:'2-digit'}).format(new Date());}catch(e){document.querySelector('#weather').textContent=weather.live?'Weather update delayed':'Live weather unavailable';document.querySelector('#weather-detail').textContent=weather.live?'Showing last received conditions':'Clear visual fallback · retries every 10 min';}}
function blocked(x,y){return (x>40&&x<430&&y>282+Math.floor(Math.sin(x/85)*2)*4&&y<354+Math.floor(Math.sin(x/85)*2)*4)||(x>80&&x<660&&y>908+Math.floor(Math.sin(x/90)*2)*4&&y<974+Math.floor(Math.sin(x/90)*2)*4)||x<24||y<24||x>MW*T-24||y>MH*T-24||Math.abs(x-river(y))<47&&Math.abs(y-650)>25||houses.some(h=>x>h.x-6&&x<h.x+h.w+6&&y>h.y+20&&y<h.y+h.h+5)||trees.some(t=>Math.abs(x-t.x)<10&&Math.abs(y-t.y)<9);}
// Original pixel artwork. All shapes use a shared two-world-unit pixel grid.
function poly(points,c){ctx.fillStyle=c;ctx.beginPath();points.forEach(([x,y],i)=>i?ctx.lineTo(x,y):ctx.moveTo(x,y));ctx.closePath();ctx.fill();}
function drawTree(t){const x=Math.round(t.x/2)*2,y=Math.round(t.y/2)*2;
 ctx.save();ctx.translate(x,y);rect(-24,-2,52,10,'#183b3260');
 poly([[-14,2],[-10,-24],[10,-24],[14,2],[22,8],[8,8],[0,2],[-6,8],[-22,8]],'#263b29');
 rect(-8,-24,16,26,'#795d36');rect(-6,-22,6,24,'#a17c42');rect(6,-20,3,24,'#4e452c');
 const crown=[[-36,-23],[-40,-34],[-36,-48],[-28,-52],[-28,-64],[-14,-76],[10,-78],[26,-68],[28,-58],[38,-52],[42,-38],[36,-22],[22,-14],[-22,-14]];
 poly(crown,'#173c2e');poly(crown.map(([a,b])=>[a*.9,b+3]),'#245e39');
 poly([[-32,-40],[-30,-52],[-20,-56],[-20,-64],[-8,-70],[10,-70],[22,-60],[20,-48],[8,-42],[-6,-44],[-16,-36]],'#397b42');
 poly([[-26,-48],[-16,-58],[-2,-62],[12,-60],[16,-52],[6,-52],[0,-56],[-12,-52],[-16,-44]],'#529349');
 poly([[-22,-28],[-26,-36],[-12,-34],[-4,-40],[8,-36],[18,-42],[30,-40],[30,-30],[20,-22],[8,-24],[0,-20]],'#317340');
 rect(-24,-52,6,4,'#70a050');rect(-8,-64,8,4,'#70a050');rect(24,-44,6,4,'#4b8844');rect(-18,-24,8,4,'#214e34');rect(10,-30,6,4,'#1c4c33');ctx.restore();}
function drawHouse(h,dark){let {x,y,w,h:hh}=h;rect(x+4,y+hh,w+8,10,'#173c3255');
 rect(x-2,y+26,w+4,hh-24,'#3a4030');rect(x+2,y+28,w-4,hh-28,'#b2a16b');
 for(let row=0;row<4;row++)for(let col=0;col<7;col++){let xx=x+4+col*16-(row%2)*8,yy=y+32+row*16;rect(xx,yy,14,14,row%2?'#c6b47e':'#d6c18a');rect(xx+2,yy+2,10,2,'#e4d29b');}
 rect(x-4,y+hh-6,w+8,8,'#696449');rect(x,y+hh-6,w,3,'#ded09c');
 rect(x+w/2-15,y+hh-36,30,36,'#51472f');rect(x+w/2-11,y+hh-32,22,32,'#34473a');rect(x+w/2-9,y+hh-30,18,29,'#263b31');rect(x+w/2+6,y+hh-18,3,3,'#d9b45e');
 for(let dx of [12,80]){rect(x+dx-2,y+47,24,24,'#625739');rect(x+dx,y+49,20,18,dark>.2?'#f3c66d':'#6b9992');rect(x+dx+8,y+49,3,18,'#665b3c');rect(x+dx,y+57,20,3,'#665b3c');rect(x+dx-4,y+68,28,4,'#8d8054');}
 poly([[x-12,y+38],[x-10,y+26],[x+8,y-10],[x+w-8,y-10],[x+w+10,y+26],[x+w+12,y+38]],'#3f3831');
 for(let row=0;row<8;row++){let inset=Math.max(0,16-row*4),yy=y-6+row*5;rect(x-6+inset,yy,w+12-inset*2,5,h.roof);rect(x-6+inset,yy+4,w+12-inset*2,1,'#573f3b');for(let xx=x+inset+(row%2)*10;xx<x+w-inset;xx+=20)rect(xx,yy,2,4,'#684743');}
 rect(x+10,y-12,w-20,4,'#d0a06c');rect(x+w-28,y-18,14,25,'#6e7159');rect(x+w-30,y-20,18,5,'#d0c096');rect(x+w/2-18,y+hh+2,36,6,'#c6bb8c');rect(x+w/2-22,y+hh+8,44,4,'#8d9069');}
function drawPlayer(){let x=Math.round(player.x/2)*2,y=Math.round(player.y/2)*2;
 const f=player.moving?Math.floor(player.walk)%4:0,step=[0,2,0,-2][f],side=player.dir===1||player.dir===3,back=player.dir===2;
 ctx.save();ctx.translate(x,y);if(player.dir===3)ctx.scale(-1,1);
 rect(-8,0,18,4,'#173c3277');
 // Boots, alternating feet, and outlined coat.
 rect(-7,-7+step,6,9,'#283b32');rect(2,-7-step,6,9,'#283b32');rect(-6,-5+step,5,5,'#765239');rect(3,-5-step,5,5,'#997043');
 poly([[-8,-21],[-10,-10],[-6,-4],[7,-4],[10,-10],[8,-21]],'#243e33');rect(-6,-21,13,14,'#467b72');rect(-4,-20,5,10,'#68a28a');rect(-7,-9,15,3,'#654732');rect(0,-9,3,3,'#dbb963');
 rect(-11,-19+step,5,10,'#2c4133');rect(-10,-17+step,4,7,'#d6a46a');rect(8,-19-step,5,10,'#293e33');rect(9,-17-step,3,7,'#efc08a');
 rect(-7,-32,15,14,'#29372d');rect(-5,-30,11,11,back?'#705336':'#e1af78');
 if(side){rect(6,-27,4,5,'#efbf88');rect(5,-28,2,3,'#253b32');rect(-7,-29,5,10,'#614932');}else if(!back){rect(-4,-26,2,3,'#293c35');rect(3,-26,2,3,'#293c35');rect(-1,-20,3,1,'#a5714f');}
 poly([[-9,-29],[-9,-33],[-5,-37],[6,-37],[10,-32],[10,-28]],'#293a30');rect(-5,-35,11,5,'#b58b4d');rect(-7,-30,17,3,'#d3ae68');rect(-3,-35,7,2,'#e4c17d');if(back)rect(-6,-26,12,6,'#6b4e32');
 if(player.charge>0&&!player.dashing){rect(-13,-42,26,3,'#24392e');rect(-12,-41,24*Math.min(1,player.charge/.32),2,'#eadc8b');}
 ctx.restore();}
const dust=[];
function movePlayer(dt){
 let ax=(keys.has('d')||keys.has('arrowright')?1:0)-(keys.has('a')||keys.has('arrowleft')?1:0),ay=(keys.has('s')||keys.has('arrowdown')?1:0)-(keys.has('w')||keys.has('arrowup')?1:0);
 const dashHeld=keys.has('shift')||keys.has(' ');
 if(!dashHeld){player.charge=0;player.dashing=false;player.dashBlocked=false;}
 if(!player.dashing&&(ax||ay)){if(ax&&!ay)player.dir=ax>0?1:3;else if(ay&&!ax)player.dir=ay>0?0:2;else if((player.dir===0||player.dir===2)&&ay)player.dir=ay>0?0:2;else player.dir=ax>0?1:3;}
 if(dashHeld&&!player.dashBlocked&&!player.dashing){player.charge+=dt;if(player.charge>=.32){player.dashing=true;player.dashDir=player.dir;}}
 if(player.dashing){[ax,ay]=[[0,1],[1,0],[0,-1],[-1,0]][player.dashDir];player.dir=player.dashDir;}
 let speed=player.dashing?280:dashHeld?0:112,len=Math.hypot(ax,ay),dx=len?ax/len*speed*dt:0,dy=len?ay/len*speed*dt:0;
 const oldX=player.x,oldY=player.y,steps=Math.max(1,Math.ceil(Math.hypot(dx,dy)/2));let hit=false;
 for(let i=0;i<steps;i++){if(dx&&!blocked(player.x+dx/steps,player.y))player.x+=dx/steps;else if(dx)hit=true;if(dy&&!blocked(player.x,player.y+dy/steps))player.y+=dy/steps;else if(dy)hit=true;}
 player.moving=Math.hypot(player.x-oldX,player.y-oldY)>.01;
 if(player.moving){player.walk+=dt*(player.dashing?19:10);if(player.dashing&&Math.random()<.6)dust.push({x:player.x,y:player.y,life:.35});}else player.walk=0;
 if(player.dashing&&hit){player.dashing=false;player.dashBlocked=true;player.charge=0;msg('Oof! Release dash to catch your breath.');}
 for(let i=dust.length-1;i>=0;i--){dust[i].life-=dt;if(dust[i].life<=0)dust.splice(i,1);}
}
function render(dt){time+=dt;movePlayer(dt);
const targetX=Math.max(0,Math.min(MW*T-W,player.x-W/2)),targetY=Math.max(0,Math.min(MH*T-H,player.y-H/2));
const follow=1-Math.exp(-dt*12);camera.x+=(targetX-camera.x)*follow;camera.y+=(targetY-camera.y)*follow;let dark=dayState().dark;
ctx.save();ctx.translate(-Math.round(camera.x),-Math.round(camera.y));
for(let ty=Math.floor(camera.y/T);ty<Math.ceil((camera.y+H)/T);ty++)for(let tx=Math.floor(camera.x/T);tx<Math.ceil((camera.x+W)/T);tx++){let x=tx*T,y=ty*T,n=noise[ty]?.[tx]??0;rect(x,y,T,T,n>.65?'#559249':n>.3?'#539047':'#508c44');if(n>.75){rect(x+3,y+5,2,4,'#6da44f');rect(x+1,y+7,6,2,'#6da44f');rect(x+10,y+11,2,2,'#3d793d');}if(isPath(x+8,y+8)){rect(x,y,T,T,n>.4?'#b1a16a':'#ac9b64');if(n>.7)rect(x+4,y+7,3,2,'#9b8d65');}let r=river(y);if(Math.abs(x-r)<73){rect(x,y,T,T,'#8e9b68');if(Math.abs(x-r)<57){rect(x,y,T,T,n>.5?'#4e9294':'#4c8b91');rect(x+((time*6+ty)%12),y+6,7,1,'#8db9ae88');}}}
// Grass fringes break up path edges; stones and layered bluffs frame the meadow.
for(let y=Math.floor(camera.y/8)*8;y<camera.y+H;y+=8)for(let x=Math.floor(camera.x/8)*8;x<camera.x+W;x+=8){if(isPath(x,y)&&!isPath(x+8,y)){rect(x+6,y,4,4,'#4b853f');rect(x+4,y+4,4,2,'#638a43');}if(isPath(x,y)&&!isPath(x,y+8)){rect(x,y+6,4,4,'#4b853f');}}
for(let y=Math.floor(camera.y/8)*8;y<camera.y+H;y+=8){let r=river(y);for(let side of [-1,1]){let x=Math.round((r+side*62)/2)*2;rect(x,y,6,8,'#3c6143');rect(x+side*4,y,4,6,'#b0a86d');rect(x-side*4,y+2,4,2,'#93c6b0');}if(y%24===0){let x=r+Math.sin(y+time)*22;rect(x,y,16,2,'#9bc9c0');rect(x+4,y+3,8,2,'#6aada9');}}
for(let x=40;x<430;x+=16){let y=294+Math.floor(Math.sin(x/85)*2)*4;rect(x,y,16,56,'#3f4930');for(let k=0;k<3;k++){rect(x,y+4+k*16,16,12,k%2?'#8e7948':'#a18a52');rect(x+2,y+5+k*16,10,3,'#b19a5b');rect(x,y+14+k*16,8,3,'#6c603b');}rect(x,y-6,16,10,'#345d34');rect(x+2,y-8,12,6,'#6a9b49');}
for(let x=80;x<660;x+=16){let y=920+Math.floor(Math.sin(x/90)*2)*4;rect(x,y,16,50,'#3a482e');for(let k=0;k<3;k++){rect(x,y+k*14,16,11,'#95804d');rect(x+2,y+2+k*14,10,3,'#ad985c');}rect(x,y-6,16,8,'#356a37');rect(x+2,y-8,12,4,'#7aaa50');}
for(const r of rocks){if(Math.abs(r.x-river(r.y))<90||isPath(r.x,r.y))continue;poly([[r.x-10,r.y],[r.x-12,r.y-8],[r.x-5,r.y-16],[r.x+7,r.y-16],[r.x+13,r.y-7],[r.x+10,r.y+2]],'#284e3a');rect(r.x-8,r.y-10,16,10,'#7e9c7a');rect(r.x-4,r.y-14,10,4,'#a4b98d');rect(r.x-6,r.y-8,4,4,'#b8c79b');rect(r.x+5,r.y-7,4,7,'#597e60');}
for(const d of dust){let size=(.35-d.life)*18+2;rect(d.x-size/2,d.y-size/2,size,size,`rgba(211,192,139,${d.life*1.6})`);}
for(const f of flowers){if(!isPath(f.x,f.y)&&Math.abs(f.x-river(f.y))>80){rect(f.x,f.y,2,4,'#3e653d');rect(f.x-1,f.y,4,2,f.c);}}
// A wooden footbridge and its rails.
for(let x=1030;x<1210;x+=10){rect(x,628,9,45,'#a18b60');rect(x,631,8,2,'#b8a77a');}for(let y of [625,672]){rect(1025,y,190,4,'#6d6547');for(let x=1025;x<1220;x+=30)rect(x,y-4,4,12,'#c0aa78');}
// Garden beds, fence, bench, sign, and a village well.
for(let x=534;x<633;x+=23){rect(x,560,18,36,'#75613f');for(let y=566;y<593;y+=9){rect(x+5,y,8,5,'#93a766');rect(x+7,y-2,4,4,'#d8b17c');}}
for(let x=430;x<662;x+=18){rect(x,604,4,17,'#c5b688');rect(x,608,18,3,'#b3a37a');rect(x,615,18,3,'#b3a37a');}
rect(653,730,43,9,'#ad8a59');rect(653,721,43,6,'#b89765');rect(657,738,4,9,'#5e5140');rect(688,738,4,9,'#5e5140');rect(697,635,4,22,'#705b40');rect(683,630,32,15,'#bfa67a');rect(689,636,19,2,'#6a6247');
ctx.fillStyle='#183b3044';ctx.beginPath();ctx.ellipse(866,730,26,10,0,0,7);ctx.fill();rect(846,710,39,20,'#9b9f8a');rect(852,710,27,12,'#3d6666');rect(844,706,43,5,'#c0bda1');rect(846,686,4,24,'#705b40');rect(881,686,4,24,'#705b40');rect(840,680,50,7,'#906c50');
let things=trees.map(t=>({y:t.y,fn:()=>drawTree(t)})).concat(houses.map(h=>({y:h.y+h.h,fn:()=>drawHouse(h,dark)})),[{y:player.y,fn:drawPlayer}]).sort((a,b)=>a.y-b.y);for(let o of things)if(o.y>camera.y-90&&o.y<camera.y+H+90)o.fn();
ctx.restore();
// Real clock lighting, with warm pools of light after dusk.
if(dark>0){rect(0,0,W,H,`rgba(13,24,55,${dark})`);ctx.save();ctx.globalCompositeOperation='screen';for(const l of [{x:704,y:616},...houses.map(h=>({x:h.x+55,y:h.y+75}))]){let x=l.x-camera.x,y=l.y-camera.y,g=ctx.createRadialGradient(x,y,0,x,y,60);g.addColorStop(0,`rgba(255,192,88,${dark*.6})`);g.addColorStop(1,'rgba(255,192,88,0)');ctx.fillStyle=g;ctx.fillRect(x-60,y-60,120,120);}ctx.restore();}
if(weather.live&&weather.code>0&&weather.code<=48)rect(0,0,W,H,'#b6c3b018');
if(wet()){rect(0,0,W,H,'#24374825');ctx.strokeStyle='#c4deeb77';ctx.lineWidth=1;ctx.beginPath();for(let i=0;i<160;i++){let x=(i*137+time*80)%W,y=(i*79+time*300)%H;ctx.moveTo(x,y);ctx.lineTo(x-3,y+9);}ctx.stroke();}if(snowy()){for(let i=0;i<130;i++){let x=(i*127+Math.sin(time+i)*9+W)%W,y=(i*61+time*24)%H;rect(x,y,2,2,'#ecf2e5bb');}}
if(dark>.25&&!wet()&&!snowy()){for(let i=0;i<24;i++){let x=(i*139+Math.sin(time*.5+i)*13-camera.x*.2+W*2)%W,y=(i*97+Math.cos(time*.8+i)*9-camera.y*.2+H*2)%H;rect(x,y,2,2,`rgba(218,232,142,${Math.max(0,Math.sin(time+i))*.7})`);}}
let near=objects.find(o=>Math.hypot(o.x-player.x,o.y-player.y)<55);if(near){ctx.fillStyle='#152b27dd';ctx.fillRect(player.x-camera.x-23,player.y-camera.y-54,46,17);ctx.fillStyle='#f2e6be';ctx.font='10px monospace';ctx.fillText('[E]',player.x-camera.x-9,player.y-camera.y-42);}
document.querySelector('#place').textContent=player.x>1000?'The Willow River':player.y<410?'Juniper Gardens':'Willowmere Meadow';
}
function frame(t){let dt=Math.min((t-last)/1000,.05);last=t;render(dt);requestAnimationFrame(frame);}
window.addEventListener('keydown',e=>{let k=e.key.toLowerCase();if(['arrowup','arrowdown','arrowleft','arrowright','w','a','s','d','e','shift',' '].includes(k)){e.preventDefault();keys.add(k);}if(k==='e'&&!e.repeat){let o=objects.find(o=>Math.hypot(o.x-player.x,o.y-player.y)<55);let h=houses.find(h=>Math.hypot(h.x+h.w/2-player.x,h.y+h.h-player.y)<45);msg(o?o.text:h?`${h.name}. A warm light, a quiet home. The door is closed for now.`:'The breeze brushes past. There’s nowhere you need to be.');}});window.addEventListener('keyup',e=>keys.delete(e.key.toLowerCase()));window.addEventListener('blur',()=>keys.clear());
for(const b of document.querySelectorAll('[data-dir]')){let key={up:'w',down:'s',left:'a',right:'d',dash:'shift'}[b.dataset.dir];b.addEventListener('pointerdown',e=>{e.preventDefault();b.setPointerCapture(e.pointerId);keys.add(key);});for(let event of ['pointerup','pointercancel','lostpointercapture'])b.addEventListener(event,()=>keys.delete(key));}
document.querySelector('#touch-interact').addEventListener('click',()=>{window.dispatchEvent(new KeyboardEvent('keydown',{key:'e'}));window.dispatchEvent(new KeyboardEvent('keyup',{key:'e'}));});
canvas.addEventListener('pointerdown',()=>canvas.focus());
document.querySelector('#sound').addEventListener('click',async()=>{try{if(!audio){audio=new AudioContext();let buffer=audio.createBuffer(1,audio.sampleRate*3,audio.sampleRate),data=buffer.getChannelData(0);for(let i=0;i<data.length;i++)data[i]=(Math.random()*2-1)*.15;let source=audio.createBufferSource();source.buffer=buffer;source.loop=true;let filter=audio.createBiquadFilter();filter.type='lowpass';filter.frequency.value=550;let gain=audio.createGain();gain.gain.value=.16;source.connect(filter).connect(gain).connect(audio.destination);source.start();}muted=!muted;if(muted)await audio.suspend();else await audio.resume();document.querySelector('#sound span').textContent=muted?'Sound off':'Sound on';}catch{msg('Ambient audio isn’t available in this browser.');}});
camera.x=player.x-W/2;camera.y=player.y-H/2;
updateClock();setInterval(updateClock,1000);updateWeather();setInterval(updateWeather,600000);requestAnimationFrame(frame);msg('Welcome back. Wander with WASD · Hold Shift to charge a dash.');
