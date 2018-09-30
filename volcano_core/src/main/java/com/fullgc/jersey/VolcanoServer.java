package com.fullgc.jersey;

import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.*;

/**
 * Created by dani on 24/09/18.
 */
@Path("/users")
@Component
public class VolcanoServer {

    Map<String, User> users = new HashMap<String, User>();
    //http GET just for the sake of the example..!
    @GET
    @Path("/signup")
    public void signup(@QueryParam("user") String user,
                          @QueryParam("password") String password) {
     users.put(user, new User(user, password));
    }

    @GET
    @Path("/login")
    public String login(@QueryParam("user") String user,
                         @QueryParam("password") String password) {
        Boolean canLogIn = users.get(user) != null && users.get(user).getPassword().equals(password);
        if (canLogIn) {
            String session = UUID.randomUUID().toString();
            users.get(user).setToken(session);
            return session;
        } else return null;
    }

    @GET
    @Path("/password")
    public String changePassword(@QueryParam("token") String token, @QueryParam("user") String user,
                        @QueryParam("oldPassword") String oldPassword, @QueryParam("newPassword") String newPassword) {
        if (token.equals(users.get(user).getToken()) && users.get(user).getPassword().equals(oldPassword) && newPassword.length() >= 6) {
            users.get(user).setPassword(newPassword);
            return "OK";
        }else return "error";
    }

    @GET
    @Path("/addFriend")
    public void addFriend(@QueryParam("token") String token, @QueryParam("user") String user,
                                 @QueryParam("friend") String friend) {
        if (token.equals(users.get(user).getToken())) users.get(user).getFriends().add(friend);
    }
    @GET
    @Path("/findFriend")
    public String findFriend(@QueryParam("token") String token, @QueryParam("user") String user,
                          @QueryParam("friend") String friend) {
        return token.equals(users.get(user).getToken()) && users.get(user).getFriends().contains(friend) ? friend : null;
    }
}

class User{

    private String password;
    private String name = "";
    private Set<String> friends = new HashSet<>();

    private String token = null;

    public User(String name, String password){
        this.name = name;
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getFriends() {
        return friends;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }
}