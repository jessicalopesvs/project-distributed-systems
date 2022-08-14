package project.alarmservice;

import java.io.IOException;
import java.util.HashMap;
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
