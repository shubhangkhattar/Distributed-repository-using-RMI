package service;

import core.Connector;
import core.IDistributedRepository;
import core.IRepository;
import implementation.Repository;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public final class RepositoryService {
    private static RepositoryService service;
    private String info = "repository service implementation";
    private Repository repository = Repository.getInstance();

    private RepositoryService(){

    }

    public RepositoryService getInstance(){
        if(service==null){
            service = new RepositoryService();
        }
        return service;
    }

    public static void main(String[] args) throws RemoteException {
        int PORT = Integer.parseInt(args[1]);
        String name = args[0];
        IRepository server = Repository.getInstance();
        IDistributedRepository stub = (IDistributedRepository) UnicastRemoteObject.exportObject((IDistributedRepository) server, 0);

        Registry registry = LocateRegistry.createRegistry(PORT);
        registry.rebind("repository", stub);
        server.directory.addressMap.put(name, String.valueOf(PORT));
        System.out.println("server is ready");
        Connector.run(name, PORT);

    }

}
