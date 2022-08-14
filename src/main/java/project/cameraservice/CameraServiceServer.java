package project.cameraservice;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import project.jmdnsutil.JmDnsUtil;

public class CameraServiceServer {
	public static final String JMDNS_SERVICE_TYPE = "_cameraservice._grpc.local.";
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
		JmDnsUtil.RegisterService(JMDNS_SERVICE_TYPE, "CameraService", port, "cameraservice=grpc");

		// starting service in the provided port
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
	}// End of Server Implementation

	/** SERVICE IMPLEMENTATION */

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
				responseObserver.onNext(MovementDetectionResponse.newBuilder()
						.setMovement(ThreadLocalRandom.current().nextBoolean()).build());
			}
			responseObserver.onCompleted();
		}

		@Override
		public void detectNoise(DetectionRequest request, StreamObserver<NoiseDetectionResponse> responseObserver) {
			for (int i = 0; i < request.getDuration(); i++) {
				System.out.println("Detecting Noise for Room #" + request.getRoom().getRoomIdentifier());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				responseObserver.onNext(NoiseDetectionResponse.newBuilder()
						.setNoise(ThreadLocalRandom.current().nextBoolean()).build());
			}
			responseObserver.onCompleted();
		}

		@Override
		public StreamObserver<Room> viewCamera(StreamObserver<CameraViewResponse> responseObserver) {
			return new StreamObserver<Room>() {
				int frameNumber = 1;

				@Override
				public void onNext(Room value) {
					System.out.println("Displaying frame#" + frameNumber + " for Room #" + value.getRoomIdentifier());
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					responseObserver.onNext(CameraViewResponse.newBuilder()
							.setImage(value.getRoomIdentifier() + "#frame#" + frameNumber).build());
					frameNumber++;
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
