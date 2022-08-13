package project.alarmservice;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import project.cameraservice.CameraServiceServer;
import project.doorservice.DoorServiceGrpc;
import project.doorservice.DoorServiceServer;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AlarmServiceServer {

    public static final String JMDNS_SERVICE_TYPE = "_alarmservice._tcp.local.";
    private Server server;

    public static void main(String[] args) throws IOException, InterruptedException {
        final CameraServiceServer server = new CameraServiceServer();
        server.start(50053);
        server.blockUntilShutdown();
    }


    public void start(int port) throws IOException, InterruptedException {
        /**
         * Registering Service with JmDNS
         */
        JmDNS jmdns = JmDNS.create("localhost");
        ServiceInfo serviceInfo = ServiceInfo.create(JMDNS_SERVICE_TYPE, "Alarm Service", port, "alarmservice=grpc");
        jmdns.registerService(serviceInfo);

        Thread.sleep(1000);

        server = ServerBuilder.forPort(port).addService(new AlarmServiceServer.AlarmServiceImpl()).build().start();
        System.out.println("Alarm Server Started, listening on port: " + port);
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
    }//End of Server Implementation


    static class AlarmServiceImpl extends AlarmServiceGrpc.AlarmServiceImplBase {

        @Override
        public void turnOnAlarm(Home request, StreamObserver<TurnOnAlarmResponse> responseObserver) {

            TurnOnAlarmResponse response = null;

            if (request.equals(true)){
                response = TurnOnAlarmResponse.newBuilder().setTurnedOn(true).build();
                System.out.println("Alarm ON");
            }
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void turnOffAlarm(Home request, StreamObserver<TurnOffAlarmResponse> responseObserver) {
            TurnOffAlarmResponse response = null;

            if (request.equals(true)){
                response = TurnOffAlarmResponse.newBuilder().setTurnedOff(true).build();
                System.out.println("Alarm OFF");
            }
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void panicButton(Home request, StreamObserver<PanicButtonResponse> responseObserver) {


//          if (request.equals(true)){
//              System.out.println("Panic Button Pressed: calling urgent service\"");
//          }try {
//                Thread.sleep(1000);
//            }catch (InterruptedException e){
//              e.printStackTrace();
//            }
//          responseObserver.
//                  onNext(PanicButtonResponse.newBuilder()
//                          .setTimestamp((int) (new Date().getTime()/1000))
//                          .setInformation().build());
//          responseObserver.onCompleted();

        }
    }


}
