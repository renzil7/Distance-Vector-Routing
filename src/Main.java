import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Scanner;

/**
 * This is the Main class. It is responsible for taking all the inputs for this
 * router and starting the sending and receiving threads
 *
 * @author Renzil Dourado rd9012
 */
public class Main {

    public static void main(String[] args) throws SocketException, IOException {

//        System.out.println("Enter the IP of this router");
//        Scanner sc = new Scanner(System.in);
//        Receiver.ip = sc.next();
//        System.out.println("Enter the name of this router");
//        Receiver.name = sc.next();
//        System.out.println("Enter the port number this router will receive on");
//        Receiver.port = sc.nextInt();
//        Receiver.socketReceive = new DatagramSocket(Receiver.port);
//
//        System.out.println("Enter the number of neighbors for this router, maximum of 2");
//        int numberOfNeighbors = sc.nextInt();
//
//        while(numberOfNeighbors>2)
//            {
//                System.out.println("Maximum two neighbors!");
//                System.out.println("Enter the number of neighbors for this router");
//                numberOfNeighbors = sc.nextInt();
//            }
//
//        for(int i=1; i<=numberOfNeighbors; i++)
//        {
//            System.out.println("Enter the IP of neighbor "+i );
//            String neighborIp = sc.next();
//            System.out.println("Enter the cost of reaching neighbor "+i );
//            int cost = sc.nextInt();
//            Receiver.neighbor.put(neighborIp, cost);
//            System.out.println("Enter the port that neighbor "+i +" is listening on" );
//            int neighborPortNumber = sc.nextInt();
//            Receiver.neighborPort.put(neighborIp, neighborPortNumber);
//            Receiver.portToIp.put(neighborPortNumber, neighborIp);
//
//            Table tab = new Table(neighborIp, cost);
//            Receiver.routingTable.put(neighborIp, tab);
//            System.out.println("Enter the subnet Mask for neighbor"+i);
//            tab.subnetMask = sc.next();
//
//
//        }


        //TO AVOID TAKING INPUTS ONE AT A TIME

        Scanner sc = new Scanner(System.in);
        Receiver.ip = sc.next();
        Receiver.name = sc.next();
        Receiver.port = sc.nextInt();
        Receiver.socketReceive = new DatagramSocket(Receiver.port);
        int numberOfNeighbors = sc.nextInt();

        for (int i = 1; i <= numberOfNeighbors; i++) {

            String neighborIp = sc.next();

            int cost = sc.nextInt();
            Receiver.neighbor.put(neighborIp, cost);

            int neighborPortNumber = sc.nextInt();
            Receiver.neighborPort.put(neighborIp, neighborPortNumber);
            Receiver.portToIp.put(neighborPortNumber, neighborIp);
            Table tab = new Table(neighborIp, cost);
            Receiver.routingTable.put(neighborIp, tab);
            tab.subnetMask = sc.next();

        }

        //starting the sending and receiving threads
        Thread sending = new Thread(new Sender());
        Thread receiving = new Thread(new Receiver());
        sending.start();
        receiving.start();

    }
}
