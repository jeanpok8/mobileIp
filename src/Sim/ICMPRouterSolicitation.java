package Sim;

public class ICMPRouterSolicitation extends ICMP{
	/**
	 * The router solicitation doesn't include any information, the router will intercept 
	 * any message from ICMPRouterSolicitation class and will reply by its capabilities
	 * to the message source
	 * */
	ICMPRouterSolicitation(NetworkAddr from, NetworkAddr to) {
		super(from, to);
		// TODO Auto-generated constructor stub
	}

}
