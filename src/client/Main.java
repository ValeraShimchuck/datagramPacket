package client;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) {
        byte bKbdInput[];
        byte buf[] = new byte[512];

        boolean pass = false;
        String authToken;

        DatagramSocket s;

        DatagramPacket pout;
        DatagramPacket pin;




        try {
            s = new DatagramSocket();
            InetAddress OutAddress = InetAddress.getLocalHost(); // custom address
            pin = new DatagramPacket(buf, buf.length);
            while(true){

                Scanner in = new Scanner(System.in);
                System.out.println("input login: ");
                String login = in.next();
                System.out.println("input password: " );
                String password = in.next();

                authToken = login+":"+password;

                bKbdInput = convertToBytes(authToken);

                pout = new DatagramPacket(bKbdInput, bKbdInput.length,
                        OutAddress,9998);

                s.send(pout);

                s.receive(pin);
                if(pin.getAddress().equals(OutAddress)){
                    String str = (String) convertFromBytes(buf);
                    StringTokenizer st;
                    st = new StringTokenizer(str,"\r\n");

                    str = new String((String) st.nextElement());

                    if(str.equals("TRUE")){
                        pass=true;
                        System.out.println("Logged");
                        break;
                    }else if(str.equals("FALSE")){
                        System.out.println("Incorrect pass/login");
                    }
                    else{
                        System.out.println("Error");
                        System.out.println(str);
                        break;
                    }
                }
            }
            if(pass){
                while (true){
                    Scanner in = new Scanner(System.in);
                    System.out.println("input message: ");
                    String message = in.nextLine();

                    bKbdInput = convertToBytes(authToken+" "+"("+message+")");



                    pout = new DatagramPacket(bKbdInput, bKbdInput.length,
                            OutAddress,9998);

                    s.send(pout);
                    if(message.equals("quit"))break;
                    s.receive(pin);

                    if(pin.getAddress().equals(OutAddress)){
                        String str = (String) convertFromBytes(buf);
                        StringTokenizer st;
                        st = new StringTokenizer(str,"\r\n");

                        str = new String((String) st.nextElement());
                        str = str.replaceAll("\0","");

                        if(str.equals("TRUE")){
                            pass=true;
                            System.out.println(message);

                        }else if(str.equals("FALSE")){
                            System.out.println("Incorrect token");
                            pass=false;
                            break;
                        }
                        else{
                            System.out.println("Error");
                            System.out.println(str);
                            break;
                        }
                    }
                }


            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static byte[] convertToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        }
    }
    private static Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }
}
