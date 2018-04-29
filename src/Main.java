import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

public class Main {
    private static Scanner kb;	// scanner connected to keyboard input, or input file
    private static int minMatches;

    public static void main(String[] args) {
        kb = new Scanner(System.in);                                                    // use keyboard and console
        ArrayList<String> input;                                                        //array list that holds the input command
        input = parse(kb);
        while (input.size() != 0) {                                                     //keep running if "quit" command not entered
            if (input.size() != 1) {                                                    //if exception was encountered then keep running, not a "quit"
                String rightFilePath = input.get(input.size()-3);
                if (!input.get(input.size()-3).endsWith("/")) {                         //account for if file path doesn't end with a "/"
                    input.remove(input.size()-3);
                    rightFilePath += "/";
                    input.add(input.size()-2, rightFilePath);
                }
                int phraseLen = Integer.valueOf(input.get(input.size()-2));
                minMatches = Integer.valueOf(input.get(input.size()-1));
                HashMap<String, ArrayList<ArrayList<String>>> hm = new HashMap<>();
                for(int i = 0; i<input.size()-3; i++) {//goes until the last file
                    ArrayList<ArrayList<String>> phrases = new ArrayList<>();
                    File thisFile = new File(rightFilePath + input.get(i));    //the actual test doc to compare against all others
//                    String line1;
//                    System.out.println(input.get(i)); //*********DEBUG STATEMENT making sure all files are being accessed*********
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(thisFile), "UTF-8"));
                        Scanner s = new Scanner(br);                    //make a scanner that uses the buffered reader to read the file
                        while (s.hasNext()) {
                            int numWords = 0;
                            ArrayList<String> stringEntry = new ArrayList<>();
                            while((numWords < phraseLen) && (s.hasNext()) && (phrases.size() == 0)){
                                stringEntry.add(s.next().replaceAll("[^\\w\\s\\ ]", "").toLowerCase());     //get the first phrase in the doc
                                numWords++;
                            }
                            if((phrases.size()>0) && (s.hasNext())){
                                ArrayList<String> newPhrase = new ArrayList<>(phrases.get(phrases.size()-1));               //for all other phrases, get the last phrase
                                newPhrase.remove(0);                                                                   //remove the first word
                                newPhrase.add(s.next().replaceAll("[^\\w\\s\\ ]", "").toLowerCase());       //add the next word
                                phrases.add(newPhrase);                                                                     //add the phrase to the 2d string array
                            }else {
                                phrases.add(stringEntry);   //add first phrase to the string array
                            }
                            hm.put(input.get(i), phrases);  //add string arrays to hash map
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                displaySimilarities(getSimilarities(hm));       // Display the number of similarities after the comparison is completed
            }
            input = parse(kb);  //run the program until quit is entered
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
        if(command.length > 3){
            input.add("dummy");
            System.out.println("Invalid input. Try again.");
            return input;
        }
        String filePath = command[0];
        try{
            int second = Integer.valueOf(command[1]);
            int third = Integer.valueOf(command[2]);
            File folder = new File(filePath);
            String[] srcFiles = folder.list();  //get the list of input files
            for(int i = 0; i<srcFiles.length; i++){
                input.add(srcFiles[i]); //get the list of input files
            }
            input.add(command[0]);  //add the filepath in input so you can access the other files
            input.add(command[1]);  //add the phrase length in input so you can access it later
            input.add(command[2]);  //add the minimum phrase matches to input
        }catch(NumberFormatException e){    //makes sure that the phrase length is an integer
            input.add("dummy");
            System.out.println("Invalid input: Phrase length parameter incorrect. Try again.");
        }catch( NullPointerException e) {   //makes sure that the file path is valid
            input.add("dummy");
            System.out.println("Invalid input: Incorrect file path. Try again.");
        }catch( ArrayIndexOutOfBoundsException e) {   //makes sure that the input (length) is valid
            input.add("dummy");
            System.out.println("Invalid input. Try again.");
        }
        return input;
    }

    /**
     * This method compares the data within the given hashmap and returns a new hashmap with the similarities between files
     * @param data is the hashmap with the data of all the phrases in each file
     * @return a new hashmap with the number of similarities between the different files
     */
    private static HashMap<String, Integer> getSimilarities(HashMap<String, ArrayList<ArrayList<String>>> data) {
        HashMap<String, Integer> similarities = new HashMap<>();
        int set1Count = 0;
        for (Map.Entry<String, ArrayList<ArrayList<String>>> entry1 : data.entrySet()) {     // Traverse every file in the map
            int set2Count = 0;
            for (Map.Entry<String, ArrayList<ArrayList<String>>> entry2 : data.entrySet()) { // Compare against every other file
                set2Count++;
                if (set2Count > set1Count) continue;
                int count = 0;
                for (int i = 0; i < entry1.getValue().size(); i++) {                         // Traverse the number of phrases
                    for (int j = 0; j < entry2.getValue().size(); j++) {                     // Compare against the phrases in the other file
                        boolean equal = true;
                        for (int k = 0; k < entry1.getValue().get(i).size(); k++) {          // Compare the words
                            if (!entry1.getValue().get(i).get(k).equals(entry2.getValue().get(j).get(k)))
                                equal = false;
                        }
                        if (equal)
                            count++;
                    }
                }
                similarities.put(entry1.getKey() + ", " + entry2.getKey(), count);
            }
            set1Count++;
        }
        return similarities;
    }

    /**
     * This method prints the similarities
     * @param similarities is the hashmap with the similarities between each pair of files
     */
    private static void displaySimilarities(HashMap<String, Integer> similarities) {
        HashMap<Integer, String> flipped = new HashMap<>(similarities.size());
        for (Map.Entry<String, Integer> entry : similarities.entrySet()) {
            flipped.put(entry.getValue(), entry.getKey());
        }
        TreeMap<Integer, String> sorted = new TreeMap<>(Collections.reverseOrder());
        sorted.putAll(flipped);
        for (Map.Entry<Integer, String> entry : sorted.entrySet()) {
            if (entry.getKey() >= minMatches) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }

}
