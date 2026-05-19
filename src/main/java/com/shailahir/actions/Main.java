package com.shailahir.actions;

import java.io.IOException;

public class Main {

    KubeAuth kubeAuth = new KubeAuth();
    KubectlExecutor kubectlExecutor = new KubectlExecutor();

    private void init() {
        this.kubeAuth.setup();
    }

    private void tearDown() throws IOException {
        this.kubeAuth.destroy();
    }

    private int start() throws IOException {
        this.kubectlExecutor.setAuth(kubeAuth);

        // TODO: Handle deployments
        return kubectlExecutor.executeCommand("get", "pods", "-a");
    }

    static void main(String[] args) throws IOException, InterruptedException {
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
