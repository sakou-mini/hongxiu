 #! python3
import os
import glob
import shutil

os.system("svn up E:\workspace/functional_test/server/")
shutil.rmtree('E:\workspace/functional_test/server/')

os.system("svn export svn://192.168.0.124/jinli/trunk/Server E:\workspace/functional_test/server")
os.system("svn commit E:\workspace/functional_test/server/ -m 'merge release' ")