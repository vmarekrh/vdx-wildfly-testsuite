/*
 * Copyright 2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  *
 * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.wildfly.test.integration.vdx.utils.server;

import org.jboss.arquillian.container.test.api.ContainerController;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface Server {

    String JBOSS_HOME = System.getProperty("jboss.home", "jboss-as");
    String DEFAULT_SERVER_CONFIG = isDomain() ? "domain.xml" : "standalone.xml";
    String DEFAULT_HOST_CONFIG = "host.xml";

    String STANDALONE_DIRECTORY = "standalone";
    String DOMAIN_DIRECTORY = "domain";

    String PATH_TO_STANDALONE_DIRECTORY = Paths.get(Server.JBOSS_HOME, STANDALONE_DIRECTORY, "configuration").toString();
    String PATH_TO_DOMAIN_DIRECTORY = Paths.get(Server.JBOSS_HOME, DOMAIN_DIRECTORY, "configuration").toString();

    String STANDALONE_RESOURCES_DIRECTORY = "configurations" + File.separator + "standalone" + File.separator;
    String DOMAIN_RESOURCES_DIRECTORY = "configurations" + File.separator + "domain" + File.separator;

    String LOGGING_PROPERTIES_FILE_NAME = "logging.properties";
    String ERRORS_LOG_FILE_NAME = "target/errors.log";

    Server server = null;

    /**
     * Starts the server. If @ServerConfig annotation is present on method in calling stacktrace (for example test method) then
     * it's applied before the server is started.
     *
     * Start of the server is expected to fail due to xml syntac error. It does not throw any exception when  tryStartAndWaitForFail of server fails.
     *
     * @throws Exception when something unexpected happens
     */
    void tryStartAndWaitForFail() throws Exception;

    /**
     * Stops server.
     *
     * Not really useful for this testing but can be handy.
     */
    void stop();

    /**
     *
     * @return true if started server is in domain mode. false if it's standalone mode.
     */
    static boolean isDomain() {
        return Boolean.parseBoolean(System.getProperty("domain", "false"));
    }

    /**
     * Creates instance of server. If -Ddomain=true system property is specified it will be domain server,
     * otherwise standalone server will be used.
     *
     * @param controller arquillian container controller
     * @return Server instance - standalone by default or domain if -Ddomain=true is set
     */
    static Server getOrCreate(ContainerController controller) {
        if (server == null) {
            if (isDomain()) {
                return new ManagedDomain(controller);
            } else {
                return new StandaloneServer(controller);
            }
        }
        return server;
    }

    Path getServerLogPath();

    String getErrorMessageFromServerStart() throws Exception;
}
