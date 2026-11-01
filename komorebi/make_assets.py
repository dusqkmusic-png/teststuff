"""Original, deterministic painted surface textures. Pillow is only needed to regenerate."""
from PIL import Image, ImageDraw, ImageFont, ImageFilter
import random, math
from pathlib import Path
root=Path(__file__).resolve().parent/'assets'; root.mkdir(exist_ok=True)
rng=random.Random(625)
N=512
for name,base,amplitude in [('grass',(105,143,66),9),('asphalt',(119,127,130),5),('plaster',(244,240,227),4),('bark',(117,87,58),9),('paving',(206,199,176),4),('roof',(181,171,152),7),('leaves',(122,163,79),7),('blossom',(235,181,184),8)]:
    im=Image.new('RGB',(N,N)); px=im.load()
    for y in range(N):
        for x in range(N):
            broad=math.sin(x*.029+math.sin(y*.02))*math.cos(y*.031)*amplitude*.4
            grain=rng.gauss(0,amplitude*.45)+broad
            px[x,y]=tuple(max(0,min(255,int(c+grain))) for c in base)
    d=ImageDraw.Draw(im,'RGBA')
    if name=='grass':
        for i in range(16000):
            x=rng.randrange(N);y=rng.randrange(N);h=rng.randrange(2,8)
            d.line((x,y,x+rng.randrange(-3,4),y-h),fill=rng.choice([(192,203,111,45),(43,89,33,45),(218,205,118,40)]),width=1)
    if name=='roof':
        for y in range(0,N,32):
            d.line((0,y,N,y),fill=(58,39,36,100),width=2)
            for x in range(-32,N,64):
                xx=x+(32 if y//32%2 else 0)
                d.line((xx,y,xx,y+32),fill=(55,35,30,65),width=2)
                d.line((xx+3,y+4,xx+59,y+4),fill=(226,170,134,60),width=2)
    if name=='bark':
        for i in range(100):
            x=rng.randrange(N);y=rng.randrange(N)
            d.line((x,y,x+3,y+rng.randrange(20,130)),fill=(53,40,26,60),width=rng.randrange(1,4))
    if name=='asphalt':
        for i in range(3800):
            x=rng.randrange(N);y=rng.randrange(N)
            d.point((x,y),fill=(218,212,193,65))
    if name in ['leaves','blossom']:
        for i in range(1900):
            x=rng.randrange(N);y=rng.randrange(N);r=rng.randrange(2,6)
            d.ellipse((x,y,x+r*2,y+r),fill=rng.choice([(200,215,121,25),(29,77,45,30)]))
    im.save(root/(name+'.png'))
# Readable bitmap typography, bundled so no platform font dependency exists at runtime.
font=ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf',24)
atlas=Image.new('RGBA',(512,256));d=ImageDraw.Draw(atlas)
lines=['info face="DejaVu Sans" size=24 bold=0 italic=0 charset="" unicode=1 stretchH=100 smooth=1 aa=1 padding=0,0,0,0 spacing=1,1', 'common lineHeight=32 base=25 scaleW=512 scaleH=256 pages=1 packed=0','page id=0 file="type.png"','chars count=95']
for i,c in enumerate(range(32,127)):
    x=(i%16)*32;y=(i//16)*40
    d.text((x,y),chr(c),font=font,fill='white',stroke_width=0)
    lines.append(f'char id={c} x={x} y={y} width=32 height=36 xoffset=0 yoffset=0 xadvance={round(d.textlength(chr(c),font=font))} page=0 chnl=15')
atlas.save(root/'type.png');(root/'type.fnt').write_text('\n'.join(lines))
print('Generated seven painted materials and bitmap font.')
