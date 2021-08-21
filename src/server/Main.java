package server;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class Main {
    private static String[] accounts = {"admin:admin"};

    public static void main(String[] args) {
        byte bKbdInput[] = new byte[256];

        byte buf[] = new byte[512];


        DatagramSocket s;

        DatagramPacket pin;

        InetAddress SrcAddress;

        int SrcPort;

        System.out.println("Datagram Socket Request Server");

        try {
            s = new DatagramSocket(9998);

            pin = new DatagramPacket(buf, buf.length);

            while (true){
                s.receive(pin);

                SrcAddress = pin.getAddress();

                SrcPort = pin.getPort();

                String str = (String) convertFromBytes(buf);
                StringTokenizer st;
                st = new StringTokenizer(str,"\r\n");

                str = new String((String) st.nextElement());

                String message = null;

                if(str.contains("("))
                message = str.substring(
                        str.indexOf("(")+1,
                        str.lastIndexOf(")")
                );
                String[] strings = str.split(" ");
                if(strings.length != 0){


                    byte log[];
                    if(!checkLog(strings[0])){
                        log = convertToBytes("FALSE");

                    }else{
                        log = convertToBytes("TRUE");

                        if(message != null){
                            System.out.println(SrcAddress.getHostName() +":"+SrcPort+"->"+message);
                            if(message.equals("quit"))break;
                        }

                    }

                    DatagramPacket pout = new DatagramPacket(log,log.length,SrcAddress,SrcPort);
                    s.send(pout);
                }



            }
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static boolean checkLog(String str) {
        for(String account : accounts){
            if(str.equals(account))return true;
        }
        return false;
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
