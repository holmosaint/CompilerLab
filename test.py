import os
import argparse
import subprocess

parser = argparse.ArgumentParser()
parser.add_argument('--sub_dir', type=str, default='')
parser.add_argument('--java_file', type=str, default='Test.java')

args = parser.parse_args()

directory = '../samples/' + args.sub_dir
print('Samples\' directory is ' + directory)

print('Use our comipler')
os.chdir('./src')
command = 'javac Main.java'
print(os.popen(command).read())
command = 'java Main ' + directory + args.java_file 
print(os.popen(command).read())
command = 'java -jar ./tools/pgi.jar < ' + directory + args.java_file.replace('.java', '.pg')
print(os.popen(command).read())

print('\nUse javac')
os.chdir(directory)

command = 'javac ' + args.java_file
print(os.popen(command).read())
command = 'java ' +args.java_file.split('.')[0]
print(os.popen(command).read())
