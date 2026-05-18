package com.shailahir.actions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    static void main(String[] args) throws IOException, InterruptedException {
        String kubeconfig = System.getenv("INPUT_KUBECONFIG");

        Path kubeconfigPath = Files.createTempFile("kubeconfig", ".yaml");

        Files.writeString(kubeconfigPath, kubeconfig);

        ProcessBuilder pb = new ProcessBuilder(
                "kubectl",
                "get",
                "pods",
                "-A"
        );

        pb.environment().put(
                "KUBECONFIG",
                kubeconfigPath.toString()
        );

        pb.inheritIO();

        Process process = pb.start();

        int exitCode = process.waitFor();

        Files.deleteIfExists(kubeconfigPath);

        System.exit(exitCode);
    }
}
