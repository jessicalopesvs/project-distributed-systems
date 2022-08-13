package project.cameraservice;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class CameraServiceServer {
	public static final String JMDNS_SERVICE_TYPE = "_cameraservice._tcp.local.";
	private Server server;

	public static void main(String[] args) throws IOException, InterruptedException {
		final CameraServiceServer server = new CameraServiceServer();
		server.start(50052);
		server.blockUntilShutdown();
	}

	public void start(int port) throws IOException, InterruptedException {
		/**
		 * Registering Service with JmDNS
		 */
		JmDNS jmdns = JmDNS.create("localhost");
		ServiceInfo serviceInfo = ServiceInfo.create(JMDNS_SERVICE_TYPE, "CameraService", port, "cameraservice=grpc");
		jmdns.registerService(serviceInfo);

		Thread.sleep(1000);

		server = ServerBuilder.forPort(port).addService(new CameraServiceImpl()).build().start();
		System.out.println("CameraService Server Started, listening on port: " + port);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("*** shutting down ***");
				try {
					CameraServiceServer.this.stop();
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
	}

	static class CameraServiceImpl extends CameraServiceGrpc.CameraServiceImplBase {
		@Override
		public void detectMovement(DetectionRequest request,
				StreamObserver<MovementDetectionResponse> responseObserver) {
			for (int i = 0; i < request.getDuration(); i++) {
				System.out.println("Detecting Movement for Room #" + request.getRoom().getRoomIdentifier());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				responseObserver
						.onNext(MovementDetectionResponse.newBuilder().setTimestamp((int) (new Date().getTime() / 1000))
								.setMovement(ThreadLocalRandom.current().nextBoolean()).build());
			}
			responseObserver.onCompleted();
		}

		@Override
		public void detectNoise(DetectionRequest request, StreamObserver<NoiseDetectionResponse> responseObserver) {
			// TODO Auto-generated method stub
			super.detectNoise(request, responseObserver);
		}

		@Override
		public StreamObserver<Room> viewCamera(StreamObserver<CameraViewResponse> responseObserver) {
			return new StreamObserver<Room>() {
				int i = 1;

				@Override
				public void onNext(Room value) {
					System.out.println("Displaying frame#" + i + " for Room #" + value.getRoomIdentifier());
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					responseObserver.onNext(CameraViewResponse.newBuilder()
							.setImage(value.getRoomIdentifier() + "#frame#" + i).build());
					i++;
				}

				@Override
				public void onError(Throwable t) {
					System.out.println("View Camera Cancelled");
					responseObserver.onCompleted();
				}

				@Override
				public void onCompleted() {
					responseObserver.onCompleted();
				}
			};
		}
	}
}
