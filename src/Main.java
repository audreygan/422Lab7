import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
static Scanner kb;	// scanner connected to keyboard input, or input file
    public static void main(String[] args) {
        kb = new Scanner(System.in); // use keyboard and console
        ArrayList<String> input;                         //array list that holds the input command
        input = parse(kb);
        while(input.size() != 0) {
            input = parse(kb);  //run the program until
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
        }catch(NumberFormatException e){    //makes sure that the phrase length is an integer
            System.out.println("Invalid input: Phrase length parameter incorrect.");
        }
        try{
            File folder = new File(filePath);
            String[] srcFiles = folder.list();  //get the list of input files
            for(int i = 0; i<srcFiles.length; i++){
                System.out.println(srcFiles[i]);
            }
        }catch( NullPointerException e) {   //makes sure that the file path is valid
            System.out.println("Invalid input: Incorrect file path.");
        }
        input.add(command[0]);  //valid input so make sure input array isn't empty
        return input;
    }
}
