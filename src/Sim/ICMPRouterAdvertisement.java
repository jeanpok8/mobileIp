package Sim;

public class ICMPRouterAdvertisement extends ICMP{
	/**
	 * This class represent the routing advertisement messages
	 * The message carry the following information
	 * Does the router has a FA connected to it, FA address if so
	 * Does the router has a HA connected to it, HA address if so
	 * */
	private boolean mRouterHasFA;
	private boolean mRouterHasHA;
	private NetworkAddr mHA;
	private NetworkAddr mFA;
	
	ICMPRouterAdvertisement(NetworkAddr from, NetworkAddr to) {
		super(from, to);
		// TODO Auto-generated constructor stub
	}

	public boolean ismRouterHasFA() {
		return mRouterHasFA;
	}

	public void setmRouterHasFA(boolean mRouterHasFA) {
		this.mRouterHasFA = mRouterHasFA;
	}

	public boolean ismRouterHasHA() {
		return mRouterHasHA;
	}

	public void setmRouterHasHA(boolean mRouterHasHA) {
		this.mRouterHasHA = mRouterHasHA;
	}

	public NetworkAddr getmHA() {
		return mHA;
	}

	public void setmHA(NetworkAddr mHA) {
		this.mHA = mHA;
	}

	public NetworkAddr getmFA() {
		return mFA;
	}

	public void setmFA(NetworkAddr mFA) {
		this.mFA = mFA;
	}
}
