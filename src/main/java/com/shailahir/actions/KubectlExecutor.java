package com.shailahir.actions;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class KubectlExecutor {

    private final static String KUBECTL_EXECUTABLE = "kubectl";

    private KubeAuth kubeAuth;

    public int executeCommand(String... args) {
        String kubectlCommand = String.join(" ", args);

        ProcessBuilder pb = new ProcessBuilder(KUBECTL_EXECUTABLE, kubectlCommand);

//        pb.command(command);


        System.out.println("Kubectl executing command: " + Arrays.toString(args));

        if (this.kubeAuth == null) {
            System.out.println("Kubectl auth is null");
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
            System.out.println("Starting Kubectl executing command: " + Arrays.toString(args));
            process = pb.start();

            System.out.println("Kubectl executing command: " + Arrays.toString(args));
            return process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void setAuth(KubeAuth kubeAuth) {
        this.kubeAuth = kubeAuth;
    }
}
