package Sim;

import java.util.Hashtable;

public class HomeAgent extends Node{

	private Hashtable<Integer, NetworkAddr> mBindingTable = new Hashtable<>();
	
	public HomeAgent(int network, int node) {
		super(network, node);
		// TODO Auto-generated constructor stub
	}
	public void recv(SimEnt src, Event ev)
	{
		if (ev instanceof ICMP)
		{
			System.out.println("HA "+_id.networkId()+ "." + _id.nodeId() +
					" receives an ICMP message" + "at time "+SimEngine.getTime());
			if(ev instanceof ICMPBindingUpdate){
				ICMPBindingUpdate ICMP = (ICMPBindingUpdate) ev;
				System.out.println("HA "+_id.networkId()+ "." + _id.nodeId() +
						" receives a binding update from "+ 
						ICMP.source().networkId()+"." + ICMP.source().nodeId());
				// Add the MN to Table, {ID : CoA}
				mBindingTable.put(ICMP.getmIdentity(), ICMP.source());
				
				// Create a new ICMPBindingACK 
				ICMPBindingAck toMN = new ICMPBindingAck(_id,ICMP.source(),ICMP.getmIdentity(), true);
				
				// Forward the message to MN's Home agent
				send(_peer , toMN, 0);
				}
			}
		}
}