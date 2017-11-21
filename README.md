# Distance-Vector-Routing
This project enables router to compute and share its routing table with all the other routers it is connected to using a DVR protocol called RIP.

The submission consists of a total of 4 java files<br>
Main.java<br>
Receiver.java<br>
Sender.java<br>
Table.java<br>

Steps to run the code:

1. javac *.java
2. Run the Main.java file on every PC
3. Enter the required inputs in the following order

Routers IP address<br>
Routers name<br>
Port the router is listening on<br>
Number of neighbors<br>
for each neighbor<br>
{<br>
IP of neighbor<br>
Cost to reach neighbor<br>
Port the neighbor is listening on<br>
Subnet mask of the neighbor<br>
}<br>
<br>
Example of input:<br>

129.21.30.37<br>
queeg<br>
9000<br>
2<br>
129.21.34.80<br>
10<br>
9001<br>
255.255.255.0<br>
129.21.22.196<br>
10<br>
9003<br>
255.255.255.0<br>


4. To stop a router, just stop the program running on that PC.


Note: 3 input files are included in the Input folder<br>
