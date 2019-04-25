import os
import argparse
import subprocess

parser = argparse.ArgumentParser()
parser.add_argument('--java_file', type=str, default='Test.java')

args = parser.parse_args()

print('Use our comipler')
os.chdir('./src')
command = 'javac Main.java'
print(os.popen(command).read())
command = 'java Main ../samples/' + args.java_file 
print(os.popen(command).read())
command = 'java -jar ./tools/pgi.jar < ../samples/' + args.java_file.replace('.java', '.pg')
print(os.popen(command).read())

print('\nUse javac')
os.chdir('../samples')
command = 'javac ' + args.java_file
print(os.popen(command).read())
command = 'java ' +args.java_file.split('.')[0]
print(os.popen(command).read())
