package Sim;

import java.util.HashMap;
import java.util.Map;

/* This class implements a simple router*/

public class Router extends SimEnt {

	private RouteTableEntry[] _routingTable;
	private int _interfaces; // number of interfaces
	protected static SimEnt serialInterface;// define a serial interface
	private int _now = 0;
	private SimEnt _peer;
	private Map<SimEnt, SimEnt> temporalTable;// routing table for
	public String _name;
	private NetworkAddr CoA = new NetworkAddr(9, 9);
	SimEnt _router;
	private NetworkAddr mHAAddr;// The foreign and home agent
	private SimEnt mR_peer;
	private int mR_interfaceNumber;
	private SimEnt mR_node;

	/* When created, number of interfaces are defined */

	Router(int interfaces, String name) {
		_routingTable = new RouteTableEntry[interfaces];
		_interfaces = interfaces;
		_name = name;
	}

	/* check if the peer is the link and connect it to the router. */

	public void setPeer(SimEnt peerer)

	{
		_peer = peerer;

		if (_peer instanceof Link) {
			((Link) _peer).setConnector(this);
		}
	}

	/*
	 * This method connects links to the router and also informs the router of
	 * the host connects to the other end of the link
	 */
	public void connectInterface(int interfaceNumber, SimEnt link, SimEnt node)

	{
		if (interfaceNumber < _interfaces) {
			_routingTable[interfaceNumber] = new RouteTableEntry(link, node);
		} else
			System.out.println("Trying to connect to port not in router");
		((Link) link).setConnector(this);
		System.out.println("Something connects to the router");
	}

