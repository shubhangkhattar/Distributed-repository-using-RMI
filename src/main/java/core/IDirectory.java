package core;

import java.net.URI;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public interface IDirectory extends Remote {

    public HashMap<String, String> addressMap = new HashMap<>();
    
    public IDistributedRepository find(String id) throws RemoteException;

    public ArrayList<String> list() throws RemoteException;
}
