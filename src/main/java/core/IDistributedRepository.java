package core;

import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IDistributedRepository extends IRepository {

    public ArrayList<IAggregate> aggregate(ArrayList<String> repids) throws RemoteException;

    public IDistributedRepository findServer(String id) throws RemoteException;

}
