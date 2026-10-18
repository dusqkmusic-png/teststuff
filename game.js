'use strict';
const canvas=document.querySelector('#world');
let ctx=canvas.getContext('2d');
canvas.width=960;canvas.height=540;
ctx.imageSmoothingEnabled=false;
const W=960,H=540,T=16,MW=100,MH=76;
let seed=71;function rand(){seed=(seed*1664525+1013904223)>>>0;return seed/4294967296;}
const keys=new Set(),player={x:768,y:660,dir:0,walk:0,moving:false,charge:0,dashing:false,dashBlocked:false,dashDir:0},camera={x:0,y:0};
let weather={code:0,temp:null,wind:0,live:false},last=0,time=0,noticeTimer,muted=true,audio;
const trees=[],flowers=[],rocks=[];
function inTown(x,y,margin=0){return x>=448-margin&&x<=1016+margin&&y>=352-margin&&y<=832+margin;}
function river(y){return 1110+Math.sin(y/145)*47;}
function isPath(x,y){return Math.abs(x-760)<27 || Math.abs(y-650)<25&&x>90&&x<1510 || Math.abs(y-390)<22&&x>450&&x<800;}
const houses=[{x:490,y:450,w:112,h:96,roof:'#af6550',name:'Juniper Cottage'},{x:845,y:410,w:112,h:96,roof:'#6b8191',name:'The Riverside House'},{x:535,y:245,w:112,h:96,roof:'#9c7259',name:'The Gardener’s Home'}];
for(let i=0;i<330;i++){let x=32+rand()*(MW*T-64),y=32+rand()*(MH*T-64);if(!inTown(x,y,55)&&fieldAt(x,y,28)<0&&!isPath(x,y)&&Math.abs(x-river(y))>100&&!houses.some(h=>x>h.x-40&&x<h.x+h.w+40&&y>h.y-60&&y<h.y+h.h+40)&&Math.hypot(x-765,y-650)>110)trees.push({x,y,s:0.8+rand()*.5});}
for(let i=0;i<65;i++)rocks.push({x:60+rand()*1460,y:80+rand()*1050});
for(let i=0;i<520;i++)flowers.push({x:rand()*MW*T,y:rand()*MH*T,c:rand()>.5?'#e2c66e':'#cba6b0'});
const objects=[{x:760,y:571,text:"The old village bell. Its bronze catches the light above the square."},{x:865,y:760,text:"Clear water tumbles softly into the fountain."},{x:700,y:650,text:'Willowmere Village • North: gardens. East: river. West and across the east bridge: enemy fields. J / Z swings your sword.'},{x:675,y:740,text:'You sit for a moment. The world can wait.'},{x:1000,y:650,text:'The water carries the day slowly downstream.'},{x:590,y:570,text:'Someone has been tending these flowers with care.'}];
function rect(x,y,w,h,c){ctx.fillStyle=c;ctx.fillRect(Math.round(x),Math.round(y),w,h);}
function msg(s){document.querySelector('#message').textContent=s;document.querySelector('#message').style.opacity=1;clearTimeout(noticeTimer);noticeTimer=setTimeout(()=>document.querySelector('#message').style.opacity=0,6500);}
function nyParts(){let p=new Intl.DateTimeFormat('en-US',{timeZone:'America/New_York',hourCycle:'h23',hour:'2-digit',minute:'2-digit',second:'2-digit'}).formatToParts(new Date());return Object.fromEntries(p.map(x=>[x.type,x.value]));}
function dayState(){let p=nyParts(),hour=+p.hour+(+p.minute)/60;return {hour,dark:hour<5||hour>=21?.52:hour<7?.52*(7-hour)/2:hour<18?0:hour<21?.52*(hour-18)/3:0,phase:hour<5||hour>=21?'Moonlit night':hour<7?'First light':hour<17?'Daylight':hour<21?'Evening glow':'Night'};}
function updateClock(){document.querySelector('#clock').textContent=new Intl.DateTimeFormat('en-US',{timeZone:'America/New_York',hour:'numeric',minute:'2-digit'}).format(new Date());document.querySelector('#date').textContent=new Intl.DateTimeFormat('en-US',{timeZone:'America/New_York',month:'short',day:'numeric',timeZoneName:'short'}).format(new Date()).toUpperCase();document.querySelector('#phase').textContent=dayState().phase+' · New York';document.querySelector('#game-clock').textContent=document.querySelector('#clock').textContent;document.querySelector('#game-date').textContent=document.querySelector('#date').textContent+' · '+dayState().phase.toUpperCase();document.querySelector('#time-icon').textContent=dayState().dark>.2?'☾':'☀';document.querySelector('#time-period').textContent=new Intl.DateTimeFormat('en-US',{timeZone:'America/New_York',timeZoneName:'short'}).formatToParts(new Date()).find(p=>p.type==='timeZoneName').value;}
function description(c){return c===0?'Clear skies':c<=3?'Cloudy':c<=48?'Misty':c<=57?'Drizzle':c<=67?'Rain':c<=77?'Snow':c<=82?'Rain showers':c<=86?'Snow showers':'Thunderstorms';}
function wet(){return weather.live&&weather.code>=51&&!(weather.code>=71&&weather.code<=77)&&!(weather.code>=85&&weather.code<=86);}
function snowy(){return weather.live&&(weather.code>=71&&weather.code<=77||weather.code>=85&&weather.code<=86);}
async function updateWeather(){try{const controller=new AbortController(),timer=setTimeout(()=>controller.abort(),12000);let r;try{r=await fetch('https://api.open-meteo.com/v1/forecast?latitude=40.7654&longitude=-73.8174&current=temperature_2m,weather_code,wind_speed_10m&temperature_unit=fahrenheit&wind_speed_unit=mph&timezone=America%2FNew_York',{signal:controller.signal});}finally{clearTimeout(timer);}if(!r.ok)throw Error('Weather unavailable');const d=await r.json();if(!d.current||!Number.isFinite(d.current.weather_code)||!Number.isFinite(d.current.temperature_2m))throw Error('Invalid weather');document.querySelector('#weather-source').textContent='LIVE';weather={code:d.current.weather_code,temp:d.current.temperature_2m,wind:Number.isFinite(d.current.wind_speed_10m)?d.current.wind_speed_10m:0,live:true};document.querySelector('#weather').textContent=`${description(weather.code)} · ${Math.round(weather.temp)}°F`;document.querySelector('#wind-detail').textContent=`Wind ${Math.round(weather.wind)} mph`;document.querySelector('#weather-icon').textContent=snowy()?'❄':wet()?'☂':weather.code===0?'☼':'☁';document.querySelector('#weather-detail').textContent='Flushing · updated '+new Intl.DateTimeFormat('en-US',{timeZone:'America/New_York',hour:'numeric',minute:'2-digit'}).format(new Date());}catch(e){document.querySelector('#weather-source').textContent=weather.live?'STALE':'OFFLINE';document.querySelector('#weather').textContent=weather.live?'Weather update delayed':'Live weather unavailable';document.querySelector('#weather-detail').textContent=weather.live?'Showing last received conditions':'Clear visual fallback · retries every 10 min';document.querySelector('#wind-detail').textContent=weather.live?`Last wind: ${Math.round(weather.wind)} mph`:'Gentle ambient breeze · live wind unavailable';}}
function blocked(x,y){return townObstacles.some(o=>x>o.x-6&&x<o.x+o.w+6&&y>o.y-4&&y<o.y+o.h+4)||(x>40&&x<430&&y>282+Math.floor(Math.sin(x/85)*2)*4&&y<354+Math.floor(Math.sin(x/85)*2)*4)||(x>80&&x<660&&y>908+Math.floor(Math.sin(x/90)*2)*4&&y<974+Math.floor(Math.sin(x/90)*2)*4)||x<24||y<24||x>MW*T-24||y>MH*T-24||Math.abs(x-river(y))<47&&Math.abs(y-650)>25||houses.some(h=>x>h.x-6&&x<h.x+h.w+6&&y>h.y+20&&y<h.y+h.h+5)||trees.some(t=>Math.abs(x-t.x)<10&&Math.abs(y-t.y)<9);}
const dust=[];
function movePlayer(dt){
 let ax=(keys.has('d')||keys.has('arrowright')?1:0)-(keys.has('a')||keys.has('arrowleft')?1:0),ay=(keys.has('s')||keys.has('arrowdown')?1:0)-(keys.has('w')||keys.has('arrowup')?1:0);
 const dashHeld=keys.has('shift')||keys.has(' ');
 if(!dashHeld){player.charge=0;player.dashing=false;player.dashBlocked=false;}
 if(!player.dashing&&battle.swing<=0&&(ax||ay)){if(ax&&!ay)player.dir=ax>0?1:3;else if(ay&&!ax)player.dir=ay>0?0:2;else if((player.dir===0||player.dir===2)&&ay)player.dir=ay>0?0:2;else player.dir=ax>0?1:3;}
 if(dashHeld&&!player.dashBlocked&&!player.dashing){player.charge+=dt;if(player.charge>=.32){player.dashing=true;player.dashDir=player.dir;}}
 if(player.dashing){[ax,ay]=[[0,1],[1,0],[0,-1],[-1,0]][player.dashDir];player.dir=player.dashDir;}
 let speed=battle.swing>0?0:player.dashing?280:dashHeld?0:112,len=Math.hypot(ax,ay),dx=len?ax/len*speed*dt:0,dy=len?ay/len*speed*dt:0;
 const oldX=player.x,oldY=player.y,steps=Math.max(1,Math.ceil(Math.hypot(dx,dy)/2));let hit=false;
 for(let i=0;i<steps;i++){if(dx&&!blocked(player.x+dx/steps,player.y))player.x+=dx/steps;else if(dx)hit=true;if(dy&&!blocked(player.x,player.y+dy/steps))player.y+=dy/steps;else if(dy)hit=true;}
 player.moving=Math.hypot(player.x-oldX,player.y-oldY)>.01;
 if(player.moving){player.walk+=dt*(player.dashing?20:10);if(player.dashing&&Math.random()<.6)dust.push({x:player.x,y:player.y,life:.35});}else player.walk=0;
 if(player.dashing&&hit){player.dashing=false;player.dashBlocked=true;player.charge=0;msg('Oof! Release dash to catch your breath.');}
 for(let i=dust.length-1;i>=0;i--){dust[i].life-=dt;if(dust[i].life<=0)dust.splice(i,1);}
}
function render(dt){time+=dt;movePlayer(dt);updateCombat(dt);
const targetX=Math.max(0,Math.min(MW*T-W,player.x-W/2)),targetY=Math.max(0,Math.min(MH*T-H,player.y-H/2));
const follow=1-Math.exp(-dt*12);camera.x+=(targetX-camera.x)*follow;camera.y+=(targetY-camera.y)*follow;let dark=dayState().dark;
ctx.save();ctx.translate(-Math.round(camera.x),-Math.round(camera.y));
drawTerrain();
for(const d of dust){let size=(.35-d.life)*18+2;rect(d.x-size/2,d.y-size/2,size,size,`rgba(211,192,139,${d.life*1.6})`);}
drawVillageDetails();drawFieldSigns();
let things=trees.map(t=>({y:t.y,fn:()=>drawTree(t)})).concat(houses.map(h=>({y:h.y+h.h,fn:()=>drawHouse(h,dark)})),townScenery.map(o=>({y:o.y+o.h,fn:()=>drawTownObject(o)})),enemies.filter(e=>e.active).map(e=>({y:e.y,fn:()=>drawEnemy(e)})),[{y:player.y,fn:drawBattlePlayer}]).sort((a,b)=>a.y-b.y);for(let o of things)if(o.y>camera.y-130&&o.y<camera.y+H+130)o.fn();
drawBattleEffects();
ctx.restore();
// Real clock lighting, with warm pools of light after dusk.
if(dark>0){rect(0,0,W,H,`rgba(8,18,54,${dark})`);ctx.save();ctx.globalCompositeOperation='screen';for(const l of [{x:704,y:616},...houses.map(h=>({x:h.x+55,y:h.y+75}))]){let x=l.x-camera.x,y=l.y-camera.y,g=ctx.createRadialGradient(x,y,0,x,y,60);g.addColorStop(0,`rgba(255,192,88,${dark*.8})`);g.addColorStop(1,'rgba(255,192,88,0)');ctx.fillStyle=g;ctx.fillRect(x-60,y-60,120,120);}ctx.restore();}
if(weather.live&&weather.code>0&&weather.code<=48)rect(0,0,W,H,'#b6c3b018');
if(wet()){rect(0,0,W,H,'#24374825');ctx.strokeStyle='#c4deeb77';ctx.lineWidth=1;ctx.beginPath();for(let i=0;i<160;i++){let x=(i*137+time*80)%W,y=(i*79+time*300)%H;ctx.moveTo(x,y);ctx.lineTo(x-3,y+9);}ctx.stroke();}if(snowy()){for(let i=0;i<130;i++){let x=(i*127+Math.sin(time+i)*9+W)%W,y=(i*61+time*24)%H;rect(x,y,2,2,'#ecf2e5bb');}}
let near=objects.find(o=>Math.hypot(o.x-player.x,o.y-player.y)<55);if(near){ctx.fillStyle='#152b27dd';ctx.fillRect(player.x-camera.x-23,player.y-camera.y-54,46,17);ctx.fillStyle='#f2e6be';ctx.font='10px monospace';ctx.fillText('[E]',player.x-camera.x-9,player.y-camera.y-42);}
document.querySelector('#place').textContent=fieldAt(player.x,player.y)>=0?fields[fieldAt(player.x,player.y)].name:player.x>1000?'The Willow River':player.y<410?'Juniper Gardens':inTown(player.x,player.y)?'Willowmere Bell Square':'Willowmere Meadow';
}
function frame(t){let dt=Math.min((t-last)/1000,.05);last=t;render(dt);requestAnimationFrame(frame);}
window.addEventListener('keydown',e=>{let k=e.key.toLowerCase();if(['arrowup','arrowdown','arrowleft','arrowright','w','a','s','d','e','shift',' ','j','z'].includes(k)){e.preventDefault();keys.add(k);}if((k==='j'||k==='z')&&!e.repeat)attack();if(k==='e'&&!e.repeat){let o=objects.find(o=>Math.hypot(o.x-player.x,o.y-player.y)<55);let h=houses.find(h=>Math.hypot(h.x+h.w/2-player.x,h.y+h.h-player.y)<45);if(o&&o.x===675){battle.hp=battle.maxHp;syncBattleHud();}msg(o?o.text+(o.x===675?' Your health is restored.':''):h?`${h.name}. A warm light, a quiet home. The door is closed for now.`:'The breeze brushes past. There’s nowhere you need to be.');}});window.addEventListener('keyup',e=>keys.delete(e.key.toLowerCase()));window.addEventListener('blur',()=>keys.clear());
for(const b of document.querySelectorAll('[data-dir]')){let key={up:'w',down:'s',left:'a',right:'d',dash:'shift'}[b.dataset.dir];b.addEventListener('pointerdown',e=>{e.preventDefault();b.setPointerCapture(e.pointerId);keys.add(key);});for(let event of ['pointerup','pointercancel','lostpointercapture'])b.addEventListener(event,()=>keys.delete(key));}
document.querySelector('#touch-interact').addEventListener('click',()=>{window.dispatchEvent(new KeyboardEvent('keydown',{key:'e'}));window.dispatchEvent(new KeyboardEvent('keyup',{key:'e'}));});
canvas.addEventListener('pointerdown',()=>{canvas.focus();attack();});document.querySelector('#touch-attack').addEventListener('pointerdown',e=>{e.preventDefault();attack();});
document.querySelector('#sound').addEventListener('click',async()=>{try{if(!audio){audio=new AudioContext();let buffer=audio.createBuffer(1,audio.sampleRate*3,audio.sampleRate),data=buffer.getChannelData(0);for(let i=0;i<data.length;i++)data[i]=(Math.random()*2-1)*.15;let source=audio.createBufferSource();source.buffer=buffer;source.loop=true;let filter=audio.createBiquadFilter();filter.type='lowpass';filter.frequency.value=550;let gain=audio.createGain();gain.gain.value=.16;source.connect(filter).connect(gain).connect(audio.destination);source.start();}muted=!muted;if(muted)await audio.suspend();else await audio.resume();document.querySelector('#sound span').textContent=muted?'Sound off':'Sound on';}catch{msg('Ambient audio isn’t available in this browser.');}});
camera.x=player.x-W/2;camera.y=player.y-H/2;
updateClock();setInterval(updateClock,1000);updateWeather();setInterval(updateWeather,600000);msg('Loading pixel-art sprites…');loadArt().then(()=>{initCombat();last=performance.now();requestAnimationFrame(frame);msg('Village is safe. Explore the fields west or east · J / Z swings your sword.');}).catch(error=>{msg(error.message);document.querySelector('#message').style.opacity=1;clearTimeout(noticeTimer);});

// Fullscreen retains all in-game overlays. Browsers without the API use a theater view.
const shell=document.querySelector('.game-shell'),fullscreenButton=document.querySelector('#fullscreen');
function syncFullscreen(){const active=!!document.fullscreenElement||shell.classList.contains('theater');fullscreenButton.textContent=active?'↙ Exit fullscreen':'⛶ Fullscreen';fullscreenButton.setAttribute('aria-pressed',String(active));}
async function toggleFullscreen(){try{if(document.fullscreenElement)await document.exitFullscreen();else if(shell.classList.contains('theater'))shell.classList.remove('theater');else if(shell.requestFullscreen)await shell.requestFullscreen();else shell.classList.add('theater');}catch{shell.classList.toggle('theater');}keys.clear();syncFullscreen();}
fullscreenButton.addEventListener('click',toggleFullscreen);document.addEventListener('fullscreenchange',syncFullscreen);
window.addEventListener('keydown',e=>{if(e.key.toLowerCase()==='f'&&!e.repeat){e.preventDefault();toggleFullscreen();}if(e.key==='Escape'){shell.classList.remove('theater');syncFullscreen();}});
