'use strict';
// Pixel artwork is loaded from local PNG atlases; Canvas only places tiles and sprite frames.
const art = {};
function loadArt(){return Promise.all(['terrain','village','combat','grass','town','traveler'].map(name=>new Promise((resolve,reject)=>{const image=new Image();image.onload=()=>{art[name]=image;resolve();};image.onerror=()=>reject(new Error(`Could not load assets/${name}.png. Keep the assets folder with index.html.`));image.src=`assets/${name}.png`;})));}
function grain(x,y,s=0){let n=Math.imul(x+173+s*43,374761393)^Math.imul(y+937,668265263);n=Math.imul(n^(n>>>13),1274126177);return ((n^(n>>>16))>>>0)/4294967295;}
const townFrames=[[[23,16,332,293],[400,5,167,303],[641,144,290,164],[958,115,289,198]],[[14,323,287,338],[326,336,321,325],[657,323,267,337],[950,352,300,309]],[[32,686,266,244],[348,686,266,244],[664,686,266,244],[980,686,266,244]],[[48,1049,264,136],[373,954,195,259],[678,968,224,240],[1041,950,129,266]]];
function sprite(sheet,col,row,x,y,w,h){if(sheet==='town'){const [sx,sy,sw,sh]=townFrames[row][col];ctx.drawImage(art.town,sx,sy,sw,sh,Math.round(x),Math.round(y),w,h);return;}const image=art[sheet],sw=image.width/(sheet==='grass'?2:4),sh=image.height/(sheet==='grass'?2:4);const inset=sheet==='terrain'||sheet==='grass'||sheet==='town'&&row===2?2:0;ctx.drawImage(image,col*sw+inset,row*sh+inset,sw-inset*2,sh-inset*2,Math.round(x),Math.round(y),w,h);}
function windState(){const mph=weather.live&&Number.isFinite(weather.wind)?Math.max(0,Math.min(75,weather.wind)):4;const gust=.72+.18*Math.sin(time*.47)+.1*Math.sin(time*1.13);return {mph,strength:Math.min(1.8,mph/12)*gust};}
let terrainArt;
function bakeTerrain(){terrainArt=document.createElement('canvas');terrainArt.width=MW*T;terrainArt.height=MH*T;const main=ctx;ctx=terrainArt.getContext('2d');ctx.imageSmoothingEnabled=false;
 for(let y=0;y<MH*T;y+=32)for(let x=0;x<MW*T;x+=32){const variant=Math.floor(grain(x,y)*4);sprite('grass',variant%2,Math.floor(variant/2),x,y,32,32);if(isPath(x+16,y+16))sprite('terrain',variant,1,x,y,32,32);if(inTown(x+16,y+16))sprite('town',variant,2,x,y,32,32);}
 for(let [start,end,base,height] of [[40,430,294,56],[80,660,920,50]])for(let x=start;x<end;x+=16){let y=base+Math.floor(Math.sin(x/(base===294?85:90))*2)*4;sprite('terrain',Math.floor(grain(x,base)*4),3,x,y-8,16,height+8);}
 for(const r of rocks){if(inTown(r.x,r.y)||isPath(r.x,r.y)||Math.abs(r.x-river(r.y))<90)continue;sprite('village',0,2,r.x-14,r.y-23,28,28);}
 for(let i=0;i<flowers.length;i+=56){const f=flowers[i];if(!inTown(f.x,f.y)&&!isPath(f.x,f.y)&&Math.abs(f.x-river(f.y))>90)sprite('village',2,3,f.x-8,f.y-7,16,16);}
 ctx=main;
}
function drawTerrain(){if(!terrainArt)bakeTerrain();ctx.drawImage(terrainArt,0,0);drawRiver();drawWindGrass();}
function drawTree(t){if(t.x<camera.x-70||t.x>camera.x+W+70)return;const variant=Math.floor(grain(Math.round(t.x),Math.round(t.y))*4),w=windState(),sway=Math.round(Math.sin(time*1.7+t.x*.013+t.y*.007)*w.strength*2);const image=art.village,sw=image.width/4,sh=image.height/4,x=Math.round(t.x)-57,y=Math.round(t.y)-112;
 // Anchor the lower trunk; bend only the upper sprite strips with the breeze.
 for(let i=0;i<4;i++){let off=Math.round(sway*(1-i/3));ctx.drawImage(image,variant*sw,i*sh/4,sw,sh/4,x+off,y+i*30,114,30);}}
