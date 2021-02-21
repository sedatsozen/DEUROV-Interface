package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.simple.parser.ParseException;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller {
    @FXML
    private Button button;
    @FXML
    private ImageView currentFrame;
    @FXML
    private Label sensor1Label;
    @FXML
    private Label sensor2Label;
    @FXML
    private Label sensor3Label;

    private ScheduledExecutorService timer; // a timer for acquiring the video stream
    private final Camera camera = new Camera(); //Custom Camera Object
    private Server server; //Java Server

    public Controller() {
    }

    @FXML
    public void initialize() throws IOException, ParseException {
        System.out.println("The program is started");
        JoystickControl joystickControl = new JoystickControl();

        server = new Server(8080); //Initialize Server

        Runnable receiveDataFromClient = () -> { //Thread for receiving data from client
            try {
                System.out.println("Server is ready to receive data from client");
                server.receiveData();
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                this.button.setText("Reconnect");
                try {
                    server = new Server(8080);
                    Thread.sleep(3000);
                } catch (IOException | InterruptedException ioException) {
                    ioException.printStackTrace();
                }
            }
        };

        Runnable sendDataToClient = () -> { //Thread for sending data to client
            try {
                Server joystickServer = new Server(8091);
                System.out.println("Joystick server started");

                while(true){
                    server.sendData(joystickServer.receiveJoystickData() + ", Mode: " + "1");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        };

//        this.timer = Executors.newSingleThreadScheduledExecutor();
//        this.timer.scheduleAtFixedRate(receiveDataFromClient, 0, 64, TimeUnit.MILLISECONDS); //Start the thread for receiving
        new Thread(receiveDataFromClient).start();
        new Thread(sendDataToClient).start();
    }

    @FXML
    protected void startCamera() {
        Runnable grabFrame = () -> { //Show camera
            Mat frame = Imgcodecs.imdecode(new MatOfByte(server.getCameraData()), Imgcodecs.IMREAD_UNCHANGED); //Convert byte array to mat
            Core.flip(frame, frame, -1);
            Image imageToShow = camera.mat2Image(frame); // Convert Mat to image
            updateImageView(currentFrame, imageToShow); //Show the converted image on screen
        };

        Platform.runLater(() -> { //Set sensor values (FX Application Thread
            if(server.getSensorData() != null){
                sensor1Label.setText("Sensor 1: " + server.getSensorData().get(0));
                sensor2Label.setText("Sensor 2: " + server.getSensorData().get(1));
                sensor3Label.setText("Sensor 3: " + server.getSensorData().get(2));
            }
        });

        this.timer = Executors.newSingleThreadScheduledExecutor();
        this.timer.scheduleAtFixedRate(grabFrame, 0, 64, TimeUnit.MILLISECONDS); //Start the camera thread
        this.button.setText("Stop Camera");
    }

//    private void stopAcquisition(){
//        if(this.timer != null && !this.timer.isShutdown()){
//            try{
//                // stop the timer
//                this.timer.shutdown();
//                this.timer.awaitTermination(16, TimeUnit.MILLISECONDS);
//            }catch(InterruptedException e){
//                // log any exception
//                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
//            }
//        }
//
//        if(this.capture.isOpened()){
//            this.capture.release();
//        }
//    }

    private void updateImageView(ImageView view, Image image){
        Camera.onFXThread(view.imageProperty(), image);
    }

}
