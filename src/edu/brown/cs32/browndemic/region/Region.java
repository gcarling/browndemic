/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.brown.cs32.browndemic.region;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import edu.brown.cs32.browndemic.disease.Disease;

/**
 *
 * @author ckilfoyl
 */
public class Region {
    //ArrayList of all land neighboring Regions by String name

    private ArrayList<Integer> _landNeighbors;

    //ArrayList of all sea neighboring Regions by String name
    private ArrayList<Integer> _waterNeighbors;

    //Hashmap of all Regions by ID
    private HashMap<Integer, Region> _regions;

    //ArrayList of diseases in this Region
    private Disease[] _diseases;

    //number of diseases in game
    private int _numDiseases;

    //Custom HashMap to keep track of overlapping infected populations
    private PopHash _hash;

    //Total Region Population
    private long _population;

    //ArrayList of dead, order corresponds to the diseases in _disease
    private Long[] _dead;

    //ArrayList of boolean isCure, order corresponds to diseases in _disease
    private Boolean[] _hasCure;

    //ArrayList of double awaresness for each disease
    private Double[] _awareness;
    private double _CLOSEPORTS = 10;

    //ArrayList of double cure progress for each disease
    private Double[] _cureProgress;

    //Unique Region name
    //emphasis on the unique, some code in here runs on that assumption (hash, equals, etc.)
    private String _name;

    //Unique Int ID of this region
    private int _ID;

    //number of seaports and airports open in this Region
    private int _sea;
    private int _air;
    //wealth of this Region (reflects infrastructure, productivity, actual wealth, etc.)
    private double _wealth,  _wet,  _dry,  _heat,  _cold, _med;
    private ArrayList<RegionTransmission> _transmissions;
    private ArrayList<String> _news;

    /**
     * constructs a new Region with the given info
     * @param name The unique String name
     * @param population the initial population count
     * @param neighbors the names of all bordering Regions
     * @param seaports if this Region has open seaports
     * @param airports if this Region has open airports
     */
    public Region(int ID, String name, long population, List<Integer> landNeighbors,
            List<Integer> waterNeighbors, HashMap<Integer, Region> hash,
            int seaports, int airports, double wealth, double wet, double dry,
            double heat, double cold, double med) {
        _name = name;
        _ID = ID;
        _population = population;
        _landNeighbors = new ArrayList<Integer>(landNeighbors);
        _waterNeighbors = new ArrayList<Integer>(waterNeighbors);
        _regions = hash;
        _sea = seaports;
        _air = airports;
        _wealth = wealth;
        _wet = wet;
        _dry = dry;
        _heat = heat;
        _cold = cold;
        _med = med;
        _transmissions = new ArrayList<RegionTransmission>();
        _news = new ArrayList<String>();
    }

    /**
     * The update method for this region
     */
    public void update() {
        for (Disease d : _diseases) {
            if (null != d) {
                awarenessCheck();
                updateAwareness(d);
                updateWealth(d);
                cure(d);
                kill(d);
                infect(d);
                transmitSeaAndAir(d);
                transmitToLandNeighbors(d);
                transmitToWaterNeighbors(d);
            }
        }
    }

    /**
     * calculates the number of pop to be infected
     * @param d the index of the disease
     * @param pop the population to infect
     * @return how many to infect
     */
    public long getNumInfected(int d, long pop) {
        int number = 0;
        //TODO calculate number of pop infected.
        return number;
    }

    /**
     * infect(Disease) updates the infected for the given disease
     * @param disease the disease to update for
     **/
    public void infect(Disease disease) {
        int index = disease.getID();
        for(InfWrapper inf : _hash.getAllOfType(index,0)){
            long number = getNumInfected(index, inf.getInf());
            String infID = inf.getID().substring(0,index) + "1" + inf.getID().substring(index + 1);
            if (inf.getInf() < number){
                _hash.put(new InfWrapper(inf.getID(), 0L));
                _hash.put(new InfWrapper(infID, _hash.get(infID).getInf() + inf.getInf()));
            } else {
                _hash.put(new InfWrapper(inf.getID(), inf.getInf() - number));
                _hash.put(new InfWrapper(infID, _hash.get(infID).getInf() + number));
            }
        }
    }

    /**
     * infect(Disease, int) updates the dead for a given disease
     * @param disease the disease to update dead for
     **/
    public void kill(Disease disease) {
        int index = disease.getID();
        for (InfWrapper inf : _hash.getAllOfType(index,1)) {
            long number = (long) (disease.getLethality() * inf.getInf());
            if (inf.getInf() < number) {
                _dead[index] = _dead[index] + inf.getInf();
                _hash.put(new InfWrapper(inf.getID(), 0L));
            } else {
                _dead[index] =  _dead[index] + number;
                _hash.put(new InfWrapper(inf.getID(), inf.getInf() - number));
            }
        }
    }

