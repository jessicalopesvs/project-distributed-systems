package project.userinterface;


//import project.doorservice.DoorRequest;
import project.cameraservice.CameraServiceServer;
import project.cameraservice.Room;
import project.doorservice.DoorServiceGrpc;
import project.doorservice.DoorServiceServer;
//import project.doorservice.LockDoorResponse;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;


public class UserInterface{

    private JTextField reply2, reply3;

    public static void main(String[] args) {

        UserInterface gui = new UserInterface();
        gui.build();
    }



    /**
     * Lock Door GUI - start
     * @return
     */

    private JLabel LockDoorLabel;
    private JTextField LockDoorNum;
    private JTextField LockDoorReply;

    private JPanel getLockDoorService() {

        DoorServiceServer DoorServer = new DoorServiceServer();

        JPanel panel = new JPanel();


        JLabel label = new JLabel("LockDoor");
        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.X_AXIS);

        Border border = BorderFactory.createTitledBorder("Lock Door Service");
        panel.setBorder(border);

        LockDoorLabel = new JLabel("Insert door number");
        panel.add(LockDoorLabel);

        LockDoorNum = new JTextField("",10);
        LockDoorNum.setEditable(true);
        panel.add(LockDoorNum);

        JButton button = new JButton("Lock");
        button.setPreferredSize(new Dimension(200, 30));
        button.addActionListener(this::actionLockDoor);
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));

        LockDoorReply= new JTextField("", 10);
        LockDoorReply.setEditable(false);
        panel.add(LockDoorReply);
        return panel;

    }

    public void actionLockDoor(ActionEvent e) {
        System.out.println("Lock door service to be invoked");

    }

    /**
     * Lock Door GUI - end
     * @return
     */


    private JLabel UnlockDoorLabel;
    private JTextField UnlockDoorNum;
    private JTextField UnlockDoorReply;

    private JPanel getUnlockDoorService() {

        DoorServiceServer DoorServer = new DoorServiceServer();

        JPanel panel = new JPanel();


        JLabel label = new JLabel("Unlock Door Service");
        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.X_AXIS);

        Border border = BorderFactory.createTitledBorder("Unlock");
        panel.setBorder(border);

        UnlockDoorLabel = new JLabel("Insert door number");
        panel.add(UnlockDoorLabel);

        UnlockDoorNum = new JTextField("",10);
        UnlockDoorNum.setEditable(true);
        panel.add(UnlockDoorNum);

        JButton button = new JButton("Unlock");
        button.setPreferredSize(new Dimension(200, 30));
        button.addActionListener(this::actionUnlockDoor);
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));

        UnlockDoorReply= new JTextField("", 10);
        UnlockDoorReply.setEditable(false);
        panel.add(UnlockDoorReply);
        return panel;

    }

    public void actionUnlockDoor(ActionEvent e) {
        System.out.println("Unlock Door action");

    }

    /**
     * Door Checker
     **/

    private JLabel DoorCheckerLabel;
    private JTextField DoorNum;
    private JLabel DoorCheckerReplyLabel;
    private JTextField DoorCheckerReply;

    private JPanel getDoorCheckerService() {

        DoorServiceServer DoorServer = new DoorServiceServer();
        JPanel frame = new JPanel();
        JPanel panel = new JPanel(new GridLayout(1,1));
        JPanel panel2 = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));


        JLabel label = new JLabel("Check if the doors are locked or unlocked");
        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);

        //Border
        Border border = BorderFactory.createTitledBorder("Check doors lockers");
        panel.setBorder(border);

        //Label
        DoorCheckerLabel = new JLabel("Insert door number");
        panel.add(DoorCheckerLabel);

        //Add number field
        DoorNum = new JTextField("",10);
        DoorNum.setEditable(true);
        panel.add(DoorNum);

        //Button Add
        JButton buttonAdd = new JButton("Add door");//change service name
        buttonAdd.setPreferredSize(new Dimension(200, 30));
        buttonAdd.addActionListener(this::actionDoorChecker);
        panel.add(buttonAdd);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));

        //Button Check

        JButton buttonCheck = new JButton("Check");//change service name
        buttonCheck.setPreferredSize(new Dimension(200, 30));
        buttonCheck.addActionListener(this::actionDoorChecker);
        panel.add(buttonCheck);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));

        //Reply
