'use strict';
// Willowmere v4: original, deterministic pixel artwork, drawn locally with no assets.
function grain(x,y,s=0){let n=Math.imul(x+173+s*43,374761393)^Math.imul(y+937,668265263);n=Math.imul(n^(n>>>13),1274126177);return ((n^(n>>>16))>>>0)/4294967295;}
function oval(x,y,rx,ry,c){ctx.fillStyle=c;ctx.beginPath();ctx.ellipse(Math.round(x),Math.round(y),rx,ry,0,0,Math.PI*2);ctx.fill();}
function leafCluster(x,y,r,variant){
 const p=variant?['#193c32','#26513b','#336744','#487c4c','#659659','#8cb16a']:['#243c30','#31543a','#426e43','#5b8750','#7a9d59','#a7b970'];
 poly([[x-r,y-3],[x-r+2,y-r*.5],[x-r*.6,y-r*.8],[x-4,y-r],[x+r*.3,y-r*.9],[x+r*.7,y-r*.65],[x+r,y-r*.2],[x+r-1,y+4],[x+r*.6,y+r*.5],[x+2,y+r*.67],[x-r*.6,y+r*.5],[x-r,y+r*.15]],p[0]);
 poly([[x-r+3,y-4],[x-r*.5,y-r*.7],[x+1,y-r*.85],[x+r*.65,y-r*.5],[x+r-3,y],[x+r*.4,y+r*.45],[x-r*.5,y+r*.35]],p[1]);
 // Irregular overlapping leaf fans, lit from the upper left. No concentric shading.
 for(let i=0;i<48;i++){const gx=grain(i,Math.floor(x),Math.floor(y)),gy=grain(i+80,Math.floor(y),Math.floor(x)),xx=x+(gx*2-1)*(r-4),yy=y+(gy*1.4-.9)*(r-3);if((xx-x)**2/(r*r)+(yy-y+3)**2/(r*r*.7)>.8)continue;const bright=yy<y-4?3:2,c=p[Math.min(5,bright+(i%3===0?1:0))];
 poly([[xx-3,yy+1],[xx-4,yy-1],[xx-1,yy-3],[xx+2,yy-2],[xx+4,yy],[xx+1,yy+2]],p[1]);rect(xx-2,yy-2,3,1,c);rect(xx-3,yy-1,5,1,c);rect(xx-1,yy,3,1,p[Math.max(1,bright-1)]);if(i%4===0)rect(xx-1,yy-3,2,1,p[5]);}
 for(let i=0;i<5;i++){let xx=x-r*.6+i*r*.27,yy=y+Math.sin(i*1.8)*r*.2;rect(xx,yy,3,1,p[0]);rect(xx+1,yy+1,1,1,p[0]);}
}
function paintTree(t){let x=Math.round(t.x),y=Math.round(t.y),v=grain(x,y)>.55?1:0;
 ctx.save();ctx.translate(x,y);oval(7,4,32,9,'#1d352b38');
 poly([[-12,3],[-7,-39],[6,-41],[11,2],[20,7],[9,8],[2,3],[-7,8],[-19,7]],'#353c2b');
 poly([[-8,-33],[1,-39],[7,-31],[7,1],[12,5],[4,4],[-1,-4],[-6,5],[-12,5]],'#8f7044');rect(-5,-30,3,28,'#b39558');rect(2,-35,2,32,'#5d5135');rect(-3,-13,3,3,'#493f2e');poly([[-3,-17],[-17,-38],[-14,-42],[1,-25],[14,-41],[17,-38],[5,-17]],'#685738');rect(-5,-27,2,12,'#c19d5f');for(let k=0;k<8;k++){rect(-6+(k%3)*4,-32+k*4,1,3,'#4d4931');}rect(-8,-2,5,2,'#839251');
 let clusters=[[-22,-40,22],[22,-43,23],[-26,-61,22],[23,-65,24],[0,-43,25],[0,-73,27],[-13,-83,20],[15,-84,18],[-4,-59,24]];
 if(v)clusters=clusters.map(([a,b,r])=>[a*.87,b-6,r*.95]);for(let [a,b,r] of clusters)leafCluster(a,b,r,v);
 // Sparse trailing leaves and moss at the roots.
 rect(-11,-2,4,2,'#697b45');rect(10,3,5,2,'#78834c');ctx.restore();}
