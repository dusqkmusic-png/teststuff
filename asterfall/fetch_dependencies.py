"""Download the exact tested Maven dependencies, verifying SHA-256."""
from pathlib import Path
import hashlib,urllib.request,os
os.chdir(Path(__file__).parent)
arts={'gdx':'com/badlogicgames/gdx/gdx/1.13.1/gdx-1.13.1','backend':'com/badlogicgames/gdx/gdx-backend-lwjgl/1.13.1/gdx-backend-lwjgl-1.13.1','gdx-natives':'com/badlogicgames/gdx/gdx-platform/1.13.1/gdx-platform-1.13.1-natives-desktop','lwjgl':'org/lwjgl/lwjgl/lwjgl/2.9.3/lwjgl-2.9.3','lwjgl-util':'org/lwjgl/lwjgl/lwjgl_util/2.9.3/lwjgl_util-2.9.3','lwjgl-linux':'org/lwjgl/lwjgl/lwjgl-platform/2.9.3/lwjgl-platform-2.9.3-natives-linux','lwjgl-windows':'org/lwjgl/lwjgl/lwjgl-platform/2.9.3/lwjgl-platform-2.9.3-natives-windows','jinput':'net/java/jinput/jinput/2.0.5/jinput-2.0.5','jutils':'net/java/jutils/jutils/1.0.0/jutils-1.0.0','ecj':'org/eclipse/jdt/ecj/3.39.0/ecj-3.39.0','loader':'com/badlogicgames/gdx/gdx-jnigen-loader/2.5.2/gdx-jnigen-loader-2.5.2'}
checks={line.split()[1]:line.split()[0] for line in Path('dependencies.sha256').read_text().splitlines()}
Path('lib').mkdir(exist_ok=True)
for name,artifact in arts.items():
 p=Path('lib')/(name+'.jar')
 if not p.exists():urllib.request.urlretrieve('https://repo.maven.apache.org/maven2/'+artifact+'.jar',p)
 if hashlib.sha256(p.read_bytes()).hexdigest()!=checks[p.as_posix()]:raise RuntimeError('Checksum mismatch: '+str(p))
 print('Verified',p)
