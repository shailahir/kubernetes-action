package com.shailahir.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }
}
