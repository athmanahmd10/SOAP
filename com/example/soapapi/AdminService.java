package com.example.soapapi;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface AdminService {
    @WebMethod
    String authenticate(@WebParam(name = "username") String username, @WebParam(name = "password") String password);

    @WebMethod
    List<User> getUsers(@WebParam(name = "token") String token);

     @WebMethod
    boolean addUser(@WebParam(name = "username") String username, @WebParam(name = "password") String password, 
    @WebParam(name = "admin") int admin, @WebParam(name = "token") String token);

    @WebMethod
    boolean removeUser(@WebParam(name = "username") String username,  @WebParam(name = "token") String token);

     @WebMethod
    boolean modifyUser(@WebParam(name = "username") String username, @WebParam(name = "newUsername") String newUsername,
     @WebParam(name = "password") String password, @WebParam(name = "newState") int newState, @WebParam(name = "token") String token);

    @WebMethod
    User getUser(@WebParam(name="username") String username, @WebParam(name="token") String token);

    @WebMethod
    boolean deconnect(@WebParam(name="username") String username, @WebParam(name="token") String token);
}
