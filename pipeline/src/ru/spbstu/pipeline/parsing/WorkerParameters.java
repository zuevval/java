package ru.spbstu.pipeline.parsing;

import ru.spbstu.pipeline.logging.Logger;

public class WorkerParameters {
    public final String className;
    public final String configFilename;
    private static final int parametersQuantity = 2;

    public WorkerParameters(String workerDescription, String delimiter, Logger logger){
        String [] parameters = workerDescription.split(delimiter);
        if (parameters.length != parametersQuantity){
            logger.log("wrong number of parameters, must be " +
                    parametersQuantity + ": " + workerDescription);
            className = null;
            configFilename = null;
        } else {
            className = parameters[0];
            configFilename = parameters[1];
        }
    }
}
