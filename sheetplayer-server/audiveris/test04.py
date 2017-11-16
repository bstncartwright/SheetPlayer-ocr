import sys
import os
import subprocess
import music21

fpath = sys.argv[1]
spath = sys.argv[2]

returncode = subprocess.call(['gradle run -p /home/kd0euh/audiveris/audiveris -PcmdLineArgs=-batch,-export,-output,/home/kd0euh/audiveris/audiveris/output/,--,' + fpath], shell=True)

base = os.path.basename(fpath)
das = os.path.splitext(base)[0]

c = music21.converter.parse('/home/kd0euh/audiveris/audiveris/output/' + das +  '/' + das + '.mxl')

c.write('midi', spath + '.mid')

print('DONE')
