import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

public class Main {
static Scanner kb;	// scanner connected to keyboard input, or input file
    public static void main(String[] args) {
        kb = new Scanner(System.in); // use keyboard and console
        ArrayList<String> input;                         //array list that holds the input command
        input = parse(kb);
        while(input.size() != 0) {
            input = parse(kb);  //run the program until quit is entered
            int phraseLen = Integer.valueOf(input.get(input.size()-1));
            for(int i = 0; i<input.size()-3; i++){
                File thisFile = new File(input.get(input.size()-2) + input.get(i));   //the actual test doc to compare against all others
                for(int j = i+1; j<input.size()-1; j++){
                    int counter = 0; int maxMatches = 0;
                    File otherFile = new File(input.get(input.size()-2) + input.get(j));    //file to be compared against
                    int k = 0; int w = 0;
                    String words1[]; String words2[];
                    String line1; String line2;
                    HashMap<String, String> hm = new HashMap<>();
                    ArrayList<ArrayList<String>> commonPhrases = new ArrayList<>();
                    ArrayList<String> phrases = new ArrayList<>();
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(thisFile), "UTF-8"));
                        line1 = br.readLine();
                        BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(otherFile), "UTF-8"));
                        line2 = br2.readLine();

                        while((line1 != null)){ //while you haven't reached the end of first file
                            words1 = line1.split("\\s");    //get the words for each line in file 1
                            words2 = line2.split("\\s");    //get the words for each line in file 2
                            if(words1[k].equals(words2[w])){    //encountered the same word
                                phrases.add(words1[k]);
                                counter++;
                                k++;
                                w++;
                            }else{  //end of phrase match, add to list of common phrases between this file and other file
                                if(counter >= phraseLen){   //makes sure added phrases are of at least the required phrase length
                                    commonPhrases.add(phrases);
                                }
                                phrases.clear();
                                counter = 0; w++;
                            }
//                            if (hm.containsKey(words1[0])){
//                                System.out.println("Found duplicate ... handle logic");
//                            }
//                            hm.put(words1[0],words1[1]); //if index==0 is ur key
                            line1 = br.readLine();
                            line2 = br.readLine();
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     *  take command line parameters for the path from the executable program to the text
     *  files and n (the length of the word sequence)
     *  prompt>java cheaters path/to/text/files 6
     * @param keyboard takes in input from the keyboard
     * @return an arraylist with the input from the user
     */
    public static ArrayList<String> parse(Scanner keyboard) {
        System.out.println("Please input command in form [path/to/text/files] [X] where X is the length of phrase sequences. Type \"quit\" to exit.");
        System.out.print("Prompt>java cheaters ");
        String s = kb.nextLine();
        String quit = "quit";
        String[] command = s.split("\\s+"); //split command up into separate words
        ArrayList<String> input = new ArrayList<>();
        if((command[0].equals(quit)) && (command.length == 1)){ //valid quit command
            return input;
        }
        if(command.length != 2){    //ensure command is 2 strings long
            System.out.println("Invalid input");
            return input;
        }
        String filePath = command[0];
        try{
            int second = Integer.valueOf(command[1]);
            File folder = new File(filePath);
            String[] srcFiles = folder.list();  //get the list of input files
            for(int i = 0; i<srcFiles.length; i++){
                input.add(srcFiles[i]); //get the list of input files
            }
        }catch(NumberFormatException e){    //makes sure that the phrase length is an integer
            System.out.println("Invalid input: Phrase length parameter incorrect.");
        }catch( NullPointerException e) {   //makes sure that the file path is valid
            System.out.println("Invalid input: Incorrect file path.");
        }
        input.add(command[0]);  //add the filepath in input so you can access the other files
        input.add(command[1]);  //add the phrase length in input so you can access it later
        return input;
    }
}
