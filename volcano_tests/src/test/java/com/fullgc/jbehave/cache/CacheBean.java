package com.fullgc.jbehave.cache;

import net.thucydides.junit.spring.SpringIntegration;
import org.junit.Rule;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dani on 28/09/18.
 */
@Service
@Scope("singleton")
@ContextConfiguration(locations = "/spring-context.xml")
public class CacheBean {

    @Rule
    public SpringIntegration springIntegration = new SpringIntegration();

    Map<String, String> token = new HashMap<>();

    Map<String, String> users = new HashMap<String, String>();

    public Map<String, String> getToken() {
        return token;
    }

    public Map<String, String> getUsers() {
        return users;
    }

}

