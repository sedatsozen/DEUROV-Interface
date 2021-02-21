package sample;
import java.io.*;

public class JoystickControl {
    private String lastChar = "}";

    public JoystickControl() {
    }

    public String getJoystickData() throws IOException, InterruptedException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("C:\\Users\\sedat\\PycharmProjects\\pythonProject\\data.json", "r");
        String line;
        if((line = randomAccessFile.readLine()) != null){
            this.lastChar = line.substring(line.length() - 1);
            return line;
        }else{
            System.out.println("Problem");
        }
        return null;
    }

    public String getLastChar() {
        return lastChar;
    }
}
