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

    protected int checkWorkers(){
        boolean hasNullWorker = (reader == null) || (writer == null);
        for (Executor ex: executors) hasNullWorker |= (ex == null);
        if(hasNullWorker){
            logger.log("Error in Transporter: unable to launch - a worker is missing");
            return -1;
        }
        return 0;
    }

    public void run(){
        if (checkWorkers() != 0) return;
        reader.run();
    }

    protected void buildPipeline(){
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