	/**
	 * This method connects two routers using a serial link and put the used
	 * interface to a hashmap, the data will be used afterwards
	 * 
	 * @param interfaceNumber
	 * @param _serialInterface
	 * @param router
	 */
	public void serialInterface(int interfaceNumber, SimEnt _serialInterface, SimEnt router) {
		_router = router;
		serialInterface = _serialInterface;
		temporalTable = new HashMap<SimEnt, SimEnt>();

		if (interfaceNumber <= 3) {
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
		SimEnt routerInterface = null;
		for (int i = 0; i < _interfaces; i++)
			if (_routingTable[i] != null) {
				if (((Node) _routingTable[i].node()).getAddr().networkId() == networkAddress) {
					routerInterface = _routingTable[i].link();
				}
			}
		return routerInterface;
	}

	/**
	 * This method searches for an entry in the routing table that matches the
	 * network number of the source field of a message comming from the
	 * disconnecting node and remove it.
	 * 
	 * @param networkAddress
	 */
	private void removeInterface(int networkAddress) {
		for (int i = 0; i < _interfaces; i++)
			if (_routingTable[i] != null) {
				if (((Node) _routingTable[i].node()).getAddr().networkId() == networkAddress) {
					_routingTable[i] = null;
				}
			}
	}

	/**
	 * This method handles the ICMP message routing between entities.
	 * 
	 * @param event
	 */
	private void routeICMP(Event event) {
		ICMP mICMP = (ICMP) event;

		if (getInterface(mICMP.destination().networkId()) != null) {
			SimEnt sendNext = getInterface(mICMP.destination().networkId());
			send(sendNext, event, _now);
			System.out.println(_name + " handles ICMP msg " + " from node: " + mICMP.source().networkId() + "."
					+ mICMP.source().nodeId() + " to node " + mICMP.destination().networkId() + "."
					+ mICMP.destination().nodeId());
			System.out.println(
					_name + " sends to node: " + mICMP.destination().networkId() + "." + mICMP.destination().nodeId());
		} else {
			System.out.println(_name + " handles ICMP msg " + " from node:" + mICMP.source().networkId() + "."
					+ mICMP.source().nodeId());
			System.out.println(_name + " forwards packet, to the serial Link " + " at time " + SimEngine.getTime());
			send(temporalTable.get(_router), event, SimEngine.getTime());
		}
	}

	/**
	 * This methods reconnect a node at a precised time.
	 * 
	 * @param peer
	 * @param interfaceNumber
	 * @param node
	 * @param mTime
	 */
	public void reConnect(SimEnt peer, int interfaceNumber, SimEnt node, int mTime) {
		mR_peer = peer;
		mR_interfaceNumber = interfaceNumber;
		mR_node = node;
		send(this, new TimerEvent(), mTime);
	}

	// Connect the home agent to the Router.
	public void connectHA(HomeAgent mAgent) {
		mHAAddr = mAgent._id;
	}

	/* When messages are received at the router this method is called */
	
	public void recv(SimEnt source, Event event) {
		if (event instanceof TimerEvent) {
			// we do the reconnect here
			_peer = mR_peer;
			if (_peer instanceof Link || mR_interfaceNumber < _interfaces) {
				((Link) _peer).setConnector(this);
				_routingTable[mR_interfaceNumber] = new RouteTableEntry(mR_peer, mR_node);
				System.out.println("A Node reconnect to the " + _name);
			} else
				System.out.println("Trying to connect to port not in router");
			((Link) mR_peer).setConnector(this);
		}

		if (event instanceof Message) {
			Message msg = (Message) event;

			if (getInterface(msg.destination().networkId()) != null) {
				SimEnt sendNext = getInterface(msg.destination().networkId());
				send(sendNext, event, _now);
				System.out.println(_name + " handles packet with seq: " + msg.seq() + " from node: "
						+ msg.source().networkId() + "." + msg.source().nodeId() + " to node "
						+ msg.destination().networkId() + "." + msg.destination().nodeId());
				System.out.println(
						_name + " sends to node: " + msg.destination().networkId() + "." + msg.destination().nodeId());
			} else  {
				System.out.println(_name + " handles packet with seq: " + msg.seq() + " from node:"
						+ msg.source().networkId() + "." + msg.source().nodeId());
				System.out
						.println(_name + "-->forwards packet, to the serial Link " + " at time " + SimEngine.getTime());
				send(temporalTable.get(_router), event, SimEngine.getTime());
			}

			}
		/*
		 * when the router get a solicitation message it will reply with a
		 * router advertisement message carring a network address.(similar to
		 * DHCP) then it will create a RA and sent it to the source of the RC
		 */
		if (event instanceof ICMPRouterSolicitation) {
			System.out.println(
					_name + " receives a ICMP solicitation msg from node: " + ((ICMP) event).source().networkId() + "."
							+ ((ICMP) event).source().nodeId() + " at time " + SimEngine.getTime());
			ICMPRouterSolicitation mRS = (ICMPRouterSolicitation) event;

			ICMPRouterAdvertisement mRA = new ICMPRouterAdvertisement(new NetworkAddr(0, 0), mRS.source());
			// add the HA address to the message if there is any connected to
			// it.
			if (mHAAddr != null) {
				mRA.setmRouterHasHA(true);
				mRA.setmHA(mHAAddr);
			}
			// add a address for the node
			mRA.setCoA(CoA);

			// send the message to the source
			SimEnt sendNext = getInterface(mRS.source().networkId());
			send(sendNext, mRA, _now);
			System.out
					.println(_name + " send a router advertisiment msg to node: " + ((ICMP) event).source().networkId()
							+ "." + ((ICMP) event).source().nodeId() + " at time " + SimEngine.getTime());
		}

		if (event instanceof ICMPBindingUpdate || event instanceof ICMPBindingAck) {
			routeICMP(event);
		}
		if (event instanceof ICMPDisconnect) {
			ICMPDisconnect ICMP = (ICMPDisconnect) event;
			if (ICMP.destination().networkId() == 0) {
				removeInterface(ICMP.destination().networkId());
				System.out.print("Node " + ((ICMP) event).source().networkId() + "." + ((ICMP) event).source().nodeId()
						+ " Disconnected " + " from " + _name + " at time " + SimEngine.getTime() + "\n");
			} else {
				routeICMP(event);
			}
		}

	}
}
