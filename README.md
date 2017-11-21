# Distance-Vector-Routing
This project enables router to compute and share its routing table with all the other routers it is connected to using a DVR protocol called RIP.

The submission consists of a total of 4 java files
Main.java \n
Receiver.java
Sender.java
Table.java

Steps to run the code:

1. javac *.java
2. Run the Main.java file on every PC
3. Enter the required inputs in the following order

Routers IP address
Routers name
Port the router is listening on
Number of neighbors
for each neighbor
{
IP of neighbor
Cost to reach neighbor
Port the neighbor is listening on
Subnet mask of the neighbor
}

Example of input:

129.21.30.37
queeg
9000
2
129.21.34.80
10
9001
255.255.255.0
129.21.22.196
10
9003
255.255.255.0


4. To stop a router, just stop the program running on that PC.


Note: 3 input files are included in the Input folder
