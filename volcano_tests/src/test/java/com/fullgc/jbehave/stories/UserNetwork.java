package com.fullgc.jbehave.stories;

import com.fullgc.api.RequestDispatcher;
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

/**
 * Created by dani on 26/09/18.
 */
@ContextConfiguration(locations = "/spring-context.xml")
@Service
public class UserNetwork {

    @Rule
    public SpringIntegration springIntegration = new SpringIntegration();

    @Autowired
    CacheBean cache;

    @When("$user is trying to add $friend to his network")
    public void  addFriend(@Named("user")String user, @Named("friend")String friend) {
        RequestDispatcher.addFriend(cache.getToken().get(user), user, friend);
    }

    @Then("$friend has been added to the friends list of $user")
    public void findUserInUserList(@Named("user")String user, @Named("friend")String friend) throws IOException {
        assert RequestDispatcher.findFriend(cache.getToken().get(user), user, friend).getResponseBody().equals(friend);
    }

}