package ru.spbstu.pipeline.implementation;

import ru.spbstu.pipeline.Consumer;
import ru.spbstu.pipeline.Executor;
import ru.spbstu.pipeline.Producer;
import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BasicExecutor implements Executor {
    protected Object inputData;
    protected Object outputData;
    protected Status status = Status.OK;;
    protected List<Producer> producers = new ArrayList<>();
    protected List<Consumer> consumers = new ArrayList<>();
    protected static Set<String> supportedInputTypes = TypeCaster.getSupportedTypes();
    protected ExecutorDataAccessor dataAccessor = new ExecutorDataAccessor();
    protected Logger logger;

    public void run(){
        dataAccessor.put(getBytes());
        for (Consumer consumer: consumers){
            consumer.loadDataFrom(this);
            consumer.run();
        }
    }

    protected void getInputData(){
        for (Producer p: producers)
            if (inputData == null){
                loadDataFrom(p);
            } else break;
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

    class ExecutorDataAccessor implements DataAccessor{
        TypeCaster caster = new TypeCaster();
        String canonicalTypeName = byte[].class.getCanonicalName();

        private void put(Object data){
            caster.put(data);
        }

        @Override
        public long size() {
            return caster.size(canonicalTypeName);
        }

        @Override
        public Object get(){
            return caster.get(canonicalTypeName);
        }
    }

    @Override
    public DataAccessor getAccessor(String canonicalName) {
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

    public void addProducer(Producer producer){
        if (producers.contains(producer)) return;
        if (producer != null) producers.add(producer);
        else if (logger != null) logger.log("Warning in BasicExecutor: " +
                "tried to add producer that is null");
    }

    public void addProducers(List<Producer> producers){
        for (Producer producer: producers)
            addProducer(producer);
    }

    public long loadDataFrom(Producer producer){
        if(producer.status() == Status.OK){
            Set<String> types = producer.outputDataTypes();
            for (String type:supportedInputTypes) {
                if (types.contains(type)) {
                    DataAccessor da = producer.getAccessor(type);
                    inputData = da.get();
                    return da.size();
                }
            }
            if(logger != null)
                logger.log("warning in BasicExecutor.loadDataFrom(Producer):" +
                        " producer unable to yield data of any of these types: " + supportedInputTypes);
        } else if (logger != null)
            logger.log("warning in BasicExecutor.loadDataFrom(Producer): one of producers is not OK");
        return -1L;
    }

    protected BasicExecutor(){}

    public BasicExecutor(String configFilename, Logger logger){
        this.logger = logger;
    }
    public BasicExecutor(Logger logger, String configFilename){
        this(configFilename, logger);
    }
}
