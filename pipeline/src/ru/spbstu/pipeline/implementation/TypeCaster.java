package ru.spbstu.pipeline.implementation;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;

public class TypeCaster {
    private static Charset charset = StandardCharsets.UTF_16BE;

    private enum supportedTypes{
        bytes(byte[].class.getCanonicalName()),
        chars(char[].class.getCanonicalName()),
        string(String.class.getCanonicalName());

        private final String canonicalName;
        supportedTypes(String canonicalName) { this.canonicalName = canonicalName; }
    }
    private static final Set<String> supportedTypesSet;
    static {
        supportedTypesSet = new LinkedHashSet<>();
        for(supportedTypes t:supportedTypes.values()){
            supportedTypesSet.add(t.canonicalName);
        }
    }

    public static Set<String> getSupportedTypes(){
        return supportedTypesSet;
    }

    private String string;
    private byte[] bytes;
    private char[] characters;

    private char[] getChars(){
        if(characters != null)
            return characters;
        if(string == null && bytes != null)
            string = new String(bytes, charset);
        if(string != null)
            characters = string.toCharArray();
        else
            characters = new char[0];
        return characters;
    }

    private String getString(){
        if(string != null)
            return string;
        if(bytes != null)
            string = new String(bytes, charset);
        else if (characters != null)
            string = new String(characters);
        else
            string = "";
        return string;
    }

    private byte[] getBytes(){
        if(bytes != null)
            return bytes;
        if (string == null && characters != null)
            string = new String(characters);
        if(string != null)
            bytes = string.getBytes(charset);
        else
            bytes = new byte[0];
        return bytes;
    }

    public Object get(String canonicalName){
        if(canonicalName == null)
            return null;
        if(canonicalName.equals(supportedTypes.string.canonicalName))
            return getString();
        if (canonicalName.equals(supportedTypes.chars.canonicalName))
            return getChars();
        if (canonicalName.equals(supportedTypes.bytes.canonicalName))
            return getBytes();
        return null;
    }

    public long size(String canonicalName){
        if(canonicalName == null)
            return 0L;
        if(canonicalName.equals(supportedTypes.string.canonicalName))
            return getString().length();
        if (canonicalName.equals(supportedTypes.chars.canonicalName))
            return getChars().length;
        if (canonicalName.equals(supportedTypes.bytes.canonicalName))
            return getBytes().length;
        return 0L;
    }

    public void put(Object data){
        if(data != null){
            if(data.getClass() == String.class)
                string = (String)data;
            if(data.getClass() == byte[].class)
                bytes = (byte[])data;
            if(data.getClass() == char[].class)
                characters = (char[]) data;
        }
    }
}