    /**
     * calculates the number of people to cure in a given population
     * @param d
     * @param pop
     * @return
     */
    public int getNumCured(int d, long pop) {
        //TODO right now cured just cures 5% of total pop per tick
        int number = (int) (0.05 * _population);
        return number;
    }

    /**
     * cure(Disease) updates the number of cured for this disease
     * @param d the disease to update cured for
     */
    public void cure(Disease d) {
        int index = d.getID();
        if (_hasCure[index] == true) {
            ArrayList<InfWrapper> infected = _hash.getAllOfType(index,1);
            for (InfWrapper inf : infected) {
                String cureID = inf.getID().substring(0,index) + "2" + inf.getID().substring(index +1);
                long number = getNumCured(index, inf.getInf());
                if (inf.getInf() < number) {
                    _hash.put(new InfWrapper(cureID, _hash.get(cureID).getInf() + inf.getInf()));
                    _hash.put(new InfWrapper(inf.getID(), 0L));
                } else {
                    _hash.put(new InfWrapper(inf.getID(), inf.getInf() - number));
                    _hash.put(new InfWrapper(cureID, _hash.get(cureID).getInf() + number));
                }
            }
        }
    }

    /**
     * checks if ports should be closed
     */
    public void awarenessCheck() {
        for (double aware : _awareness) {
            if (aware > _CLOSEPORTS && !(_air == 0 && _sea == 0)) {
                _air = 0;
                _sea = 0;
                _news.add(_name + " has closed it's air and seaports.");
            }
        }
    }

    public void updateAwareness(Disease d) {
        int index = d.getID();
        //TODO awareness += vis*(infected + dead)
        _awareness[index] = _awareness[index] + d.getVisibility() * (getInfected().get(index) + _dead[index]);
    }

    public void updateWealth(Disease d) {
        int index = d.getID();
    //TODO update wealth calculation here
    }

    /**
     * setCure(int) sets the boolean cured for the disease at int to true
     * int d the index of the disease to cure
     */
    public void setCure(int d) {
        _hasCure[d] = true;
    }

    /**
     * introduces a disease to this region, initializes necessary lists
     * @param d hte disease to introduce
     */
    public void introduceDisease(Disease d) {
        int index = d.getID();
        String ID = "";
        for (int i = 0; i < _numDiseases; i++) {
            if (i == index) {
                ID += "1";
            } else {
                ID += "0";
            }
        }
        _hash.put(new InfWrapper(ID, 1L));
        _dead[index] = 0L;
        _hasCure[index] = false;
        _awareness[index] = 0.0;
        _cureProgress[index] = 0.0;
    }

    /**
     * Transmits the disease to all regions with open sea/airports if the transmission
     * conditions are met (probability)
     * @param d the disease to transmit
     */
    public void transmitSeaAndAir(Disease d) {
        for (Region region : _regions.values()) {
            if (region.hasDisease(d)) {
                continue;
            }
            int air = region.getAir();
            int sea = region.getAir();
            if (air > 0 && _air > 0) {
                boolean transmit = false;
                //TODO conditions for plane/sea transmit
                if (transmit) {
                    RegionTransmission rt = new RegionTransmission(_name, region.getName(), d.getID(), true);
                    _transmissions.add(rt);
                    region.introduceDisease(d);
                    continue;
                }
            }
            if (sea > 0 && _sea > 0) {
                boolean transmit = false;
                //TODO fill in conditions for ship transmission
                if (transmit) {
                    RegionTransmission rt = new RegionTransmission(_name, region.getName(), d.getID(), true);
                    _transmissions.add(rt);
                    region.introduceDisease(d);
                }
            }
        }
    }

    /**
     * transmits the disease to all Land Neighbors if the conditions are met
     * @param d the disease to transmit
     */
    public void transmitToLandNeighbors(Disease d) {
        for (Integer id : _landNeighbors) {
            Region region = _regions.get(id);
            if (region.hasDisease(d)) {
                continue;
            }
            boolean transmit = false;
            //TODO fill in conditions for land transmission
            if (transmit) {
                region.introduceDisease(d);
            }
        }
    }

    /**
     * transmits the disease to all Water Neighbors if the conditions are met
     * @param d the disease to transmit
     */
    public void transmitToWaterNeighbors(Disease d) {
        for (Integer id : _landNeighbors) {
            Region region = _regions.get(id);
            if (region.hasDisease(d)) {
                continue;
            }
            boolean transmit = false;
            //TODO fill in conditions for water transmission
            if (transmit) {
                region.introduceDisease(d);
            }
        }
    }

    /**
     * prompts a natural disaster with the given intensity in this region
     * @param intensity on a scale of 1-10
     */
    public void naturalDisaster(int intensity) {
        String news = "";
        //TODO generate disaster and impact wealth, maybe population?
        _news.add(news);
    }

