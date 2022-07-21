import LexicalAnalysis.JsonReader;

import java.util.ArrayList;

public class QueryCorrection {
    /*
        Should/Could investigate different keyboard types

     */
    private static char[][][] greekKeyboardLayout = {
            {{'`','~'},{'1','!'},{'2','@'},{'3','#'},{'4','$'},{'5','%'},{'6','^'},{'7','&'},{'8','*'},{'9','('},{'0',')'},{'-' ,'_'},{'=','+'}},
            {   null,  {';',':'},{'ς','΅'},{'ε','Ε'} ,{'ρ','Ρ'},{'τ','Τ'},{'υ','Υ'},{'θ','Θ'},{'ι','Ι'},{'ο','Ο'},{'π','Π'},{'[','{' },{']','}'},{'\\','|'}},
            {   null,  {'α','Α'},{'σ','Σ'},{'δ','Δ'} ,{'φ','Φ'},{'γ','Γ'},{'η','Η'},{'ξ','Ξ'},{'κ','Κ'},{'λ','Λ'},{'΄','¨'},{'\'','"'}},
            {   null,  {'ζ','Ζ'},{'χ','Χ'},{'ψ','Ψ'} ,{'ω','Ω'},{'β','Β'},{'ν','Ν'},{'μ','Μ'},{',','<'},{'.','>'},{'/','?'}}
    };

    /* take with tonos or without tonos and compare based on greekKeyboardLayout

     */

    private static char[][] greekVowelAccentuation = {
            {'α','ά'},
            {'ε','έ'},
            {'η','ή'},
            {'ι','ί','ϊ','ΐ'},
            {'ο','ό'},
            {'υ','ύ','ϋ','ΰ'},
            {'ω','ώ'}
    };

    public static char[][][] GetGreekKeyboardLayout() {
        return greekKeyboardLayout;
    }

    public static char[][] GetGreekVowelAccentuations(){
        return greekVowelAccentuation;
    }

    /*
        Typing wrong character correction:

        e.g.: βαζχ -> βάζο | βάζω.
              Which one though?If we consider that 'χ' is closer to 'ω' than 'ο' we could assume
              that the relevant word is "βάζω" instead of "βάζο".

        This idea only works for similar words(homophones) that are written differently and have the same edit distance.
        For this reason we need to break down misspelled queries cases and see how they would be handled theoretically

        βαζχ :
                deletion: not relevant with miss-typed character
                          - add character
                addition: not relevant with miss-typed character
                          should check if removal of the extra character reduces edit distance
                          or make sure not to investigate extra character
                transposition*: should be checked if transposition exists before checking for miss-typed characters on keyboard
                incorrectCharacter: should be checked with edit distance and then find which character is more relevant based on keyboard layout
                                    Note: Could also take into consideration that it might be TYPO(same character 'ι','η' but incorrectly typed)

        *transposition:
            κναο -> κανο  : ν->υπαρχει και ειναι μοναδικο αλλα σε διαφορετικη θεση
            καννοι -> κανονι:

        τονους: Edit distance with "tonous" and keyboard correction with lowercase + no_tonous

        Note:On Keyboard Distance -  * Distance of 1 is the most important thing to check
                                     * For Distance of 2 we should only check left and right from distance 1 up/down keys(looks like diagonals from correct key)
                                       because distance of 2 on up/down is too far to be counted as misstype
                                       e.g. : We want letter 'ξ',
                                              Distance 1 is :
                                                up: 'θ','ι'
                                                down:'ν','μ'
                                                left:'η'
                                                right:'κ'
                                              Distance 2 is :
                                                up: 'υ'
                                                down:','
                                                left:
                                                right:
                                       If we observe the distances from the wanted key we can see that '7','8',' ','β',',','γ','λ' are way too far to be counted
                                       as miss-types.


        Some Sources on fat-fingering : https://datagenetics.com/blog/november42012/index.html
     */

    /*
        This function a letter and finds the row,column and if it is upper case character

        Note: 4 rows ,10~13 columns(see greekKeyboardLayout array)
     */
    public static Triplet GetRowColumnAndUpper(char letter) {
        for(int row = 0; row < greekKeyboardLayout.length;row++){
            for(int col = 0; col < greekKeyboardLayout[row].length;col++) {
                if(greekKeyboardLayout[row][col] != null) {
                    if (greekKeyboardLayout[row][col][0] == letter) {
                        return new Triplet(row, col, 0);
                    } else if (greekKeyboardLayout[row][col][1] == letter) {
                        return new Triplet(row, col, 1);
                    }
                }
            }
        }
        return null;
    }

