import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class acts as a Receiver thread. It is responsible for
 * updating the routing table and detecting if any router has failed,
 * if so, it sends triggered updates to all its neighbors
 *
 * @author Renzil Dourado
 */
public class Receiver implements Runnable{

    static DatagramSocket socketReceive;
    static String ip, name;
    static int port;
    static HashMap<String, Integer> neighbor = new HashMap<>();
    static HashMap<String, Integer> neighborPort = new HashMap<>();
    static HashMap<String, Table> routingTable = new HashMap<>();
    static Table sync = new Table("nothing", 0);
    static HashMap<String, Long> lastHeard = new HashMap<>();
    static int infinity = 9999;
    static HashMap<Integer, String> portToIp = new HashMap<>();


    /**
     * This method listens for a packet, if received it updates the routing table
     * @throws IOException
     */
    public static void receiver() throws IOException {

        byte[] inComingMessage = new byte[1024];
        DatagramPacket incoming = new DatagramPacket(inComingMessage, inComingMessage.length);
        socketReceive.setSoTimeout(10000);
        try {
            //listen for packets
            socketReceive.receive(incoming);
        }
        catch(SocketTimeoutException e) {
            //timeout if no neighbors respond
            System.out.println("This router has been isolated");
            System.exit(0);
        }
        String incomingMessage = new String(incoming.getData());
        lastHeard.put(portToIp.get(incoming.getPort()),System.currentTimeMillis());

        //check when you last heard from all neighbors, if it is greater
        //than a particular threshold, shut down the router

        for(String neighbor : lastHeard.keySet()) {
            Long currentTime = System.currentTimeMillis();
            if(currentTime - lastHeard.get(neighbor)>3000)
                triggeredUpdates(neighbor);
        }


        List<String> bestEntry = Arrays.asList(incomingMessage.split(";"));


        //receive the message and update the routing tables
        for (String entry : bestEntry) {

            List<String> oneLine = Arrays.asList(entry.split(","));

            if (oneLine.size() == 4) {
                String destination = oneLine.get(0);
                String nextHop = oneLine.get(1);
                int newCost = Integer.parseInt(oneLine.get(2));
                String subnetMask = oneLine.get(3);

                if(nextHop.equals(Receiver.ip)) {
                    newCost = infinity;
                }

                if (routingTable.containsKey(destination))      //if destination already there in table just update it
                {
                    boolean flag = false;
                    Table table = routingTable.get(destination);
                    HashMap<String, Integer> nextHops = table.getNextHops();
                    int currentCost;

                    if (!nextHops.containsKey(portToIp.get(incoming.getPort()))) {
                        if(newCost>= infinity)
                            nextHops.put(portToIp.get(incoming.getPort()), newCost);
                        else
                            nextHops.put(portToIp.get(incoming.getPort()), newCost + neighbor.get(portToIp.get(incoming.getPort())));
                        flag = true;
                    }

                    if (!flag) {

                        currentCost = nextHops.get(portToIp.get(incoming.getPort()));

                        if (newCost + neighbor.get(portToIp.get(incoming.getPort())) != currentCost)
                        {
                            if(newCost >= infinity)
                            {
                                nextHops.put(portToIp.get(incoming.getPort()), newCost);
                                flag = true;
                            }
                            else {
                                nextHops.put(portToIp.get(incoming.getPort()), newCost + neighbor.get(portToIp.get(incoming.getPort())));
                                flag = true;
                            }
                        }
                    }
                } else {
                    Table tab = new Table(portToIp.get(incoming.getPort()), newCost + neighbor.get(portToIp.get(incoming.getPort())));
                    routingTable.put(destination, tab);
                    tab.subnetMask = subnetMask;
                }
            }
        }
    }

    /**
     * This method prints the routing table
     */
    public static void printTable(){

        System.out.println("Routing table for IP " + Receiver.ip);
        System.out.println();

        System.out.println("Destination \t" + "Subnet Mask\t"+ "Next Hop \t" + "Distance");

        for (String dest : routingTable.keySet()) {
            Table tab = routingTable.get(dest);
            HashMap<String, Integer> nextHopp = tab.getNextHops();
            String savenxthop = "";
            int min = 100000000;

            for (String nex : nextHopp.keySet()) {

                if (nextHopp.get(nex) < min) {
                    savenxthop = nex;
                    min = nextHopp.get(nex);
                }

            }
            System.out.print(calculateCIDR(dest, tab.subnetMask) +"\t");
            System.out.print(tab.subnetMask +"\t");
            System.out.print(calculateCIDR(savenxthop, tab.subnetMask) +"\t");
            System.out.print(nextHopp.get(savenxthop) + "\t");
            System.out.println();

        }
        System.out.println();
        System.out.println();
        System.out.println("------------------------------------------------------------------------------");
    }


    /**
     * This method extracts the network prefix given an ip address
     * and a subnet mask
     *
     * @param destination IP address
     * @param subnetMask  subnet mask
     * @return
     */
    public static String calculateCIDR(String destination, String subnetMask) {

        String[] dest = destination.split("\\.");
        String[] subnet = subnetMask.split("\\.");

        String result = "";


        for (int i = 0; i < dest.length; i++) {
            int temp = Integer.parseInt(dest[i]) & Integer.parseInt(subnet[i]);
            result += temp;
            if(i!=3)
                result += ".";
        }

        return result;
    }


    /**
     * This method handles what happens when a neighbor fails
     * It performs the necessary changes in the routing table
     * and sends the updates to its neighbors immediately
     *
     * @param deadNeighbor IP address of neighbor who died
     */
    public static void triggeredUpdates(String deadNeighbor)
    {
        neighbor.put(deadNeighbor, infinity);
        System.out.println("The router with IP "+deadNeighbor+" has gone down");

        for(String destination : routingTable.keySet()) {
            Table toBeModified = routingTable.get(destination);
            if(destination.equals(deadNeighbor)) {
                for(String everyNextHop : toBeModified.nextHop.keySet()) {
                    toBeModified.nextHop.put(everyNextHop, infinity);
                }
            }
            else
                toBeModified.nextHop.put(deadNeighbor,infinity);
        }

        //send updates to all neighbors
        for (String ip : Receiver.neighbor.keySet()) {
            try {
                Sender.broadcast(Receiver.neighborPort.get(ip), ip, Receiver.routingTable, Receiver.socketReceive);

            } catch (IOException e) {

                e.printStackTrace();
            }
        }

    }


    @Override
    public void run() {

        while(true) {
            synchronized (Receiver.sync) {
                sync.notify();

                try {
                    receiver();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(1000);
                }

                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Receiver.printTable();

                try {
                    sync.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}