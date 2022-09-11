import utils.EditDistance;
import utils.Pair;
import utils.Triplet;

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

        This idea only works for similar words(homophones/consonants) that are written differently and have the same edit distance.
        For this reason we need to break down misspelled queries cases and see how they would be handled theoretically

        βαζχ :
                deletion: not relevant with miss-typed character
                          - the algorithm does not do well with these cases
                addition: not relevant with miss-typed character
                          should check if removal of the extra character reduces edit distance
                          or make sure not to investigate extra character
                transposition*: it is not checked since it has nothing to do with substitutions
                substitution: should be checked with edit distance and then find which character is more relevant based on keyboard layout
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

    /**
     * This function a letter and finds the row,column and if it is upper case character
     *
     * Note: 4 rows ,10~13 columns(see greekKeyboardLayout array)
     *
     * @param letter a letter that can be found on the keyboard(greek layout)
     * @return the Row,Column  on the keyboard(see keyboardLayout) and if it is uppercase
     */
    public static KeyboardLayoutCoordinates GetRowColumnAndUpper(char letter) {
        for(int row = 0; row < greekKeyboardLayout.length;row++){
            for(int col = 0; col < greekKeyboardLayout[row].length;col++) {
                if(greekKeyboardLayout[row][col] != null) {
                    if (greekKeyboardLayout[row][col][0] == letter) {
                        return new KeyboardLayoutCoordinates(row, col, 0);
                    } else if (greekKeyboardLayout[row][col][1] == letter) {
                        return new KeyboardLayoutCoordinates(row, col, 1);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Removes Accentuation from the character given as input
     *
     * @param input the character to remove accentuation
     * @return the character without accentuation
     */
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

    /**
     *
     * @param incorrectCharacter character input(expected to exist on keynoard of type QWERTY)
     * @param distance distance to check - 1 or 2(some surrounding letters have higher by 1cm distance)
     * @return All surrounding letters according to distance 1 or 2
     */
    public static ArrayList<Character> GetSurroundingCharacters(Character incorrectCharacter,Integer distance) {
        int row,col,upper;
        ArrayList<Character> surroundingCharacters = new ArrayList<>();
        KeyboardLayoutCoordinates characterPosition = GetRowColumnAndUpper(RemoveAccentuations(incorrectCharacter));
        //System.out.println(incorrectCharacter);
        row = characterPosition.getRow();
        col = characterPosition.getCol();
        upper = characterPosition.getUpperCase();

        if(row == -1) {
            //System.out.println("in");
            return null;
        }
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

    /**
     * This function takes the adjacent characters from a character in the keyboard and based on the currently investigated
     * index in the validQuery it searches which character is the most expected to be returned(this is incase one character
     * maps to more than one keys that decrease the edit distance)
     *
     * @param adjacentCharacters List of adjacent characters(from some character in the keyboard layout)
     * @param validQuery the query that is syntactically correct
     * @param expectedIndex the index in the validQuery that is currently being searched(for keyboard distance error)
     * @return the index that is most expected to reduce the edit distance on the following steps
     */
    public static int FindMostExpectedAdjacent(ArrayList<Character> adjacentCharacters,String validQuery,int expectedIndex, int maxIndex, int depth) {
        int mostExpectedIndex = -1;

        for(char adjacent : adjacentCharacters) {
//            System.out.println(adjacent);
            for(int i = expectedIndex; i <= maxIndex; i++) {
//                System.out.println("vald: " + RemoveAccentuations(validQuery.charAt(i)) + " adj " + adjacent);
//                System.out.println(RemoveAccentuations(validQuery.charAt(i)) == adjacent);
                if(RemoveAccentuations(validQuery.charAt(i)) == adjacent) {
//                    System.out.println(RemoveAccentuations(validQuery.charAt(i)) == adjacent);
                    if(i < mostExpectedIndex || mostExpectedIndex == -1) {
                        if(depth != 0) {
                            depth--;
                            continue;
                        }
                        mostExpectedIndex = i;
//                        System.out.println("ZZZZZZ" + validQuery.charAt(i));
                    }
                }
            }
        }
        //System.out.println(mostExpectedIndex);
        return mostExpectedIndex;
    }

    /**
     * Takes a misspelled query and a list of valid queries
     *
     * @param validQueriesAndEditDistance : the list of queries and their respective Edit Distance that were marked as valid and were given to investigate
     * @param initialQuery : The misspelled query that is being corrected
     * @return A list of (ValidQuery,Corrected_Query,KeyboardDistance) entries which are the given query modified to be closer to each respective validQuery.For each of these,keyboard distance is also stored.
     */
    public static ArrayList<Triplet> CorrectKeyboardMisType(ArrayList<Pair<String,Integer>> validQueriesAndEditDistance, String initialQuery/*,Integer keyboardDistance*/) {

        /*
            if query length is greater than valid query length then use LCS
αοοουημα -> αίουημα -> αίοτημα
         */
        ArrayList<Triplet> results = new ArrayList<>();
        //if same length as valid
        int minlength;
        int currentQueryEditDistance = -1;
        int updatedQueryEditDistance = 100;

        int expectedLetterIndex;
        int tmpExpectedLetterIndex;
        String correctString = "";
        String updatedString = "";
        String query;

        String paddedQuery = "";
        String paddedValidQuery = "";

        for(Pair<String,Integer> validQueryPair : validQueriesAndEditDistance) {

            Triplet result = new Triplet();

            correctString = "";
            query = initialQuery;
            String validQuery = validQueryPair.left;
            currentQueryEditDistance = validQueryPair.right;
//            System.out.println("current " + currentQueryEditDistance);
            if(query.length() < validQuery.length()) {
                minlength = query.length();
                paddedQuery = PadString(query,validQuery.length() - query.length());
                paddedValidQuery = validQuery;
            } else {
                minlength = validQuery.length();
                paddedQuery = query;
                paddedValidQuery = PadString(validQuery,query.length() - validQuery.length());
            }
            //change to min length and extra characters check if any decrease edit distance
            expectedLetterIndex = 0;
//            System.out.println(minlength);
            for(int i = 0; i < minlength; i++) {
                //System.out.println(query.charAt(i)+ " vQ:" + validQuery.charAt(i));
                if(paddedQuery.charAt(i) != paddedValidQuery.charAt(i)) {
//                    System.out.println(RemoveAccentuations(paddedQuery.charAt(i)));
                    ArrayList<Character> surroundingCharacters = GetSurroundingCharacters(RemoveAccentuations(paddedQuery.charAt(i)),1);
                    //System.out.println(RemoveAccentuations(paddedQuery.charAt(i)));
//                    System.out.println("query: "+query+" updatedstring: "+updatedString + " exp index:" + expectedLetterIndex + " i:" + i);
                    for(int depth = 0; depth < 3; depth++) {
                        tmpExpectedLetterIndex = FindMostExpectedAdjacent(surroundingCharacters, paddedValidQuery, expectedLetterIndex, i, depth);
//                        System.out.println(tmpExpectedLetterIndex);
                        if (tmpExpectedLetterIndex == -1) {
                            continue;
                        } else {
                            expectedLetterIndex = tmpExpectedLetterIndex;
                        }
                        char adjacentCharacter = paddedValidQuery.charAt(expectedLetterIndex);
                        updatedString = TransformToMisspelledQueries.replaceChar(query, adjacentCharacter, i);
                        //System.out.println("query: "+query+" updatedstring: "+updatedString + " exp index:" + expectedLetterIndex + " i:" + i);
                        updatedQueryEditDistance = EditDistance.calculate(validQuery, updatedString);
                        //System.out.println("initial d" + currentQueryEditDistance + " updated d" + updatedQueryEditDistance );//+ " improved d" + improvedStringEditDistance);
                        if (updatedQueryEditDistance < currentQueryEditDistance) {// && improvedStringEditDistance <= updatedStringEditDistance) {
                            //System.out.println("padded valid at i:" + paddedValidQuery.charAt(expectedLetterIndex) + " adjacent: " +RemoveAccentuations(adjacentCharacter));
                            correctString = updatedString;
                            query = updatedString;
                            currentQueryEditDistance = updatedQueryEditDistance;
                            expectedLetterIndex++;
                            break;
                        }
                    }
                }
                else {
                    expectedLetterIndex++;
                    continue;
                }
            }
//            System.out.println("q:" + query.length() + " vQ: " + validQuery.length());
            if(validQuery.length() < query.length()) {
//                System.out.println("INSIDE IF");
                for(int i = minlength; i < query.length(); i++) {
                    if(paddedQuery.charAt(i) != paddedValidQuery.charAt(i)) {
                        ArrayList<Character> surroundingCharacters = GetSurroundingCharacters(RemoveAccentuations(paddedQuery.charAt(i)),1);
                        //System.out.println(surroundingCharacters);
                        for(char adjacentCharacter: surroundingCharacters) {
                            updatedString = TransformToMisspelledQueries.replaceChar(query,adjacentCharacter,i);
    //                        System.out.println("query: "+query+" updatedstring: "+updatedString + " exp index:" + expectedLetterIndex);
                            if(adjacentCharacter == RemoveAccentuations(paddedValidQuery.charAt(expectedLetterIndex))) {
    //                            System.out.println("eq but diff" + updatedString.charAt(i) + " padQ: " + paddedValidQuery.charAt(expectedLetterIndex) );
                                updatedString = TransformToMisspelledQueries.replaceChar(updatedString,paddedValidQuery.charAt(expectedLetterIndex),i);
                            }
                            updatedQueryEditDistance = EditDistance.calculate(validQuery,updatedString);
    //                        System.out.println("initial d" + currentQueryEditDistance + " updated d" + updatedQueryEditDistance + " improved d" + improvedStringEditDistance);
                            if(updatedQueryEditDistance < currentQueryEditDistance) {// && improvedStringEditDistance <= updatedStringEditDistance) {
    //                            System.out.println("padded valid at i:" + paddedValidQuery.charAt(expectedLetterIndex) + " adjacent: " +RemoveAccentuations(adjacentCharacter));
                                correctString = updatedString;
                                if(RemoveAccentuations(paddedValidQuery.charAt(expectedLetterIndex)) == adjacentCharacter) {
                                    query = updatedString;
                                    currentQueryEditDistance = updatedQueryEditDistance;
                                    expectedLetterIndex++;
                                    break;
                                }
                            }
                        }
                    }
                    else {
                        expectedLetterIndex++;
                        continue;
                    }
                }
            }
            if(correctString == "")
                correctString = initialQuery;
            result.setLeft(validQueryPair.left);
            result.setMid(correctString);
            result.setRight(validQueryPair.right - currentQueryEditDistance);
            results.add(result);
        }
        return results;
    }

    public static void main(String[] args) {
        char[][][] kLay = GetGreekKeyboardLayout();

    }
}
