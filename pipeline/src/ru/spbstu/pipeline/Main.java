package ru.spbstu.pipeline;

import ru.spbstu.pipeline.logging.*;
import ru.spbstu.pipeline.parsing.TransporterParser;
import ru.spbstu.pipeline.parsing.WorkerParameters;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String [] args){
        Logger logger = UtilLogger.of(java.util.logging.Logger.getLogger("logger"));
        TransporterParser tp = new TransporterParser("test_run/transporter.properties", logger);
        WorkerParameters readerParams =  tp.reader();
        WorkerParameters writerParams =  tp.writer();
        if(readerParams == null || writerParams == null) return;
        try{
            Writer w = (Writer)Class.forName(writerParams.className)
                    .getConstructor(String.class, Logger.class)
                    .newInstance(writerParams.configFilename, logger);
            Reader r = (Reader)Class.forName(readerParams.className)
                    .getConstructor(String.class, Logger.class)
                    .newInstance(readerParams.configFilename, logger);
            r.addConsumer(w);
            w.addProducer(r);
            r.run();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        } catch (NoSuchMethodException e){
            e.printStackTrace();
        } catch (IllegalAccessException e){
            e.printStackTrace();
        } catch (InstantiationException e){
            e.printStackTrace();
        } catch (InvocationTargetException e){
            e.printStackTrace();
        } catch(ClassCastException e){
            e.printStackTrace();
        }

    }
}
