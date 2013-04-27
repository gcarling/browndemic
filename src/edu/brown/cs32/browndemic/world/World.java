/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.brown.cs32.browndemic.world;

import edu.brown.cs32.browndemic.disease.Disease;
import edu.brown.cs32.browndemic.region.Region;
import edu.brown.cs32.browndemic.region.RegionTransmission;
import java.util.List;

/**
 *
 * @author Graham
 */
public interface World {
    
    /**
     * gives a certain disease a perk
     * @param dis The disease
     * @param perk The perk
     * @param buy Whether we're buying or selling
     */
    public void addPerk(int dis, int perk, boolean buy);

    /**
     * Get the population
     * @return integer population value
     */
    public long getPopulation();
    
    /**
     * getHealthy() gets the number of healthy people in this world
     * @return integer healthy people value
     */
    public long getHealthy();

    /**
     * getInfected() gets the number of infected people in this world (not dead people)
     * @return integer infected value
     */
    public long getInfected();

    /**
     * getDead() gets the number of dead people in this world
     * @return integer dead value
     */
    public long getDead();
    
    //get all regions
    public List<Region> getRegions();
    
    //get all diseases
    public List<Disease> getDiseases();
    
    //tells me which diseases are cured
    public List<Boolean> getCured();
    
    //gets the news
    public List<String> getNews();
    
    //get moving transmissions
    public List<RegionTransmission> getTransmissions();
    
    //get the winner(s)
    public List<Integer> getWinners();
    
    //tells if the game is over
    public boolean isGameOver();

    /**
     * Lets me know if all diseases have been cured
     * @return boolean
     */
    public boolean allCured();
    
    /**
     * Lets me know if all the people in the world are dead
     * @return boolean
     */
    public boolean allKilled();

    
}
