package LexicalAnalysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class TextFileProcessing {

    // correct words and incorrect words have the same index. for each co
    static LinkedHashSet<String> correctWords = new LinkedHashSet<>();  //  a set of words (read by the file)
    static LinkedHashSet<ArrayList<String>> MultipleIncorrectWords = new LinkedHashSet<>(); // the error words to be produced and written in a file

//    SecureRandom rand = new SecureRandom();

    public static void readFile(String path) throws FileNotFoundException, IOException, FileNotFoundException {

        FileReader fl = new FileReader(path);
        BufferedReader bfr = new BufferedReader(fl);
        String line;
        boolean flag;
        ArrayList<String> incorrectWords;
        while ((line = bfr.readLine()) != null) {
            String[] tmp = line.split(",");
            flag = true;
            incorrectWords = new ArrayList<>();
            for (String word : tmp) {
                if(flag) {
                    correctWords.add(word);
                    flag = false;
                }
                else
                    incorrectWords.add(word);
            }
            MultipleIncorrectWords.add(incorrectWords);
        }
    }

    public static void ReadCommaSeperatedWords(String DatasetRelativePath) throws IOException {

        readFile(DatasetRelativePath);

//        System.out.println(names);
//        System.out.println(errNames);
//        System.out.println(wcPair);
//        System.out.println(cwPair);

    }

    public String ConvertToCommaSeperatedRow(String CorrectWord,ArrayList<String> IncorrectWords){
        String finalOutput = CorrectWord;
        for (String word : IncorrectWords) {
            finalOutput += "," + word;
        }
        return finalOutput;
    }

    public void WriteToTextFile(LinkedHashSet<String> correctWords, LinkedHashSet<ArrayList<String>> MultipleIncorrectWords){

    }

    public static void main(String[] args) throws IOException {
        ReadCommaSeperatedWords("src/main/resources/names/additions.txt");
    }
}
