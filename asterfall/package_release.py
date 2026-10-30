"""Build downloadable Windows and source packages. Run after build.py."""
from pathlib import Path
import urllib.request,zipfile,hashlib,shutil,os
root=Path(__file__).resolve().parent;os.chdir(root)
archive=root/'windows-runtime.zip'
if not archive.exists():urllib.request.urlretrieve('https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.17%2B10/OpenJDK17U-jre_x64_windows_hotspot_17.0.17_10.zip',archive)
assert hashlib.sha256(archive.read_bytes()).hexdigest()=='bd17db1cc08b3a8a98cedc7b8b8a203f683793be65a9ffbe4ceaacde71fc2b6b','Runtime checksum mismatch'
staging=root/'release-staging';staging.mkdir(exist_ok=True)
with zipfile.ZipFile(archive) as z:z.extractall(staging)
runtime=next(staging.glob('jdk*'))
dist=root.parent/'dist';dist.mkdir(exist_ok=True)
def add(z,p,arc):
 if p.is_dir():
  for f in sorted(p.rglob('*')):
   if f.is_file():z.write(f,arc+'/'+f.relative_to(p).as_posix())
 else:z.write(p,arc)
common=['asterfall.jar','START-HERE.txt','THIRD-PARTY.txt','content','assets','licenses']
with zipfile.ZipFile(dist/'Asterfall-Windows.zip','w',zipfile.ZIP_DEFLATED,compresslevel=6) as z:
 for n in common+['Asterfall.exe','Play-with-log.bat']:add(z,root/n,'Asterfall/'+n)
 add(z,runtime,'Asterfall/runtime')
 for f in sorted((root/'lib').glob('*.jar')):
  if f.name not in ['ecj.jar','lwjgl-linux.jar']:z.write(f,'Asterfall/lib/'+f.name)
with zipfile.ZipFile(dist/'Asterfall-Source.zip','w',zipfile.ZIP_DEFLATED,compresslevel=6) as z:
 for n in common+['src','lib','build.py','make_assets.py','paint_materials.py','fetch_dependencies.py','package_release.py','DEVELOPMENT.md','dependencies.sha256','launch4j.xml','README.md']:add(z,root/n,'Asterfall-Source/'+n)
for p in dist.glob('*.zip'):
 with zipfile.ZipFile(p) as z:assert z.testzip() is None
 print(p.name,round(p.stat().st_size/1048576,1),'MiB')
(dist/'SHA256SUMS.txt').write_text('\n'.join(hashlib.sha256(p.read_bytes()).hexdigest()+'  '+p.name for p in sorted(dist.glob('*.zip')))+'\n')
