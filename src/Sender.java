import java.io.IOException;
import java.net.*;
import java.util.HashMap;

/**
 * This class acts as a Sender thread and continuously sends route updates
 * to its neighbors.
 *
 * @author Renzil Dourado rd9012
 */
public class Sender implements Runnable{

    /**
     * This method broadcasts its routing table to all its neighbors
     *
     * @param neighborPort          The port on which the neighbor is listening
     * @param neighborIp            The IP of the neighbor
     * @param routingTable          The routing table hash map
     * @param socketReceive         The socket to be sent using
     * @throws SocketException
     * @throws UnknownHostException
     * @throws IOException
     */
    public static void broadcast(int neighborPort, String neighborIp, HashMap<String, Table> routingTable, DatagramSocket socketReceive) throws SocketException, UnknownHostException, IOException {

        InetAddress IPAddress = InetAddress.getByName(neighborIp);
        byte[] outGoingMessage = new byte[1024];
        String message = "";
        String finalMessage = "";

        for (String destination : routingTable.keySet()) {
            int min = 10000;
            for (String nexthop : routingTable.get(destination).nextHop.keySet()) {
                int cost = routingTable.get(destination).nextHop.get(nexthop);

                //searching for the best path to a particular destination to be sent to its neighbor
                if (!neighborIp.equals(destination) && cost <min) {

                    message = destination + "," + nexthop + "," + cost;
                    min = cost;
                }

            }
            //sending the message as destination, next hop, cost to reach next hop
            finalMessage += message + ","+routingTable.get(destination).subnetMask +";";

        }

        outGoingMessage = finalMessage.getBytes();
        DatagramPacket outgoing = new DatagramPacket(outGoingMessage, outGoingMessage.length, IPAddress, neighborPort);
        socketReceive.send(outgoing);

    }


    @Override
    public void run() {

        while(true) {
            synchronized (Receiver.sync) {
                Receiver.sync.notify();

                //broadcasting to all neighbors
                for (String ip : Receiver.neighbor.keySet()) {

                    try {

                        broadcast(Receiver.neighborPort.get(ip), ip, Receiver.routingTable, Receiver.socketReceive);

                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                }
                try {
                    Receiver.sync.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
