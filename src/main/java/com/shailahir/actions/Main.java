package com.shailahir.actions;

public class Main {

    static void main(String[] args) {
        String testInput = System.getenv("INPUT_TESTINPUT");

        if (testInput == null) {
            testInput = "Hard coded Test Input";
        }

        System.out.println("Input : " + testInput);
    }
}
