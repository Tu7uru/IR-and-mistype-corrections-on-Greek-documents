package utils;

import KeyboardMistype.QueryCorrection;
import LexicalAnalysis.TextFileProcessing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class TransformToMisspelledQueries {

    public static Character[] greekLetters = {'α','β','γ','δ','ε','ζ','η','θ','ι','κ','λ','μ',
            'ν','ξ','ο','π','ρ','σ','τ','υ','φ','χ','ψ','ω'};

    public static String addChar(String str, char ch, int position) {
        return str.substring(0, position) + ch + str.substring(position);
    }

    public static String replaceChar(String str, char ch, int position) {
        return str.substring(0, position) + ch + str.substring(position + 1);
    }

    public static String removeChar(String str, int position) {
        if(position > 0) {
            return str.substring(0, position) + str.substring(position + 1);
        } else {
            return str.substring(position + 1);
        }
    }

    public static ArrayList<String> AddExtraCharacter(ArrayList<String> tokenizedQueries) {
        int length;
        int index;
        int numofletters = 24;
        char extra;
        Random random = new Random();

        String transformedQuery;
        ArrayList<String> transformedQueries = new ArrayList<>();

        for(String query : tokenizedQueries) {
            length = query.length();
            index = random.nextInt(length);
            while((extra = greekLetters[random.nextInt(numofletters)]) == ',');
            transformedQuery = addChar(query,extra,index);
            transformedQueries.add(transformedQuery);
        }

        return transformedQueries;
    }

    public static ArrayList<String> RemoveCharacter(ArrayList<String> tokenizedQueries) {
        int length;
        int index;
        int numofletters = 24;
        Random random = new Random();

        String transformedQuery;
        ArrayList<String> transformedQueries = new ArrayList<>();

        for(String query : tokenizedQueries) {
            length = query.length();
            index = random.nextInt(length);
            transformedQuery = removeChar(query,index);
            transformedQueries.add(transformedQuery);
        }

        return transformedQueries;
    }

    public static ArrayList<String> IncorrectCharacter(ArrayList<String> tokenizedQueries) {
        int length;
        int index;
        int numofletters = 24;
        Random random = new Random();

        String transformedQuery;
        ArrayList<String> transformedQueries = new ArrayList<>();

        for(String query : tokenizedQueries) {
            length = query.length();
            index = random.nextInt(length);
            transformedQuery = replaceChar(query,greekLetters[random.nextInt(numofletters)],index);
            transformedQueries.add(transformedQuery);
        }
        return transformedQueries;
    }

    public static ArrayList<String> IncorrectSurroundingCharacter(ArrayList<String> tokenizedQueries, int AtPosition) {
        int length;
        int index;
        int numofletters;
        char letter;
        char mistypedCharacter;
        ArrayList<Character> surroundingCharacters;
        Random random = new Random();

        String transformedQuery;
        ArrayList<String> transformedQueries = new ArrayList<>();

        for(String query : tokenizedQueries) {
            length = query.length();
            if(AtPosition == -1)
                index = random.nextInt(length);
            else {
                if(length <= AtPosition)
                    index = length - 1;
                else
                    index = AtPosition;
            }
//            System.out.println("word:" + query + " index:" + index + " length:" +length);
            letter = query.charAt(index);
            //System.out.println(letter);
            surroundingCharacters = QueryCorrection.GetSurroundingCharacters(letter,1);
            if(surroundingCharacters != null) {
                numofletters = surroundingCharacters.size();
                //System.out.println(numofletters);
                while((mistypedCharacter = surroundingCharacters.get(random.nextInt(numofletters))) == ',');
                transformedQuery = replaceChar(query, mistypedCharacter, index);
                transformedQueries.add(transformedQuery);
            }
        }
        return transformedQueries;
    }

    public static ArrayList<ArrayList<String>> CreateIncorrectSurroundingCharacter(ArrayList<String> CorrectWords, int NumOfMistypes) throws IOException {

        ArrayList<String> incAt0;
        ArrayList<String> incAt1;
        ArrayList<String> incAt2;
        ArrayList<String> incAt3;
        ArrayList<String> incAt4;
        ArrayList<String> incAt6;

        if(NumOfMistypes == 1) {
            incAt0 = IncorrectSurroundingCharacter(CorrectWords, 0);
            incAt1 = IncorrectSurroundingCharacter(CorrectWords, 1);
            incAt2 = IncorrectSurroundingCharacter(CorrectWords, 2);
            incAt3 = IncorrectSurroundingCharacter(CorrectWords, 3);
            incAt4 = IncorrectSurroundingCharacter(CorrectWords, 4);
            incAt6 = IncorrectSurroundingCharacter(CorrectWords, 6);

        } else if(NumOfMistypes == 2){
            incAt0 = CorrectWords;
            incAt1 = CorrectWords;
            incAt2 = CorrectWords;
            incAt3 = CorrectWords;
            incAt4 = CorrectWords;
            incAt6 = null;
            int tmp0=0,tmp1 = 1,tmp2 = 2, tmp3 = 3, tmp4 = 4;
            for(int pos = 0; pos < NumOfMistypes; pos++){
                incAt0 = IncorrectSurroundingCharacter(incAt0, tmp0 + pos);
                incAt1 = IncorrectSurroundingCharacter(incAt1, tmp1 + pos);
                incAt2 = IncorrectSurroundingCharacter(incAt2, tmp2 + pos);
                incAt3 = IncorrectSurroundingCharacter(incAt3, tmp3 + pos);
                incAt4 = IncorrectSurroundingCharacter(incAt4, tmp4 + pos);
            }
        }
        else {
            incAt0 = CorrectWords;
            incAt1 = CorrectWords;
            incAt2 = CorrectWords;
            incAt3 = CorrectWords;
            incAt4 = null;
            incAt6 = null;
            int tmp0=0,tmp1 = 1,tmp2 = 2, tmp3 = 3, tmp4 = 4;
            for(int pos = 0; pos < NumOfMistypes; pos++){
                incAt0 = IncorrectSurroundingCharacter(incAt0, tmp0 + pos);
                incAt1 = IncorrectSurroundingCharacter(incAt1, tmp1 + pos);
                incAt2 = IncorrectSurroundingCharacter(incAt2, tmp2 + pos);
                incAt3 = IncorrectSurroundingCharacter(incAt3, tmp3 + pos);
            }
        }
        ArrayList<ArrayList<String>> mulitpleIncorrectWords= new ArrayList<>();

        for(int index = 0; index < CorrectWords.size(); index++) {
            ArrayList<String> incWords = new ArrayList<>();
            incWords.add(incAt0.get(index));
            incWords.add(incAt1.get(index));
            incWords.add(incAt2.get(index));
            incWords.add(incAt3.get(index));
            if(NumOfMistypes <= 2)
                incWords.add(incAt4.get(index));
            if(NumOfMistypes == 1)
                incWords.add(incAt6.get(index));
            mulitpleIncorrectWords.add(incWords);
        }
        /*if(NumOfMistypes == 1)
            TextFileProcessing.WriteToTextFile(CorrectWords,mulitpleIncorrectWords,Filename);
        else
            TextFileProcessing.WriteToTextFile(CorrectWords,mulitpleIncorrectWords,Filename );*/
//        System.out.println(finalOutput);
        return mulitpleIncorrectWords;
    }

    public static void main(String[] args) throws IOException {
        /*String x = "κάρδαμο";
        System.out.println(addChar(x,'ζ' ,6));
        System.out.println(removeChar(x ,0));
        System.out.println(replaceChar(x,'ζ' ,0));

        ArrayList<String> qs = new ArrayList<>();
        qs.add("κάρδαμο");
        System.out.println(AddExtraCharacter(qs));
        System.out.println(RemoveCharacter(qs));
        System.out.println(IncorrectCharacter(qs));
        System.out.println(IncorrectSurroundingCharacter(qs, -1));
*/

        // CREATE mistyped queries
        TextFileProcessing tfProcessing = new TextFileProcessing();
        tfProcessing.readFile("src/main/resources/names/additions.txt");
        // 1 mistype
        ArrayList<ArrayList<String>> SubstitutionIncorrectWords1 = CreateIncorrectSurroundingCharacter(tfProcessing.getCorrectWords(),1);
        tfProcessing.WriteToTextFile(tfProcessing.getCorrectWords(),SubstitutionIncorrectWords1,"OnePerWordSubstitutions.txt");

        // 1 mistype and character addition
        ArrayList<ArrayList<String>> SubNExtra_incorrectWords = new ArrayList<>();
        for(ArrayList<String> incorrectWords : SubstitutionIncorrectWords1) {
             SubNExtra_incorrectWords.add(AddExtraCharacter(incorrectWords));
        }
        tfProcessing.WriteToTextFile(tfProcessing.getCorrectWords(),SubNExtra_incorrectWords,"OneSubstitutionAndAddition.txt");
        // 2 mistypes
        ArrayList<ArrayList<String>> SubstitutionIncorrectWords2 = CreateIncorrectSurroundingCharacter(tfProcessing.getCorrectWords(),2);
        tfProcessing.WriteToTextFile(tfProcessing.getCorrectWords(),SubstitutionIncorrectWords2,"TwoPerWordSubstitutions.txt");

        // 2 mistypes and character addition
        SubNExtra_incorrectWords = new ArrayList<>();
        for(ArrayList<String> incorrectWords : SubstitutionIncorrectWords2) {
            SubNExtra_incorrectWords.add(AddExtraCharacter(incorrectWords));
        }
        tfProcessing.WriteToTextFile(tfProcessing.getCorrectWords(),SubNExtra_incorrectWords,"TwoSubstitutionsAndAddition.txt");

        //3 mistypes
        ArrayList<ArrayList<String>> SubstitutionIncorrectWords3 = CreateIncorrectSurroundingCharacter(tfProcessing.getCorrectWords(),3);
        tfProcessing.WriteToTextFile(tfProcessing.getCorrectWords(),SubstitutionIncorrectWords3,"ThreePerWordSubstitutions.txt");
    }
}