    public static Character RemoveAccentuations(char input) {
        for(char[] row : greekVowelAccentuation) {
            for(char letter : row) {
                if(input == letter) {
                    return row[0];
                }
            }
        }
        return input;
    }

    public static ArrayList<Character> GetSurroundingCharacters(Character incorrectCharacter,Integer distance) {
        int row,col,upper;
        ArrayList<Character> surroundingCharacters = new ArrayList<>();
        Triplet characterPosition = GetRowColumnAndUpper(incorrectCharacter);
        row = characterPosition.getRow();
        col = characterPosition.getCol();
        upper = characterPosition.getUpperCase();
        int MAX_LEFT_COL,LAST_RIGHT_COL,MAX_UP_ROW,MAX_DOWN_ROW;

        /*
            Distance 1:
            up:row - 1, col , col + 1
            down: row + 1, col, col -1

         */

        if(distance == 1) {
            if(row == 0) {
                if(col == 0) {
                    /* no up and left */
                    surroundingCharacters.add(greekKeyboardLayout[row][col+1][upper]);
                }else if(col == 1){
                    surroundingCharacters.add(greekKeyboardLayout[row][col+1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row+1][col][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row][col-1][upper]);
                }
                else if(col == 12) {
                    /* no up and right */
                    surroundingCharacters.add(greekKeyboardLayout[row][col-1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row+1][col][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row+1][col-1][upper]);
                }
                else {
                    /* no up */
                    surroundingCharacters.add(greekKeyboardLayout[row][col-1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row][col+1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row+1][col][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row+1][col-1][upper]);
                }
            }
            else if(row == 1) {
                if(col == 1) {
                    /* no  left */
                    surroundingCharacters.add(greekKeyboardLayout[row -1][col][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row-1][col+1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row][col+1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row+1][col][upper]);
                } else if(col == 13) {
                    /* no right, down and up*/
                    surroundingCharacters.add(greekKeyboardLayout[row][col-1][upper]);
                } else if(col == 12){
                    /* only one up */
                    surroundingCharacters.add(greekKeyboardLayout[row - 1][col][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row][col+1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row+1][col-1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row][col-1][upper]);
                }
                else {
                    /* all */
                    surroundingCharacters.add(greekKeyboardLayout[row - 1][col][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row - 1][col + 1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row][col+1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row + 1][col][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row + 1][col - 1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row][col-1][upper]);

                }
            }
            else if(row == 2) {
                if(col == 1) {
                    /* no  left */
                    surroundingCharacters.add(greekKeyboardLayout[row -1][col][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row-1][col+1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row][col+1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row+1][col][upper]);
                } else if(col == 11) {
                    /* no right*/
                    surroundingCharacters.add(greekKeyboardLayout[row - 1][col][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row - 1][col + 1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row+1][col-1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row][col-1][upper]);
                } else {
                    /* all */
                    surroundingCharacters.add(greekKeyboardLayout[row - 1][col][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row - 1][col + 1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row][col+1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row+1][col][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row+1][col-1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row][col-1][upper]);
                }
            }
            else if(row == 3) {
                if(col == 1) {
                    /* no  left and down */
                    surroundingCharacters.add(greekKeyboardLayout[row -1][col][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row-1][col+1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row][col+1][upper]);
                } else if(col == 10) {
                    /* no right and down*/
                    surroundingCharacters.add(greekKeyboardLayout[row - 1][col][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row - 1][col + 1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row][col-1][upper]);
                } else {
                    /* no down */
                    surroundingCharacters.add(greekKeyboardLayout[row - 1][col][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row - 1][col + 1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row][col+1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row][col - 1][upper]);

                }
            }
        } else if(distance == 2) {
            if(row == 0) {
                if(col <= 2 ) {
                    surroundingCharacters.add(greekKeyboardLayout[row + 1][col + 1][upper]);
                } else {
                        surroundingCharacters.add(greekKeyboardLayout[row+1][col+1][upper]);
                        surroundingCharacters.add(greekKeyboardLayout[row+1][col-2][upper]);
                }
            }
            else if(row == 1) {
                if(col == 1 || col == 2) {
                    /* no  left */
                    surroundingCharacters.add(greekKeyboardLayout[row + 1][col + 1][upper]);
                } else if(col == 11 || col == 12){
                    surroundingCharacters.add(greekKeyboardLayout[row-1][col-1][upper]);
                }else if(col == 13 ) {
                    /* no right, down and up*/
                    surroundingCharacters.add(greekKeyboardLayout[row+1][col-2][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row-1][col-1][upper]);

                } else {
                    /* all */
                    surroundingCharacters.add(greekKeyboardLayout[row - 1][col - 1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row - 1][col + 2][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row + 1][col + 1][upper]);
                }
            }
            else if(row == 2) {
                if(col == 1) {
                    /* no  left */
                    surroundingCharacters.add(greekKeyboardLayout[row + 1][col+1][upper]);
                } else if(col == 11 || col == 10) {
                    /* no right*/
                    surroundingCharacters.add(greekKeyboardLayout[row - 1][col - 1][upper]);
                } else {
                    /* all */
                    surroundingCharacters.add(greekKeyboardLayout[row - 1][col - 1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row + 1][col + 1][upper]);
                }
            }
            else if(row == 3) {
                if(col == 1) {
                    /* no  left and down */
                    surroundingCharacters.add(greekKeyboardLayout[row - 1][col + 2][upper]);
                } else if(col == 10) {
                    /* no right and down*/
                    surroundingCharacters.add(greekKeyboardLayout[row - 1][col - 1][upper]);
                } else {
                    /* no down */
                    surroundingCharacters.add(greekKeyboardLayout[row - 1][col - 1][upper]);
                    surroundingCharacters.add(greekKeyboardLayout[row - 1][col + 2][upper]);
                }
            }
        }
        return surroundingCharacters;
    }


