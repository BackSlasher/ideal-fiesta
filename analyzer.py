#!/bin/python3

import datetime
import argparse
import csv


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument("filename", type=argparse.FileType("r"))
    parser.add_argument("-b", "--hours-back", type=int, default=1)
    return parser.parse_args()


def analyze_data(file, hours_back):
    reader = csv.DictReader(file, fieldnames=["time", "memory"])
    rows = [row for row in reader]
    for row in rows:
        row["time"] = datetime.datetime.fromtimestamp(float(row["time"]))
        row["memory"] = int(row["memory"])

    # filter
    now = datetime.datetime.now()
    allowed = datetime.timedelta(hours=hours_back)
    rows = [row for row in rows if (now - row["time"]) < allowed]

    # max
    max_mem = max([row["memory"] for row in rows])

    # magic
    recommended_mem = int(max_mem * 1.15)

    print(f"I recommend you have {recommended_mem} mb of memory.")


def main():
    args = parse_args()
    analyze_data(args.filename, args.hours_back)


if __name__ == "__main__":
    main()
