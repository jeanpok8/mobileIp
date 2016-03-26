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
	private NetworkAddr mFAAddr;
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

			if (getInterface(((Message) event).destination().networkId()) != null) {
				SimEnt sendNext = getInterface(((Message) event).destination().networkId());
				send(sendNext, event, _now);
				System.out.println(_name + " handles packet with seq: " + ((Message) event).seq()+
						" from node: "+((Message) event).source().networkId()+"." + ((Message) event).source().nodeId()
						+" to node "+((Message) event).destination().networkId()+"."+((Message) event).destination().nodeId());
                       System.out.println(_name + " sends to node: " + ((Message) event).destination().networkId()+"." +
						((Message) event).destination().nodeId());

			} else {

				System.out.println(_name + " handles packet with seq: " + ((Message) event).seq() + " from node:"
						+ ((Message) event).source().networkId() + "." + ((Message) event).source().nodeId());
				System.out.println(_name + "-->forwards packet, to the serial Link "+" at time "+SimEngine.getTime());
				send(temporalTable.get(_router), event,SimEngine.getTime());
			}
		}
		// when the router get a solicitation message it will look
		// if it has a foreign or home agent connected to it
		// then it will create a RA and sent it to the source of the RC
		if (event instanceof ICMPRouterSolicitation) {
			ICMPRouterSolicitation mRC = (ICMPRouterSolicitation) event;
			ICMPRouterAdvertisement mRA = new ICMPRouterAdvertisement(new NetworkAddr(0, 0), mRC.source());
			if(mHAAddr != null){
				mRA.setmRouterHasHA(true);
				mRA.setmHA(mHAAddr);
			}
			if(mFAAddr != null){
				mRA.setmRouterHasFA(true);
				mRA.setmFA(mFAAddr);
			}
			// send the message to the source 
		}
	}
	// Connect the foreign agent to the Router.
	public void connectFA(ForeignAgent mAgent){
		mFAAddr = mAgent._id;
	}
	// Connect the home agent to the Router.
	public void connectHA(HomeAgent mAgent){
		mHAAddr = mAgent._id;
	}
}
