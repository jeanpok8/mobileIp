package Sim;

public class Tunnel extends Message{
	
	private Message mMessage;
	Tunnel(NetworkAddr from, NetworkAddr to, int seq, Message mMessage) {
		super(from, to, seq);
		this.mMessage = mMessage;
		// TODO Auto-generated constructor stub
	}
	public Message getmMessage() {
		return mMessage;
	}
}
