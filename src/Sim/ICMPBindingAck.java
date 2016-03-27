package Sim;

public class ICMPBindingAck extends ICMP{
	/**
	 * The binding ACK contains the following information
	 * A sequence number
	 * Binding status (successful or not)
	 * */
	private int mMNIdentity;
	private boolean mBindingStatus;
	
	ICMPBindingAck(NetworkAddr from, NetworkAddr to, int mMNIdentity, boolean mBindingStatus) {
		super(from, to);
		this.mMNIdentity 		= mMNIdentity;
		this.mBindingStatus = mBindingStatus;
	}
	public int getmIdentity() {
		return mMNIdentity;
	}
	public boolean getmBindingStatus() {
		return mBindingStatus;
	}
	public void setmBindingStatus(boolean mBindingStatus) {
		this.mBindingStatus = mBindingStatus;
	}
}
