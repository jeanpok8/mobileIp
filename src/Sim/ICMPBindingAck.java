package Sim;

public class ICMPBindingAck extends ICMP{
	/**
	 * The binding ACK contains the following information
	 * A sequence number
	 * Binding status (successful or not)
	 * */
	private int mMNIdentity;
	
	private boolean mBindingSuccessful;
	ICMPBindingAck(NetworkAddr from, NetworkAddr to, int mMNIdentity, boolean mBindingSuccessful) {
		super(from, to);
		this.mMNIdentity 		= mMNIdentity;
		this.mBindingSuccessful = mBindingSuccessful;
	}
	public boolean ismBindingSuccessful() {
		return mBindingSuccessful;
	}
	public int getmIdentity() {
		return mMNIdentity;
	}
}
