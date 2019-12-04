package ru.spbstu.pipeline.parsing;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;

class MyProperties extends Hashtable <String, String> {

    final private static char keyValueSeparator = '=';
    final private static char lineSeparator = '\n';
    final private static char carriageReturn = '\r';
    final private static char lineContinuation = '\\';
    final private static char empty = ' ';
    final private static int eof = -1;
    final private static Charset charset = StandardCharsets.UTF_8;

    void load(InputStream inStream) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, charset));
        int currentChar;
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        boolean readingKey = true;
        boolean skipNextNewline = false;
        while ((currentChar = reader.read()) != eof){
            char character = (char) currentChar;
            switch (character) {
                case lineContinuation:
                    skipNextNewline = true;
                    break;

                case keyValueSeparator:
                    readingKey = false; // from next char until line separator: read value
                    break;

                case lineSeparator:
                    if (skipNextNewline){
                        skipNextNewline = false;
                        break;
                    }
                    put(key.toString(), value.toString());
                    readingKey = true; // reading key again
                    key.setLength(0); // reset 'key' string
                    value.setLength(0); // reset 'value' string
                    break;

                case empty:
                    break; // break SWITCH, continue WHILE (skip whitespaces)

                case carriageReturn:
                    break; // ignore carriage return

                default:
                    if (readingKey) key.append(character);
                    else value.append(character);
            }
        }
        if(key.length() > 0 && value.length() > 0) // if no newline in the end of the file
            put(key.toString(), value.toString());
    }
}

