#!/bin/python3

import sys
import time
from datetime import datetime
import requests


def sample_memory():
    try:
        dat = requests.get("http://server:9000/mem_info", timeout=3)
        mem = int(dat.content.strip())
        print(mem)
    except Exception as e:
        mem = -1
        print(e)

    with open("/output/data.csv", "a") as f:
        print(f"{datetime.now().timestamp()},{mem}", file=f)


def main():
    while True:
        sample_memory()
        sys.stdout.flush()
        # tried fancier things, didn't work. This is enough
        time.sleep(5)


if __name__ == "__main__":
    main()
