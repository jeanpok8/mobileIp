package Sim;

public class ICMPBindingUpdate extends ICMP{

	/**
	 * The Binding update message include the nodes current CoA and its identity
	 * It include the following information
	 * 
	 * A sequence number
	 * A CoA : Care of Address of the mobile node
	 * The message is sent to the HomeAgent
	 * */
	private NetworkAddr mCoA;
	private NetworkAddr mHomeAddress;
	private int 		mIdentity;
	
	ICMPBindingUpdate(NetworkAddr from, NetworkAddr to, NetworkAddr mCoA,NetworkAddr mHomeAddress, int mIdentity) {
		super(from, to);
		this.mCoA 			= mCoA;
		this.mHomeAddress 	= mHomeAddress;
		this.mIdentity 		= mIdentity;
	}
	public NetworkAddr getmCoA() {
		return mCoA;
	}
	public int getmIdentity() {
		return mIdentity;
	}
	public NetworkAddr getmHomeAddress() {
		return mHomeAddress;
	}
}