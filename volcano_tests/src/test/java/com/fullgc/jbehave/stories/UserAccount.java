package com.fullgc.jbehave.stories;

import com.fullgc.dispatcher.RequestDispatcher;
import com.fullgc.jbehave.cache.CacheBean;
import net.thucydides.junit.spring.SpringIntegration;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.Objects;

/**
 * Created by dani on 26/09/18.
 */
@ContextConfiguration(locations = "/spring-context.xml")
@Service
public class UserAccount {

    @Rule
    public SpringIntegration springIntegration = new SpringIntegration();

    @Autowired
    CacheBean cache;

    @When("$user is signing up for Volcano with the user-name: $user and the password: $password")
    public void  newUser(@Named("user")String user, @Named("password") String password) {
        RequestDispatcher.createUser(user, password);
        cache.getUsers().put(user, password);
    }

    @When("$user is changing his password to $newPassword")
    public void  changePassword(@Named("user")String user, @Named("newPassword") String newPassword) throws IOException {
        String response = RequestDispatcher.changePassword(cache.getToken().get(user), user, cache.getUsers().get(user), newPassword).getResponseBody();
        if (response.equals("OK"))
            cache.getUsers().put(user, newPassword);
    }

    @Then("$user is $ableorNot to log in with the user-name: $user and the password: $password")
    public void logIn(@Named("user")String user, @Named("ableorNotAble")String ableOrNot, @Named("password") String password) throws IOException {
        String token = RequestDispatcher.logIn(user, password).getResponseBody();
        if (Objects.equals(ableOrNot, "able")){
            assert !token.isEmpty();
            cache.getToken().put(user, token);
        }
        else assert token.isEmpty();
    }

}