package com.shailahir.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class.getName());

    KubeAuth kubeAuth = new KubeAuth();
    KubectlExecutor kubectlExecutor = new KubectlExecutor();

    private void init() {
        LOGGER.debug("init started");

        this.kubeAuth.setup();

        LOGGER.debug("init finished");
    }

    private void tearDown() {
        LOGGER.debug("tearDown started");

        this.kubeAuth.destroy();

        LOGGER.debug("tearDown finished");
    }

    private int start() {
        LOGGER.debug("action started");
        LOGGER.debug("Setting auth");

        this.kubectlExecutor.setAuth(kubeAuth);

        String deployments = System.getenv("INPUT_DEPLOYMENTS");
        String deploymentDelimiter = System.getenv("INPUT_DEPLOYMENTSDELIMITER");

        if (deployments != null && deploymentDelimiter != null) {
            LOGGER.info("Executing in deployment mode");

            String[] deploymentFilenames = deployments.split(deploymentDelimiter);

            LOGGER.info("# of deployments = {}", deploymentFilenames.length);

            for (String filename : deploymentFilenames) {
                Path path = Paths.get(filename);

                LOGGER.info("Checking deployment file {}", path);

                if (path.toFile().exists()) {
                    LOGGER.info("File exists, dispatching deployment command");
                    
                    this.kubectlExecutor.executeCommand("apply", "-f", path.toString());
                } else {
                    LOGGER.warn("deployment file {} does not exist", filename);
                }
            }

            this.kubectlExecutor.executeCommand("get", "pods");
        }

        return 0;
    }

    static void main(String[] args) {
        Main main = new Main();

        try {
            main.init();

            int exitCode = main.start();

            main.tearDown();

            System.exit(exitCode);
        } catch (Exception e) {


            System.exit(1);
        }
    }
}
