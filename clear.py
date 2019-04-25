import os

def remove(root):
    if root == '.git':
        return
    print(root)
    os.chdir(root)
    os.system('del *.class')
    paths = os.listdir('.')
    for path in paths:
        if os.path.isdir(path):
            remove(path)
    os.chdir('../')

if __name__ == '__main__':
    remove('.')