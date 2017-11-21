import java.util.HashMap;
/**
 * This class is used to store the routing table for a router.
 * @author Renzil Dourado rd9012
 */
public class Table {

    HashMap<String, Integer> nextHop = new HashMap<>();
    String subnetMask = "";

    public Table(String nextHop, int cost){

        //the hash map next hop has key = ip address of next hop, value = distance
        this.nextHop.put(nextHop, cost);
    }

    public HashMap<String, Integer> getNextHops()
    {
        return this.nextHop;
    }

}
