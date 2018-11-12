
all:
	javac -cp ./jSerialComm-2.3.0.jar TwoWaySerialComm.java

clean:
	rm -rf *.class

run:
	java -cp ./jSerialComm-2.3.0.jar:. TwoWaySerialComm
