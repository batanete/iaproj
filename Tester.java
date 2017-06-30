import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Tester {
	private static ArrayList<String[]> inputs;
	private static ArrayList<String> outputs;

	//read inputs from allTests.txt
	private static void readInputs() throws IOException{
		inputs=new ArrayList<String[]>();

		BufferedReader br = new BufferedReader(new FileReader("inputs.txt"));
		try {

			String line = br.readLine();

			while (line != null) {
				inputs.add(line.split(" "));
				line = br.readLine();
			}

		} finally {
			br.close();
		}

	}

	//run tests and print output on each line to an output file
	private static void runTests() throws IOException{
		ELearning.testmode=true;

		outputs=new ArrayList<String>();
		for(String[] input:inputs){
			Boolean[] exerciseResults=new Boolean[7];
			int time=Integer.parseInt(input[0]);
			for(int i=0;i<7;i++){
				if(input[i+1].equals("true"))
					exerciseResults[i]=true;
				else if(input[i+1].equals("false"))
					exerciseResults[i]=false;
			}
			
			ELearning fi = new ELearning ("AINet.net",time,exerciseResults);
			outputs.add(fi.doCalculations ());
		}
	}
	
	private static void writeResults(){
		BufferedWriter writer = null;
        try {
            //create file
            File logFile = new File("outputs.txt");


            writer = new BufferedWriter(new FileWriter("outputs.txt"));
            
            for(String s:outputs)
            	writer.write(s+"\n");
            
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
	}


	public static void main(String[] args) throws IOException{
		readInputs();
		
		/*for(String[] input:inputs){
			for(String s:input)
				System.out.print(s+" ");
			System.out.println();
		}*/
		
		runTests();
		
		writeResults();

	}


}
