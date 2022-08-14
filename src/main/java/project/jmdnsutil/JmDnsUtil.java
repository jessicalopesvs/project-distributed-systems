package project.jmdnsutil;

import java.io.IOException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

public class JmDnsUtil {

	/**
	 * Registering Service with JmDNS
	 */
	public static void RegisterService(String serviceType, String serviceName, int port, String serviceDescription)
			throws IOException, InterruptedException {
		JmDNS jmdns = JmDNS.create("localhost");
		ServiceInfo serviceInfo = ServiceInfo.create(serviceType, serviceName, port, serviceDescription);
		jmdns.registerService(serviceInfo);
		// wait a bit before completing registration
		Thread.sleep(1000);
	}

	/**
	 * Discovering Service with JmDNS
	 */
	public static GrpcServiceListener DiscoverService(String serviceType) throws IOException, InterruptedException {
		JmDNS jmdns = JmDNS.create("localhost");
		GrpcServiceListener listener = new GrpcServiceListener();
		jmdns.addServiceListener(serviceType, listener);
		// wait a bit before completing registration
		Thread.sleep(5000);
		return listener;
	}

	/**
	 * JmDNS Listener Class
	 */
	public static class GrpcServiceListener implements ServiceListener {
		private int servicePort;

		@Override
		public void serviceAdded(ServiceEvent event) {
			System.out.println("Service added: " + event.getInfo());
		}

		@Override
		public void serviceRemoved(ServiceEvent event) {
			System.out.println("Service removed: " + event.getInfo());
		}

		@Override
		public void serviceResolved(ServiceEvent event) {
			System.out.println("Service resolved: " + event.getInfo());
			this.servicePort = event.getInfo().getPort();
		}

		public int getServicePort() {
			return servicePort;
		}
	}
}