function drawHouse(h,dark){let {x,y,w,h:hh}=h;const roof=h.roof==='#6b8191'?['#344b54','#4b6470','#637d85','#8fa6a0']:['#513e3b','#80564b','#a67459','#c1946b'];
 oval(x+w/2+7,y+hh+3,w*.57,10,'#223d3238');rect(x-3,y+29,w+6,hh-23,'#494a38');rect(x,y+29,w,hh-27,'#c0b28b');
 for(let row=0;row<6;row++)for(let col=0;col<9;col++){let xx=x+col*14-(row%2)*7,yy=y+32+row*10;if(xx<x||xx+12>x+w||yy>y+hh-2)continue;let n=grain(col,row,x);rect(xx,yy,12,8,n>.5?'#c9bc96':'#b5a985');rect(xx+1,yy,10,1,'#e1d1a7');rect(xx+11,yy+2,1,6,'#93886b');if(n>.65)rect(xx+3,yy+4,3,1,'#a69c77');}
 // Heavy timber corner posts and carved sill.
 for(let dx of [0,w-7]){rect(x+dx,y+32,7,hh-31,'#695841');rect(x+dx+1,y+33,2,hh-34,'#a58d62');}
 rect(x-3,y+hh-5,w+6,7,'#776d52');rect(x-1,y+hh-5,w+2,2,'#d4c39a');
 let door=x+w/2;rect(door-15,y+hh-38,30,38,'#83734f');rect(door-12,y+hh-35,24,35,'#3d3e30');rect(door-10,y+hh-33,20,33,'#6e6145');for(let i=0;i<4;i++)rect(door-8+i*5,y+hh-32,1,30,'#97805a');rect(door-10,y+hh-16,20,3,'#4c4533');rect(door+5,y+hh-18,3,3,'#d8b864');
 for(let dx of [14,w-36]){rect(x+dx-3,y+48,26,24,'#61543c');rect(x+dx,y+50,20,18,dark>.15?'#e4bf78':'#55767a');if(dark<=.15){rect(x+dx+1,y+51,8,7,'#90aaa3');rect(x+dx+11,y+60,7,6,'#728d87');}rect(x+dx+9,y+50,2,18,'#b19a6b');rect(x+dx,y+58,20,2,'#b19a6b');rect(x+dx-4,y+70,28,3,'#b8a47b');for(let k=0;k<3;k++){rect(x+dx+k*7,y+74,6,5,'#476b43');rect(x+dx+k*7+2,y+73,3,2,k%2?'#e3c78a':'#c28783');}}
 // Individual overlapping shingles with varied faces and ridge caps.
 poly([[x-12,y+35],[x+3,y-15],[x+w-3,y-15],[x+w+12,y+35]],'#343a32');
 for(let row=0;row<10;row++){let inset=Math.max(0,11-row*1.4),yy=y-12+row*4.5;for(let xx=x-8+inset;xx<x+w+8-inset;xx+=12){let width=Math.min(11,x+w+8-inset-xx),n=grain(Math.floor(xx),row,x);rect(xx,yy,width,5,roof[n>.6?2:1]);rect(xx,yy,width,1,roof[3]);rect(xx+width-1,yy+1,1,3,roof[0]);rect(xx+2,yy+3,width-4,1,roof[0]);}}
 rect(x-12,y+34,w+24,4,roof[0]);rect(x-10,y+34,w+20,1,roof[3]);for(let xx=x+2;xx<x+w-2;xx+=10){rect(xx,y-16,9,4,roof[0]);rect(xx,y-16,9,1,roof[3]);}
 rect(x+w-28,y-29,14,28,'#77796a');for(let yy=y-26;yy<y-3;yy+=6){rect(x+w-27,yy,12,1,'#b5b09a');rect(x+w-22+(yy%2)*3,yy+1,1,4,'#515c51');}rect(x+w-30,y-32,18,5,'#b1ac94');rect(x+w-27,y-32,12,2,'#3c4b43');
 rect(door-18,y+hh+2,36,5,'#c5baa0');rect(door-22,y+hh+7,44,5,'#827f68');rect(door-21,y+hh+7,42,1,'#d9caaa');for(let i=0;i<3;i++){let yy=y+hh+19+i*10;rect(door-9+(i%2)*4,yy,17,6,'#8d9880');rect(door-8+(i%2)*4,yy,14,2,'#b4baa0');}
}
function drawPlayer(){const x=Math.round(player.x),y=Math.round(player.y),step=player.moving?[0,2,0,-2][Math.floor(player.walk)%4]:0,side=player.dir%2===1,back=player.dir===2,w=windState();
 ctx.save();ctx.translate(x,y);if(player.dir===3)ctx.scale(-1,1);const flutter=Math.round(w.strength*Math.sin(time*5.4)*2.2),bob=player.moving?Math.abs(step)*.4:0;
 oval(0,1,11,4,'#1c302d66');
 // Short, broad 16-bit proportions: oversized face, green tunic, leather boots.
 for(let [xx,off] of [[-8,step],[2,-step]]){rect(xx,-9+off,7,9,'#3e3c2b');rect(xx+1,-7+off,5,6,'#a47a46');rect(xx,-2+off,8,3,'#58402c');rect(xx+1,-2+off,5,1,'#c19855');}
 ctx.translate(0,-bob);
 // Wind lifts the soft pointed cap behind the head, rather than moving the whole sprite.
 poly([[-6,-34],[-4,-46],[6,-43],[13+flutter,-38],[19+flutter,-33+flutter],[12,-29],[4,-29]],'#253f2b');poly([[-3,-35],[-2,-43],[5,-41],[11,-36],[16+flutter,-33+flutter],[8,-32]],'#538f47');rect(0,-41,4,2,'#91b75c');
 poly([[-9,-25],[-12,-11],[-8,-5],[9,-5],[12,-10],[8,-25]],'#293f2b');poly([[-6,-25],[-9,-10],[-5,-7],[7,-7],[8,-11],[5,-25]],'#549447');rect(-5,-23,4,11,'#85b256');rect(5,-20,3,10,'#39713b');poly([[-8,-7],[-4,-10],[0,-7],[4,-10],[8,-7],[8,-5],[-8,-5]],'#3c743c');rect(-9,-13,18,3,'#765235');rect(-2,-13,4,4,'#e7be68');rect(-1,-12,2,2,'#7b6135');
 rect(-13,-23+step,6,11,'#29412d');rect(-12,-22+step,5,6,'#70a650');rect(-12,-16+step,4,5,'#f0bd8d');rect(8,-23-step,6,11,'#29412d');rect(9,-22-step,4,6,'#78a850');rect(10,-16-step,4,5,'#e7af7a');
 // Ears, warm blond hair, a visible brow and crisp eyes in four facing directions.
 rect(-10,-35,20,13,'#5d4631');rect(-8,-35,16,12,back?'#c79b50':'#f4c994');poly([[-12,-31],[-8,-33],[-7,-27],[-10,-27]],'#dfb17a');poly([[8,-33],[12,-31],[10,-27],[8,-27]],'#ffdaa1');
 if(back){rect(-8,-33,16,9,'#bd8745');rect(-6,-33,3,8,'#e5bf68');rect(2,-32,4,7,'#e2b15a');}else if(side){rect(-8,-34,6,12,'#bf8944');rect(-6,-33,3,11,'#e4b65d');rect(8,-31,4,4,'#ffdaa1');rect(6,-32,2,3,'#274b55');rect(5,-33,3,1,'#946236');rect(7,-25,3,1,'#b87955');}else{rect(-6,-30,3,4,'#fff0c7');rect(4,-30,3,4,'#fff0c7');rect(-4,-30,2,3,'#285665');rect(4,-30,2,3,'#285665');rect(-6,-32,5,1,'#805933');rect(3,-32,4,1,'#805933');rect(-1,-25,3,1,'#b97b55');rect(0,-28,2,2,'#ffdda5');}
 poly([[-10,-34],[-9,-38],[-4,-40],[5,-39],[10,-35],[7,-31],[3,-34],[0,-31],[-3,-34],[-7,-30-flutter]],'#835a31');poly([[-8,-35],[-6,-38],[1,-38],[7,-35],[4,-33],[1,-35],[-2,-33],[-5,-35],[-7,-31-flutter]],'#edc66f');rect(-6,-37,5,1,'#ffe39a');
 poly([[-10,-37],[-7,-42],[4,-43],[9,-39],[10,-36],[5,-36],[0,-38],[-6,-36]],'#2f6033');poly([[-7,-39],[-3,-42],[4,-41],[7,-39],[1,-39],[-3,-38]],'#8bb75b');rect(-9,-37,18,2,'#547f3c');
 // Small strapped wooden shield; decorative while this remains a peaceful world.
 if(!back){let sx=side?-10:-13;poly([[sx-3,-21],[sx+5,-22],[sx+7,-18],[sx+5,-9],[sx+1,-6],[sx-3,-10]],'#413d2d');poly([[sx-1,-19],[sx+4,-20],[sx+5,-17],[sx+3,-10],[sx+1,-9],[sx-1,-11]],'#ba985c');rect(sx+1,-18,1,7,'#e1c27e');rect(sx-1,-15,5,1,'#7d7049');}else{rect(-5,-22,10,11,'#6e613c');rect(-4,-21,8,8,'#b4995c');rect(-1,-21,2,9,'#dfbf74');}
 if(player.charge>0&&!player.dashing){rect(-14,-51,28,4,'#24392e');rect(-13,-50,26*Math.min(1,player.charge/.32),2,'#eadc8b');}ctx.restore();}
