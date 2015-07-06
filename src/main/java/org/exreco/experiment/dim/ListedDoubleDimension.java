package org.exreco.experiment.dim;



public class ListedDoubleDimension extends ObjectDimension {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5169655671252360949L;

	public ListedDoubleDimension(String listOfIntegers) {
		super();
		listOfIntegers = listOfIntegers.trim();
		if (listOfIntegers != null && listOfIntegers.length() > 0) {
			String[] setsArray = listOfIntegers.split("[;, \t\n]+");
			for (int i = 0; i < setsArray.length; i++) {
				this.getPossibleValues().add(Double.parseDouble(setsArray[i]));
			}

		}
	}

}
