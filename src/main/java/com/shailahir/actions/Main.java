package com.shailahir.actions;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Main {

    KubeAuth kubeAuth = new KubeAuth();
    KubectlExecutor kubectlExecutor = new KubectlExecutor();

    private void init() {
        this.kubeAuth.setup();
    }

    private void tearDown() throws IOException {
        this.kubeAuth.destroy();
    }

    private int start() {
        this.kubectlExecutor.setAuth(kubeAuth);

        String deployments = System.getenv("INPUT_DEPLOYMENTS");
        String deploymentDelimiter = System.getenv("INPUT_DEPLOYMENTSDELIMITER");

        if (deployments != null && deploymentDelimiter != null) {
            String[] deploymentFilenames = deployments.split(deploymentDelimiter);

            // TODO: execute with thead pools
            for (String filename : deploymentFilenames) {
                Path path = Paths.get(filename);
                System.out.println("Path = " + path);

                if (path.toFile().exists()) {
                    this.kubectlExecutor.executeCommand("apply", "-f", path.toAbsolutePath().toString());
                }
            }

            this.kubectlExecutor.executeCommand("get", "pods");
        }

        return 0;
    }

    static void main(String[] args) throws IOException, InterruptedException {
        Main main = new Main();

        System.out.println("Main command called with args: " + Arrays.toString(args));

        try {
            main.init();

            int exitCode = main.start();

            main.tearDown();

            System.exit(exitCode);
        } catch (Exception e) {

            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
