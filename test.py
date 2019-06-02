import os
import argparse
import subprocess

def evaluateSpiglet(directory, spg_file):
    os.chdir('./src')
    print('Judging ', spg_file)
    try:
        command = 'javac Main.java'
        print(os.popen(command).read())
        command = 'java Main ' + '../' + directory + spg_file
        print(os.popen(command).read())
        command = 'java -jar ../kgi.jar < ' + '../' + directory + \
                  spg_file.replace('.spg', '.kg')
        str1 = os.popen(command).read()
        # print(str1)
    except:
        print('nmdwsm')
    
    os.chdir('..')
    try:
        command = 'java -jar pgi.jar < ' + directory + spg_file
        str2 = os.popen(command).read()
        # print(str2)
    except:
        print('nmdwsm')
    print('Judge ', str1 == str2)

def evaluateMiniJava(directory, java_file):
    print('------------------------------------------')
    cur_path = os.path.dirname(os.path.abspath(__file__))
    cur_path = cur_path.replace('test.py', '')
    print('Eavluate ' + java_file)
    print('Samples\' directory is ' + directory)

    print('Use our comipler')
    os.chdir('./src')
    try:
        command = 'javac Main.java'
        print(os.popen(command).read())
        command = 'java Main ' + '../' + directory + java_file 
        print(os.popen(command).read())
        command = 'java -jar ../pgi.jar < ' + '../' + directory + \
                    java_file.replace('.java', '.spg')
        print(os.popen(command).read())
        str1 = os.popen(command).read()
    except:
        print('???')
    os.chdir('..')
    
    print('\nUse javac')
    os.chdir(directory)
    try:
        command = 'javac ' + java_file
        print(os.popen(command).read())
        command = 'java ' + java_file.split('.')[0]
        print(os.popen(command).read())
        str2 = os.popen(command).read()
        print("JUDGE: ", str1 == str2)
    except:
        print('???')
    os.chdir(cur_path)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--sub_dir', type=str, default='samples/')
    parser.add_argument('--test', type=str, default='')

    args = parser.parse_args()
    directory = args.sub_dir
    assert os.path.exists(directory)
    if 'java' in args.test:
        print('nmdwsm')
    if 'spg' in args.test:
        files = os.listdir(directory)
        files = [file_name for file_name in files if file_name.endswith('spg')]
        for file_name in files:
            evaluateSpiglet(directory, file_name)

if __name__ == '__main__':
    main()