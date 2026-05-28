
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

        LOGGER.info("Action started");

        this.kubectlExecutor.setAuth(kubeAuth);

        String deployments =
                System.getenv("INPUT_DEPLOYMENTS");

        String deploymentsDelimiter =
                System.getenv("INPUT_DEPLOYMENTSDELIMITER");

        String namespace =
                System.getenv("INPUT_NAMESPACE");

        String rolloutDeployments =
                System.getenv("INPUT_ROLLOUTDEPLOYMENTS");

        String rolloutTimeout =
                System.getenv("INPUT_ROLLOUTTIMEOUT");

        String dryRun =
                System.getenv("INPUT_DRYRUN");

        String showPods =
                System.getenv("INPUT_SHOWPODS");

        /*
         * Defaults
         */
        if (deploymentsDelimiter == null ||
                deploymentsDelimiter.isBlank()) {

            deploymentsDelimiter = ",";
        }

        if (namespace == null || namespace.isBlank()) {
            namespace = "default";
        }

        if (rolloutTimeout == null ||
                rolloutTimeout.isBlank()) {

            rolloutTimeout = "300s";
        }

        if (dryRun == null || dryRun.isBlank()) {
            dryRun = "false";
        }

        if (showPods == null || showPods.isBlank()) {
            showPods = "true";
        }

        LOGGER.info("Namespace: {}", namespace);

        // Apply deployments
        if (deployments != null &&
                !deployments.isBlank()) {

            LOGGER.info("Deployment mode enabled");

            String[] deploymentFiles =
                    deployments.split(deploymentsDelimiter);

            LOGGER.info("Deployments count: {}",
                    deploymentFiles.length);

            for (String filename : deploymentFiles) {

                String trimmedFilename = filename.trim();

                Path path = Paths.get(trimmedFilename);

                LOGGER.info("Checking deployment file: {}",
                        path);

                if (!path.toFile().exists()) {

                    LOGGER.error(
                            "Deployment file does not exist: {}",
                            trimmedFilename
                    );

                    return 1;
                }

                LOGGER.info("Applying deployment file: {}",
                        trimmedFilename);

                int result;

                if ("true".equalsIgnoreCase(dryRun)) {

                    result = this.kubectlExecutor.executeCommand(
                            "apply",
                            "--dry-run=client",
                            "-f",
                            path.toString(),
                            "-n",
                            namespace
                    );

                } else {

                    result = this.kubectlExecutor.executeCommand(
                            "apply",
                            "-f",
                            path.toString(),
                            "-n",
                            namespace
                    );
                }

                if (result != 0) {

                    LOGGER.error(
                            "Failed applying deployment file: {}",
                            trimmedFilename
                    );

                    return result;
                }
            }
        }

        // Rollout restart support
        if (rolloutDeployments != null && !rolloutDeployments.isBlank()) {
            LOGGER.info("Rollout restart enabled");
            String[] deploymentNames = rolloutDeployments.split(deploymentsDelimiter);
            for (String deploymentName : deploymentNames) {
                String trimmedDeployment = deploymentName.trim();

                LOGGER.info("Rolling out deployment: {}", trimmedDeployment); /* * Restart deployment */
                int restartResult = this.kubectlExecutor.executeCommand("rollout", "restart", "deployment/" + trimmedDeployment, "-n", namespace);
                if (restartResult != 0) {
                    LOGGER.error("Rollout restart failed: {}", trimmedDeployment);
                    return restartResult;
                }

                // Wait for rollout
                LOGGER.info("Waiting for rollout status: {}", trimmedDeployment);
                int rolloutStatusResult = this.kubectlExecutor.executeCommand("rollout", "status", "deployment/" + trimmedDeployment, "-n", namespace, "--timeout=" + rolloutTimeout);
                if (rolloutStatusResult != 0) {
                    LOGGER.error("Rollout status failed: {}", trimmedDeployment); /* * Fetch deployment diagnostics */
                    LOGGER.error("Fetching deployment diagnostics");
                    this.kubectlExecutor.executeCommand("describe", "deployment", trimmedDeployment, "-n", namespace);
                    this.kubectlExecutor.executeCommand("get", "pods", "-n", namespace, "-o", "wide");
                    return rolloutStatusResult;
                }

                // Explicit health wait
                LOGGER.info("Waiting for deployment to become Available");
                int availableResult = this.kubectlExecutor.executeCommand("wait", "--for=condition=Available", "deployment/" + trimmedDeployment, "-n", namespace, "--timeout=" + rolloutTimeout);
                if (availableResult != 0) {
                    LOGGER.error("Deployment did not become healthy: {}", trimmedDeployment);
                    this.kubectlExecutor.executeCommand("describe", "deployment", trimmedDeployment, "-n", namespace);
                    this.kubectlExecutor.executeCommand("get", "pods", "-n", namespace, "-o", "wide");
                    return availableResult;
                }
                LOGGER.info("Deployment is healthy: {}", trimmedDeployment);
            }
        }



        /*
         * Show pods
         */
        if ("true".equalsIgnoreCase(showPods)) {

            LOGGER.info("Fetching pod status");

            this.kubectlExecutor.executeCommand(
                    "get",
                    "pods",
                    "-n",
                    namespace
            );
        }

        LOGGER.info("Action completed successfully");

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