    /**
     *
     * @param input input string to be padded
     * @param numOfPads number of pads to add on the right(' ' - space pads)
     * @return padded string
     */
    public static String PadString(String input,int numOfPads){

        for(int i = 0; i < numOfPads; i++){
            input += ' ';
        }
        return input;
    }


    public static ArrayList<Character> findPossibleSurroundingLetters(){
        return null;
    }

    public static int FindMostExpectedAdjacent(ArrayList<Character> adjacentCharacters,String validQuery,int expectedIndex ) {
        char mostExpected = 0;
        int mostExpectedIndex = -1;

        for(char adjacent : adjacentCharacters) {
//            System.out.println(adjacent);
            for(int i = expectedIndex; i < validQuery.length(); i++) {
                //System.out.println("vald: " + RemoveAccentuations(validQuery.charAt(i)) + " adj " + adjacent);
                //System.out.println(RemoveAccentuations(validQuery.charAt(i)) == adjacent);
                if(RemoveAccentuations(validQuery.charAt(i)) == adjacent) {
                    //System.out.println(RemoveAccentuations(validQuery.charAt(i)) == adjacent);
                    if(i < mostExpectedIndex || mostExpectedIndex == -1) {
                        mostExpected = validQuery.charAt(i);
                        mostExpectedIndex = i;
                        //System.out.println("ZZZZZZ" + validQuery.charAt(i));
                    }
                }
            }
        }
        //System.out.println(mostExpectedIndex);
        return mostExpectedIndex;
    }

