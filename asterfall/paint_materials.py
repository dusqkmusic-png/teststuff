"""Original tileable hand-painted-style materials; no external game artwork."""
from PIL import Image,ImageDraw,ImageFilter
from pathlib import Path
import random,math
out=Path('assets/materials');out.mkdir(parents=True,exist_ok=True)
S=256

def noise(base,seed=4):
 r=random.Random(seed);im=Image.new('RGB',(S,S));p=im.load()
 for y in range(S):
  for x in range(S):
   n=r.gauss(0,2)+math.sin(x*.12)*1.4+math.sin(y*.07)*1.4;p[x,y]=tuple(max(0,min(255,int(c+n))) for c in base)
 return im

def stone(name,base,seed,brick=False):
 r=random.Random(seed);im=noise(tuple(int(c*.65) for c in base),seed);d=ImageDraw.Draw(im)
 rows=4 if brick else 3;h=S//rows
 for row in range(rows+1):
  for col in range(-1,5):
   w=86 if brick else 84;xx=col*w+(row%2)*w//2;yy=row*h
   v=r.randint(-14,14);c=tuple(max(0,min(255,k+v)) for k in base)
   corners=[(xx+3,yy+5),(xx+w-7,yy+3),(xx+w-3,yy+10),(xx+w-4,yy+h-7),(xx+w-12,yy+h-3),(xx+6,yy+h-4),(xx+3,yy+h-12)]
   d.polygon(corners,fill=c);d.line(corners[:3],fill=tuple(min(255,k+22) for k in c),width=3);d.line(corners[3:6],fill=tuple(int(k*.8) for k in c),width=3)
   # Painted mottling, fissures and moss in joints.
   for j in range(20):
    x=xx+r.randint(8,w-10);y=yy+r.randint(10,h-10);rad=r.randint(2,8);v=r.randint(-8,8);d.ellipse((x-rad,y-rad*.45,x+rad,y+rad*.45),fill=tuple(max(0,min(255,k+v)) for k in c))
   if r.random()<.3:d.line([(xx+25,yy+4),(xx+33,yy+17),(xx+29,yy+32)],fill=tuple(int(k*.67) for k in c),width=1)
   if r.random()<.55:
    for j in range(15):
     x=xx+r.randrange(0,w);y=yy+r.choice([2,h-3]);d.ellipse((x,y-2,x+r.randrange(3,13),y+3),fill=r.choice([(88,109,65),(111,126,75),(117,135,86)]))
 im.filter(ImageFilter.GaussianBlur(.35)).save(out/(name+'.png'))
stone('stone',(167,153,115),8,True);stone('paving',(179,165,132),14);stone('darkstone',(105,112,109),31,True)
im=noise((99,132,69),52);d=ImageDraw.Draw(im);r=random.Random(51)
for i in range(1800):
 x=r.randrange(S);y=r.randrange(S);c=r.choice([(111,143,75),(108,139,73),(88,121,62),(124,151,83)]);d.line((x,y,x+r.randrange(-2,3),y-r.randrange(2,6)),fill=c,width=1)
im.save(out/'grass.png')
im=noise((117,79,48),9);d=ImageDraw.Draw(im)
for y in range(0,256,64):
 d.line((0,y,256,y),fill=(69,51,35),width=4);d.line((0,y+4,256,y+4),fill=(158,111,61),width=2)
 for k in range(12):
  pts=[(x,y+7+k*4+math.sin(x*.03+k)*2) for x in range(256)];d.line(pts,fill=(106+k%3*8,70+k%3*6,41),width=1)
im.save(out/'wood.png')
for name,base in [('cloth',(29,111,125)),('ivory',(228,216,184)),('leather',(98,61,41)),('gold',(202,154,66)),('water',(63,139,160)),('leaf',(91,143,70))]:
 im=noise(base,7);d=ImageDraw.Draw(im)
 if name=='water':
  for j in range(35):
   y=j*8;pts=[(x,y+math.sin(x*.025+j)*2) for x in range(256)];d.line(pts,fill=(105,174,181),width=1)
 else:
  for y in range(0,256,4):d.line((0,y,256,y),fill=tuple(int(k*.95) for k in base))
 im.save(out/(name+'.png'))
# Almond anime eyes painted onto the surface of the 3D head.
im=Image.new('RGBA',(256,128));d=ImageDraw.Draw(im)
shape=[(15,72),(38,34),(84,15),(132,10),(184,23),(224,53),(239,75),(222,101),(183,117),(83,116),(38,99)]
d.polygon(shape,fill=(252,250,239,255));d.ellipse((86,15,183,127),fill=(41,83,102,255));d.ellipse((98,34,170,118),fill=(62,151,167,255));d.ellipse((115,22,158,114),fill=(25,41,57,255));d.ellipse((107,32,132,61),fill='white');d.ellipse((145,84,160,99),fill=(226,250,248,255));d.line(shape[:7],fill=(46,35,42,255),width=9);d.line(shape[6:]+[shape[0]],fill=(85,52,48,255),width=4)
im.resize((256,128),Image.Resampling.LANCZOS).save(out/'eye.png')
