FROM openjdk

COPY . /app
WORKDIR /app
RUN javac Main.java

CMD java Main
