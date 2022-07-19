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


    /**
     *
     * @param validQueries : the list of queries that were marked as valid based on the distance from the query given(minimum distance)
     * @param query : The query that is being corrected/tested
     * @param keyboardDistance : keyboard distance to check(1 or 2)
     * @param EditDistanceQ : all valid queries have the same distance from the query given
     * @return should be a sorted list of the queries based on the relativity with the query given()
     */
    public static String KeyboardDistance(ArrayList<String> validQueries,String query,Integer keyboardDistance,Integer EditDistanceQ) {

        /*
            if query length is greater than valid query length then use LCS
αοοουημα -> αίουημα -> αίοτημα
copy from valid str to find if letter has ί ?
         */


        /*
            Find which character/s is/are the incorrect one/s
        */

        /*
            Find if it is addition/deletion/substitution/transposition
        */

        /*
            add

            find which character is extra and ignore it, then find if incorrect character exists and check if any adjacent reduces edit distance
        */

        /*
            del

            find if incorrect character exists and check if any adjacent reduces edit distance
        */

        /*
            subst
        */
        //if same length as valid
        int length;
        String correctString = "";

        String paddedQuery = "";
        String paddedValidQuery = "";

        for(String validQuery : validQueries) {

            if(query.length() < validQuery.length()) {
                length = validQuery.length();
                paddedQuery = PadString(query,validQuery.length() - query.length());
                paddedValidQuery = validQuery;
            } else {
                length = query.length();
                paddedQuery = query;
                paddedValidQuery = PadString(validQuery,query.length() - validQuery.length());
            }

            for(int i = 0; i < length; i++) {
                //System.out.println(query.charAt(i)+ " vQ:" + validQuery.charAt(i));
                if(paddedQuery.charAt(i) != paddedValidQuery.charAt(i)) {
                    ArrayList<Character> surroundingCharacters = GetSurroundingCharacters(RemoveAccentuations(paddedQuery.charAt(i)),1);
                    //System.out.println(surroundingCharacters);
                    for(char adjacentCharacter: surroundingCharacters) {
                        String updatedString = TransformToMisspelledQueries.replaceChar(query,adjacentCharacter,i);
                        //System.out.println("query: "+query+" updatedstring: "+updatedString);
                        if(EditDistance.calculate(validQuery,updatedString) < EditDistanceQ) {
                            correctString = updatedString;
                            //System.out.println(correctString);
                        }
                        if(EditDistanceQ > EditDistance.calculate(validQuery,updatedString)) {
                            EditDistanceQ = EditDistance.calculate(validQuery,updatedString);
                            query = updatedString;
                        }
                    }
                }
            }
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
        System.out.println();


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
            Test- Finding correct Word on keyboard distance or finding any correction based on keyboard error
         */
        ArrayList<String> validQs = new ArrayList<>();
        ArrayList<String> validQs2 = new ArrayList<>();

        //validQs.add("βάζω");
        //validQs.add("βάζο");
        validQs.add("αιτημα");
        validQs.add("ετοιμα");

        //αοτοιμα;
        //ετοιμα 2;
        //εοτοιμα 1;


        validQs2.add("βάζο");
        validQs2.add("βάζω");

        //System.out.println(KeyboardDistance(validQs,"νάσψ",1,3));
        //System.out.println(KeyboardDistance(validQs,"νάσκ",1,3));
        System.out.println(KeyboardDistance(validQs,"αοοοτοιμα",1,5));

        System.out.println(KeyboardDistance(validQs2,"νάσψ",1,3));
        System.out.println(KeyboardDistance(validQs2,"νάσκ",1,3));
    }
}