    /**
     *
     * @param validQueriesAndEditDistance : the list of queries that were marked as valid based on the distance from the query given(minimum distance)
     * @param initialQuery : The query that is being corrected/tested
     * @param keyboardDistance : keyboard distance to check(1 or 2)
     * @return should be a sorted list of the queries based on the relativity with the query given()
     */
    public static String KeyboardDistance(ArrayList<Pair<String,Integer>> validQueriesAndEditDistance,String initialQuery,Integer keyboardDistance) {

        /*
            if query length is greater than valid query length then use LCS
αοοουημα -> αίουημα -> αίοτημα
copy from valid str to find if letter has ί ?
         */
        //if same length as valid
        int length;
        int currentQueryEditDistance;
        int updatedQueryEditDistance = 100;
        int improvedStringEditDistance = 100;

        int expectedLetterIndex;
        int tmpExpectedLetterIndex;
        //Boolean foundCorrectAdjacent;
        String correctString = "";
        String query;

        String paddedQuery = "";
        String paddedValidQuery = "";

        for(Pair<String,Integer> validQueryPair : validQueriesAndEditDistance) {

            correctString = "";
            query = initialQuery;
            String validQuery = validQueryPair.left;
            currentQueryEditDistance = validQueryPair.right;
            improvedStringEditDistance = -1; // initial value, it does not mean anything at the start - will always be the minimum value

            if(query.length() < validQuery.length()) {
                length = validQuery.length();
                paddedQuery = PadString(query,validQuery.length() - query.length());
                paddedValidQuery = validQuery;
            } else {
                length = query.length();
                paddedQuery = query;
                paddedValidQuery = PadString(validQuery,query.length() - validQuery.length());
            }

            expectedLetterIndex = 0;
            for(int i = 0; i < length; i++) {
                //System.out.println(query.charAt(i)+ " vQ:" + validQuery.charAt(i));
                //foundCorrectAdjacent = Boolean.FALSE;
                if(paddedQuery.charAt(i) != paddedValidQuery.charAt(i)) {
                    ArrayList<Character> surroundingCharacters = GetSurroundingCharacters(RemoveAccentuations(paddedQuery.charAt(i)),1);
                    //System.out.println(surroundingCharacters);
                    tmpExpectedLetterIndex = FindMostExpectedAdjacent(surroundingCharacters,paddedValidQuery,expectedLetterIndex);
                    if(tmpExpectedLetterIndex == -1) {
                        continue;
                    }
                    else {
                        expectedLetterIndex = tmpExpectedLetterIndex;
                    }
                    char adjacentCharacter = paddedValidQuery.charAt(expectedLetterIndex);
                    //for(char adjacentCharacter: surroundingCharacters) {
                        String updatedString = TransformToMisspelledQueries.replaceChar(query,adjacentCharacter,i);
                        System.out.println("query: "+query+" updatedstring: "+updatedString + " exp index:" + expectedLetterIndex);
                        //if(adjacentCharacter == RemoveAccentuations(paddedValidQuery.charAt(expectedLetterIndex))) {
                        //    System.out.println("eq but diff" + updatedString.charAt(i) + " padQ: " + paddedValidQuery.charAt(expectedLetterIndex) );
                        //    updatedString = TransformToMisspelledQueries.replaceChar(updatedString,paddedValidQuery.charAt(expectedLetterIndex),i);
                        //}
                        updatedQueryEditDistance = EditDistance.calculate(validQuery,updatedString);
                        System.out.println("initial d" + currentQueryEditDistance + " updated d" + updatedQueryEditDistance + " improved d" + improvedStringEditDistance);
                        if(updatedQueryEditDistance < currentQueryEditDistance) {// && improvedStringEditDistance <= updatedStringEditDistance) {
                            System.out.println("padded valid at i:" + paddedValidQuery.charAt(expectedLetterIndex) + " adjacent: " +RemoveAccentuations(adjacentCharacter));
                            correctString = updatedString;
                            query = updatedString;
                            //improvedStringEditDistance = updatedStringEditDistance;
                            //if(paddedValidQuery.charAt(expectedLetterIndex) == adjacentCharacter) {
                            //    query = updatedString;
                                currentQueryEditDistance = updatedQueryEditDistance;
                                //foundCorrectAdjacent = Boolean.TRUE;
                                //improvedStringEditDistance = updatedQueryEditDistance;
                                expectedLetterIndex++;
                                //break;
                            //}
                        }
                    //}
                }
                else {
                    expectedLetterIndex++;
                    continue;
                }
            }


//            expectedLetterIndex = 0;
//            for(int i = 0; i < length; i++) {
//                //System.out.println(query.charAt(i)+ " vQ:" + validQuery.charAt(i));
//                //foundCorrectAdjacent = Boolean.FALSE;
//                if(paddedQuery.charAt(i) != paddedValidQuery.charAt(i)) {
//                    ArrayList<Character> surroundingCharacters = GetSurroundingCharacters(RemoveAccentuations(paddedQuery.charAt(i)),1);
//                    //System.out.println(surroundingCharacters);
//                    for(char adjacentCharacter: surroundingCharacters) {
//                        String updatedString = TransformToMisspelledQueries.replaceChar(query,adjacentCharacter,i);
//                        System.out.println("query: "+query+" updatedstring: "+updatedString + " exp index:" + expectedLetterIndex);
//                        if(adjacentCharacter == RemoveAccentuations(paddedValidQuery.charAt(expectedLetterIndex))) {
//                            System.out.println("eq but diff" + updatedString.charAt(i) + " padQ: " + paddedValidQuery.charAt(expectedLetterIndex) );
//                            updatedString = TransformToMisspelledQueries.replaceChar(updatedString,paddedValidQuery.charAt(expectedLetterIndex),i);
//                        }
//                        updatedQueryEditDistance = EditDistance.calculate(validQuery,updatedString);
//                        System.out.println("initial d" + currentQueryEditDistance + " updated d" + updatedQueryEditDistance + " improved d" + improvedStringEditDistance);
//                        if(updatedQueryEditDistance < currentQueryEditDistance) {// && improvedStringEditDistance <= updatedStringEditDistance) {
//                            System.out.println("padded valid at i:" + paddedValidQuery.charAt(expectedLetterIndex) + " adjacent: " +RemoveAccentuations(adjacentCharacter));
//                            correctString = updatedString;
//                            //query = updatedString;
//                            //improvedStringEditDistance = updatedStringEditDistance;
//                            if(RemoveAccentuations(paddedValidQuery.charAt(expectedLetterIndex)) == adjacentCharacter) {
//                                query = updatedString;
//                                currentQueryEditDistance = updatedQueryEditDistance;
//                                //foundCorrectAdjacent = Boolean.TRUE;
//                                //improvedStringEditDistance = updatedQueryEditDistance;
//                                expectedLetterIndex++;
//                                break;
//                            }
//                        }
//                    }
//                }
//                else {
//                    expectedLetterIndex++;
//                    continue;
//                }
//            }
        }
        /*
            transp

            if transposition then keyboard error offers nothing
        */
        return correctString;
    }

