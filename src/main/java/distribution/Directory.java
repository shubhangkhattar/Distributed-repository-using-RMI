package distribution;

import core.IDirectory;
import core.IDistributedRepository;
import implementation.Repository;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public final class Directory implements IDirectory {

    private static Directory directory;
    private static Repository repository = Repository.getInstance();

    public Directory(){

    }


    public static Directory getInstance(){
        if(directory==null){
            directory = new Directory();
        }
        return directory;
    }

    public void register(String id, String port){
        repository.directory.addressMap.put(id, port);
        System.out.println("put done "+repository.directory.addressMap);
    }

    @Override
    public IDistributedRepository find(String id) throws RemoteException {
        String port = repository.directory.addressMap.get(id);
        System.out.println(repository.directory.addressMap);

        try{
            Registry registry = LocateRegistry.getRegistry("localhost", Integer.parseInt(port));
            IDistributedRepository server = (IDistributedRepository) registry.lookup("repository");
            return server;
        }
        catch (Exception e){
            System.out.println("SERVER WITH NAME:"+id+" DOES NOT FOUND");
        }

        return null;
    }

    @Override
    public ArrayList<String> list() {
        ArrayList<String> result = new ArrayList<>();
        for ( String key : addressMap.keySet() ) {
            result.add(key);
        }
        return result;
    }



}
