package com.fullgc.jetty;


/**
 * Created by dani on 24/09/18.
 */

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AllowSymLinkAliasChecker;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;

/**
 * An example of how to manually bootstrap a Jetty server
 */
public class JettyServer {

    public static void main(String[] args) throws Exception {

        Server server = new Server(8083);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/volcano/");
        File warFile = new File("volcano_core/target/volcano-1.2-SNAPSHOT.war");
        context.setWar(warFile.getAbsolutePath());
        context.addAliasCheck(new AllowSymLinkAliasChecker());

        server.setHandler(context);

        server.start();

        server.join();

    }
}