    /**
     * gets the ArrayList of all air/sea transmissions
     * @return _transmissions
     */
    public ArrayList<RegionTransmission> getTransmissions() {
        return _transmissions;
    }

    /**
     * clears the transmissions list
     */
    public void clearTransmissions() {
        _transmissions.clear();
    }

    /**
     * gets the news
     * @return
     */
    public ArrayList<String> getNews() {
        return _news;
    }

    /**
     * clears the news
     */
    public void clearNews() {
        _news.clear();
    }

    /**
     * hasDisease(Disease) returns true if this region has been infected by this disease
     * @param d
     * @return
     */
    public boolean hasDisease(Disease d) {
        return _diseases[d.getID()] == null;
    }

    public void setNumDiseases(int num) {
        _numDiseases = num;
        _diseases = new Disease[num];
        _dead = new Long[num];
        _hasCure = new Boolean[num];
        _awareness = new Double[num];
        _cureProgress = new Double[num];
        _hash = new PopHash(num);
        _hash.addZero(_population);
    }

    /**
     * getAir() gets a the number of open airports in this Region
     * @return _air;
     */
    public int getAir() {
        return _air;
    }

    /**
     * getSea() gets the number of open seaports in this Region
     * @return _sea
     */
    public int getSea() {
        return _sea;
    }

    /**
     * getWealth() returns the wealth of this region
     * @return _wealth
     */
    public double getWealth() {
        return _wealth;
    }

    /**
     * getNeighbors() gets the ids of all bordering Regions
     * @return _neighbors
     */
    public ArrayList<Integer> getLandNeighbors() {
        return _landNeighbors;
    }

    /**
     * getNeighbors() gets the ids of all bordering Regions (by Water)
     * @return _neighbors
     */
    public ArrayList<Integer> getWaterNeighbors() {
        return _waterNeighbors;
    }

    /**
     * getInfected() gets the ArrayList of infected people in this Region
     * @return _infected
     */
    public ArrayList<Long> getInfected() {
        ArrayList<Long> infected = new ArrayList<Long>();
        for (int i = 0; i < _numDiseases; i++) {
            long num = 0L;
            if (_diseases[i] != null) {
                for (InfWrapper inf : _hash.getAllOfType(i,1)) {
                    num += inf.getInf();
                }
            }
            infected.add(i, num);
        }
        return infected;
    }

    /**
     * getTotalInfected() gets the total number of infected people in this Region
     * @return
     */
    public Long getTotalInfected() {
        long num = 0;
        for (int i = 0; i < _numDiseases; i++) {
            if (_diseases[i] != null) {
                for (InfWrapper inf : _hash.getAllOfType(i, 1)) {
                    num += inf.getInf();
                }
            }
        }
        return num;
    }

    /**
     * getKilled() gets the ArrayList of dead people in this Region
     * @return _dead;
     */
    public ArrayList<Long> getKilled() {
        ArrayList<Long> dead = new ArrayList<Long>();
        for(Long d : _dead)
            dead.add(d);
        return dead;
    }

    /**
     * getCured() gets the ArrayList of cured people in this Region
     * @return _cured;
     **/
    public ArrayList<Long> getCured() {
        ArrayList<Long> list = new ArrayList<Long>();
        for (int i = 0; i < _numDiseases; i++) {
            long num = 0L;
            if (_diseases[i] != null) {
                for (InfWrapper inf : _hash.getAllOfType(i, 2)) {
                    num += inf.getInf();
                }
            }
            list.add(i, num);
        }
        return list;
    }

    /**
     * getAlive() gets the number of healthy people in this Region
     * @return _healthy
     */
    public long getHealthy() {
        return _hash.getZero().getInf();
    }

    /**
     * getName() gets the unique String name for this Region
     * @return _name
     */
    public String getName() {
        return _name;
    }

    /**
     * getID() gets the unique int ID for this region
     * @return _ID
     */
    public int getID() {
        return _ID;
    }

    //accessor for getting population
    public long getPopulation() {
        return _population;
    }

    /**
     * toString() returns a String with the name and population counts for this Region
     * @return
     */
    @Override
    public String toString() {
        return _name + ", healthy: " + _hash.getZero().getInf() + ", infected: " + getTotalInfected() +
                ", dead: " + _dead;
    }

    /**
     * equals(Object o) returns true if o is a Region with the same name,
     * false otherwise
     * @param o the object to compare to
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this.getClass() != o.getClass()) {
            return false;
        } else {
            Region r = (Region) o;
            return _ID == r.getID();
        }
    }

    /**
     * hashCode() returns the hashCode for this Region's String name
     * @return _name.hashCode()
     */
    @Override
    public int hashCode() {
        return new Integer(_ID).hashCode();
    }
}
