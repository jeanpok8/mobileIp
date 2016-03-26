package Sim;

import java.util.Hashtable;

public class ForeignAgent extends Node{
	/**
	 * The foreign Agent has a visitor table {<node identity>:<node address>}
	 * the identity is a int number.
	 * The foreign agent receives binding update for a visitor MN, put the MN it its
	 * visitor table and forward the BU to MN's home agent 
	 * The foreign agent receives binding ACK form the HA and forward them to the MN
	 * */
	private Hashtable<Integer, NetworkAddr> mVisitorTable = new Hashtable<Integer, NetworkAddr>();
	public ForeignAgent(int network, int node) {
		super(network, node);
		// TODO Auto-generated constructor stub
	}
	public void recv(SimEnt src, Event ev)
	{
		if (ev instanceof ICMP)
		{
			System.out.println("FA "+_id.networkId()+ "." + _id.nodeId() +
					" receives an ICMP message" + "at time "+SimEngine.getTime());
			if(ev instanceof ICMPBindingUpdate){
				ICMPBindingUpdate ICMP = (ICMPBindingUpdate) ev;
				System.out.println("FA "+_id.networkId()+ "." + _id.nodeId() +
						" receives a binding update from "+ 
						ICMP.source().networkId()+"." + ICMP.source().nodeId());
				// Add the MN to the visitor table, {ID : NetworkAddress}
				mVisitorTable.put(ICMP.getmIdentity(), ICMP.source());
				
				// Create a new ICMPBinding update, the destentaion is HA now
				ICMPBindingUpdate toHA = new ICMPBindingUpdate(ICMP.source(), ICMP.getmHomeAddress(),
						ICMP.getmCoA(), ICMP.getmHomeAddress(), ICMP.getmIdentity());
				// Forward the message to MN's Home agent
				send(_peer , toHA, 0);
			}
			if(ev instanceof ICMPBindingAck){
				ICMPBindingAck ICMP = (ICMPBindingAck) ev;
				System.out.println("FA "+_id.networkId()+ "." + _id.nodeId() +
						" receives a binding ACK from "+ 
						ICMP.source().networkId()+"." + ICMP.source().nodeId());
				if(mVisitorTable.containsKey(ICMP.getmIdentity())){
					
				}
			}
			}
		}

}
