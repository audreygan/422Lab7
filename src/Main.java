import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Main{
    private static int minMatches;

    public static void main(String[] args) {
        Scanner kb = new Scanner(System.in);                                            // Use keyboard and console
        ArrayList<String> input;                                                        // Array list that holds the input command
        input = parse(kb);
        while (input.size() != 0) {                                                     // Keep running if "quit" command not entered
            if (input.size() != 1) {                                                    // If exception was encountered then keep running, not a "quit"
                String rightFilePath = input.get(input.size()-3);
                if (!input.get(input.size()-3).endsWith("/")) {                         // Account for if file path doesn't end with a "/"
                    input.remove(input.size()-3);
                    rightFilePath += "/";
                    input.add(input.size()-2, rightFilePath);
                }
                int phraseLen = Integer.valueOf(input.get(input.size()-2));
                minMatches = Integer.valueOf(input.get(input.size()-1));
                HashMap<String, ArrayList<String>> phraseMap = new HashMap<>();         // Key is the phrase(String) and the value is the array list of filenames that contain that phrase
                for(int i = 0; i < input.size()-3; i++) {                               // Goes until the last file
                    ArrayList<ArrayList<String>> phrases = new ArrayList<>();
                    File thisFile = new File(rightFilePath + input.get(i));    // The actual test doc to compare against all others
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(thisFile), "UTF-8"));
                        Scanner s = new Scanner(br);                                    // Make a scanner that uses the buffered reader to read the file
                        while (s.hasNext()) {
                            int numWords = 0;
                            ArrayList<String> stringEntry = new ArrayList<>();
                            String firstPhrase = "";
                            if (phrases.size() == 0) {
                                while ((numWords < phraseLen) && (s.hasNext())) {
                                    stringEntry.add(s.next().replaceAll("[^\\w\\s\\ ]", "").toLowerCase());     // Get the first phrase in the doc
                                    numWords++;
                                }
                                phrases.add(stringEntry);                               // Add first phrase to the string array
                                for(int j = 0; j < stringEntry.size(); j++){            // Turn the string entry array into a string
                                    firstPhrase += stringEntry.get(j);
                                }
                                managePhrase(phraseMap, input, firstPhrase, i);
                            } else if (phrases.size() > 0 && s.hasNext()) {
                                String thisPhrase = "";
                                ArrayList<String> newPhrase = new ArrayList<>(phrases.get(phrases.size() - 1));              // For all other phrases, get the last phrase
                                newPhrase.remove(0);                                                                    // Remove the first word
                                newPhrase.add(s.next().replaceAll("[^\\w\\s\\ ]", "").toLowerCase());         // Add the next word
                                phrases.add(newPhrase);
                                for(int j = 0; j < newPhrase.size(); j++){              // Turn the string entry array into a string
                                    thisPhrase += newPhrase.get(j);
                                }
                                managePhrase(phraseMap, input, thisPhrase, i);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                HashMap<String, FilePair> matchesMap = new HashMap<>();     // Hash map that finds how many matches there are between file pairs
                getSimMap(phraseMap, matchesMap);
                displaySimilarities(matchesMap);
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
    private static ArrayList<String> parse(Scanner keyboard) {
        System.out.println("Please input command in form [path/to/text/files] [X] where X is the length of phrase sequences. Type \"quit\" to exit.");
        System.out.print("Prompt>java cheaters ");
        String s = keyboard.nextLine();
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
     * If the phrase already is in the hashmap, add the file that contains it to that phrases list
     * Otherwise, create a new list and add the file to that list for the phrase
     * @param phraseMap is the HashMap of phrases
     * @param input combined with index i is the file to be added
     * @param phrase is the key for the hashmap
     * @param i is the index of the file name within input
     */
    private static void managePhrase(HashMap<String, ArrayList<String>> phraseMap, ArrayList<String> input, String phrase, int i) {
        if (phraseMap.containsKey(phrase)) {                    // If the phrase already exists in the hashmap, append file to list
            phraseMap.get(phrase).add(input.get(i));
        } else {
            ArrayList<String> newFileList = new ArrayList<>();  // Otherwise, add the new phrase and create a new file list for that phrase
            newFileList.add(input.get(i));
            phraseMap.put(phrase,newFileList);
        }
    }

    /**
     * This method fills a HashMap of Strings and FilePairs and updates the number of matches that each pair has
     * @param phraseMap is the HashMap that holds the phrases and files to be compared
     * @param matchesMap is the HashMap that will be filled up and updated after this method is complete
     */
    private static void getSimMap(HashMap<String, ArrayList<String>> phraseMap, HashMap<String, FilePair> matchesMap) {
        for(Map.Entry<String, ArrayList<String>> entry : phraseMap.entrySet()){         // Go through the entries in the first hash map
            for(int n = 0; n < entry.getValue().size(); n++){                           // Go through the file name array lists for each phrase
                for(int m = n + 1; m < entry.getValue().size(); m++){
                    if(!entry.getValue().get(n).equals(entry.getValue().get(m))) {
                        String pairString = entry.getValue().get(n) + ", " + entry.getValue().get(m);
                        if (matchesMap.containsKey(pairString)) {                       // If the file pair string name is already a key, increment the number of matches
                            matchesMap.get(pairString).numMatches += 1;
                        } else {                                                        // Otherwise it's a new pair so add this new file pair to the hash map
                            FilePair newPair = new FilePair();
                            newPair.file1 = entry.getValue().get(n);
                            newPair.file2 = entry.getValue().get(m);
                            newPair.numMatches = 1;
                            matchesMap.put(pairString, newPair);
                        }
                    }
                }
            }
        }
    }

    /**
     * This method prints the similarities
     * @param matchesMap is the hashmap with the similarities between each pair of files
     */
    private static void displaySimilarities(HashMap<String, FilePair> matchesMap) {
        ArrayList<FilePair> sortedPairs = new ArrayList<>(matchesMap.values());
        Collections.sort(sortedPairs);
        Collections.reverse(sortedPairs);
        for (FilePair f : sortedPairs){
            if (f.numMatches >= minMatches) {
                System.out.println(f.numMatches + ": " + f.file1 + ", " + f.file2);
            }
        }
    }

    /**
     * This class hold the strings of two files that are a pair because they share at least 1 similar phrase
     */
    private static class FilePair implements Comparable<FilePair>{
        String file1;
        String file2;
        int numMatches;

        @Override
        public int compareTo(FilePair thatPair){
            return Integer.compare(this.numMatches, thatPair.numMatches);
        }
    }

}
