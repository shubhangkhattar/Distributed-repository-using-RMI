package implementation;

import core.IAggregate;
import core.IDistributedRepository;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;

public final class Repository implements IDistributedRepository {

    private static Repository repository;
    private HashMap<String, ArrayList<Integer>> map = new HashMap<>();


    private Repository(){

    }

    public static Repository getInstance(){
        if(repository==null){
            repository = new Repository();
        }
        return repository;
    }

    @Override
    public int sum(String key) throws RemoteException {
        int sum = 0;
        if(repository.map.containsKey(key)){
            ArrayList<Integer> list = repository.map.get(key);
            for(int i:list){
                sum += i;
            }
        }
        return sum;
    }

    @Override
    public void add(String key, int value) throws RemoteException{
        if(repository.map.containsKey(key)){
            ArrayList<Integer> list = repository.map.get(key);
            list.add(value);
            repository.map.put(key, list);
        }
        else{
            ArrayList<Integer> list = new ArrayList<>();
            list.add(value);
            repository.map.put(key, list);
        }
    }

    @Override
    public void set(String key, int value) throws RemoteException{
        ArrayList<Integer> list = new ArrayList<>();
        list.add(value);
        repository.map.put(key, list);
    }

    @Override
    public void delete(String key) throws RemoteException {
        if(repository.map.containsKey(key)){
            repository.map.remove(key);
        }
    }

    @Override
    public ArrayList<String> list() throws RemoteException {
        ArrayList<String> keyList = new ArrayList<>();
        for ( String key : repository.map.keySet() ) {
            keyList.add(key);
        }
        return keyList;
    }

    @Override
    public int getValue(String key) throws RemoteException {
        if(repository.map.containsKey(key)){
            ArrayList<Integer> list = repository.map.get(key);
            return list.get(0);
        }
        else{
            return 404;
        }
    }

    @Override
    public ArrayList<Integer> getValues(String key) throws RemoteException{
        if(repository.map.containsKey(key)){
            ArrayList<Integer> list = repository.map.get(key);
            return list;
        }
        else{
            return null;
        }
    }

    @Override
    public void reset() throws RemoteException {
        repository.map = new HashMap<>();
    }

    @Override
    public ArrayList<IAggregate> aggregate(ArrayList<String> repids) throws RemoteException {
        ArrayList<IAggregate> aggregates = new ArrayList<>();
        for(String id:repids){
            IAggregate remoteRepository = repository.findServer(id);
            aggregates.add(remoteRepository);
        }
        return aggregates;
    }

    @Override
    public IDistributedRepository findServer(String id) throws RemoteException {
        return repository.directory.find(id);
    }

    @Override
    public String handleRemoteRequest(String data) throws RemoteException, NotBoundException {
        try{
            String[] splittedData = data.split(" ");

            System.out.println(splittedData.length);

            String action = splittedData[1];

            String[] addressAndKey = splittedData[2].split("\\.");

            String address = addressAndKey[0];
            String key = addressAndKey[1];

            String answer = "";

            int port = Integer.parseInt(directory.addressMap.get(address));

            Registry registry = LocateRegistry.getRegistry("localhost", port);

            IDistributedRepository server = (IDistributedRepository) registry.lookup("repository");

            switch(action){
                case "ADD":{
                    server.add(key, Integer.parseInt(splittedData[3]));
                    answer += "OK";
                    break;
                }
                case "GET":{
                    ArrayList<Integer> values = server.getValues(key);
                    for(Integer i: values){
                        answer+=i + " ";
                    }
                    break;
                }
                case "GETS":{
                    answer += server.getValue(key);
                    break;
                }
                case "DELETE":{
                    server.delete(key);
                    answer += "OK";
                    break;
                }
                case "LIST":{
                    ArrayList<String> keys = server.list();
                    for(String k:keys){
                        answer += k + " ";
                    }
                    break;
                }
                case "SUM":{
                    answer += server.sum(key);
                }
                case "SET":{
                    server.set(key, Integer.parseInt(splittedData[3]));
                    answer += "OK";
                    break;
                }
            }

            return answer;
        }
        catch (Exception e){
            return "SOMETHING WENT WRONG, Please check server name properly";
        }

    }
}
