package ru.spbstu.pipeline;

import ru.spbstu.pipeline.implementation.Transporter;
import ru.spbstu.pipeline.logging.*;
import ru.spbstu.pipeline.parsing.TransporterParser;
import ru.spbstu.pipeline.parsing.WorkerParameters;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String [] args){
        Logger logger = UtilLogger.of(java.util.logging.Logger.getLogger("logger"));
        Transporter tr = new Transporter("test_run/transporter.properties", logger);
        tr.run();

    }
}
