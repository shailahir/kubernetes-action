package com.shailahir.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KubectlExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(KubectlExecutor.class.getName());

    private final static String KUBECTL_EXECUTABLE = "kubectl";

    private KubeAuth kubeAuth;

    public int executeCommand(String... args) {

        LOGGER.info("Kubectl executing command: {}", Arrays.toString(args));

        List<String> command = new ArrayList<>();

        command.add(KUBECTL_EXECUTABLE);

        if (KubeAuthType.KUBECONFIG == this.kubeAuth.getKubeAuthType()) {
            command.add("--kubeconfig");
            command.add(this.kubeAuth.getKubeConfigFilepath());
        }

        command.addAll(List.of(args));

        ProcessBuilder pb = new ProcessBuilder(command);

        if (this.kubeAuth == null) {
            LOGGER.info("Kubectl auth is null, exiting");
            return 1;
        }


        pb.inheritIO();
        Process process = null;
        try {
            LOGGER.info("Kubectl starting command: {}", Arrays.toString(args));
            process = pb.start();
            LOGGER.info("Kubectl executed command: {}", Arrays.toString(args));
            return process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setAuth(KubeAuth kubeAuth) {
        this.kubeAuth = kubeAuth;
    }
}
