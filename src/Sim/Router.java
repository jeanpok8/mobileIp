package Sim;

import java.util.HashMap;
import java.util.Map;

// This class implements a simple router

public class Router extends SimEnt {

	private RouteTableEntry[] _routingTable;
	private int _interfaces; // number of interfaces
	protected static SimEnt serialInterface;// define a serial interface
	private int _now = 0;
	private SimEnt _peer;
	private Map<SimEnt, SimEnt> temporalTable;// routing table for
	public String _name;
	SimEnt _router;
	
	// The foreign and home agent
	private NetworkAddr mHAAddr;

	// When created, number of interfaces are defined

	Router(int interfaces, String name) {
		_routingTable = new RouteTableEntry[interfaces];
		_interfaces = interfaces;
		_name = name;
	}

	public void setPeer(SimEnt peerer)

	{
		_peer = peerer;

		if (_peer instanceof Link) {
			((Link) _peer).setConnector(this);
		}
	}

	/*This method connects links to the router and also informs the
	router of the host connects to the other end of the link
    */
	public void connectInterface(int interfaceNumber, SimEnt link, SimEnt node)

	{
		if (interfaceNumber < _interfaces) {
			_routingTable[interfaceNumber] = new RouteTableEntry(link, node);
		} else
			System.out.println("Trying to connect to port not in router");
		((Link) link).setConnector(this);
	}

	public void serialInterface(int interfaceNumber, SimEnt _serialInterface, SimEnt router) {
		_router = router;
		serialInterface = _serialInterface;
		temporalTable = new HashMap<SimEnt, SimEnt>();

		if (interfaceNumber <= 2) {
			temporalTable.put(_router, serialInterface);
			System.out.print(_name + " Connected " + "\n");
		}
		((Link) serialInterface).setConnector(this);
     }

	/*
	 * This method searches for an entry in the routing table that matches the
	 * network number in the destination field of a messages. The link
	 * represents that network number is returned .
	 */

	private SimEnt getInterface(int networkAddress) {
		SimEnt routerInterface=null;
		for (int i = 0; i < _interfaces; i++)
			if (_routingTable[i] != null) {
				if (((Node) _routingTable[i].node()).getAddr().networkId() == networkAddress) {
					routerInterface = _routingTable[i].link();
				}
			}
		return routerInterface;
	}

	/* When messages are received at the router this method is called */

	public void recv(SimEnt source, Event event) {

		if (event instanceof Message) {
			Message msg = (Message) event;

			if (getInterface(msg.destination().networkId()) != null) {
				SimEnt sendNext = getInterface(msg.destination().networkId());
				send(sendNext, event, _now);
				System.out.println(_name + " handles packet with seq: " + msg.seq()+
						" from node: "+msg.source().networkId()+"." + msg.source().nodeId()
						+" to node "+msg.destination().networkId()+"."+msg.destination().nodeId());
                       System.out.println(_name + " sends to node: " + msg.destination().networkId()+"." +
                    		   msg.destination().nodeId());
			} else {
				System.out.println(_name + " handles packet with seq: " + msg.seq() + " from node:"
						+ msg.source().networkId() + "." + msg.source().nodeId());
				System.out.println(_name + "-->forwards packet, to the serial Link "
						+" at time "+SimEngine.getTime());
				send(temporalTable.get(_router), event,SimEngine.getTime());
			}
		}
		// when the router get a solicitation message it will reply with 
		// a router solicitation message carring a network address.(similar to DHCP)
		// then it will create a RA and sent it to the source of the RC
		if (event instanceof ICMPRouterSolicitation) {
			System.out.println(_name + " receices a ICMP solicitation msg from node: "+
		((ICMP) event).source().networkId() + "." + ((ICMP) event).source().nodeId()
		+" at time "+SimEngine.getTime());
			ICMPRouterSolicitation mRS = (ICMPRouterSolicitation) event;
			
			ICMPRouterAdvertisement mRA = new ICMPRouterAdvertisement(new NetworkAddr(0, 0), mRS.source());
			// add the HA address to the message if there is any connected to it.
			if(mHAAddr != null){
				mRA.setmRouterHasHA(true);
				mRA.setmHA(mHAAddr);
			}
			// add a address for the node
			mRA.setCoA(new NetworkAddr(9, 9));
			
			// send the message to the source 
			SimEnt sendNext = getInterface(mRS.source().networkId());
			send(sendNext, mRA, _now);
			System.out.println(_name + " send a router advertisiment msg to node: "+
					((ICMP) event).source().networkId() + "." + ((ICMP) event).source().nodeId()
					+" at time "+SimEngine.getTime());
			}
		if (event instanceof ICMPBindingUpdate || event instanceof ICMPBindingAck) {
			ICMP mICMP = (ICMP) event;

			if (getInterface(mICMP.destination().networkId()) != null) {
				SimEnt sendNext = getInterface(mICMP.destination().networkId());
				send(sendNext, event, _now);
				System.out.println(_name + " handles ICMP msg " + 
						" from node: "+mICMP.source().networkId()+"." + mICMP.source().nodeId()
						+" to node "+mICMP.destination().networkId()+"."+mICMP.destination().nodeId());
                       System.out.println(_name + " sends to node: " + mICMP.destination().networkId()+"." +
                    		   mICMP.destination().nodeId());
			} else {
				System.out.println(_name + " handles ICMP msg " + " from node:"
						+ mICMP.source().networkId() + "." + mICMP.source().nodeId());
				System.out.println(_name + "-->forwards packet, to the serial Link "
						+" at time "+SimEngine.getTime());
				send(temporalTable.get(_router), event,SimEngine.getTime());
			}
		}
	}
	// Connect the home agent to the Router.
	public void connectHA(HomeAgent mAgent){
		mHAAddr = mAgent._id;
	}
}