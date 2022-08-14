package project.alarmservice;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import project.jmdnsutil.JmDnsUtil;

public class AlarmServiceServer {
	public static final String JMDNS_SERVICE_TYPE = "_alarmservice._grpc.local.";
	private Server server;
	private AtomicBoolean serverStarting;

	public AtomicBoolean getServerStarting() {
		return serverStarting;
	}

	public void setServerStarting(AtomicBoolean serverStarting) {
		this.serverStarting = serverStarting;
	}

	public void start(int port) throws IOException, InterruptedException {
		// calling jmdns util class to register service
		JmDnsUtil.RegisterService(JMDNS_SERVICE_TYPE, "AlarmService", port, "alarmservice=grpc");

		// starting service in the provided port
		server = ServerBuilder.forPort(port).addService(new AlarmServiceServer.AlarmServiceImpl()).build().start();
		System.out.println("AlarmService Server Started, listening on port: " + port);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("*** shutting down ***");
				try {
					AlarmServiceServer.this.stop();
				} catch (InterruptedException e) {
					e.printStackTrace(System.out);
				}
				System.out.println("*** server shut down ***");
			}
		});
	}

	private void stop() throws InterruptedException {
		if (server != null) {
			server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
		}
	}

	public void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			server.awaitTermination();
		}
	}// End of Server Implementation

	/** SERVICE IMPLEMENTATION */

	static class AlarmServiceImpl extends AlarmServiceGrpc.AlarmServiceImplBase {
		private HashMap<String, Boolean> homes = new HashMap<>();

		@Override
		public void turnOnAlarm(Home request, StreamObserver<TurnOnAlarmResponse> responseObserver) {
			System.out.println("Trying to Turn on Alarm for Home: " + request.getHomeIdentifier());
			TurnOnAlarmResponse response;
			if (homes.containsKey(request.getHomeIdentifier())) {
				Boolean alarmStatus = homes.get(request.getHomeIdentifier());
				if (alarmStatus) {
					response = TurnOnAlarmResponse.newBuilder().setHome(request)
							.setTurnOnMessage("Alarm Already Turned On").build();
				} else {
					homes.put(request.getHomeIdentifier(), true);
					response = TurnOnAlarmResponse.newBuilder().setHome(request).setTurnOnMessage("Alarm Turned On")
							.build();
				}
			} else {
				homes.put(request.getHomeIdentifier(), true);
				response = TurnOnAlarmResponse.newBuilder().setHome(request)
						.setTurnOnMessage("Home Registered & Alarm Turned On").build();
			}
			System.out.println("[Homes Alarms] " + homes.toString());
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		}

		@Override
		public void turnOffAlarm(Home request, StreamObserver<TurnOffAlarmResponse> responseObserver) {
			System.out.println("Trying to Turn off Alarm for Home: " + request.getHomeIdentifier());
			TurnOffAlarmResponse response = null;
			if (homes.containsKey(request.getHomeIdentifier())) {
				Boolean alarmStatus = homes.get(request.getHomeIdentifier());
				if (!alarmStatus) {
					response = TurnOffAlarmResponse.newBuilder().setHome(request)
							.setTurnOffMessage("Alarm Already Turned Off").build();
				} else {
					homes.put(request.getHomeIdentifier(), false);
					response = TurnOffAlarmResponse.newBuilder().setHome(request).setTurnOffMessage("Alarm Turned Off")
							.build();
				}
			} else {
				homes.put(request.getHomeIdentifier(), false);
				response = TurnOffAlarmResponse.newBuilder().setHome(request)
						.setTurnOffMessage("Home Registered & Alarm Turned Off").build();
			}
			System.out.println("[Homes Alarms] " + homes.toString());
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		}

		@Override
		public void panicButton(Home request, StreamObserver<PanicButtonResponse> responseObserver) {
			System.out.println("Panic Button Pressed: calling urgent service");
			responseObserver.onNext(PanicButtonResponse.newBuilder()
					.setInformation("Will reach out to the Police in a momment...").build());
			for (int i = 0; i < ThreadLocalRandom.current().nextInt(3, 10); i++) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				responseObserver.onNext(PanicButtonResponse.newBuilder()
						.setInformation(
								"Talking with the Police, we will notify once the patrol has been dispatched...")
						.build());
			}
			responseObserver.onNext(
					PanicButtonResponse.newBuilder().setInformation("The Police patrol has been dispatched.").build());
			responseObserver.onCompleted();
		}
	}
}
