package algorithm;

import java.util.List;

public class Individual implements Comparable<Individual> {
	int[] path;
	double len;
	double fitness;
	
	@Override
	public int compareTo(Individual p) {
		return (p.fitness == this.fitness) ? 0 : (p.fitness > this.fitness) ? 1 : -1;
	}
	
	public Individual(int[] path, double len, double fitness) {
		this.path = path;
		this.len = len;
		this.fitness = fitness;
	}
	
	public void setPath(int[] path) {
		this.path = path;
	}
	
	public void setLen(double len) {
		this.len = len;
	}
	
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	
    /*public Object clone() {   
        Individual o = null;   
        try {   
            o = (Individual) super.clone();   
        } catch (CloneNotSupportedException e) {   
            e.printStackTrace();   
        }   
        return o;   
    }  */
    public Individual clone() {   
        Individual o = null;  
        int[] p = this.path.clone();
        o = new Individual(p, len, fitness);
        return o;   
    }


}
