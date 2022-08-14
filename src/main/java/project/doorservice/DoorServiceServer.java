package project.doorservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import project.jmdnsutil.JmDnsUtil;

public class DoorServiceServer {
	public static final String JMDNS_SERVICE_TYPE = "_doorservice._grpc.local.";
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
		JmDnsUtil.RegisterService(JMDNS_SERVICE_TYPE, "DoorService", port, "doorservice=grpc");

		// starting service in the provided port
		server = ServerBuilder.forPort(port).addService(new DoorServiceImpl()).build().start();
		System.out.println("DoorService Server Started, listening on port: " + port);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("*** shutting down ***");
				try {
					DoorServiceServer.this.stop();
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

	static class DoorServiceImpl extends DoorServiceGrpc.DoorServiceImplBase {
		private HashSet<Integer> lockedDoors = new HashSet<>();
		private HashSet<Integer> unlockedDoors = new HashSet<>();

		// constructor creating doors, all door will be initially unlocked
		DoorServiceImpl() {
			int numberOfDoors = ThreadLocalRandom.current().nextInt(5, 15);
			System.out.println("Door Service has been created with " + numberOfDoors + " Unlocked Doors");
			for (int i = 1; i <= numberOfDoors; i++) {
				unlockedDoors.add(i);
			}
			System.out.println(
					"[Locked Doors] " + lockedDoors.toString() + " | [Unlocked Doors] " + unlockedDoors.toString());
		}

		@Override
		public void lockDoor(DoorRequest request, StreamObserver<LockDoorResponse> responseObserver) {
			System.out.println("Trying to Lock Door #" + request.getDoorNumber());
			LockDoorResponse response;
			if (unlockedDoors.contains(request.getDoorNumber())) {
				// if door is unlocked, lock it
				System.out.println("[Locked] Door #" + request.getDoorNumber());
				lockedDoors.add(request.getDoorNumber());
				unlockedDoors.remove(request.getDoorNumber());
				response = LockDoorResponse.newBuilder().setLockMessage("Door Locked").build();
			} else if (lockedDoors.contains(request.getDoorNumber())) {
				// if door is already locked, don't do anything
				System.out.println("[Already Locked] Door #" + request.getDoorNumber());
				response = LockDoorResponse.newBuilder().setLockMessage("Door Already Locked").build();
			} else {
				// if door isn't found, don't do anything
				System.out.println("[Not Found] Door #" + request.getDoorNumber());
				response = LockDoorResponse.newBuilder().setLockMessage("Door Not Found").build();
			}
			System.out.println(
					"[Locked Doors] " + lockedDoors.toString() + " | [Unlocked Doors] " + unlockedDoors.toString());
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		}

		@Override
		public void unlockDoor(DoorRequest request, StreamObserver<UnlockDoorResponse> responseObserver) {
			System.out.println("Trying to unlock Door #" + request.getDoorNumber());
			UnlockDoorResponse response;
			if (lockedDoors.contains(request.getDoorNumber())) {
				// if door is locked, unlock it
				System.out.println("[Unlocked] Door #" + request.getDoorNumber());
				unlockedDoors.add(request.getDoorNumber());
				lockedDoors.remove(request.getDoorNumber());
				response = UnlockDoorResponse.newBuilder().setUnlockMessage("Door Unlocked").build();
			} else if (unlockedDoors.contains(request.getDoorNumber())) {
				// if door is already unlocked, don't do anything
				System.out.println("[Already Unlocked] Door #" + request.getDoorNumber());
				response = UnlockDoorResponse.newBuilder().setUnlockMessage("Door Already Unlocked").build();
			} else {
				// if door isn't found, don't do anything
				System.out.println("[Not Found] Door #" + request.getDoorNumber());
				response = UnlockDoorResponse.newBuilder().setUnlockMessage("Door Not Found").build();
			}
			System.out.println(
					"[Locked Doors] " + lockedDoors.toString() + " | [Unlocked Doors] " + unlockedDoors.toString());
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		}

		@Override
		public StreamObserver<DoorRequest> checkDoors(StreamObserver<DoorsResponse> responseObserver) {
			return new StreamObserver<DoorRequest>() {
				ArrayList<Integer> locked = new ArrayList<>();
				ArrayList<Integer> unlocked = new ArrayList<>();
				ArrayList<Integer> notFound = new ArrayList<>();

				@Override
				public void onNext(DoorRequest value) {
					System.out.println("Checking Door #" + value.getDoorNumber());
					if (lockedDoors.contains(value.getDoorNumber())) {
						if (!locked.contains(value.getDoorNumber())) {
							locked.add(value.getDoorNumber());
						}
					} else if (unlockedDoors.contains(value.getDoorNumber())) {
						if (!unlocked.contains(value.getDoorNumber())) {
							unlocked.add(value.getDoorNumber());
						}
					} else {
						if (!notFound.contains(value.getDoorNumber())) {
							notFound.add(value.getDoorNumber());
						}
					}
					System.out.println("[Locked Doors] " + lockedDoors.toString() + " | [Unlocked Doors] "
							+ unlockedDoors.toString());
				}

				@Override
				public void onError(Throwable t) {
					System.out.println("Door Checking Cancelled");
				}

				@Override
				public void onCompleted() {
					System.out.println("Finished checking Doors | Locked: " + locked.toString() + " | Unlocked: "
							+ unlocked.toString());
					DoorsResponse response = DoorsResponse.newBuilder().addAllLockedDoors(locked)
							.addAllUnlockedDoors(unlocked).addAllNotFound(notFound).build();
					responseObserver.onNext(response);
					responseObserver.onCompleted();
				}
			};
		}
	}
}
