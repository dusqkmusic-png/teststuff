from pathlib import Path
import subprocess, os, zipfile
root=Path(__file__).resolve().parent;os.chdir(root)
if not (root/'lib/ecj.jar').exists():subprocess.run(['python3','fetch_dependencies.py'],check=True)
cp=os.pathsep.join(str(p) for p in sorted(Path('lib').glob('*.jar')) if p.name!='ecj.jar')
Path('classes').mkdir(exist_ok=True)
subprocess.run(['java','-jar','lib/ecj.jar','-17','-warn:none','-cp',cp,'-d','classes',*[str(p) for p in sorted(Path('src').rglob('*.java'))]],check=True)
with zipfile.ZipFile('Neighborhood.jar','w',zipfile.ZIP_DEFLATED) as z:
    classpath='Class-Path: '+' '.join(p.as_posix() for p in sorted(Path('lib').glob('*.jar')) if p.name!='ecj.jar')
    wrapped=classpath[:70]+'\n'+''.join(' '+classpath[i:i+69]+'\n' for i in range(70,len(classpath),69))
    z.writestr('META-INF/MANIFEST.MF','Manifest-Version: 1.0\nMain-Class: neighborhood.Desktop\n'+wrapped+'\n')
    for p in Path('classes').rglob('*.class'):z.write(p,p.relative_to('classes'))
print('Built Neighborhood.jar')
