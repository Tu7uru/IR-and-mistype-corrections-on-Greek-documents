import LexicalAnalysis.JsonReader;
import Model.Query;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class KeyboardErrorCorrectionTesting {

    /**
     * Test - for each possible character find column,row and upper case
     *
     * Function to test: GetRowColumnAndUpper
     *
     * Results: Correct
     */
    public static void TestFindRowColumnAndUpper(){
        char[][][] kLay = QueryCorrection.GetGreekKeyboardLayout();

        for(char[][] row : kLay) {
            if(row != null) {
                for (char[] cell : row) {
                    if(cell != null) {
                        for (char character : cell) {
                            KeyboardLayoutCoordinates res = QueryCorrection.GetRowColumnAndUpper(character);
                            System.out.println("char '" + character + "':" + res.getRow() + ", " + res.getCol() + ", " + res.getUpperCase());
                        }
                    }
                }
            }
        }
    }

    /**
     * Test - for each possible character find surrounding keys at distance 1(or 2)
     *
     * Function to test: GetSurroundingCharacters
     *
     * Results: Correct for distance 1 and 2
     */
    public static void TestGetSurroundingCharacters() {

        char[][][] kLay = QueryCorrection.GetGreekKeyboardLayout();

        for (int i = 1; i <= 2; i++) {
            System.out.println("DISTANCE :" + i);
            for (char[][] row : kLay) {
                for (char[] cell : row) {
                    if (cell != null) {
                        for (char character : cell) {
                            ArrayList<Character> res = QueryCorrection.GetSurroundingCharacters(character, i);
                            System.out.println("surroundings of " + character + ":" + res);
                        }
                    }
                }
            }
        }
    }

    /**
     * Remove Accentuations testing
     *
     * Function to test - RemoveAccentuations
     *
     * Result: Correct
     */
    public static void TestRemoveAccentuations() {
        for (char[] row : QueryCorrection.GetGreekVowelAccentuations()) {
            for (char letter : row) {
                System.out.println("Letter Before Accentuation Removal: '" + letter + "' After removal '" + QueryCorrection.RemoveAccentuations(letter) +"'");
            }
        }
    }

    /**
     * test - String padding on the right
     *
     * Result: Correct
     *
     * @param input the string to be padded
     */
    public static void TestStringPadding(String input,int numOfPads) {
        System.out.println("Initial String:{" + input + "} Padded String:{" + QueryCorrection.PadString(input,numOfPads) + "}");

    }

    /**
     * Test - Load Consonants
     *
     * Result: Correct
     * @param path path to consonants json file
     */
    public static void TestLoadConsonants(String path) throws IOException, ParseException {
        ArrayList<ArrayList<String>> consonants = JsonReader.parseJsonFileConsonants(path);
        for(ArrayList<String> listQ : consonants) {
            System.out.print("{");
            for(String query: listQ) {
                System.out.print(" "+query);
            }
            System.out.println("}");
        }
    }

    /**
     * Test - Finding correct Word on keyboard distance or finding any correction based on keyboard error
     *
     * Result: Correct
     *
     * @param character character that is being checked for valid surroundings based on validQuery currently expected index
     * @param AtDistance distance of surrounding characters(1 or 2)
     * @param validQuery the correct query to compare characters
     * @param currentlyExpectedIndex Valid Query currently expected index is a letter that has the priority if more than one characters can fit the spot
     */
    public static void TestFindMostExpectedAdjacent(char character,int AtDistance,String validQuery,int currentlyExpectedIndex, int maxIndex) {
        System.out.println(QueryCorrection.FindMostExpectedAdjacent(QueryCorrection.GetSurroundingCharacters(character, AtDistance),validQuery,currentlyExpectedIndex, maxIndex,1));
    }

    /**
     * A simple test to see the results of the algorithm
     *
     * @param validQueriesAndEditDistance a list of pairs that contain the valid query(left side) and the edit distance(right) that the initialQuery(second param) has from that valid query
     * @param initialQuery the query that is corrected(possible input from a user).
     */
    public static void TestKeyboardDistance(ArrayList<Pair<String,Integer>> validQueriesAndEditDistance,String initialQuery/*,Integer keyboardDistance*/) {
        ArrayList<Triplet> trips = QueryCorrection.CorrectKeyboardMissType(validQueriesAndEditDistance, initialQuery);
        int index = 0;
        for (Triplet trip : trips) {
//            System.out.println("correct: " + trip.getLeft() + " initial:" + initialQuery + " final: " + trip.getMid() + " old distance: " + validQueriesAndEditDistance.get(index).right + " new distance: " + EditDistance.calculate((String) trip.getLeft(), (String) trip.getMid()) + " num of keyboard distance changes applied:" + trip.getRight());
            System.out.println( "\\selectlanguage{greek}" + trip.getLeft() + " & \\selectlanguage{greek}" + initialQuery + " & \\selectlanguage{greek}" + trip.getMid() +  " & " + validQueriesAndEditDistance.get(index).right  + " & " + EditDistance.calculate((String) trip.getLeft(), (String) trip.getMid()) + " \\\\");
            System.out.println("\\hline");
            index++;
        }
    }

    /**
     * Test - Apply keyboardDistance on Consonants file for bulk checking of the algorithm
     * Note: Bulk checking is responsible to take consonants and create the misspelled queries too
     *
     * @param path path to consonants file
     */
    public static void TestKeyboardDistanceBulk(String path) throws IOException, ParseException {
        ArrayList<String> misspelledConsonants = new ArrayList<>();

        ArrayList<ArrayList<String>> consonants = JsonReader.parseJsonFileConsonants(path);
        System.out.println(consonants.size());
        for(ArrayList<String> consonantList : consonants) {
            misspelledConsonants = TransformToMisspelledQueries.IncorrectSurroundingCharacter(consonantList);

            int listIndex = 0;
            int misspelledIndex = 0;
//            ArrayList<Pair<String,Integer>> ListOfQueriesAndEditDistance = new ArrayList<>();
            for( misspelledIndex = 0; misspelledIndex < misspelledConsonants.size(); misspelledIndex++) {
                ArrayList<Pair<String,Integer>> ListOfQueriesAndEditDistance = new ArrayList<>();
                for (listIndex = 0; listIndex < misspelledConsonants.size(); listIndex++) {
                    String misspelledQuery = misspelledConsonants.get(misspelledIndex);
                    String validQuery = consonantList.get(listIndex);
//                    System.out.println(misspelledQuery);
//                    System.out.println(validQuery);
                    int editDistance = EditDistance.calculate(validQuery, misspelledQuery);
//                    System.out.println(editDistance);
                    Pair<String, Integer> ValQuery_EditDistance = new Pair(validQuery, editDistance);
                    ListOfQueriesAndEditDistance.add(ValQuery_EditDistance);
                }
                //System.out.println(ListOfQueriesAndEditDistance);
                //for (listIndex = 0; listIndex < misspelledConsonants.size(); listIndex++) {
                    ArrayList<Triplet> results = QueryCorrection.CorrectKeyboardMissType(ListOfQueriesAndEditDistance, misspelledConsonants.get(misspelledIndex));
                    listIndex = 0;
                    for (Triplet trip : results) {
                        System.out.println("correct: " + trip.getLeft() + " initial:" + misspelledConsonants.get(misspelledIndex) + " final: " + trip.getMid() + " old distance: " + ListOfQueriesAndEditDistance.get(listIndex).right + " new distance: " + EditDistance.calculate((String) trip.getLeft(), (String) trip.getMid()) + " num of keyboard distance changes applied:" + trip.getRight());
//                        System.out.println( "\\selectlanguage{greek}" + trip.getLeft() + " & \\selectlanguage{greek}" + misspelledConsonants.get(misspelledIndex) + " & \\selectlanguage{greek}" + trip.getMid() +  " & " + ListOfQueriesAndEditDistance.get(listIndex).right  + " & " + EditDistance.calculate((String) trip.getLeft(), (String) trip.getMid()) + " \\\\");
//                        System.out.println("\\hline");
                        listIndex++;
                    }
               // }
            }

        }
    }

    public static void main(String[] args) throws IOException, ParseException {
//        TestFindRowColumnAndUpper();
//        TestGetSurroundingCharacters();
//        TestRemoveAccentuations();
//        TestStringPadding("βαζ",3);
//        TestLoadConsonants("src/main/resources/consonants.json");
//        TestFindMostExpectedAdjacent('υ',1,"αίτημα",1);

        ArrayList<Pair<String,Integer>> validQs = new ArrayList<>();
//        validQs.add(new Pair("έτοιμα",4));
//        validQs.add(new Pair("ακώλυτος",4));
//        validQs.add(new Pair("ακόλλητος",3));
//        TestKeyboardDistance(validQs,"ακόλλητπς");
//        TestKeyboardDistance(validQs,"αοοοόλλητος");
//        TestKeyboardDistance(validQs,"αιώλυτος");

        // Testing specific words

//validQs.add(new Pair("ακόλλητος",3));
//        TestKeyboardDistance(validQs,"αοοοόλλητος");


//        validQs.add(new Pair("αίτημα",4));
//        TestKeyboardDistance(validQs,"αοοουημα");

//        validQs.add(new Pair("ακόλλητος",5));
//        TestKeyboardDistance(validQs,"αοοοόλοηυος");

//        validQs.add(new Pair("ακόλλητος",2));
//        TestKeyboardDistance(validQs,"ακλλυτος");

//        validQs.add(new Pair("βάζω",4));
//        TestKeyboardDistance(validQs,"νσχβ");

//        validQs.add(new Pair("ισχύ",2));
//        TestKeyboardDistance(validQs,"νσχβ");


//        validQs.add(new Pair("αίτημα",3));
//        TestKeyboardDistance(validQs,"απλότημα");

//        validQs.add(new Pair("απλότητα",1));
//        TestKeyboardDistance(validQs,"απλότημα");

        //These are the 4 words from the report
//        validQs.add(new Pair("μέση",4));
//        TestKeyboardDistance(validQs,"νσχβ");

        //These are the 4 words
//        validQs.add(new Pair("μάχη",4));
//        TestKeyboardDistance(validQs,"νσχβ");

//        validQs.add(new Pair("μαδώ",4));
//        TestKeyboardDistance(validQs,"νσχβ");
        
        validQs.add(new Pair("βάζω",4));
        TestKeyboardDistance(validQs,"νσχβ");

        //EOF 4 words
//        validQs.add(new Pair("βάζο",4));
//        TestKeyboardDistance(validQs,"νσχβ");




//        TestKeyboardDistanceBulk("src/main/resources/consonants.json");
    }
}
