package client;

import core.IAggregate;
import core.IDistributedRepository;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        String host = "localhost";
        Integer port = 6231;

        try{
            Registry registry = LocateRegistry.getRegistry(host, port);
            IDistributedRepository server = (IDistributedRepository) registry.lookup("repository");
            Scanner scanner = new Scanner(System.in);
            while(true) {
                String data = scanner.nextLine();
                String[] clientCommand = data.split(" ");
                String operation = clientCommand[0];

                switch (operation) {
                    case "ADD": {
                        if(clientCommand.length<3){
                            System.out.println("PLEASE ENTER PROPER DETAILS");
                            break;
                        }
                        String[] addressAndKey = clientCommand[1].split("\\.");

                        if(addressAndKey.length==1){
                            server.add(clientCommand[1], Integer.parseInt(clientCommand[2]));
                            System.out.println("OK");
                        }
                        else{
                            String address = addressAndKey[0];
                            String key = addressAndKey[1];
                            IDistributedRepository remoteRepository = server.findServer(address);
                            remoteRepository.add(key, Integer.parseInt(clientCommand[2]));
                            System.out.println("OK");
                        }
                        break;
                    }
                    case "SET": {
                        if(clientCommand.length<3){
                            System.out.println("PLEASE ENTER PROPER DETAILS");
                            break;
                        }
                        String[] addressAndKey = clientCommand[1].split("\\.");
                        if(addressAndKey.length==1){
                            server.set(clientCommand[1], Integer.parseInt(clientCommand[2]));
                            System.out.println("OK");
                        }
                        else{
                            String address = addressAndKey[0];
                            String key = addressAndKey[1];
                            IDistributedRepository remoteRepository = server.findServer(address);
                            remoteRepository.set(key, Integer.parseInt(clientCommand[2]));
                            System.out.println("OK");
                        }
                        break;
                    }
                    case "DELETE": {
                        if(clientCommand.length<2){
                            System.out.println("PLEASE ENTER PROPER DETAILS");
                            break;
                        }
                        String[] addressAndKey = clientCommand[1].split("\\.");
                        if(addressAndKey.length==1){
                            server.delete(clientCommand[1]);
                            System.out.println("OK");
                        }
                        else{
                            String address = addressAndKey[0];
                            String key = addressAndKey[1];
                            IDistributedRepository remoteRepository = server.findServer(address);
                            remoteRepository.delete(key);
                            System.out.println("OK");
                        }

                        break;
                    }
                    case "LIST": {
                        if(clientCommand.length<2){
                            System.out.println("PLEASE ENTER PROPER DETAILS");
                            break;
                        }
                        String[] addressAndKey = clientCommand[1].split("\\.");
                        if(addressAndKey.length==1){
                            ArrayList<String> keysList = server.list();
                            if(keysList==null){
                                System.out.println("Empty List");
                                break;
                            }
                            System.out.println("KEYS are as follows:");
                            for(String s: keysList){
                                System.out.print(s+" ");
                            }
                            System.out.println();
                        }
                        else{
                            String address = addressAndKey[0];
                            String key = addressAndKey[1];
                            String command = "REMOTE " + data;
                            System.out.println(server.handleRemoteRequest(command));
                        }

                        break;
                    }
                    case "GETS": {
                        if(clientCommand.length<2){
                            System.out.println("PLEASE ENTER PROPER DETAILS");
                            break;
                        }
                        String[] addressAndKey = clientCommand[1].split("\\.");
                        if(addressAndKey.length==1){
                            int result = server.getValue(clientCommand[1]);
                            if(result==404){
                                System.out.println("Sorry but value does not exist");
                                break;
                            }
                            System.out.println("Data is: "+result);
                        }
                        else{
                            String address = addressAndKey[0];
                            String key = addressAndKey[1];
                            IDistributedRepository remoteRepository = server.findServer(address);
                            int result = remoteRepository.getValue(key);
                            if(result==404){
                                System.out.println("Sorry but value does not exist");
                                break;
                            }
                            System.out.println("Data is: "+result);
                        }

                        break;
                    }
                    case "GET": {
                        if(clientCommand.length<2){
                            System.out.println("PLEASE ENTER PROPER DETAILS");
                            break;
                        }
                        String[] addressAndKey = clientCommand[1].split("\\.");
                        if(addressAndKey.length==1){
                            ArrayList<Integer> valueList = server.getValues(clientCommand[1]);
                            if(valueList==null){
                                System.out.println("Key value is empty or does not exist at all");
                                break;
                            }
                            System.out.println("Values are as follows:");
                            for(Integer s: valueList){
                                System.out.print(s+" ");
                            }
                            System.out.println();
                        }
                        else{
                            String address = addressAndKey[0];
                            String key = addressAndKey[1];
                            IDistributedRepository remoteRepository = server.findServer(address);
                            ArrayList<Integer> valueList = remoteRepository.getValues(key);
                            if(valueList==null){
                                System.out.println("Key value is empty or does not exist at all");
                                break;
                            }
                            System.out.println("Values are as follows:");
                            for(Integer s: valueList){
                                System.out.print(s+" ");
                            }
                            System.out.println();
                        }
                        break;
                    }
                    case "REMOTE":{
                        System.out.println(server.handleRemoteRequest(data));
                        break;
                    }
                    case "SUM":{
                        if(clientCommand.length<2){
                            System.out.println("PLEASE ENTER PROPER DETAILS");
                            break;
                        }
                        String[] addressAndKey = clientCommand[1].split("\\.");
                        if(addressAndKey.length==1){
                            System.out.println("sum is "+ server.sum(clientCommand[1]));
                        }
                        else{
                            String address = addressAndKey[0];
                            String key = addressAndKey[1];
                            IDistributedRepository remoteRepository = server.findServer(address);
                            int answer = remoteRepository.sum(key);
                            System.out.println("sum  is "+answer);
                        }
                        break;
                    }
                    case "DSUM":{
                        if(clientCommand.length<4){
                            System.out.println("PLEASE ENTER PROPER DETAILS");
                            break;
                        }
                        ArrayList<String> repids = new ArrayList<>();

                        for(int i=3; i<clientCommand.length; i++){
                            repids.add(clientCommand[i]);
                        }
                        String key = clientCommand[1];
                        int ans = 0;
                        ArrayList<IAggregate> serverList = server.aggregate(repids);
                        int id = 0;
                        boolean breakFlag = false;
                        for(IAggregate aggregate:serverList){
                            if(aggregate==null){
                                System.out.println("Server with name:"+repids.get(id)+" does not exist");
                                breakFlag = true;
                            }
                            id++;
                            if(breakFlag){
                                break;
                            }
                            ans += aggregate.sum(key);
                        }
                        if(breakFlag){
                            break;
                        }
//                        try{
//                            for(String s:repids){
//                                String command = "REMOTE SUM " + s  + "." + key;
//                                String answer = server.handleRemoteRequest(command);
//                                ans+=Integer.parseInt(answer);
//                            }
//                        }
//                        catch (Exception e){
//                            System.out.println("Please check server names");
//                            break;
//                        }

                        System.out.println("DSUM is going to be " + ans);
                        break;
                    }
                    case "RESET":{
                        server.reset();
                        System.out.println("DONE");
                        break;
                    }

                    case "EXIT": {

                        return;
                    }
                    default: {
                        System.out.println("Please add proper command");
                        break;
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
