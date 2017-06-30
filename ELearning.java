/*
 * This simple Java program illustrates the integration of a simple
 * HUGIN Fraud Detection Model. The objective is to compute the
 * posterior probability of fraud for a specific insurance
 * claim. Information on the claim is entered to the model and the
 * posterior belief of fraud is computed and display on standard
 * output.
 *
 * The application is compiled with this command:
 * javac -cp hapi72.jar;. FraudIntegration.java
 * where hapi72.jar is the HUGIN Java API
 *
 * The application is executed with this command (the dll/so file
 * associated with the HUGIN Java API should be placed in the current
 * directory): 
 * java -cp hapi72.jar;. FraudIntegration fraud.net where
 * fraud.net is the HUGIN network defining the fraud model.
 * 
 * The output on standard output should be:
 * P(Result=unusual | evidence) = 76,05%
 * 
 *
 * Author: Anders L Madsen @ HUGIN EXPERT A/S
 * 
 * For any questions or comments, please contact the author at
 * alm@hugin.com
 */
import COM.hugin.HAPI.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

class ELearning {

	//test mode
	public static boolean testmode=false;

	// the model
	Domain dom = null;
	int time;
	Boolean exerciseResults[];

	// the nodes / variables in the model

	// Labeled(none)...

	// NumberedDCNode(time)  ...; 
	// ...

	// BooleanDCNode  ...; 
	ArrayList<BooleanDCNode> Exercises=new ArrayList<BooleanDCNode>();
	ArrayList<BooleanDCNode> chapters=new ArrayList<BooleanDCNode>();

	BooleanDCNode Ifs,Cycles,Turtle,Strings,Lists,Dictionaries,Files;
	ArrayList<BooleanDCNode> Results=new ArrayList<>();

	// ...

	// ContinuousChanceNode ...; // none in model
	public ELearning(String name,int time,Boolean[] exerciseResults)
	{
		try {

			// load model from file. Done once.
			dom = new Domain (name, new DefaultClassParseListener ());
			this.time=time;
			this.exerciseResults=exerciseResults;

			// Get handles for the nodes of the model. One list of
			// nodes for each node type. Done once.

			// ContinuousChanceNodes ... none in the example

			// LabelledDCNodes

			// ...

			// NumberedDCNodes
			// ...


			// BooleanDCNodes
			int i=1;
			BooleanDCNode exercise;

			exercise=(BooleanDCNode)dom.getNodeByName("Ex"+i);
			while(exercise!=null){
				Exercises.add(exercise);
				i++;
				exercise=(BooleanDCNode)dom.getNodeByName("Ex"+i);
			}


			Ifs=(BooleanDCNode)dom.getNodeByName("Ifs");
			chapters.add(Ifs);

			Cycles=(BooleanDCNode)dom.getNodeByName("Cycles");
			chapters.add(Cycles);

			Turtle=(BooleanDCNode)dom.getNodeByName("Turtle");
			chapters.add(Turtle);

			Strings=(BooleanDCNode)dom.getNodeByName("Strings");
			chapters.add(Strings);

			Lists=(BooleanDCNode)dom.getNodeByName("Lists");
			chapters.add(Lists);

			Dictionaries=(BooleanDCNode)dom.getNodeByName("Dictionaries");
			chapters.add(Dictionaries);

			Files=(BooleanDCNode)dom.getNodeByName("Files");
			chapters.add(Files);


			BooleanDCNode result;
			i=0;

			result=(BooleanDCNode)dom.getNodeByName("Result"+i);
			while(result!=null){
				Results.add(result);
				i++;
				result=(BooleanDCNode)dom.getNodeByName("Result"+i);
			}



			// ...

			// IntervalDCNodes

			// ...

		} catch (Exception e) {
			e.printStackTrace ();
			System.err.println (e.getMessage ());	    
		}
	}

