package com.fullgc.jbehave.steps;

/**
 * Created by dani on 24/09/18.
 */

import com.fullgc.dispatcher.RequestDispatcher;
import com.fullgc.jbehave.cache.CacheBean;
import net.thucydides.junit.spring.SpringIntegration;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;


@ContextConfiguration(locations = "/spring-context.xml")
@Service
public class GivenUserSteps {

    @Rule
    public SpringIntegration springIntegration = new SpringIntegration();

    @Autowired
    CacheBean cache;

    @Given("$user is a Volcano enthusiast")
    public String user() {
        return "good for you!";
    }

    @Given("$user is logged in")
    public void logIn(@Named("user")String user) throws IOException {
        if (cache.getToken().get(user) == null){
            String session = RequestDispatcher.logIn(user, cache.getUsers().get(user)).getResponseBody();
            assert session != null;
            cache.getToken().put(user, session);
        }
    }

    @Given("$user has registered to Volcano")
    public void quickSignup(@Named("user")String user) {
        RequestDispatcher.createUser(user, "password");
    }

}
