package ru.spbstu.pipeline.implementation;

import ru.spbstu.pipeline.*;
import ru.spbstu.pipeline.logging.*;
import ru.spbstu.pipeline.parsing.TransporterParser;
import ru.spbstu.pipeline.parsing.WorkerParameters;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class Transporter {
    private static final String loggerTitle = "logger";
    private static final String logFilename = "transporter.log";
    private List<Executor> executors;
    private Reader reader;
    private Writer writer;
    private Logger logger;
    private Status status = Status.OK;

    public Transporter(String configFilename, boolean suppressConsoleLogging){
        java.util.logging.Logger utilLogger = java.util.logging.Logger.getLogger(loggerTitle);
        FileHandler fh;
        try {
            fh = new FileHandler(logFilename);
            utilLogger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            if (suppressConsoleLogging) utilLogger.setUseParentHandlers(false);
        } catch (IOException e){
            System.out.println("warning in Transporter: unable to create log file");
        }
        logger = UtilLogger.of(utilLogger);
        buildPipeline(configFilename);
    }

    public Transporter(String configFilename, Logger logger){
        this.logger = logger;
        buildPipeline(configFilename);
    }

    private int checkWorkers(){
        if(status != Status.OK) return -1;

        boolean hasNullWorker = (reader == null) || (writer == null);
        for (Executor ex: executors) hasNullWorker |= (ex == null);
        if(hasNullWorker){
            status = Status.ERROR;
            logger.log("Error in Transporter: unable to launch - a worker is missing");
            return -1;
        }

        boolean workersAreOK = writer.status() == Status.OK;
        for(Executor ex: executors) workersAreOK &= (ex.status() == Status.OK);
        if(!workersAreOK){
            status = Status.ERROR;
            logger.log("Error in Transporter: pipeline is built successfully, but not all workers are OK");
            return -1;
        }
        return 0;
    }

    public void run(){
        System.out.println("Launching Transporter. Log will be written to " + logFilename);
        if (checkWorkers() != 0) {
            status = Status.ERROR;
            logger.log("Transporter not ready to run.");
            return;
        }
        logger.log("Starting transporter run...");
        reader.run();
        logger.log("Transporter run is over.");
    }

    private void buildPipeline(String configFilename){
        TransporterParser parser = new TransporterParser(configFilename, logger);
        if(parser.getStatus() != Status.OK){
            status = Status.ERROR;
            logger.log("error in Transporter.buildPipeline: unable to parse configurational file");
            return;
        }
        executors = new ArrayList<>();
        WorkerParameters readerParams =  parser.reader();
        WorkerParameters writerParams =  parser.writer();
        WorkerParameters [] executorParams = parser.executors();
        reader = createWorker(readerParams, Reader.class);
        writer = createWorker(writerParams, Writer.class);
        if(executorParams.length == 0){
            if(checkWorkers() != 0) return;
            reader.addConsumer(writer);
            writer.addProducer(reader);
        } else {
            for (WorkerParameters executorParam: executorParams)
                executors.add(createWorker(executorParam, Executor.class));
            if(checkWorkers() != 0) return;
            for (int i = 1; i < executors.size(); i++){
                Executor producer = executors.get(i-1);
                Executor consumer = executors.get(i);
                producer.addConsumer(consumer);
                consumer.addProducer(producer);
            }
            Executor firstExecutor = executors.get(0);
            reader.addConsumer(firstExecutor);
            firstExecutor.addProducer(reader);
            Executor lastExecutor = executors.get(executors.size() - 1);
            writer.addProducer(lastExecutor);
            lastExecutor.addConsumer(writer);
        }
    }

    private <T> T createWorker(WorkerParameters workerParameters, Class<T>workerType){
        T res = null;
        if(workerParameters == null || workerParameters.className == null){
            logger.log("Error in Transporter.createWorker: null argument passed");
            return res;
        }
        try{
            res =  workerType.cast(Class.forName(workerParameters.className)
                    .getConstructor(String.class, Logger.class)
                    .newInstance(workerParameters.configFilename, logger));
        } catch (NoClassDefFoundError | ClassNotFoundException | NullPointerException e){
            status = Status.ERROR;
            logger.log("Error in Transporter: no such class: " + workerParameters.className);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException e){
            status = Status.ERROR;
            logger.log("Error in Transporter: class " +
                    workerParameters.className + " doesn't implement constructor " +
                    "with parameters (String configFilename, Logger logger)");
        } catch (InvocationTargetException e){
            status = Status.ERROR;
            logger.log("Error in Transporter: could not create instance of " +
                    workerParameters.className + " - exception thrown in constructor");
        } catch(ClassCastException e) {
            status = Status.ERROR;
            logger.log("Error building Transporter: couldn't cast type " + workerParameters.className
                    + " to type " + workerType.getName());
        }
        return res;
    }
}
