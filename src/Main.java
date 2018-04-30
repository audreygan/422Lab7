import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Main{
    private static Scanner kb;	// scanner connected to keyboard input, or input file
    private static int minMatches;

    private static class FilePair implements Comparable<FilePair>{
        String file1;
        String file2;
        int numMatches;
        @Override
        public int compareTo(FilePair thatPair){
            return ((Integer)this.numMatches).compareTo((Integer)thatPair.numMatches);
        }
    }

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
                HashMap<String, ArrayList<String>> phraseMap = new HashMap<>();   //key is the phrase(String) and the value is the array list of filenames that contain that phrase
//                HashMap<String, ArrayList<ArrayList<String>>> hm = new HashMap<>();
                for(int i = 0; i<input.size()-3; i++) {//goes until the last file
                    ArrayList<ArrayList<String>> phrases = new ArrayList<>();
                    File thisFile = new File(rightFilePath + input.get(i));    //the actual test doc to compare against all others
//                    System.out.println(input.get(i)); //*********DEBUG STATEMENT making sure all files are being accessed*********
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(thisFile), "UTF-8"));
                        Scanner s = new Scanner(br);                    //make a scanner that uses the buffered reader to read the file
                        while (s.hasNext()) {
                            int numWords = 0;
                            ArrayList<String> stringEntry = new ArrayList<>(); String firstPhrase = new String();
                            if((phrases.size() == 0)){
                                while((numWords < phraseLen) && (s.hasNext())){
                                    stringEntry.add(s.next().replaceAll("[^\\w\\s\\ ]", "").toLowerCase());     //get the first phrase in the doc
                                    numWords++;
                                }
                                phrases.add(stringEntry);   //add first phrase to the string array
                                for(int j = 0; j<stringEntry.size(); j++){  //turn the string entry array into a string
                                    firstPhrase += stringEntry.get(j);
                                }
                                if(phraseMap.containsKey(firstPhrase)){   //if the phrase already exists in the hashmap, append file to list
                                    phraseMap.get(firstPhrase).add(input.get(i));
                                }else{
                                    ArrayList<String> newFileList = new ArrayList<>();  //otherwise, add the new phrase and create a new file list for that phrase
                                    newFileList.add(input.get(i));
                                    phraseMap.put(firstPhrase,newFileList);
                                }
                            }else if((phrases.size()>0) && (s.hasNext())) {
                                String thisPhrase = new String();
                                ArrayList<String> newPhrase = new ArrayList<>(phrases.get(phrases.size() - 1));               //for all other phrases, get the last phrase
                                newPhrase.remove(0);                                                                   //remove the first word
                                newPhrase.add(s.next().replaceAll("[^\\w\\s\\ ]", "").toLowerCase());       //add the next word
                                phrases.add(newPhrase);
                                for(int j = 0; j<newPhrase.size(); j++){  //turn the string entry array into a string
                                    thisPhrase += newPhrase.get(j);
                                }
                                if(phraseMap.containsKey(thisPhrase)){   //if the phrase already exists in the hashmap, append file to list
                                    phraseMap.get(thisPhrase).add(input.get(i));
                                }else{
                                    ArrayList<String> newFileList = new ArrayList<>();  //otherwise, add the new phrase and create a new file list for that phrase
                                    newFileList.add(input.get(i));
                                    phraseMap.put(thisPhrase,newFileList);
                                }
                            }
//                            hm.put(input.get(i), phrases);  //add string arrays to hash map
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }   //finished creating hashmap with phrases and the list of files that the phrases appear in
                //make a hash map that finds how many matches there are between file pairs
                HashMap<String, FilePair> matchesMap = new HashMap<>();
                for(Map.Entry<String, ArrayList<String>> entry : phraseMap.entrySet()){ //go through the entries in the first hash map
                    for(int n=0; n< entry.getValue().size(); n++){  //go through the file name array lists for each phrase
                        for(int m=n+1; m<entry.getValue().size(); m++){
                            if(!entry.getValue().get(n).equals(entry.getValue().get(m))) {
                                String pairString = entry.getValue().get(n) + ", " + entry.getValue().get(m);
                                if (matchesMap.containsKey(pairString)) { //if the file pair string name is already a key, increment the number of matches
                                    matchesMap.get(pairString).numMatches += 1;
                                } else {                                          //otherwise it's a new pair so add this new file pair to the hash map
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
    private static ConcurrentHashMap<String, Integer> getSimilarities(HashMap<String, ArrayList<ArrayList<String>>> data) {
        ConcurrentHashMap<String, Integer> similarities = new ConcurrentHashMap<>();
        int set1Count = 0;
        for (Map.Entry<String, ArrayList<ArrayList<String>>> entry1 : data.entrySet()) {     // Traverse every file in the map
            int set2Count = 0;
            for (Map.Entry<String, ArrayList<ArrayList<String>>> entry2 : data.entrySet()) { // Compare against every other file
                set2Count++;
                if (set2Count > set1Count) continue;
                int count = 0;
                for (int i = 0; i < entry1.getValue().size(); i++) {                         // Traverse the number of phrases
                    for (int j = 0; j < entry2.getValue().size(); j++) {                     // Compare against the phrases in the other file
                        if (entry1.getValue().get(i).equals(entry2.getValue().get(j)))
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
     * @param matchesMap is the hashmap with the similarities between each pair of files
     */
    private static void displaySimilarities(HashMap<String, FilePair> matchesMap) {
        ArrayList<FilePair> sortedPairs = new ArrayList<FilePair>(matchesMap.values());
        Collections.sort(sortedPairs);
        Collections.reverse(sortedPairs);
        for(int k = 0; k<sortedPairs.size(); k++){
            if(sortedPairs.get(k).numMatches >= minMatches){
                System.out.println(sortedPairs.get(k).numMatches + ": " + sortedPairs.get(k).file1 + ", " + sortedPairs.get(k).file2);
            }
        }
    }

}