//        DoorCheckerReplyLabel = new JLabel("Door Checker Report");
//        panel2.add(DoorCheckerReplyLabel);
        Border border2 = BorderFactory.createTitledBorder(("Door Checker Report"));
        panel2.setBorder(border2);
        DoorCheckerReply= new JTextField("", 30);
        DoorCheckerReply.setPreferredSize(new Dimension(30, 100));
        DoorCheckerReply.setEditable(false);
        DoorCheckerReply.getAutoscrolls();
        panel2.add(DoorCheckerReply);

        frame.add(panel);
        frame.add(panel2);
        return frame;

    }

    public void actionDoorChecker(ActionEvent e) {
        System.out.println("Door Checker");
    }//end of door checker

    /**---CAMERA SERVICE-----*/

    /**MOVEMENT SENSOR**/

    private JLabel MovementSensorLabel;
    private JLabel RoomLabel;
    private JTextField Room;
    private JLabel TimeLabel;
    private JTextField Time;
    private JTextField MovementSensorReply;

    private JPanel getMovementSensorService() {
        JPanel frame = new JPanel();

        JPanel MovementPanel = new JPanel();
        JPanel MovementPanelReply = new JPanel();
//        MovementPanel.setLayout();

        JLabel label = new JLabel("LockDoor");



        MovementPanel.setLayout(new BoxLayout(MovementPanel, BoxLayout.PAGE_AXIS));
        MovementPanelReply.setLayout(new BoxLayout(MovementPanelReply, BoxLayout.PAGE_AXIS));

        Border border = BorderFactory.createTitledBorder("Movement Sensor");
        frame.setBorder(border);

//        MovementSensorLabel = new JLabel("");
//        MovementPanel.add(MovementSensorLabel);

        RoomLabel = new JLabel("Room");
        MovementPanel.add(RoomLabel);

        Room= new JTextField("",10);
        Room.setEditable(false);
        MovementPanel.add(Room);

        TimeLabel = new JLabel("Time");
        MovementPanel.add(TimeLabel);

        Time = new JTextField("",10);
        Time.setEditable(false);
        MovementPanel.add(Time);

        MovementSensorReply= new JTextField("");
        MovementSensorReply.setEditable(false);
        MovementSensorReply.setPreferredSize(new Dimension(500, 100));
        MovementPanelReply.add(MovementSensorReply);
        frame.add(MovementPanel);
        frame.add(MovementPanelReply);
        return frame;

    }

    public void actionMovementSensor(ActionEvent e) {
        System.out.println("Lock door service to be invoked");

    }

    /**NOISE SENSOR**/

    private JLabel NoiseSensorLabel;
    private JTextField NoiseSensorReply;

    private JPanel getNoiseSensorService() {

        JPanel NoisePanel = new JPanel();
        JPanel NoisePanelReply = new JPanel();
        JPanel frame = new JPanel();

        JLabel label = new JLabel("LockDoor");
        BoxLayout boxLayout = new BoxLayout(NoisePanel, BoxLayout.X_AXIS);

        Border border = BorderFactory.createTitledBorder("Movement Sensor");
        frame.setBorder(border);


        NoisePanel.setLayout(new BoxLayout(NoisePanel, BoxLayout.PAGE_AXIS));
        NoisePanelReply.setLayout(new BoxLayout(NoisePanelReply, BoxLayout.PAGE_AXIS));

//        MovementSensorLabel = new JLabel("");
//        MovementPanel.add(MovementSensorLabel);
        RoomLabel = new JLabel("Room");
        NoisePanel.add(RoomLabel);

        Room= new JTextField("",10);
        Room.setEditable(false);
        NoisePanel.add(Room);

        TimeLabel = new JLabel("Time");
        NoisePanel.add(TimeLabel);

        Time = new JTextField("",10);
        Time.setEditable(false);
        NoisePanel.add(Time);


        NoiseSensorReply= new JTextField("");
        NoiseSensorReply.setEditable(false);
        NoiseSensorReply.setPreferredSize(new Dimension(500, 100));
        NoisePanelReply.add(NoiseSensorReply);
        frame.add(NoisePanel);
        frame.add(NoisePanelReply);

        return frame;

    }

    public void actionNoiseSensor(ActionEvent e) {
        System.out.println("Lock door service to be invoked");

    }//end of noise sensor


    //VIEW CAMERA SERVICE

    private JLabel CameraServiceLabel;
    private JLabel CameraServiceReplyLabel;
    private JTextField RoomNum;
    private JTextField CameraServiceReply;

    private JPanel getViewCameraService() {

        JPanel viewCameraPanel = new JPanel();
        JPanel viewCameraPanel1 = new JPanel();
        JPanel viewCameraPanel2 = new JPanel();
        JPanel viewCameraPanel3 = new JPanel();



        JLabel label = new JLabel("View Camera Service");

        viewCameraPanel1.setLayout(new BoxLayout(viewCameraPanel1, BoxLayout.PAGE_AXIS));
        viewCameraPanel2.setLayout(new BoxLayout(viewCameraPanel2, BoxLayout.PAGE_AXIS));
//        viewCameraPanel3.setLayout(new BoxLayout(viewCameraPanel3, BoxLayout.PAGE_AXIS));

        //Border
        Border border = BorderFactory.createTitledBorder("Choose the room to check camera");
        viewCameraPanel.setBorder(border);

        //Label
        CameraServiceLabel = new JLabel("Room");
        viewCameraPanel1.add(CameraServiceLabel);

        //Add number field
        RoomNum = new JTextField("",10);
        RoomNum.setEditable(true);
        viewCameraPanel1.add(RoomNum);

        //Button Add
        JButton roomSubmitBtn = new JButton("Add room number");//change service name
        roomSubmitBtn.setPreferredSize(new Dimension(200, 30));
        roomSubmitBtn.addActionListener(this::actionViewCamera);
        viewCameraPanel1.add(roomSubmitBtn);
        viewCameraPanel1.add(Box.createRigidArea(new Dimension(10, 0)));



        //Reply
        CameraServiceReplyLabel = new JLabel("Camera View");
        viewCameraPanel2.add(CameraServiceReplyLabel);
        CameraServiceReply= new JTextField("");
        CameraServiceReply.setPreferredSize(new Dimension(330, 200));
        CameraServiceReply.setEditable(false);
        viewCameraPanel2.add(CameraServiceReply);

        //Button Add
        JButton CameraCloseBtn = new JButton("Stop Video Streaming");//change service name
        CameraCloseBtn.setPreferredSize(new Dimension(200, 30));
        CameraCloseBtn.addActionListener(this::actionViewCamera);
        viewCameraPanel2.add(CameraCloseBtn);
        viewCameraPanel2.add(Box.createRigidArea(new Dimension(10, 0)));

        //adding
        viewCameraPanel.add(viewCameraPanel1);
        viewCameraPanel.add(viewCameraPanel2);
//        viewCameraPanel.add(viewCameraPanel3);

        return viewCameraPanel;

    }

    public void actionViewCamera(ActionEvent e) {
        System.out.println("Camera view Checker");

    }//end of door checker



    /**---ALARM SERVICE-----*/

    //Alarm on


    private JLabel AlarmOnLabel;
    private JTextField AlarmOnReply;

    private JPanel getAlarmOnSensorService() {

        JPanel AlarmPanel = new JPanel();

        JLabel label = new JLabel("Alarm On");
        BoxLayout boxLayout = new BoxLayout(AlarmPanel, BoxLayout.X_AXIS);

        //Formatting border
        Border border = BorderFactory.createTitledBorder("Alarm on");
        AlarmPanel.setBorder(border);

        //Button
        JButton AlarmOnBtn = new JButton("Alarm On");
        AlarmOnBtn.setPreferredSize(new Dimension(200, 30));
        AlarmOnBtn.addActionListener(this::actionAlarmOn);
        AlarmPanel.add(AlarmOnBtn);
        AlarmPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        //Reply
        AlarmOnReply= new JTextField("", 10);
        AlarmOnReply.setEditable(false);
        AlarmPanel.add(AlarmOnReply);

        return AlarmPanel;

    }

    public void actionAlarmOn(ActionEvent e) {
        System.out.println("Alarm on service to be invoked");

    }

    //Alarm on


    private JLabel AlarmOffLabel;
    private JTextField AlarmOffReply;

    private JPanel getAlarmOffSensorService() {

        JPanel AlarmPanel = new JPanel();

        JLabel label = new JLabel("Alarm On");
        BoxLayout boxLayout = new BoxLayout(AlarmPanel, BoxLayout.X_AXIS);

        //Formatting border
        Border border = BorderFactory.createTitledBorder("Alarm off");
        AlarmPanel.setBorder(border);

        //Button
        JButton AlarmOffBtn = new JButton("Alarm Off");
        AlarmOffBtn.setPreferredSize(new Dimension(200, 30));
        AlarmOffBtn.addActionListener(this::actionAlarmOff);
        AlarmPanel.add(AlarmOffBtn);
        AlarmPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        //Reply
        AlarmOffReply= new JTextField("", 10);
        AlarmOffReply.setEditable(false);
        AlarmPanel.add(AlarmOffReply);

        return AlarmPanel;

    }

    public void actionAlarmOff(ActionEvent e) {
        System.out.println("Alarm off service to be invoked");

    }

    //Panic button - Call the police

    /**---ALARM SERVICE-----*/

    //Alarm on


    private JLabel PanicBtnLabel;
    private JTextField PanicBtnReply;

    private JPanel getPanicBtnService() {

        JPanel PanicPanel = new JPanel();


        JLabel label = new JLabel("Panic Button");
        BoxLayout boxLayout = new BoxLayout(PanicPanel, BoxLayout.X_AXIS);

        //Formatting border
        Border border = BorderFactory.createTitledBorder("Panic Button - call police");
        PanicPanel.setBorder(border);

        //Button
        JButton PanicBtn = new JButton("Alarm On");
        PanicBtn.setPreferredSize(new Dimension(200, 30));
        PanicBtn.addActionListener(this::actionPanicButton);
        PanicPanel.add(PanicBtn);
        PanicPanel.add(Box.createRigidArea(new Dimension(10, 0)));


        //Reply
        PanicBtnReply= new JTextField("");
        PanicBtnReply.setEditable(false);
        PanicBtnReply.setPreferredSize(new Dimension(330, 200));

        PanicPanel.add(PanicBtnReply);

        return PanicPanel;

    }

    public void actionPanicButton(ActionEvent e) {
        System.out.println("Panic Button to be invoked");

    }



    /**
     *  BUILD GUI
     */

    public void build(){
        JFrame frame = new JFrame("Smart security system");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane first = new JTabbedPane();

        //add buttons on pannel
        JPanel panel = new JPanel();

        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);

        panel.setLayout(boxLayout);

        //Door
        JPanel panel1 = new JPanel();
        panel1.add(getLockDoorService());
        panel1.add(getUnlockDoorService());
        panel1.add(getDoorCheckerService());

        //Camera
        JPanel panel2 = new JPanel();
        panel2.add(getMovementSensorService());
        panel2.add(getNoiseSensorService());
        panel2.add(getViewCameraService());


        //Alarm
        JPanel panel3 = new JPanel();

        panel3.add(getAlarmOnSensorService());
        panel3.add(getAlarmOffSensorService());
        panel3.add(getPanicBtnService());

        //TABS

        JTabbedPane tp = new JTabbedPane();

        tp.add("Door", panel1);
        tp.add("Camera", panel2);
        tp.add("Alarm", panel3);


        frame.add(tp);
        frame.setSize(800,650);
        frame.setVisible(true);

    }
}

