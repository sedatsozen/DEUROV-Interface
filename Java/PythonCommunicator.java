package sample;

import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class PythonCommunicator{
    private ServerSocket Server;
    private Socket socket;

    public PythonCommunicator() throws IOException {
//        Server = new ServerSocket(8080);
//        this.executePythonCommand("python C:\\Users\\sedat\\PycharmProjects\\pythonProject\\main.py");
//        socket = Server.accept();
//        System.out.println( "The Client "+ socket.getInetAddress() + " : " + socket.getPort()+" IS CONNECTED ");
    }

    public Mat grabFrame() throws IOException {
        Mat frame = new Mat();
        byte[] data = new byte[1024];
        System.out.println("Receiving from " + socket.getInetAddress() + ", " + socket.getPort());
        InputStream stream = socket.getInputStream();
        int count = stream.read(data);
        System.out.println(Arrays.toString(data));
        MatOfByte test = new MatOfByte(data);
        System.out.println("Test: " + test);
        frame = Imgcodecs.imdecode(test, Imgcodecs.IMREAD_UNCHANGED);
        System.out.println("Frame: " + frame.empty());
        return test;
    }

    public void receiveByteArray() throws IOException {
        System.out.println("Receiving from " + socket.getInetAddress() + ", " + socket.getPort());
        InputStream stream = socket.getInputStream();

        while(true){
            byte[] data = new byte[1024];
            int count = stream.read(data);
            Mat mat = Imgcodecs.imdecode(new MatOfByte(data), Imgcodecs.IMREAD_ANYCOLOR);
            Image imageToShow = Utils.mat2Image(mat);

            System.out.println(mat);
            //System.out.println(Arrays.toString(data));
        }
    }

    public void executePythonCommand(String command) throws IOException{
        Process p = Runtime.getRuntime().exec(command);
    }

    public void closeSocket() throws IOException {
        socket.close();
        System.out.println("Socket is closed");
    }

}
