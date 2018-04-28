import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

public class Main {
    static Scanner kb;	// scanner connected to keyboard input, or input file
    public static int minMatches;
    public static void main(String[] args) {
        kb = new Scanner(System.in); // use keyboard and console
        ArrayList<String> input;                         //array list that holds the input command
        input = parse(kb);
        while(input.size() != 0) {//keep running if "quit" command not entered
            if(input.size() == 1){} //if exception was encountered then keep running, not a "quit"
            else{
                String rightFilePath = input.get(input.size()-3);
                if(!input.get(input.size()-3).endsWith("/")){   //account for if file path doesn't end with a "/"
                    input.remove(input.size()-3);
                    rightFilePath += "/";
                    input.add(input.size()-2, rightFilePath);
                }
                int phraseLen = Integer.valueOf(input.get(input.size()-2));
                minMatches = Integer.valueOf(input.get(input.size()-1));
                HashMap<String, ArrayList<ArrayList<String>>> hm = new HashMap<>();
                for(int i = 0; i<input.size()-3; i++) {//goes until the last file
                    ArrayList<ArrayList<String>> phrases = new ArrayList<>();
                    File thisFile = new File(rightFilePath + input.get(i));   //the actual test doc to compare against all others
//                    String line1;
//                    System.out.println(input.get(i)); //*********DEBUG STATEMENT making sure all files are being accessed*********
                    try{
                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(thisFile), "UTF-8"));
                        Scanner s = new Scanner(br);    //make a scanner that uses the buffered reader to read the file
                        while (s.hasNext()) {
                            int numWords = 0;
                            ArrayList<String> stringEntry = new ArrayList<>();
                            while((numWords < phraseLen) && (s.hasNext()) && (phrases.size() == 0)){
                                stringEntry.add(s.next().replaceAll("[^\\w\\s\\ ]", "").toLowerCase()); //get the first phrase in the doc
                                numWords++;
                            }
                            if((phrases.size()>0) && (s.hasNext())){
                                ArrayList<String> newPhrase = new ArrayList<>(phrases.get(phrases.size()-1));     //for all other phrases, get the last phrase
                                newPhrase.remove(0);                                                        //remove the first word
                                newPhrase.add(s.next().replaceAll("[^\\w\\s\\ ]", "").toLowerCase());//add the next word
                                phrases.add(newPhrase);                                                             //add the phrase to the 2d string array
                            }else {
                                phrases.add(stringEntry);   //add first phrase to the string array
                            }
                            hm.put(input.get(i), phrases);  //add string arrays to hash map
                        }
//                        line1 = br.readLine();
//                        while (line1 != null) {
//                            if (line1.equals("")) { //if the file has an empty line skip it
//                                line1 = br.readLine();
//                                if(line1 == null){
//                                    break;
//                                }
//                            }
//                            line1 = line1.replaceAll("[^\\w\\s\\ ]", "").toLowerCase();
////                            System.out.println(line1); //DEBUG STATEMENT to see what's in the file
//                            line1 = br.readLine();
//                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
}
