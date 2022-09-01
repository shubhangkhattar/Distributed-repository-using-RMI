package core;

import java.net.URI;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface IRegistry extends IDirectory, Remote {
    public static HashMap<String, String> addressMap = new HashMap<>();

    public void register(String id, String uri) throws RemoteException;
    public void unregister(String id) throws RemoteException;
}