function drawHouse(h,dark){sprite('town',houses.indexOf(h),1,h.x-14,h.y-33,h.w+28,h.h+47);if(dark>.12){ctx.save();ctx.globalCompositeOperation='screen';let g=ctx.createRadialGradient(h.x+h.w/2,h.y+h.h-20,1,h.x+h.w/2,h.y+h.h-20,28);g.addColorStop(0,'#ffcc7a99');g.addColorStop(1,'#ffb44a00');ctx.fillStyle=g;ctx.fillRect(h.x+h.w/2-28,h.y+h.h-48,56,56);ctx.restore();}}
const travelerFrames=[[[213,8,127,260],[484,8,129,260],[781,8,132,260],[1070,8,134,260]],[[200,285,133,260],[473,285,146,260],[778,285,142,260],[1064,285,139,260]],[[204,567,133,274],[480,567,138,257],[774,567,136,257],[1069,567,136,253]],[[210,841,138,260],[473,842,151,259],[772,842,149,259],[1058,842,155,259]]];
function drawPlayer(){const image=art.traveler,frame=player.moving?Math.floor(player.walk)%4:1,[sx,sy,sw,sh]=travelerFrames[player.dir][frame],scale=54/270,dw=Math.round(sw*scale),dh=Math.round(sh*scale),x=Math.round(player.x-dw/2),y=Math.round(player.y-dh),w=windState(),flutter=Math.round(Math.sin(time*5.4)*w.strength);
 const split=Math.floor(sh*.35),top=Math.round(split*scale);ctx.drawImage(image,sx,sy+split,sw,sh-split,x,y+top,dw,dh-top);ctx.drawImage(image,sx,sy,sw,split,x+flutter,y,dw,top);
 if(player.charge>0&&!player.dashing){rect(player.x-14,player.y-53,28,4,'#24392e');rect(player.x-13,player.y-52,26*Math.min(1,player.charge/.32),2,'#eadc8b');}}
function drawVillageDetails(){
 // Bridge uses an opaque plank tile, painted over the moving river.
 for(let x=1028;x<1210;x+=30)sprite('village',0,3,x,626,30,50);
 sprite('town',0,3,649,708,54,44);sprite('town',1,3,677,619,48,42);sprite('town',3,1,831,671,70,72);
 for(let x=534;x<633;x+=32)sprite('town',2,0,x,561,32,38);
 for(let x=850;x<977;x+=32)sprite('town',2,0,x,561,32,38);

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

// Town props are sorted alongside characters, so walking behind pillars occludes correctly.
const townScenery=[
 {col:0,row:0,x:686,y:392,w:148,h:166},
 {col:1,row:0,x:625,y:584,w:35,h:70},
 {col:1,row:0,x:871,y:584,w:35,h:70},
 {col:3,row:0,x:510,y:791,w:124,h:41},
 {col:3,row:0,x:877,y:791,w:124,h:41},
 {col:3,row:3,x:668,y:795,w:34,h:62},
 {col:3,row:3,x:818,y:795,w:34,h:62}
];
const townObstacles=[{x:534,y:585,w:128,h:13},{x:850,y:585,w:128,h:13},{x:845,y:723,w:40,h:14},{x:650,y:737,w:52,h:9},{x:690,y:529,w:23,h:21},{x:806,y:529,w:23,h:21},{x:510,y:814,w:124,h:12},{x:877,y:814,w:124,h:12},{x:673,y:838,w:22,h:14},{x:824,y:838,w:22,h:14}];
function drawTownObject(o){if(o.row===0&&o.col===1){const w=windState();ctx.save();ctx.translate(o.x+o.w/2,o.y+o.h);ctx.transform(1,0,Math.sin(time*2.2+o.x)*w.strength*.025,1,0,0);sprite('town',o.col,o.row,-o.w/2,-o.h,o.w,o.h);ctx.restore();}else sprite('town',o.col,o.row,o.x,o.y,o.w,o.h);}
