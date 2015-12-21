package tud.ke.ml.project.classifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import tud.ke.ml.project.framework.classifier.ANearestNeighbor;
import tud.ke.ml.project.util.Pair;

/**
 * This implementation assumes the class attribute is always available (but probably not set)
 * @author cwirth
 *
 */
public class NearestNeighbor extends ANearestNeighbor implements Serializable {

	private static final long serialVersionUID = 2010906213520172559L;
	
	protected double[] scaling;
	protected double[] translation;
	
	private List<List<Object>> trainingsData;
	
	protected String[] getMatrikelNumbers() {
		//@Philipp : please enter your matricule number here after mine
		return new String[]{"2908582", ""};
	}
	
	@Override
	protected void learnModel(List<List<Object>> data) {
		trainingsData = data;
	}

	protected Map<Object,Double> getUnweightedVotes(List<Pair<List<Object>, Double>> subset) {
		//Declaration of variables
		Map<Object,Double> unWeightedVotes=new HashMap<Object,Double>();
		int classAttributeIndex=getClassAttribute();
		String classAttributeName;
		
		//For each instance with its distance, we add 1 to the corresponding class
		for(Pair<List<Object>, Double> pair : subset) {
			//Because we know that the class attribute is always a string :
			classAttributeName=(String) pair.getA().get(classAttributeIndex);
			if (unWeightedVotes.containsKey(classAttributeName)) {
				unWeightedVotes.put(classAttributeName, unWeightedVotes.get(classAttributeName)+1);
			} else {
				unWeightedVotes.put(classAttributeName, (double) 1);
			}
		}
		
		return unWeightedVotes;
	}

	protected Map<Object, Double> getWeightedVotes(List<Pair<List<Object>, Double>> subset) {
		//Declaration of variables
		Map<Object,Double> weightedVotes=new HashMap<Object,Double>();
		int classAttributeIndex=getClassAttribute();
		double sumDist=0;
		String classAttributeName;
		
		//For each instance with its distance, we add the weight to the corresponding class
		for(Pair<List<Object>, Double> pair : subset) {
			//Because we know that the class attribute is always a string :
			classAttributeName=(String) pair.getA().get(classAttributeIndex);
			if (weightedVotes.containsKey(classAttributeName)) {
				weightedVotes.put(classAttributeName, weightedVotes.get(classAttributeName)+1/Math.pow(pair.getB(), 2));
			} else {
				weightedVotes.put(classAttributeName, (double)1/Math.pow(pair.getB(), 2));
			}
			sumDist +=1/Math.pow(pair.getB(), 2);
		}
		
		//For each class we divide the vote by the sum of all distances (normalization)
		for(Object obj : weightedVotes.keySet()) {
			weightedVotes.put(obj, weightedVotes.get(obj)/sumDist);
		}
		
		return weightedVotes;
	}

	protected Object getWinner(Map<Object, Double> votes) {
		//Declaration of variables
		Set<Object> keys=votes.keySet();
		Iterator<Object> keysIt=keys.iterator();
		Object winner=keysIt.next();
		Object key;
		
		while(keysIt.hasNext()){
			key=keysIt.next();
			if (votes.get(key) > votes.get(winner)) {
				winner=key;
			}
		}
		return winner;
	}

	protected Object vote(List<Pair<List<Object>, Double>> subset) {
		Map<Object,Double> votes;
		
		if (isInverseWeighting()) {
			votes=getWeightedVotes(subset);
		} else {
			votes=getUnweightedVotes(subset);
		}
		return getWinner(votes);
	}


	protected List<Pair<List<Object>, Double>> getNearest(List<Object> data) {
		//Declaration of variables
		// variable to create the list of instances with their distances relative to data
		List<Pair<List<Object>, Double>> distanceList = new ArrayList<Pair<List<Object>, Double>>();
		//variable to count the kNearest elements
		int kNearest=getkNearest();
		//the final reduced list
		List<Pair<List<Object>, Double>> finalList = new ArrayList<Pair<List<Object>, Double>>();
		
		double distance; // to save the current distance in a loop
		Pair<List<Object>, Double> pair; //to save the current pair in a loop
		int i=0; //to count
		
		//if metric==0, we use the Manhattan distance, elseif metric==1, we use the euclidean distance
		if (getMetric()==0) {
			// we get the list of all instances with their distances relative to data
			for (List<Object> instance : trainingsData) {
				distance=determineManhattanDistance(data, instance);
				pair =new Pair<List<Object>, Double>(instance,distance);
				distanceList.add(pair);
			}
		} else if (getMetric()==1) {
			// we get the list of all instances with their distances relative to data
			for (List<Object> instance : trainingsData) {
				distance=determineEuclideanDistance(data, instance);
				pair =new Pair<List<Object>, Double>(instance,distance);
				distanceList.add(pair);
			}
		}
		
		//we reduce the list to the kNearest elements
		  //we sort the list according to the distance
		Collections.sort(distanceList, new Comparator<Pair<List<Object>, Double>>() {
		    @Override
		    //we define the compare function for a Pair<List<Object>, Double>.
		    public int compare(final Pair<List<Object>, Double> o1, final Pair<List<Object>, Double> o2) {
		    	if (o2.getB()>o1.getB()) {
		    		return 1;
		    	} else {
		    		return -1;
		    	}
		    }
		});
		  //we take the k first elements of the sorted arraylist or all the elements if there are less than k elements in this list
		while (i<kNearest && i<distanceList.size()) {
			finalList.add(distanceList.get(i));
			i++;
			
		}

		return distanceList;
	}

	protected double determineManhattanDistance(List<Object> instance1,List<Object> instance2) {
		//Declaration of variables
		double distance =0;
		//we have to be careful of the classAttribute, because this is the class/attribute that we want to determine, we do not want to
		//calculate a distance for this attribute
		int classAttributeIndex=getClassAttribute();
		
		
		for (int i=0; i<instance1.size(); i++){
			if (i!=classAttributeIndex) {
				//if the object is of type String
				if (instance1.get(i) instanceof String) {
					if (instance1.get(i)!=instance2.get(i)) {
						distance+=1;
					}
					//else currObject is of type Double
				} else {
					distance+=Math.abs((double)instance1.get(i)-(double)instance2.get(i));
				}
			}
		}
		
		return distance;
	}

	protected double determineEuclideanDistance(List<Object> instance1,List<Object> instance2) {
		//Declaration of variables
		double distance =0;
		//we have to be careful of the classAttribute, because this is the class/attribute that we want to determine, we do not want to
		//calculate a distance for this attribute
		int classAttributeIndex=getClassAttribute();
		
		
		for (int i=0; i<instance1.size(); i++){
			//If the current index do not correspond to the class we want to determine, then we compute the distance
			if (i!=classAttributeIndex) {
				//if the object is of type String
				if (instance1.get(i) instanceof String) {
					if (instance1.get(i)!=instance2.get(i)) {
						distance+=1;
					}
					//else currObject is of type Double
				} else {
					distance+=Math.pow((double)instance1.get(i)-(double)instance2.get(i),2);
				}
			}
		}
		
		return Math.sqrt(distance);
	}

	protected double[][] normalizationScaling() {
		return null;
	}

}
