package project.alarmservice;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class AlarmServiceServer {

    public static final String JMDNS_SERVICE_TYPE = "_alarmservice._tcp.local.";
    private Server server;

    public static void main(String[] args) throws IOException, InterruptedException {
        final AlarmServiceServer server = new AlarmServiceServer();
        server.start(50053);
        server.blockUntilShutdown();
    }

    public void start(int port) throws IOException, InterruptedException {
        /**
         * Registering Service with JmDNS
         */
        JmDNS jmdns = JmDNS.create("localhost");
        ServiceInfo serviceInfo = ServiceInfo.create(JMDNS_SERVICE_TYPE, "AlarmService", port, "alarmservice=grpc");
        jmdns.registerService(serviceInfo);

        Thread.sleep(1000);

        server = ServerBuilder.forPort(port).addService(new DoorServiceImpl()).build().start();
        System.out.println("AlarmService Server Started, listening on port: " + port);
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
    }

    //ALARM IMPLEMENTATION

    static class DoorServiceImpl extends AlarmServiceGrpc.AlarmServiceImplBase {

        @Override //ALARM METHOD - UNARY
        public void TurnOnAlarm(Home request, StreamObserver<TurnOnAlarmResponse> responseObserver) {
            System.out.println("Turning alarm on...");
            TurnOnAlarmResponse response;
            if (request.get)) {
                response = LockDoorResponse.newBuilder().setLocked(true).build();
                System.out.println("[Locked] Door #" + request.getDoorNumber());
            } else {
                response = LockDoorResponse.newBuilder().setLocked(false).build();
                System.out.println("[Not Found] Door #" + request.getDoorNumber());
            }
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override // ALARM FF METHOD - UNARY
        public void TurnOffAlarm(DoorRequest request, StreamObserver<UnlockDoorResponse> responseObserver) {
            System.out.println("Unlocking Door #" + request.getDoorNumber());
            UnlockDoorResponse response = UnlockDoorResponse.newBuilder().setUnlocked(true).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override //CHECK DOOR METHOD - CLIENT STREAM
        public StreamObserver<DoorRequest> checkDoors(StreamObserver<DoorsResponse> responseObserver) {
            return new StreamObserver<DoorRequest>() {
                //Create array list to register doors that are locked and unlocked
                ArrayList<Integer> locked = new ArrayList<>();
                ArrayList<Integer> unlocked = new ArrayList<>();

                @Override
                public void onNext(DoorRequest value) {
                    System.out.println("Checking door #" + value.getDoorNumber());
                    if (value.getDoorNumber() % 2 == 0) { //if door divided by 2 is 0 - the door is locked
                        if (!locked.contains(value.getDoorNumber())) {
                            locked.add(value.getDoorNumber());
                        }
                    } else {// door is unlocked
                        if (!unlocked.contains(value.getDoorNumber())) {
                            unlocked.add(value.getDoorNumber());
                        }
                    }
                }

                @Override
                public void onError(Throwable t) {
                    System.out.println("Door Checking Cancelled");
                }

                @Override
                public void onCompleted() { //Response
                    System.out.println("Finished checking Doors | Locked: " + locked.toString() + " | Unlocked: "
                            + unlocked.toString());
                    DoorsResponse response = DoorsResponse.newBuilder().addAllLockedDoors(locked)
                            .addAllUnlockedDoors(unlocked).build();
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                }
            };
        }
    }
}