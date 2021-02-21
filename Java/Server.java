package sample;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Server {
    private Socket socket;
    private final Camera camera = new Camera();
    private byte[] cameraData;
    private ArrayList<String> sensorData;
    private OutputStreamWriter outputStreamWriter;

    public Server(int port) throws IOException {
        //Start a server at initialization
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("The server has been started, waiting for connection...");
        socket = serverSocket.accept();
        outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
        System.out.println( "The Client "+ socket.getInetAddress() + " : " + socket.getPort() + " is connected");
    }

    public void receiveData() throws IOException, ParseException {
        //Receive data from client
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String response;
        JSONParser jsonParser = new JSONParser();
        while ((response = bufferedReader.readLine()) != null) {
            String replaced = response.replaceAll("'", "\"");
            JSONObject jsonObject = (JSONObject) jsonParser.parse(replaced); //Parse incoming json data
            JSONArray cameraArray = (JSONArray) jsonObject.get("camera");
            JSONArray sensorArray = (JSONArray) jsonObject.get("sensors");

            this.sensorData = this.convertSensorData(sensorArray);
            this.cameraData = camera.transformToByteArray(cameraArray);
        }
    }

    public String receiveJoystickData() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String response;

        if((response = bufferedReader.readLine()) != null){
            return response;
        }
        return null;
    }

    public void sendData(String dataToSend) throws IOException {
        try{
            outputStreamWriter.write(dataToSend);
        }catch (SocketException socketException){
            socketException.printStackTrace();
        }
    }

    private ArrayList<String> convertSensorData(JSONArray sensorArray){
        ArrayList<String> sensorValues = new ArrayList<>();
        for(Object object : sensorArray.toArray()){
            String value = object.toString();
            sensorValues.add(value);
        }
        return sensorValues;
    }

    public void sendPingRequest(String ipAddress) throws IOException {
        InetAddress inetAddress = InetAddress.getByName(ipAddress);
        System.out.println("Sending Ping Request to " + ipAddress);
        if (inetAddress.isReachable(5000))
            System.out.println("Reached!");
        else
            System.out.println("Unable to reach");

    }

    public byte[] getCameraData() {
        return cameraData;
    }

    public ArrayList<String> getSensorData() {
        return sensorData;
    }
}
