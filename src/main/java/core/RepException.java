package core;

import java.rmi.RemoteException;

public class RepException extends RemoteException {

    public RepException(String s){
        super(s);
    }
}
