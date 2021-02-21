import socket
import threading
import _thread
import cv2
import numpy

class Client(threading.Thread):
    def __init__(self, host, port):
        threading.Thread.__init__(self)
        self.host = host
        self.port = port
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.connect((self.host, self.port))
        self.cap = cv2.VideoCapture(0)
        self.joystick_data = None
        self.mode = None
        
    def gather(self):
        while True:
            #Capture the frame and resize
            ret, old_frame = self.cap.read()
            frame = cv2.resize(old_frame, (int(old_frame.shape[1] * 50 / 100), int(old_frame.shape[0] * 50 / 100)))
            
            #Encode the frame
            encode_param = [int(cv2.IMWRITE_JPEG_QUALITY), 90]
            result, encoded_image = cv2.imencode('.jpg', frame, encode_param)
            camera_array = numpy.array(encoded_image).tolist()
                    
            #To JSON formatted string
            test = {"camera": camera_array, "sensors": [1, 2, 3]} #Replace the elements in "sensors" to real sensor values
            data_to_send = str(test)
            self.sock.send(bytes(data_to_send, "utf-8"))
            self.sock.send(bytes("\n", "utf-8"))
                              
    def gather_data(self):
        t3 = threading.Thread(target = self.gather)
        t3.start()
        print("Started gathering")
        
    def decode_response(self, response):
        incoming_string = response.decode("utf-8")
        separated_data = incoming_string.split(", ")
        self.joystick_data = separated_data[0]
        self.mode = separated_data[1]
        

#    def send_data(self):
#        t2 = threading.Thread(target = self.send)
#        t2.start()
#        print("Started sending")

    def receive(self):
        print("Awaiting message...")
        try:
            while True:
                response = self.sock.recv(4096)
                self.decode_response(response)
                print(self.joystick_data)
                print(self.mode)
        except ConnectionResetError:
            print("Connection lost")

    def receive_data(self):
        t = threading.Thread(target=self.receive)
        t.start()
        print("Started listening")


HOST = "192.168.0.6" #Static IP of the Raspberry
PORT = 8080 #If changed, change the port in the interface correspondingly

client = Client(HOST, PORT) #Instantiate the client
client.start() #Start all of the threads
client.receive_data() 
client.gather_data()