function paintTerrain(){
 const left=0,top=0;
 for(let y=top;y<MH*T;y+=16)for(let x=left;x<MW*T;x+=16){let n=grain(x,y),path=isPath(x+8,y+8),r=river(y+8),distance=Math.abs(x+8-r);
 rect(x,y,16,16,path?'#b0a27c':n>.5?'#658755':'#638453');
 for(let i=0;i<8;i++){let xx=x+Math.floor(grain(i,x,y)*15),yy=y+Math.floor(grain(i,y,x)*15);if(path){rect(xx,yy,2,1,i%3?'#baad89':'#938b69');if(i===2&&n>.7){rect(xx,yy,3,2,'#898975');rect(xx,yy,2,1,'#c8bd9b');}}else{rect(xx,yy,1,2,i%2?'#7b985e':'#51764c');if(i%3===0){rect(xx-1,yy+1,3,1,'#759258');rect(xx,yy-1,1,2,'#86a064');}}}
 if(!path&&n>.85){rect(x+3,y+6,3,1,'#436c47');rect(x+4,y+3,1,4,'#7e9d61');rect(x+2,y+4,1,2,'#76945a');}
 if(path){for(let [dx,dy] of [[16,0],[-16,0],[0,16],[0,-16]])if(!isPath(x+8+dx,y+8+dy)){let xx=x+(dx>0?13:dx<0?0:0),yy=y+(dy>0?13:dy<0?0:0);rect(xx,yy,dx?3:16,dy?3:16,'#788952');for(let k=0;k<4;k++)rect(xx+(dx?0:k*4),yy+(dy?0:k*4),dx?4:2,dy?4:2,'#68844f');}}
 if(distance<76){rect(x,y,16,16,distance<60?'#4d8385':'#a0a078');if(distance<60){for(let k=0;k<3;k++){let xx=x+(grain(k,x,y)*12),yy=y+k*5;rect(xx,yy,6,1,'#6a9b96');rect(xx+2,yy+1,3,1,'#568e8c');}}else{rect(x+3,y+4,4,2,'#b4b08b');rect(x+8,y+10,3,2,'#858c66');}}
 }
 for(let y=top;y<MH*T;y+=6){let r=river(y);for(let side of [-1,1]){let x=Math.round(r+side*60);rect(x,y,3,6,'#3d6156');rect(x-side*3,y,3,2,'#b1c7b1');rect(x+side*4,y+2,3,4,'#818e63');if(y%18===0){rect(x+side*8,y-6,1,8,'#54724b');rect(x+side*10,y-3,1,6,'#7e9557');}}if(y%18===0){let x=r+Math.sin(time*.6+y*.1)*30;rect(x,y,12,1,'#91b7ac');rect(x+3,y+2,5,1,'#74a49c');}}
 // Existing impassable bluffs now have irregular strata and climbing moss.
 for(let [start,end,base,height] of [[40,430,294,56],[80,660,920,50]])for(let x=start;x<end;x+=8){let y=base+Math.floor(Math.sin(x/(base===294?85:90))*2)*4;rect(x,y,8,height,'#565b43');for(let yy=0;yy<height-4;yy+=7){let n=grain(x,yy);rect(x,y+yy,8,6,n>.5?'#8b7e59':'#9a8961');rect(x+1,y+yy,6,1,'#b1a071');rect(x+4,y+yy+2,1,3,'#746d50');}rect(x,y-7,8,9,'#3e6543');rect(x,y-8,8,2,'#9cad73');rect(x+1,y-6,5,2,'#749259');if(grain(x,base)>.5){rect(x+2,y+3,3,13,'#4b7250');rect(x+1,y+9,5,2,'#6d8c58');}}
 for(const r of rocks){if(isPath(r.x,r.y)||Math.abs(r.x-river(r.y))<90)continue;poly([[r.x-11,r.y],[r.x-12,r.y-7],[r.x-5,r.y-15],[r.x+5,r.y-16],[r.x+12,r.y-6],[r.x+9,r.y+2]],'#3f594c');poly([[r.x-9,r.y-6],[r.x-4,r.y-13],[r.x+4,r.y-13],[r.x+8,r.y-6],[r.x+4,r.y-1],[r.x-7,r.y-1]],'#8b9b81');rect(r.x-4,r.y-11,7,2,'#c3c8a3');rect(r.x-7,r.y-7,3,4,'#a9b393');rect(r.x+5,r.y-5,3,4,'#647f6a');rect(r.x-6,r.y,5,2,'#6f8b56');}
 for(const f of flowers){if(isPath(f.x,f.y)||Math.abs(f.x-river(f.y))<82)continue;rect(f.x,f.y,1,6,'#3e6548');rect(f.x-2,f.y+3,2,1,'#8ca069');rect(f.x-2,f.y,5,1,f.c);rect(f.x,f.y-2,1,5,f.c);rect(f.x,f.y,1,1,'#f3ddb0');}
}
function wood(x,y,w,h){rect(x,y,w,h,'#594e39');rect(x+1,y+1,w-2,h-3,'#a0875d');rect(x+1,y+1,w-2,1,'#c8b17f');rect(x+3,y+4,w-7,1,'#806e4c');rect(x+5,y+h-4,w-8,1,'#b49b6b');}
function drawVillageDetails(){
 // Rebuilt bridge: beam shadows, end grain, iron nails and carved balusters.
 rect(1026,634,188,44,'#233d3444');for(let x=1030;x<1210;x+=9){wood(x,627,9,47);rect(x+3,631,1,1,'#4c5347');rect(x+3,667,1,1,'#4c5347');}
 for(let y of [625,672]){wood(1025,y,190,5);for(let x=1025;x<1220;x+=27){wood(x,y-6,5,16);rect(x-1,y-8,7,3,'#d0ba8c');}}
 for(let x=534;x<633;x+=23){rect(x,560,18,36,'#574e39');for(let yy=563;yy<595;yy+=4)rect(x+2,yy,14,1,'#84724d');for(let y=566;y<593;y+=9){rect(x+7,y,2,6,'#476d42');rect(x+3,y,6,2,'#819b57');rect(x+9,y+1,6,2,'#6e8d4b');rect(x+7,y-2,4,3,'#d1a16b');}}
 for(let x=430;x<662;x+=18){wood(x,603,5,19);rect(x-1,601,7,3,'#d1bf90');wood(x+5,607,13,3);wood(x+5,615,13,3);}
 for(let y of [724,730,736])wood(653,y,43,5);wood(653,714,43,6);rect(657,741,4,8,'#554a35');rect(688,741,4,8,'#554a35');wood(695,633,5,23);wood(681,628,34,17);rect(686,633,23,1,'#64583b');rect(688,636,18,1,'#64583b');rect(691,639,12,1,'#64583b');
 oval(866,731,25,7,'#233d3440');for(let row=0;row<3;row++)for(let col=0;col<5;col++){let xx=845+col*8,yy=710+row*7;rect(xx,yy,7,6,'#929782');rect(xx,yy,6,1,'#c4c5a6');}oval(865,710,22,7,'#555f51');oval(865,709,17,4,'#4c8180');rect(858,708,10,1,'#8bb2a0');wood(844,685,5,30);wood(882,685,5,30);poly([[837,687],[849,675],[881,675],[894,687]],'#554c3d');for(let x=840;x<892;x+=7)wood(x,682,6,5);wood(849,690,35,3);rect(865,693,1,16,'#c5b38b');
 // Stumps, herb pots, and scattered fallen leaves give the village lived-in detail.
 for(let [x,y] of [[622,531],[975,499],[650,344]]){rect(x-6,y-9,12,10,'#805f3f');oval(x,y-9,7,3,'#bda275');oval(x,y-9,4,2,'#806648');rect(x-2,y-10,4,1,'#d1bb8b');}
 for(const h of houses){let x=h.x+h.w+8,y=h.y+h.h-3;rect(x-4,y-10,8,10,'#945e43');rect(x-5,y-11,10,3,'#bf8960');rect(x-3,y-13,6,3,'#4a6e47');rect(x-1,y-18,2,8,'#718f50');rect(x-4,y-16,4,2,'#8a9f62');}
}

