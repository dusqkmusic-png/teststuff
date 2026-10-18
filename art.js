'use strict';
// Pixel artwork is loaded from local PNG atlases; Canvas only places tiles and sprite frames.
const art = {};
function loadArt(){return Promise.all(['terrain','adventurer','village','combat'].map(name=>new Promise((resolve,reject)=>{const image=new Image();image.onload=()=>{art[name]=image;resolve();};image.onerror=()=>reject(new Error(`Could not load assets/${name}.png. Keep the assets folder with index.html.`));image.src=`assets/${name}.png`;})));}
function grain(x,y,s=0){let n=Math.imul(x+173+s*43,374761393)^Math.imul(y+937,668265263);n=Math.imul(n^(n>>>13),1274126177);return ((n^(n>>>16))>>>0)/4294967295;}
function sprite(sheet,col,row,x,y,w,h){const image=art[sheet],sw=image.width/4,sh=image.height/4;const inset=sheet==='terrain'?2:0;ctx.drawImage(image,col*sw+inset,row*sh+inset,sw-inset*2,sh-inset*2,Math.round(x),Math.round(y),w,h);}
function windState(){const mph=weather.live&&Number.isFinite(weather.wind)?Math.max(0,Math.min(75,weather.wind)):4;const gust=.72+.18*Math.sin(time*.47)+.1*Math.sin(time*1.13);return {mph,strength:Math.min(1.8,mph/12)*gust};}
let terrainArt;
function bakeTerrain(){terrainArt=document.createElement('canvas');terrainArt.width=MW*T;terrainArt.height=MH*T;const main=ctx;ctx=terrainArt.getContext('2d');ctx.imageSmoothingEnabled=false;
 for(let y=0;y<MH*T;y+=32)for(let x=0;x<MW*T;x+=32){const variant=Math.floor(grain(x,y)*4);sprite('terrain',variant,0,x,y,32,32);if(isPath(x+16,y+16))sprite('terrain',variant,1,x,y,32,32);}
 for(let [start,end,base,height] of [[40,430,294,56],[80,660,920,50]])for(let x=start;x<end;x+=16){let y=base+Math.floor(Math.sin(x/(base===294?85:90))*2)*4;sprite('terrain',Math.floor(grain(x,base)*4),3,x,y-8,16,height+8);}
 for(const r of rocks){if(isPath(r.x,r.y)||Math.abs(r.x-river(r.y))<90)continue;sprite('village',0,2,r.x-14,r.y-23,28,28);}
 for(let i=0;i<flowers.length;i+=14){const f=flowers[i];if(!isPath(f.x,f.y)&&Math.abs(f.x-river(f.y))>90)sprite('village',2,3,f.x-8,f.y-7,16,16);}
 ctx=main;
}
function drawTerrain(){if(!terrainArt)bakeTerrain();ctx.drawImage(terrainArt,0,0);drawRiver();drawWindGrass();}
function drawTree(t){if(t.x<camera.x-70||t.x>camera.x+W+70)return;const variant=Math.floor(grain(Math.round(t.x),Math.round(t.y))*4),w=windState(),sway=Math.round(Math.sin(time*1.7+t.x*.013+t.y*.007)*w.strength*2);const image=art.village,sw=image.width/4,sh=image.height/4,x=Math.round(t.x)-57,y=Math.round(t.y)-112;
 // Anchor the lower trunk; bend only the upper sprite strips with the breeze.
 for(let i=0;i<4;i++){let off=Math.round(sway*(1-i/3));ctx.drawImage(image,variant*sw,i*sh/4,sw,sh/4,x+off,y+i*30,114,30);}}
