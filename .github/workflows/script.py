import os
from re import M
import sys
import glob
import time

def main():
    terminalArgument = sys.argv                    #creating alias to sys argv
    commitMessage = sys.argv[1]
    print(commitMessage.strip().split("\n")[0])
if __name__ == "__main__":
    main()


