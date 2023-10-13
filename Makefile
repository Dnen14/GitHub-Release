all:
	javac ./*.java

clean:
	rm ./*.class

run:
	make all
	java -cp ".:postgresql-42.2.8.jar" Cashier_GUI