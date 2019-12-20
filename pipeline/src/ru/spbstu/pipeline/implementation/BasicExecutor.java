package ru.spbstu.pipeline.implementation;

import org.jetbrains.annotations.NotNull;
import ru.spbstu.pipeline.Consumer;
import ru.spbstu.pipeline.Executor;
import ru.spbstu.pipeline.Producer;
import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BasicExecutor extends AbstractConsumer implements Executor {
    protected Object outputData;
    protected List<Consumer> consumers = new ArrayList<>();
    protected ExecutorDataAccessor dataAccessor = new ExecutorDataAccessor();

    public void run(){
        dataAccessor.put(getBytes());
        for (Consumer consumer: consumers){
            long consumerLoadStatus = consumer.loadDataFrom(this);
            if (consumerLoadStatus != 0L)
                consumer.run();
            else if (logger != null)
                logger.log("Warning in Executor.run(): did not launch consumer - " +
                        "consumer.loadDataFrom(this) returned status 0");
        }
    }

    protected void getInputData(){
        if (inputData == null) {
            status = Status.EXECUTOR_ERROR;
            if(logger != null)
                logger.log("error in BasicExecutor.get: all producers failed to provide valid inputData");
        }
    }

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
    public DataAccessor getAccessor(String canonicalName) {
        Objects.requireNonNull(dataAccessor);
        dataAccessor.canonicalTypeName = canonicalName;
        return dataAccessor;
    }

    protected Object getBytes() {
        if (outputData != null) return outputData;
        if (inputData == null) getInputData();
        outputData = inputData;
        return outputData;
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
