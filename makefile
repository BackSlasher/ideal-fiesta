.PHONY: serve

Main.class: Main.java
	javac Main.java

serve: Main.class
	java Main