    public static void main(String[] args) {
        String tmp1 = "βάζχ";
        String tmp2 = "βαωζ";


        char[][][] kLay = GetGreekKeyboardLayout();

        /*
            Test - for each possible character find  column,row and upper case

            Function to test: FindRowColumnAndUpper

            Results: Correct
        */

        /*for(char[][] row : kLay) {
            for(char[] cell : row) {
                for(char character: cell) {
                    Triplet res = FindRowColumnAndUpper(character);
                    System.out.println("char '" + character + "':" + res.getRow() + ", "+ res.getCol() +", "+res.getUpperCase());
                }
            }
        }*/

        /*
            Test - for each possible character find surrounding keys at distance 1(or 2)

            Function to test: GetSurroundingCharacters

            Results: Correct for distance 1 and 2
        */
        /*for (int i = 1; i <= 2; i++) {
            System.out.println("DISTANCE :" + i);
            for (char[][] row : kLay) {
                for (char[] cell : row) {
                    if (cell != null) {
                        //System.out.println(cell);
                        for (char character : cell) {
                            ArrayList<Character> res = GetSurroundingCharacters(character, i);
                            System.out.println("surroundings of " + character + ":" + res);
                        }
                    }
                }
            }
        }*/

        /*
            Remove Accentuations testing

            Function to test - RemoveAccentuations

            Result: Correct
         */

        /*for (char[] row : GetGreekVowelAccentuations()) {
            for(char letter : row) {
                System.out.println(RemoveAccentuations(letter));
            }
        }*/

        /*
            test - String padding on the right

            worked
         */
        /*String x = "βαζ";

        System.out.println("pad:" + PadString(x,3) + " | x: "+ x);
        */

        /*
            Test - Load Consonants and convert to misspelled queries

         */
        //ArrayList<ArrayList<String>> consonants = JsonReader.parseJsonFileConsonants();

        /*
            Test- Finding correct Word on keyboard distance or finding any correction based on keyboard error
         */
        FindMostExpectedAdjacent(GetSurroundingCharacters('υ', 1),"αίτημα",1);
        ArrayList<Pair<String,Integer>> validQs = new ArrayList<>();
        ArrayList<Pair<String,Integer>> validQs2 = new ArrayList<>();

        //validQs.add("βάζω");
        //validQs.add("βάζο");
        validQs.add(new Pair("αίτημα",7));
        //validQs.add("ετοιμα");

        //αοτοιμα;
        //ετοιμα 2;
        //εοτοιμα 1;


        //validQs2.add("βάζο");
        //validQs2.add("βάζω");

        //System.out.println(KeyboardDistance(validQs,"νάσψ",1,3));
        //System.out.println(KeyboardDistance(validQs,"νάσκ",1,3));
        System.out.println(KeyboardDistance(validQs,"αοοουυοιμα",1));

        //System.out.println(KeyboardDistance(validQs2,"νάσψ",1,3));
        //System.out.println(KeyboardDistance(validQs2,"νάσκ",1,3));
    }
}
