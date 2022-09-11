package LexicalAnalysis;

import org.w3c.dom.Text;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class TextFileProcessing {

    // correct words and incorrect words have the same index. for each co
    static ArrayList<String> correctWords = new ArrayList<>();  //  a set of words (read by the file)
    static ArrayList<ArrayList<String>> MultipleIncorrectWords = new ArrayList<>(); // the error words to be produced and written in a file

//    SecureRandom rand = new SecureRandom();

    public TextFileProcessing(){

    }

    public ArrayList<String> getCorrectWords(){
        return this.correctWords;
    }

    public ArrayList<ArrayList<String>> getMulitpleIncorrectWords(){
        return this.MultipleIncorrectWords;
    }


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

    public static String ConvertToCommaSeperatedRow(String CorrectWord, ArrayList<String> IncorrectWords){
        String finalOutput = CorrectWord;
        for (String word : IncorrectWords) {
            finalOutput += "," + word;
        }
        return finalOutput;
    }

    public static void WriteToTextFile(ArrayList<String> CorrectWords, ArrayList<ArrayList<String>> MultipleIncorrectWords, String Filename) throws IOException {

        String finalOutput = "";
        String path = "src/main/resources/names/" + Filename;
        for(int index = 0; index < CorrectWords.size();index++) {
            finalOutput += TextFileProcessing.ConvertToCommaSeperatedRow(CorrectWords.get(index),MultipleIncorrectWords.get(index)) + "\n";
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(path, false));
        writer.append(finalOutput);

        writer.close();
    }

    public static void main(String[] args) throws IOException {
        ReadCommaSeperatedWords("src/main/resources/names/additions.txt");
        //System.out.println(ConvertToCommaSeperatedRow(correctWords.get(0),MultipleIncorrectWords.get(0)));
    }
}
