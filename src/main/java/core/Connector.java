package core;

import implementation.Repository;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class Connector {
    public static void run(String name, int PORT) {
        try{
//            int PORT = 6232;
//            String name = "r2";
//            IRepository server = Repository.getInstance();
//            IRepository stub = (IRepository) UnicastRemoteObject.exportObject((IRepository) server, 0);
//
//            Registry registry = LocateRegistry.createRegistry(PORT);
//            registry.rebind("repository", stub);
//            server.addressMap.put(name, String.valueOf(PORT));
//            System.out.println("server is ready");
            IDistributedRepository server = Repository.getInstance();

            boolean flag = true;
            while(true){
                if(flag){
                    flag = false;
                    DatagramSocket socket = new DatagramSocket(null);
                    socket.setReuseAddress(true);
                    socket.bind(new InetSocketAddress(8888));
                    socket.setBroadcast(true);

                    String senderAddress = InetAddress.getLocalHost().getHostAddress();

                    String broadCastMessage;
                    if(false){
                        broadCastMessage = "SEND_ADDRESS:" + PORT;
                    }
                    else {
                        broadCastMessage = name + ":" + PORT;
                    }
                    byte[] buffer = broadCastMessage.getBytes();

                    DatagramPacket packet
                            = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), 8889);

                    try {
                        socket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    List<InetAddress> inetAddressArrayList = listAllBroadcastAddresses();
                    System.out.println(inetAddressArrayList.size());
                    for(InetAddress inetAdd : inetAddressArrayList){
                        packet = new DatagramPacket(buffer, buffer.length, inetAdd, 8889);

                        try {
                            socket.send(packet);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
                else{
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    DatagramSocket socket = new DatagramSocket(null);
                    socket.setBroadcast(true);
                    socket.setReuseAddress(true);
                    InetSocketAddress socketAddress = new InetSocketAddress(8889);
                    socket.bind(socketAddress);

                    socket.receive(packet);

                    String dataReceived = new String(packet.getData(), 0, packet.getLength());
                    dataReceived = dataReceived.replaceAll("\0", "");
                    String[] splitData = dataReceived.split(":");

                    if(!server.directory.addressMap.containsKey(splitData[0])){
                        server.directory.register(splitData[0], splitData[1]);
                    }
                    System.out.println(server.directory.addressMap);
                    flag = true;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private static List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces
                = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            networkInterface.getInterfaceAddresses().stream()
                    .map(InterfaceAddress::getBroadcast)
                    .filter(Objects::nonNull)
                    .forEach(broadcastList::add);
        }
        return broadcastList;
    }
}
