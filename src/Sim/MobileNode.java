package Sim;

public class MobileNode extends Node {
	/**
	 * This class is MN class, each MN has an identity to authenticate with the
	 * HA and a Network address which hold the MN's home agent address
	 */
	private NetworkAddr mHA;
	private int mIdentity;

	public MobileNode(int network, int node, int mIdentity, NetworkAddr mHA) {
		super(network, node);
		this.mIdentity = mIdentity;
		this.mHA = mHA;
	}

	/**
	 * This methpd notifies a Router that it wants to get disconnected from it.
	 * 
	 * @param mTime
	 */
	public void disconnect(int mTime) {
		ICMPDisconnect dmsg = new ICMPDisconnect(_id, new NetworkAddr(0, 0));
		ICMPDisconnect dmsgThome = new ICMPDisconnect(_id, mHA);

		send(_peer, dmsg, mTime);
		send(_peer, dmsgThome, mTime);
	}

	@Override
	public void recv(SimEnt src, Event ev) {
		// send a solicitation
		if (ev instanceof TimerEvent) {

		}
		if (ev instanceof Tunnel) {
			System.out.println("Node " + _id.networkId() + "." + _id.nodeId() + " received a message from HA seq: "
					+ ((Message) ev).seq() + " at time " + SimEngine.getTime());
		}
		if (ev instanceof ICMP) {
			if (ev instanceof ICMPRouterAdvertisement) {
				ICMPRouterAdvertisement mRA = (ICMPRouterAdvertisement) ev;
				_id = mRA.getCoA();
				System.out.println("The mobile node received a router advertisement with a CoA " + _id.networkId() + ","
						+ _id.nodeId() + " at time " + SimEngine.getTime());
				SendBindingUpdate();
				System.out.println("MN sent a Binding Update message to home agent " + mHA.networkId() + ","
						+ mHA.nodeId() + " at time " + SimEngine.getTime());
			}
		}
		if (ev instanceof ICMPBindingAck) {
			ICMPBindingAck mICMP = (ICMPBindingAck) ev;
			System.out.println("MN received a binding ACK, the binding status is " + mICMP.getmBindingStatus());
		}
	}

	private void SendBindingUpdate() {
		ICMPBindingUpdate mUP = new ICMPBindingUpdate(_id, mHA, mIdentity);
		send(_peer, mUP, 0);
	}

	public void SendRouterSolicitation(int mDelay) {
		ICMPRouterSolicitation RS = new ICMPRouterSolicitation(_id, new NetworkAddr(0, 0));
		send(_peer, RS, mDelay);
	}

	public NetworkAddr getmHA() {
		return mHA;
	}

	public void setmHA(NetworkAddr mHA) {
		this.mHA = mHA;
	}

	public int getmIdentity() {
		return mIdentity;
	}

	public void setmIdentity(int mIdentity) {
		this.mIdentity = mIdentity;
	}
}
