#!/bin/python3

from datetime import datetime
import asyncio
import pycron
import signal
import requests

def sample_memory():
    dat = requests.get('http://server:9000/mem_info')
    mem = int(dat.content.strip())
    with open("/output/data.csv", 'a') as f:
        print(f"{mem},{datetime.now().timestamp()}", file=f)

# Every 30s
@pycron.cron("*/30 * * * * *")
async def cron_sample(timestamp: datetime):
    sample_memory()



def main():
    sample_memory()
    pycron.start()


if __name__ == '__main__':
    main()
