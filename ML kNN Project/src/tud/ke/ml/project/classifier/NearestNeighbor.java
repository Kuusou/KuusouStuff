package tud.ke.ml.project.classifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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
		return new String[]{"2908582", ""};
	}
	
	@Override
	protected void learnModel(List<List<Object>> data) {
		trainingsData = data;
	}

	protected Map<Object,Double> getUnweightedVotes(List<Pair<List<Object>, Double>> subset) {
		Map<Object,Double> unWeightedVotes=new HashMap<Object,Double>();
		
		return null;
	}

	protected Map<Object, Double> getWeightedVotes(List<Pair<List<Object>, Double>> subset) {
		return null;
	}

	protected Object getWinner(Map<Object, Double> votes) {
		Set<Object> keys=votes.keySet();
		Iterator<Object> keysIt=keys.iterator();
		Object winner=keysIt.next();
		while(keysIt.hasNext()){
			Object key=keysIt.next();
			if (votes.get(key) > votes.get(winner)) {
				winner=key;
			}
		}
		return winner;
	}

	protected Object vote(List<Pair<List<Object>, Double>> subset) {
		Map<Object,Double> unWeightedVotes=getUnweightedVotes(subset);
		
		return unWeightedVotes;
	}


	protected List<Pair<List<Object>, Double>> getNearest(List<Object> data) {
		Iterator<List<Object>> instances= trainingsData.iterator();
		List<Pair<List<Object>, Double>> distanceList = new ArrayList<Pair<List<Object>, Double>>();
		while (instances.hasNext()) {
			List<Object> instance=instances.next();
			double distance=determineManhattanDistance(data, instance);
			distanceList.add(new Pair(instance,distance));
		}
		return distanceList;
	}

	protected double determineManhattanDistance(List<Object> instance1,List<Object> instance2) {
		return 0;
	}

	protected double determineEuclideanDistance(List<Object> instance1,List<Object> instance2) {
		return 0;
	}

	protected double[][] normalizationScaling() {
		return null;
	}

}
