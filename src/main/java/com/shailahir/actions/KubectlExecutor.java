package com.shailahir.actions;

import java.io.IOException;
import java.util.Arrays;

public class KubectlExecutor {

    private final static String KUBECTL_EXECUTABLE = "kubectl";

    private KubeAuth kubeAuth;

    public int executeCommand(String... args) {
        ProcessBuilder pb = new ProcessBuilder(
                KUBECTL_EXECUTABLE
        );

        pb.command(args);

        System.out.println("Kubectl executing command: " + Arrays.toString(args));

        if (this.kubeAuth == null) {
            return 1;
        }

        if (KubeAuthType.KUBECONFIG == this.kubeAuth.getKubeAuthType()) {
            pb.environment().put(
                    "KUBECONFIG",
                    KubeAuth.KUBECONFIG_FILE
            );
        }

        pb.inheritIO();

        Process process = null;
        try {
            process = pb.start();
            return process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void setAuth(KubeAuth kubeAuth) {
        this.kubeAuth = kubeAuth;
    }
}
