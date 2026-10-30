from pathlib import Path
import subprocess,os,zipfile
root=Path(__file__).parent;os.chdir(root)
jars=[str(p) for p in Path('lib').glob('*.jar') if p.name!='ecj.jar']
subprocess.run(['java','-jar','lib/ecj.jar','-17','-warn:none','-cp',os.pathsep.join(jars),'-d','classes',*[str(p) for p in Path('src').rglob('*.java')]],check=True)
with zipfile.ZipFile('asterfall.jar','w',zipfile.ZIP_DEFLATED) as z:
 z.writestr('META-INF/MANIFEST.MF','Manifest-Version: 1.0\nMain-Class: game.Desktop\nClass-Path: '+' '.join(x.replace('\\','/') for x in jars)+'\n\n')
 for p in Path('classes').rglob('*.class'):z.write(p,p.relative_to('classes'))
print('Built asterfall.jar')
