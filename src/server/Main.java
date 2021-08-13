package server;

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

                String str = new String(buf);
                StringTokenizer st;
                st = new StringTokenizer(str,"\r\n");

                str = new String((String) st.nextElement());

                String message = null;

                if(str.contains("("))
                message = str.substring(
                        str.indexOf("(")+1,
                        str.lastIndexOf(")")
                );
                str = str.replaceAll("\0","");
                String[] strings = str.split(" ");
                if(strings.length != 0){


                    byte log[];
                    if(!checkLog(strings[0])){
                        log = "FALSE".getBytes();

                    }else{
                        log = "TRUE".getBytes();

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
}
