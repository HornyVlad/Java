import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Main {
    public static double [] res = new double[9];
    public static void main(String[] args) throws IOException {
        try {
            DatagramSocket serverSocket = new DatagramSocket(8080);
            byte [] buffer = new byte[1024];
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            String receivedMessage = null;
            double [] func = new double[4];
            Thread[] thread = new Thread[9];

            byte [] sendingDataBuffer = new byte[1024];
            String SendingMessage;
            InetAddress senderAddress;
            int senderPort;
            DatagramPacket outputPacket;

            //clientSocket.setSoTimeout(3000);
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 3; j++) {
                    serverSocket.receive(datagramPacket);
                    receivedMessage = new String(datagramPacket.getData());
                    func[j] = Double.parseDouble(receivedMessage);
                    System.out.println(func[j]);
                }
                thread[i] = new Thread(new MyThread(func[0],func[1],func[2],i));
                thread[i].start();
            }

            for(int i = 0; i < 9; i++){
                thread[i].join();
                SendingMessage = Double.toString(res[i]);
                sendingDataBuffer = SendingMessage.getBytes();
                senderAddress = datagramPacket.getAddress();
                senderPort = datagramPacket.getPort();
                outputPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, senderAddress, senderPort);
                serverSocket.send(outputPacket);
            }
            serverSocket.close();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}