function drawHouse(h,dark){sprite('village',houses.indexOf(h),1,h.x-12,h.y-28,h.w+24,h.h+42);if(dark>.12){ctx.save();ctx.globalCompositeOperation='screen';for(const dx of [22,h.w-23]){let g=ctx.createRadialGradient(h.x+dx,h.y+70,1,h.x+dx,h.y+70,19);g.addColorStop(0,'#ffc46eaa');g.addColorStop(1,'#ffb44a00');ctx.fillStyle=g;ctx.fillRect(h.x+dx-19,h.y+51,38,38);}ctx.restore();}}
const adventurerFrames=[[[135,42,149,249],[421,42,149,249],[713,42,149,249],[1002,43,148,248]],[[128,330,163,247],[413,337,164,243],[704,330,169,250],[999,330,168,250]],[[114,625,162,243],[412,623,158,245],[704,624,157,244],[992,624,162,244]],[[102,906,179,247],[397,906,177,248],[692,904,178,250],[986,905,175,250]]];
function drawPlayer(){const image=art.adventurer,frame=player.moving?Math.floor(player.walk)%4:1,[sx,sy,sw,sh]=adventurerFrames[player.dir][frame],scale=48/270,dw=Math.round(sw*scale),dh=Math.round(sh*scale),x=Math.round(player.x-dw/2),y=Math.round(player.y-dh),w=windState(),flutter=Math.round(Math.sin(time*5.4)*w.strength);
 const split=Math.floor(sh*.35),top=Math.round(split*scale);ctx.drawImage(image,sx,sy+split,sw,sh-split,x,y+top,dw,dh-top);ctx.drawImage(image,sx,sy,sw,split,x+flutter,y,dw,top);
 if(player.charge>0&&!player.dashing){rect(player.x-14,player.y-53,28,4,'#24392e');rect(player.x-13,player.y-52,26*Math.min(1,player.charge/.32),2,'#eadc8b');}}
function drawVillageDetails(){
 // Bridge uses an opaque plank tile, painted over the moving river.
 for(let x=1028;x<1210;x+=30)sprite('village',0,3,x,626,30,50);
 for(let x=430;x<662;x+=46)sprite('village',1,3,x,596,48,28);
 sprite('village',1,2,649,708,54,44);sprite('village',2,2,677,619,48,42);sprite('village',3,1,832,667,68,72);
 for(let x=534;x<633;x+=24)sprite('village',2,3,x,557,24,40);
 for(const [x,y] of [[622,531],[975,499],[650,344]])sprite('village',3,3,x-12,y-18,24,24);
}
function drawRiver(){const image=art.terrain,sw=image.width/4,sh=image.height/4,frame=Math.floor(time*3)%4;
 // Sample the water tiles through a continuous curved river mask.
 ctx.save();ctx.beginPath();for(let y=Math.floor(camera.y)-8;y<=camera.y+H+8;y+=4){let x=river(y)-57;if(y===Math.floor(camera.y)-8)ctx.moveTo(x,y);else ctx.lineTo(x,y);}for(let y=camera.y+H+8;y>=Math.floor(camera.y)-8;y-=4)ctx.lineTo(river(y)+57,y);ctx.closePath();ctx.clip();
 const flow=(time*17)%32;
 for(let y=Math.floor(camera.y/32)*32-32;y<camera.y+H+32;y+=32)for(let x=1024;x<1220;x+=32)ctx.drawImage(image,frame*sw+2,sh*2+2,sw-4,sh-4,x,y+flow,32,32);
 for(let i=0;i<100;i++){let y=(i*37+time*(19+grain(i,13)*10))%(MH*T),x=river(y)+(grain(i,44)*2-1)*48;if(y<camera.y||y>camera.y+H)continue;rect(x-3,y,7,1,'#c0ded355');rect(x,y+2,3,1,'#8fc2bd55');}ctx.restore();
 for(let y=Math.floor(camera.y/18)*18;y<camera.y+H;y+=18)for(let side of [-1,1]){let x=river(y)+side*57;rect(x,y,3,1,'#c6d7b488');rect(x+side*2,y+2,2,2,'#9bbda577');}
 for(let yy of [300,526,844,1070]){if(yy<camera.y-30||yy>camera.y+H+30)continue;let xx=river(yy)+(yy%3?29:-26);sprite('village',0,2,xx-10,yy-14,20,20);let phase=(time*10)%12;rect(xx-8,yy+4+phase,5,1,'#c2dac299');rect(xx+3,yy+7+phase,5,1,'#b9d8c277');}
}
function drawWindGrass(){const w=windState();for(let y=Math.floor(camera.y/40)*40;y<camera.y+H;y+=40)for(let side of [-1,1]){let x=river(y)+side*72,bend=Math.round(Math.sin(time*2+y*.02)*w.strength*2);sprite('village',3,2,x-10+bend,y-24,24,28);}}
