package Sim;

// An example of how to build a topology and starting the simulation engine

public class Run {
	public static void main(String[] args) throws InterruptedException {
		// Creates two links
		Link link1 = new Link(1);
		Link link2 = new Link(2);
		Link link3 = new Link(3);// serial link.
		Link link4 = new Link(4);
		
		
		
		
		Node host1 = new Node(1, 2);
		host1.setPeer(link1);
		HomeAgent mHA = new HomeAgent(3,1);
		mHA.setPeer(link2);
		
		NetworkAddr mHAAddress = new NetworkAddr(3, 1);
		MobileNode mMN = new MobileNode(4, 1, 22545, mHAAddress);
		mMN.setPeer(link4);
		
		// Set the serial interface
		Router routeNodeA = new Router(4, "Router A");
		Router routeNodeB = new Router(4, "Router B");
		routeNodeA.setPeer(link3);
		routeNodeB.setPeer(link3);
		routeNodeA.serialInterface(3, link3, routeNodeB);
		routeNodeB.serialInterface(3, link3, routeNodeA);
		//----------------------------------------------
		
		
		routeNodeA.connectInterface(0, link1, host1);
		routeNodeA.connectInterface(1, link2, mHA);
		routeNodeB.connectInterface(0, link4, mMN);
		
		// pass HA and FA addresses to the routers
		routeNodeA.connectHA(mHA);
		
		host1.StartSending(1, 1, 2, 20, 1);
	
		
		mMN.SendRouterSolicitation(5);
		mMN.disconnect(5);
		routeNodeA.reConnect(link4,2,mMN,11);//reconnect MN to the router A
	
	
		// Start the simulation engine and of we go!
		Thread t = new Thread(SimEngine.instance());

		t.start();
		try {
			t.join();
		} catch (Exception e) {
			System.out.println("The motor seems to have a problem, time for service?");
		}

	}
}
