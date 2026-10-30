from PIL import Image,ImageFont,ImageDraw
from pathlib import Path
import math,random,struct,wave
out=Path('assets');out.mkdir(exist_ok=True)
font=ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf',32)
im=Image.new('RGBA',(1024,512));d=ImageDraw.Draw(im);lines=['info face="DejaVu Sans" size=32 bold=0 italic=0 charset="" unicode=1 stretchH=100 smooth=1 aa=1 padding=0,0,0,0 spacing=1,1','common lineHeight=40 base=30 scaleW=1024 scaleH=512 pages=1 packed=0','page id=0 file="ui.png"','chars count=95'];x=y=2
for code in range(32,127):
 c=chr(code);adv=font.getlength(c);box=font.getbbox(c);w=max(1,box[2]-box[0]);h=max(1,box[3]-box[1])
 if x+w+3>1024:x=2;y+=45
 d.text((x-box[0],y-box[1]),c,font=font,fill='white')
 lines.append(f'char id={code} x={x} y={y} width={w} height={h} xoffset={box[0]} yoffset={box[1]} xadvance={adv:.0f} page=0 chnl=15');x+=w+3
im.save(out/'ui.png');(out/'ui.fnt').write_text('\n'.join(lines))
# Original synthesized effects and a slow ambient bed, no third-party audio assets.
sr=22050
random.seed(3)
def write(name,dur,fn):
 with wave.open(str(out/name),'wb') as f:
  f.setnchannels(1);f.setsampwidth(2);f.setframerate(sr)
  f.writeframes(b''.join(struct.pack('<h',int(max(-1,min(1,fn(i/sr)))*25000)) for i in range(int(dur*sr))))
write('slash.wav',.23,lambda t:random.uniform(-1,1)*math.exp(-t*24)*.3+math.sin(2*math.pi*(450*t-600*t*t))*math.exp(-t*20)*.2)
write('hit.wav',.22,lambda t:math.sin(2*math.pi*95*t)*math.exp(-t*30)*.5+random.uniform(-1,1)*math.exp(-t*40)*.15)
write('loot.wav',.7,lambda t:sum(math.sin(2*math.pi*f*t)*max(0,1-(t-j*.1)*3)*(t>=j*.1) for j,f in enumerate([659.25,830.61,987.77]))*.13)
write('cast.wav',.5,lambda t:math.sin(2*math.pi*(440*t+700*t*t))*math.exp(-t*7)*.2+math.sin(2*math.pi*880*t)*math.exp(-t*8)*.1)
chords=[[146.83,220,293.66,369.99],[130.81,196,261.63,329.63],[164.81,246.94,329.63,392],[110,164.81,220,293.66]]
def music(t):
 k=int(t//4)%4;u=t%4;fade=min(1,u/.7,(4-u)/.7);v=sum(math.sin(2*math.pi*f*t)*.038+math.sin(2*math.pi*f*2*t)*.009 for f in chords[k])*fade
 beat=t%1;f=chords[k][int(t*2)%4]*2;v+=math.sin(2*math.pi*f*t)*math.exp(-beat*5)*.035
 return v
write('garden.wav',16,music)
# Windows icon: original moon-flower emblem.
im=Image.new('RGBA',(256,256),(25,43,63,255));d=ImageDraw.Draw(im)
for j in range(5):
 a=j*math.tau/5-math.pi/2;cx=128+math.cos(a)*45;cy=124+math.sin(a)*45;d.ellipse((cx-39,cy-39,cx+39,cy+39),fill=(227,175,208))
d.ellipse((104,100,152,148),fill=(246,215,149));im.save(out/'asterfall.ico',sizes=[(16,16),(32,32),(48,48),(128,128),(256,256)])
