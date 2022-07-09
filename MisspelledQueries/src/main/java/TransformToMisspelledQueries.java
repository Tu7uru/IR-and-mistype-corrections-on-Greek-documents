import java.util.ArrayList;
import java.util.Random;

public class TransformToMisspelledQueries {

    public Character[] greekLetters = {'α','β','γ','δ','ε','ζ','η','θ','ι','κ','λ','μ',
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

    public ArrayList<String> AddExtraCharacter(ArrayList<String> tokenizedQueries) {
        int length;
        int index;
        int numofletters = 24;
        Random random = new Random();

        String transformedQuery;
        ArrayList<String> transformedQueries = new ArrayList<>();

        for(String query : tokenizedQueries) {
            length = query.length();
            index = random.nextInt(length);
            transformedQuery = addChar(query,this.greekLetters[random.nextInt(numofletters)],index);
            transformedQueries.add(transformedQuery);
        }

        return transformedQueries;
    }

    public ArrayList<String> RemoveCharacter(ArrayList<String> tokenizedQueries) {
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

    public ArrayList<String> IncorrectCharacter(ArrayList<String> tokenizedQueries) {
        int length;
        int index;
        int numofletters = 24;
        Random random = new Random();

        String transformedQuery;
        ArrayList<String> transformedQueries = new ArrayList<>();

        for(String query : tokenizedQueries) {
            length = query.length();
            index = random.nextInt(length);
            transformedQuery = addChar(query,this.greekLetters[random.nextInt(numofletters)],index);
            transformedQueries.add(transformedQuery);
        }
        return transformedQueries;
    }
    public static void main(String[] args) {
        String x = "κάρδαμο";
        System.out.println(addChar(x,'ζ' ,6));
        System.out.println(removeChar(x ,0));
        System.out.println(replaceChar(x,'ζ' ,0));
    }
}
