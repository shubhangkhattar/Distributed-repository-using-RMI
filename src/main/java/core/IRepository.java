package core;

import distribution.Directory;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IRepository extends IAggregate, Remote {

//    public HashMap<String, String> addressMap = new HashMap<>();

    public Directory directory = Directory.getInstance();

    public void add(String map, int value) throws RemoteException;

    public void set(String key, int value) throws RemoteException;

    public void delete(String key) throws RemoteException;

    public ArrayList<String> list() throws RemoteException;

    public int getValue(String key) throws RemoteException;

    public ArrayList<Integer> getValues(String key) throws RemoteException;

    public String handleRemoteRequest(String data) throws RemoteException, NotBoundException;

    //TO-DO aggregate part

    public void reset() throws RemoteException;

    //TO-DO Enum keys


    //TO-DO Enum values


}
