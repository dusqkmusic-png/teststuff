"""Fetch pinned Maven dependencies; subsequent downloads verify the recorded hashes."""
from pathlib import Path
import urllib.request, hashlib, json, concurrent.futures
ROOT=Path(__file__).resolve().parent
BASE='https://repo.maven.apache.org/maven2/'
artifacts={
'gdx':'com/badlogicgames/gdx/gdx/1.13.1/gdx-1.13.1',
'backend':'com/badlogicgames/gdx/gdx-backend-lwjgl3/1.13.1/gdx-backend-lwjgl3-1.13.1',
'gdx-natives':'com/badlogicgames/gdx/gdx-platform/1.13.1/gdx-platform-1.13.1-natives-desktop',
'loader':'com/badlogicgames/gdx/gdx-jnigen-loader/2.5.2/gdx-jnigen-loader-2.5.2',
'ecj':'org/eclipse/jdt/ecj/3.39.0/ecj-3.39.0',
'jlayer':'com/badlogicgames/jlayer/jlayer/1.0.1-gdx/jlayer-1.0.1-gdx',
'jorbis':'org/jcraft/jorbis/0.0.17/jorbis-0.0.17'}
for module in ['lwjgl','lwjgl-glfw','lwjgl-jemalloc','lwjgl-openal','lwjgl-opengl','lwjgl-stb']:
    base=f'org/lwjgl/{module}/3.3.3/{module}-3.3.3'
    artifacts[module]=base
    for platform in ['linux','windows','macos','macos-arm64']:
        artifacts[f'{module}-{platform}']=base+'-natives-'+platform
lock=ROOT/'dependencies.sha256.json'
checks=json.loads(lock.read_text()) if lock.exists() else {}
(ROOT/'lib').mkdir(exist_ok=True)
def fetch(item):
    name,artifact=item; path=ROOT/'lib'/(name+'.jar')
    if not path.exists():
        with urllib.request.urlopen(BASE+artifact+'.jar',timeout=90) as r:path.write_bytes(r.read())
    digest=hashlib.sha256(path.read_bytes()).hexdigest()
    if name in checks and checks[name]!=digest:raise RuntimeError('Checksum mismatch: '+name)
    return name,digest
with concurrent.futures.ThreadPoolExecutor(max_workers=6) as pool:
    result=dict(pool.map(fetch, artifacts.items()))
lock.write_text(json.dumps(result,indent=2)+'\n')
print('Verified',len(result),'pinned dependency jars.')
