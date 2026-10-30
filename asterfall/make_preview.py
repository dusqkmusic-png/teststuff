"""Assemble an actual rendered movement/combat sequence, never concept art."""
from pathlib import Path
from PIL import Image
root=Path(__file__).resolve().parent
files=sorted((root.parent/'feel-frames').glob('*.png'))
if not files:raise RuntimeError('Run --smoke to record actual gameplay frames first')
frames=[Image.open(p).convert('RGB').resize((960,600),Image.Resampling.LANCZOS).quantize(colors=96) for p in files]
frames[0].save(root.parent/'preview-feel.gif',save_all=True,append_images=frames[1:],duration=45,loop=0,optimize=True)
print('Wrote actual rendered movement / sword / dodge preview:',len(frames),'frames')