	// Main procedure. This is where the actual calculations are performed. 
	protected String doCalculations () {
		String res="";

		DecimalFormat d = new DecimalFormat ();
		d.setMaximumFractionDigits (2);

		try{ 
			// compile the model for inference / analysis. Done once.
			dom.compile ();

			// link nodes (indicators) of model to data sources, use
			// node name as unique identifier. In the example data is
			// hard coded into the source code. The link between the
			// data source and HUGIN is made once.

			// in principle we should now have a loop over all cases,
			// but we only consider a single case
			// 1) insert information from case

			insertEvidence ();

			// 2) propagate evidence
			dom.propagate(Domain.H_EQUILIBRIUM_SUM, Domain.H_EVIDENCE_MODE_NORMAL);
			double chance=0.0;
			

			//if testmode is on, display results better suited for checking test results
			
			if(!testmode)
				res+="confianca para cada capitulo:\n";

			for(BooleanDCNode chapter:chapters){
				chance=chapter.getBelief(1)*100.0;

				if(testmode)
					res+=chance+" ";
				else
					res+=chapter.getName()+":"+chance+"%\n";
			}
			
			chance=Results.get(time).getBelief(1)*100.0;
			if(chance<50.0){
				if(testmode)
					res+=chance+" ";
				else
					res+="aluno não está apto(confiança="+chance+"%)!\n";
			}
			else{
				if(testmode)
					res+=chance+" ";
				else
					res+="aluno está apto(confiança="+chance+"%)!\n";
			}

			// 4) remove observations to prepare for next case
			dom.initialize ();

			// do steps 1-4 for all cases or each time the probability
			// of fraud is to be updated (evidence can be entered
			// incrementally). A propagation is required prior to
			// accessing the posterior belief of Result
		} catch (Exception e) {
			e.printStackTrace ();
			System.err.println (e.getMessage ());	    
		}
		return res;
	}

	// It is very important that the data values entered matches the
	// state labels/values. Otherwise they are ignored. State labels
	// are case sensitive. Some values may need to be computed if not
	// presented in the data source (e.g., age)
	//
	// 
	protected void insertEvidence () {
		try{

			for(int i=0;i<exerciseResults.length;i++){
				if(i>=Exercises.size())
					break;
				enterObservation (Exercises.get(i), exerciseResults[i].toString());
			}

			//... more data
		} catch (Exception e) {
			e.printStackTrace ();
			System.err.println (e.getMessage ());	    
		}
	}

	// There is one enterObservation method for each node type
	protected void enterObservation (ContinuousChanceNode n, double value) {
		try{ 
			n.enterValue (value);
		} catch (Exception e) {
			e.printStackTrace ();
			System.err.println (e.getMessage ());	    
		}

	}

	// Notice that select state is case sensitive. getStateIndex returns -1 for unmathed values. 
	protected void enterObservation (LabelledDCNode n, String label) {
		try{ 
			n.selectState (n.getStateIndex (label));
		} catch (Exception e) {
			e.printStackTrace ();
			System.err.println (e.getMessage ());	    
		}
	}

	protected void enterObservation (BooleanDCNode n, String label) {
		try{ 
			n.selectState (n.getStateIndex (label));
		} catch (Exception e) {
			e.printStackTrace ();
			System.err.println (e.getMessage ());	    
		}
	}

	protected void enterObservation (NumberedDCNode n, double value) {
		try{ 
			n.selectState (n.getStateIndex (value));
		} catch (Exception e) {
			e.printStackTrace ();
			System.err.println (e.getMessage ());	    
		}
	}

	protected void enterObservation (IntervalDCNode n, double value) {
		try{ 
			n.selectState (n.getStateIndex (value));
		} catch (Exception e) {
			e.printStackTrace ();
			System.err.println (e.getMessage ());	    
		}
	}

	static public void main (String args[])
	{
		int time=0;
		if (args.length < 8) {
			System.out.println ("usage: <net><time><exercise results(boolean)>");
			System.exit (-1);
		}

		try{
			time=Integer.parseInt(args[0]);
		}
		catch(NumberFormatException e){
			System.out.println ("time must be int");
			System.exit (-1);
		}

		Boolean[] exerciseResults=new Boolean[args.length-1];

		for(int i=1;i<args.length;i++){
			if(args[i].equals("true"))
				exerciseResults[i-1]=true;
			else if(args[i].equals("false"))
				exerciseResults[i-1]=false;
		}

		//check if test mode is on
		if(args.length>8){

			if(args[8].equals("-t")){
				ELearning.testmode=true;
			}
		}

		ELearning fi = new ELearning ("AINet.net",time,exerciseResults);
		System.out.println(fi.doCalculations ());

	}
}