// Static terrain and individual tree sprites are baked once, keeping detailed art inexpensive.
let terrainArt;
function drawTerrain(){
 if(!terrainArt){terrainArt=document.createElement('canvas');terrainArt.width=MW*T;terrainArt.height=MH*T;const main=ctx;ctx=terrainArt.getContext('2d');paintTerrain();ctx=main;}
 ctx.drawImage(terrainArt,Math.floor(camera.x),Math.floor(camera.y),W,H,Math.floor(camera.x),Math.floor(camera.y),W,H);
 drawRiver();
 drawWindGrass();
}
function drawTree(t){
 if(t.x<camera.x-65||t.x>camera.x+W+65)return;
 if(!t.art){t.art=document.createElement('canvas');t.art.width=120;t.art.height=136;const main=ctx;ctx=t.art.getContext('2d');ctx.translate(60-Math.round(t.x),119-Math.round(t.y));paintTree(t);ctx=main;}
 // Keep roots planted while the canopy bends gently with passing gusts.
 const w=windState(),sway=Math.sin(time*1.7+t.x*.013+t.y*.007)*w.strength*2.4;
 ctx.drawImage(t.art,0,100,120,36,Math.round(t.x)-60,Math.round(t.y)-19,120,36);
 for(let row=0;row<100;row+=20){let shift=Math.round(sway*(1-row/115));ctx.drawImage(t.art,0,row,120,20,Math.round(t.x)-60+shift,Math.round(t.y)-119+row,120,20);}
}

