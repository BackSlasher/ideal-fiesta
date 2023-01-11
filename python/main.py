#!/bin/python3

import sys
import time
from datetime import datetime
import requests

import argparse


def parse_args():
    parser = argparse.ArgumentParser()
    subparsers = parser.add_subparsers(title="subcommands", dest="command")
    stress = subparsers.add_parser("stress")
    stress.add_argument(
        "-a", "--amount", type=int, default=10, help="Amount of mem in MB to acquire"
    )
    subparsers.add_parser("monitor")
    return parser.parse_args()


def monitor_memory():
    try:
        dat = requests.get("http://server:9000/mem_info", timeout=3)
        mem = int(dat.content.strip())
        print(f"Current observation: {mem} mb")
    except Exception as e:
        mem = -1
        print(e)
    with open("/output/data.csv", "a") as f:
        print(f"{datetime.now().timestamp()},{mem}", file=f)


def stress(amount_mb):
    try:
        dat = requests.get(
            "http://server:9000/leak", headers={"memory_mb": str(amount_mb)}, timeout=3
        )
        print(f"Leaked {amount_mb} mb")
    except Exception as e:
        print(e)


def main():
    args = parse_args()
    while True:
        if args.command == "monitor":
            monitor_memory()
        elif args.command == "stress":
            stress(args.amount)
        sys.stdout.flush()
        # tried fancier things, didn't work. This is enough
        time.sleep(5)


if __name__ == "__main__":
    main()
