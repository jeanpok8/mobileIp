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
				System.out.println(_name + " handles packet with seq: " + ((Message) event).seq() + " from node: "
						+ ((Message) event).source().networkId() + "." + ((Message) event).source().nodeId());
                       System.out.println(_name + " Router sends to node: " + ((Message) event).destination().networkId() + "."
						+ ((Message) event).destination().nodeId());

			} else {

				System.out.println(_name + " handles packet with seq: " + ((Message) event).seq() + " from node:"
						+ ((Message) event).source().networkId() + "." + ((Message) event).source().nodeId());
				System.out.println(_name + "-->forwards packet, to the serial Link "+" at time "+SimEngine.getTime());
				send(temporalTable.get(_router), event,SimEngine.getTime());
			}
		}
	}
}
