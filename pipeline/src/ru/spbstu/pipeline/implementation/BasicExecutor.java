package ru.spbstu.pipeline.implementation;

import org.jetbrains.annotations.NotNull;
import ru.spbstu.pipeline.Consumer;
import ru.spbstu.pipeline.Executor;
import ru.spbstu.pipeline.Producer;
import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.logging.Logger;

import java.util.*;

public class BasicExecutor extends AbstractConsumer implements Executor {
    protected Object outputData;
    protected List<Consumer> consumers = new ArrayList<>();
    protected final ExecutorDataAccessor dataAccessor = new ExecutorDataAccessor();

    public void run(){
        synchronized (dataAccessor){
            dataAccessor.put(getBytes());
        }
        for (Consumer consumer: consumers){
            long consumerLoadStatus = consumer.loadDataFrom(this);
            if (consumerLoadStatus == 0L && logger != null)
                logger.log("Warning in Executor.run(): " +
                        "consumer.loadDataFrom(this) returned status 0");
        }
    }

    @NotNull
    @Override
    public Set<String> outputDataTypes() {
        return TypeCaster.getSupportedTypes();
    }

    class ExecutorDataAccessor extends ProducerDataAccessor{

        private void put(Object data){
            caster.put(data);
        }
    }

    @NotNull
    @Override
    public DataAccessor getAccessor(@NotNull String canonicalName) {
        Objects.requireNonNull(dataAccessor);
        dataAccessor.canonicalTypeName = canonicalName;
        return dataAccessor;
    }

    protected Object getBytes() {
        if (outputData != null) return outputData;
        while (readyProducersCounter == 0){
            try {
                Thread.sleep(waitPeriodMillis);
            } catch(InterruptedException e){
                status = Status.EXECUTOR_ERROR;
                if(logger != null)
                    logger.log("Error in Executor.getBytes(): " +
                            "exception in Thread.sleep(...)");
            }
        }
        for (Map.Entry<Producer, Producer.DataAccessor> entry : producers.entrySet()) {
            synchronized (entry.getValue()){
                Producer.DataAccessor dataAccessor = entry.getValue();
                if (dataAccessor != null){
                    return makeOutput(dataAccessor.get());
                }
            }
        }
        if(logger != null)
            logger.log("Warning in Executor.run(): one of producers loaded input data," +
                    "but all input data accessors are null");
        return null;
    }

    protected synchronized Object makeOutput(@NotNull Object data){
        return data;
    }

    @Override
    public Status status(){
        return this.status;
    }

    public void addConsumer(Consumer consumer) {
        if (consumers.contains(consumer)) return;
        if (consumer != null) consumers.add(consumer);
        else if (logger != null) logger.log("Warning in BasicExecutor: " +
                "tried to add consumer that is null");
    }

    public void addConsumers(List<Consumer> consumers){
        for (Consumer consumer: consumers)
            addConsumer(consumer);
    }

    protected BasicExecutor(){}

    public BasicExecutor(String configFilename, Logger logger){
        this.logger = logger;
    }
    public BasicExecutor(Logger logger, String configFilename){
        this(configFilename, logger);
    }
}
