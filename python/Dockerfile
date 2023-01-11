FROM python

RUN apt-get update && apt-get install -y cron

COPY requirements.txt /app/
WORKDIR /app
RUN pip install -r requirements.txt

COPY . /app

CMD python main.py

