package com.selesse.jxlint.actions;

import com.google.common.io.Resources;
import com.selesse.jxlint.cli.ProgramOptionExtractor;
import com.selesse.jxlint.settings.ProgramSettings;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URI;
import java.net.URL;

public class JettyWebRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(JettyWebRunner.class);
    private static final String webApplicationDirectory = "webapp";
    private static final String contextPath = "/";
    private ProgramSettings programSettings;
    private String port;

    public JettyWebRunner(ProgramSettings programSettings, String port) {
        this.programSettings = programSettings;
        this.port = port;
    }

    public void start() {
        startJetty();
    }

    private void startJetty() {
        // Set JSP to use Standard JavaC always
        System.setProperty("org.apache.jasper.compiler.disablejsr199", "false");

        int portIntValue;

        try {
            portIntValue = Integer.parseInt(port);
        }
        catch (NumberFormatException e) {
            System.err.println("Error parsing port '" + port + "', reverting to default");
            portIntValue = Integer.parseInt(ProgramOptionExtractor.DEFAULT_PORT);
        }

        Server server = new Server(portIntValue);

        URL warUrl = Resources.getResource(webApplicationDirectory);
        String warUrlString = warUrl.toExternalForm();
        WebAppContext context = new WebAppContext(warUrlString, contextPath);
        context.setAttribute("programSettings", programSettings);
        server.setHandler(context);

        try {
            server.start();

            URI jettyUri = new URI("http://localhost:" + port);

            Desktop desktop = Desktop.getDesktop();
            try {
                LOGGER.info("Opening user's browser to {}", jettyUri);
                desktop.browse(jettyUri);
            }
            catch (Exception e) {
                LOGGER.error("Error opening " + jettyUri + ", try visiting the URL manually", e);
            }

            server.join();
        }
        catch (Exception e) {
            LOGGER.error("Error starting Jetty", e);
        }
    }
}
