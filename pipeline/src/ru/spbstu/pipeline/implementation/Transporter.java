package ru.spbstu.pipeline.implementation;

import ru.spbstu.pipeline.*;
import ru.spbstu.pipeline.logging.*;
import ru.spbstu.pipeline.parsing.TransporterParser;
import ru.spbstu.pipeline.parsing.WorkerParameters;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Transporter {
    protected TransporterParser parser;
    protected List<Executor> executors;
    protected Reader reader;
    protected Writer writer;
    protected Logger logger;

    public Transporter(String configFilename, Logger logger){
        this.logger = logger;
        parser = new TransporterParser(configFilename, logger);
        executors = new ArrayList<>();
        buildPipeline();
    }

    public void run(){
        boolean hasNullWorker = (reader == null) && (writer == null);
        for (Executor ex: executors) hasNullWorker &= (ex == null);
        if(hasNullWorker){
            logger.log("Error in Transporter: unable to launch - a worker is missing");
            return;
        }
        reader.run();
    }

    protected void buildPipeline(){
        WorkerParameters readerParams =  parser.reader();
        WorkerParameters writerParams =  parser.writer();
        //TODO add executors along with TransporterParser.executors()
        WorkerParameters [] executorParams = new WorkerParameters[0];
        reader = createWorker(readerParams, Reader.class);
        writer = createWorker(writerParams, Writer.class);
        if(executorParams.length == 0){
            reader.addConsumer(writer);
            writer.addProducer(reader);
        } else {
            // TODO add executors
        }
    }

    protected <T> T createWorker(WorkerParameters workerParameters, Class<T>workerType){
        T res = null;
        try{
            res =  workerType.cast(Class.forName(workerParameters.className)
                    .getConstructor(String.class, Logger.class)
                    .newInstance(workerParameters.configFilename, logger));
        } catch (ClassNotFoundException e){
            logger.log("Error in Transporter: no such class: " + workerParameters.className);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException e){
            logger.log("Error in Transporter: class " +
                    workerParameters.className + " doesn't implement constructor " +
                    "with parameters (String configFilename, Logger logger)");
        } catch (InvocationTargetException e){
            logger.log("Error in Transporter: could not create instance of " +
                    workerParameters.className + " - exception thrown in constructor");
        } catch(ClassCastException e) {
            logger.log("Error building Transporter: couldn't cast type" + workerParameters.className
                    + " to type " + workerType.getName());
        }
        return res;
    }
}
