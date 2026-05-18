package com.shailahir.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    static void main(String[] args) throws IOException, InterruptedException {
        String testInput = System.getenv("INPUT_TESTINPUT");

        if (testInput == null) {
            testInput = "Hard coded Test Input";
        }

        System.out.println("Input : " + testInput);

        ProcessBuilder pb = new ProcessBuilder(
                "kubectl",
                "version",
                "--client"
        );

        pb.redirectErrorStream(true);

        Process process = pb.start();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );

        String line;

        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();

        System.out.println("Exit code: " + exitCode);


        String kubeconfig = System.getenv("INPUT_KUBECONFIG");

        Path kubeconfigPath = Files.createTempFile("kubeconfig", ".yaml");

        Files.writeString(kubeconfigPath, kubeconfig);

        pb = new ProcessBuilder(
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

        process = pb.start();

        exitCode = process.waitFor();

        Files.deleteIfExists(kubeconfigPath);

        System.exit(exitCode);
    }
}
