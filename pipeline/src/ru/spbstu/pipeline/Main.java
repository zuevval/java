package ru.spbstu.pipeline;

import ru.spbstu.pipeline.implementation.Transporter;

public class Main {
    private static final String suppressConsoleLogging= "-s";
    private static final int minArguments = 1;
    /** Main.main: create and run an instance of Transporter.
     * Command-line parameters:
     * 0. Configuration file name: relative path under [...]/pipeline/ folder
     *      e. g. 'test_run/transporter.properties'
     * 1. (optional). '-s' to suppress console logging
     * */
    public static void main(String [] args){
        if(args.length < minArguments)
            System.out.println("missing required argument: properties file path");
        String configFilename = args[0];
        boolean suppress = (args.length >= 2 && args[1].equals(Main.suppressConsoleLogging));
        Transporter tr = new Transporter(configFilename, suppress);
        tr.run();

    }
}