// Wind strength follows reported mph. Without weather, use a labeled gentle ambient breeze.
function windState(){const mph=weather.live&&Number.isFinite(weather.wind)?Math.max(0,Math.min(75,weather.wind)):4;const gust=.72+.18*Math.sin(time*.47)+.1*Math.sin(time*1.13);return {mph,strength:Math.min(1.8,mph/12)*gust};}
function drawRiver(){
 const top=Math.max(0,Math.floor(camera.y/4)*4),bottom=Math.min(MH*T,camera.y+H+4);
 // Deeper center, translucent-looking shallows, irregular pebble banks.
 for(let y=top;y<bottom;y+=4){const r=river(y);rect(r-56,y,112,4,'#417c80');rect(r-43,y,86,4,'#34717a');rect(r-27,y,54,4,'#306872');
 for(let side of [-1,1]){let xx=r+side*54;rect(xx,y,4,4,'#72a49a');if(y%12===0){rect(r+side*60,y,4,3,'#889878');rect(r+side*64,y+1,3,2,'#bcc09a');}if(y%24===0){rect(r+side*48,y,5,2,'#81a89b');}}
 }
 // Every streak is advected downstream along the meandering centerline.
 for(let i=0;i<160;i++){const y=(i*37+time*(18+grain(i,13)*16))%(MH*T),lane=(grain(i,44)*2-1)*47,x=river(y)+lane+Math.sin(y*.025+time*.7+i)*2;if(y<camera.y-15||y>camera.y+H+15)continue;const len=5+grain(i,78)*15;rect(x,y,1,len,'#8db9b122');rect(x-4,y+3,7,1,'#a7c7b466');rect(x-2,y+4,4,1,'#6caaa555');rect(x-2,y+len,5,1,'#6caaa566');}
 // Broken foam ribbons follow bends; shallow rocks disturb the current.
 for(let i=0;i<55;i++){let y=(i*29+time*16)%(MH*T);if(y<camera.y-10||y>camera.y+H+10)continue;for(let side of [-1,1]){let x=river(y)+side*(51+Math.sin(y*.07)*2);rect(x,y,4,1,'#cee1bf99');rect(x+side*2,y+2,2,3,'#92c0ad77');}}
 for(let yy of [300,526,844,1070]){if(yy<camera.y-30||yy>camera.y+H+30)continue;let xx=river(yy)+(yy%3?29:-26);oval(xx+2,yy+3,11,4,'#264e5866');poly([[xx-7,yy],[xx-5,yy-7],[xx+3,yy-9],[xx+8,yy-3],[xx+6,yy+2]],'#58796e');rect(xx-3,yy-7,6,2,'#a8b69a');rect(xx+4,yy-4,2,4,'#3d605d');let phase=(time*10)%12;rect(xx-8,yy+4+phase,5,1,'#c2dac299');rect(xx+3,yy+7+phase,5,1,'#b9d8c277');}
 // Fine wind-driven surface ripples, independent of downstream motion.
 const w=windState();for(let i=0;i<26;i++){let y=(i*53+time*5)% (MH*T);if(y<camera.y||y>camera.y+H)continue;let x=river(y)+(grain(i,92)-.5)*70;rect(x,y,7+w.strength*5,1,'#a0c1b350');}
}
function drawWindGrass(){const w=windState();for(let i=0;i<flowers.length;i+=3){const f=flowers[i];if(f.x<camera.x||f.x>camera.x+W||f.y<camera.y||f.y>camera.y+H||isPath(f.x,f.y)||Math.abs(f.x-river(f.y))<80)continue;let bend=Math.round(Math.sin(time*2.6+f.x*.02+f.y*.01)*w.strength*2);rect(f.x,f.y+4,1,4,'#446c43');rect(f.x+bend,f.y,1,5,'#8da76b');rect(f.x+bend-2,f.y+2,2,1,'#728e54');}
 // Reeds bend at their tips, with fixed stalk bases.
 for(let y=Math.floor(camera.y/36)*36;y<camera.y+H;y+=36)for(let side of [-1,1]){let x=river(y)+side*70,bend=Math.round(Math.sin(time*2+y*.02)*w.strength*3);rect(x,y-6,1,7,'#496d4e');rect(x+bend,y-12,1,7,'#94a76c');rect(x+bend-1,y-13,3,3,'#a99c60');}
}
