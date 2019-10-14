import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Compressor {

    Compressor( String configName ) {
        config = new Config(configName);
        dictionary = new ArrayList<String>();

        openFiles(config.getInputFileName(), config.getOutputFileName());
        if (config.getMode() == Config.Mode.COMPRESS)
            Compress();
        else
            Decompress();
    }

    private void Compress() {
        try {
            // add atomics in dictionary
            dictionary.addAll(config.getAlphabet());
            writer.setCodeSize((int)(Math.log(dictionary.size()) / Math.log(2)) + 2);

            String syms;
            String cur;
            char[] first = new char[1];

            int res = inputFile.read(first, 0, 1);
            if (res == -1)
                Logger.get().registerLog(Logger.ErrorType.BAD_READ, "Empty file");
            cur = new String(first);

            String toFindInDict = "a";

            while ((syms = inputFile.readLine()) != null) {
                //syms += "\n";
                for (int i = 0; i <= syms.length(); i++) {
                    char c = i != syms.length() ? syms.charAt(i) : '\n';
                    toFindInDict = toFindInDict.replace(toFindInDict.charAt(0), c);

                    if (!dictionary.contains(toFindInDict)) {
                        continue;
                        //Logger.get().registerLog(Logger.ErrorType.BAD_GRAMMAR,
                        //        "Element " + toFindInDict + " is not in alphabet");
                    }


                    String expanded = cur + c;

                    if (dictionary.contains(expanded)) {
                        cur = expanded;
                    }
                    else {
                        writer.writeCode(dictionary.indexOf(cur));
                        if (dictionary.size() < (1 << writer.getCodeSize()) - 1)
                            dictionary.add(expanded);
                        cur = toFindInDict;
                    }
                }
            }
            writer.writeCode(dictionary.indexOf(cur));
            inputFile.close();
            writer.close();
        } catch (IOException e) {
            Logger.get().registerLog(Logger.ErrorType.BAD_READ, e.getMessage());
        }
    }

    private void Decompress() {

        // add atomics in dictionary
        dictionary.addAll(config.getAlphabet());

        reader.setCodeSize((int)(Math.log(dictionary.size()) / Math.log(2)) + 2);
        try {
            int[] codeOld = new int[1], codeCur = new int[1];
            if (!reader.readCode(codeOld))
                Logger.get().registerLog(Logger.ErrorType.BAD_FILE, "Empty file");
            String old = dictionary.get(codeOld[0]);
            outputFile.write(old);
            char sym;

            while (reader.readCode(codeCur)) {
                String cur = dictionary.get(codeCur[0]);
                outputFile.write(cur);

                sym = cur.charAt(0);

                if (dictionary.size() < (1 << reader.getCodeSize()) - 1)
                    dictionary.add(dictionary.get(codeOld[0]) + sym);
                codeOld = codeCur.clone();
            }
            reader.close();
            outputFile.close();
        } catch (IOException e) {
            Logger.get().registerLog(Logger.ErrorType.BAD_WRITE, e.getMessage());
        }
    }

    private void openFiles( String in, String out) {
        try {
            switch (config.getMode()) {
            case COMPRESS:
                inputFile = new BufferedReader(new FileReader(in));
                writer = new BitWriter(new BufferedWriter(new FileWriter(out)));
                break;
            case DECOMPRESS:
                outputFile = new BufferedWriter(new FileWriter(out));
                reader = new BitReader(new BufferedReader(new FileReader(in)));
                break;
            }
        } catch (IOException e) {
            Logger.get().registerLog(Logger.ErrorType.BAD_FILE, e.getMessage());
        }
    }

    private BufferedReader inputFile;
    private BufferedWriter outputFile;
    private BitWriter writer;
    private BitReader reader;
    private Config config;
    private List<String> dictionary;
}
