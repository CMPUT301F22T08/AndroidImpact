import os
from pydoc import stripid
from re import M
import sys
import glob
import time

def main():
    file = sys.argv[1]
    with open(file, 'r') as f:
        lines = f.readlines()
        print(lines[0].strip())



if __name__ == "__main__":
    main()


