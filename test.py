import os
import argparse
import subprocess

def evaluate(directory, java_file):
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
    parser.add_argument('--java_file', type=str, default='')

    args = parser.parse_args()
    directory = args.sub_dir
    assert os.path.exists(directory)
    if args.java_file != '':
        evaluate(directory, args.java_file)
    else:
        files = os.listdir(directory)            
        files = [item for item in files if item.endswith('.java')]
        for file in files:
            evaluate(directory, file)


if __name__ == '__main__':
    main()