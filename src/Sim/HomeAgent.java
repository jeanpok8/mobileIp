package Sim;

public class HomeAgent extends Node{

	private NetworkAddr mMN ;
	
	public HomeAgent(int network, int node) {
		super(network, node);
		// TODO Auto-generated constructor stub
	}
	public void recv(SimEnt src, Event ev)
	{
		if (ev instanceof ICMP)
		{
			System.out.println("HA "+_id.networkId()+ "." + _id.nodeId() +
					" receives an ICMP message" + "at time "+SimEngine.getTime());
			if(ev instanceof ICMPBindingUpdate){
				ICMPBindingUpdate ICMP = (ICMPBindingUpdate) ev;
				System.out.println("HA "+_id.networkId()+ "." + _id.nodeId() +
						" receives a binding update from "+ 
						ICMP.source().networkId()+"." + ICMP.source().nodeId());
				// Add the MN to Table, {ID : CoA}
				//mBindingTable.put(ICMP.getmIdentity(), ICMP.source());
				mMN = ICMP.source();
				// Create a new ICMPBindingACK 
				ICMPBindingAck toMN = new ICMPBindingAck(_id,ICMP.source(),ICMP.getmIdentity(), true);
				
				// Forward the message to MN's Home agent
				send(_peer , toMN, 0);
				}
			}
		if(ev instanceof Tunnel){
			// take out the message and change the source address
			Message mMsg = ((Tunnel) ev).getmMessage();
			//Message mTempMessage = new Message(_id, mMsg._destination, mMsg._seq);
			// send the message to the corresponding node
			send(_peer, mMsg,0);
		}
		if(ev instanceof Message){
			Message mMsg = (Message) ev;
			Tunnel mTunnel = new Tunnel(_id, new NetworkAddr(4, 1), mMsg._seq, mMsg);
			send(_peer , mTunnel, 0);
			System.out.println("HA tunneld a message form "+ mMsg.source().networkId() +"."+ mMsg.source().nodeId());
		}
	}
	public NetworkAddr getmMN() {
		return mMN;
	}
	public void setmMN(NetworkAddr mMN) {
		this.mMN = mMN;
	}
}