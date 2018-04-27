import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

public class Main {
static Scanner kb;	// scanner connected to keyboard input, or input file
    public static void main(String[] args) {
        kb = new Scanner(System.in); // use keyboard and console
        ArrayList<String> input;                         //array list that holds the input command
        input = parse(kb);
        while(input.size() != 0) {//keep running if "quit" command not entered
            if(input.size() == 1){} //if exception was encountered then keep running, not a "quit"
            else{
                String rightFilePath = input.get(input.size()-2);
                if(!input.get(input.size()-2).endsWith("/")){   //account for if file path doesn't end with a "/"
                    input.remove(input.size()-2);
                    rightFilePath += "/";
                    input.add(input.size()-1, rightFilePath);
                }
                int phraseLen = Integer.valueOf(input.get(input.size()-1));
                for(int i = 0; i<input.size()-3; i++) {//goes until the second to last file
                    HashMap<String, ArrayList<ArrayList<String>>> hm = new HashMap<>();
                    ArrayList<ArrayList<String>> phrases = new ArrayList<>();
                    File thisFile = new File(rightFilePath + input.get(i));   //the actual test doc to compare against all others
                    String line1;
//                    System.out.println(input.get(i)); //*********DEBUG STATEMENT making sure all files are being accessed*********
                    try{
                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(thisFile), "UTF-8"));
                        Scanner s = new Scanner(br);
                        while (s.hasNext()) {
                            int counter = 0;
                            ArrayList<String> stringEntry = new ArrayList<>();
                            while((counter < phraseLen) && (s.hasNext())){
                                stringEntry.add(s.next().replaceAll("[^\\w\\s\\ ]", "").toLowerCase());
                                if(counter == 0){
                                    //need to figure out how to go get the phrases while only moving one word (phrases should be 1-5, 2-6, 3-7, etc)
                                    //not 1-5, 6-10, etc
                                }
                                counter++;
                            }
                            phrases.add(stringEntry);
                            hm.put(input.get(i),phrases);
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
                    for (int j = i + 1; j < input.size() - 2; j++) {//goes until the last file
                        File otherFile = new File(rightFilePath + input.get(j));    //file to be compared against
                        int k = 0; int w = 0;
                        String words1[]; String words2[];
                        String line2;
                        ArrayList<ArrayList<String>> commonPhrases = new ArrayList<>();
//                        try {
//                            BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(otherFile), "UTF-8"));
//                            line2 = br2.readLine();
//
//    //                        while((line1 != null)){ //while you haven't reached the end of first file
//    //                            words1 = line1.split("\\s");    //get the words for each line in file 1
//    //                            words2 = line2.split("\\s");    //get the words for each line in file 2
//    //                            if(words1[k].equals(words2[w])){    //encountered the same word
//    //                                phrases.add(words1[k]);
//    //                                counter++;
//    //                                k++;
//    //                                w++;
//    //                            }else{  //end of phrase match, add to list of common phrases between this file and other file
//    //                                if(counter >= phraseLen){   //makes sure added phrases are of at least the required phrase length
//    //                                    commonPhrases.add(phrases);
//    //                                }
//    //                                phrases.clear();
//    //                                counter = 0; w++;
//    //                            }
//    ////                            if (hm.containsKey(words1[0])){
//    ////                                System.out.println("Found duplicate ... handle logic");
//    ////                            }
//    ////                            hm.put(words1[0],words1[1]); //if index==0 is ur key
//    //                            line1 = br.readLine();
//    //                            line2 = br.readLine();
//    //                        }
//
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
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
        String filePath = command[0];
        try{
            int second = Integer.valueOf(command[1]);
            File folder = new File(filePath);
            String[] srcFiles = folder.list();  //get the list of input files
            for(int i = 0; i<srcFiles.length; i++){
                input.add(srcFiles[i]); //get the list of input files
            }
            input.add(command[0]);  //add the filepath in input so you can access the other files
            input.add(command[1]);  //add the phrase length in input so you can access it later
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
