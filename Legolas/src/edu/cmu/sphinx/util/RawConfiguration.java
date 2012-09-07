/*
 * Copyright 1999-2002 Carnegie Mellon University.  
 * Portions Copyright 2002 Sun Microsystems, Inc.  
 * Portions Copyright 2002 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 * 
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL 
 * WARRANTIES.
 *
 */

package edu.cmu.sphinx.util;

import java.util.LinkedList;
import java.util.List;

import edu.cmu.sphinx.decoder.Decoder;
import edu.cmu.sphinx.decoder.pruner.Pruner;
import edu.cmu.sphinx.decoder.scorer.AcousticScorer;
import edu.cmu.sphinx.decoder.search.ActiveListFactory;
import edu.cmu.sphinx.decoder.search.PartitionActiveListFactory;
import edu.cmu.sphinx.decoder.search.SearchManager;
import edu.cmu.sphinx.decoder.search.SimpleBreadthFirstSearchManager;
import edu.cmu.sphinx.instrumentation.BestPathAccuracyTracker;
import edu.cmu.sphinx.instrumentation.Monitor;
import edu.cmu.sphinx.linguist.Linguist;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.util.LogMath;

/**
 * Raw configuration of Sphinx4 decoder for optimal performance
 * 
 * @author Apurv Tiwari
 *
 */
public class RawConfiguration {
	
	private Recognizer recognizer;
	private LogMath logMath;
	private double relativeWordBeamWidth;
	private Decoder decoder;
	private double relativeBeamWidth;
	private int absoluteBeamWidth;
	
	public RawConfiguration() {
		logMath = new LogMath(1.0001f, false);
		relativeWordBeamWidth = 0.0;
		relativeBeamWidth = 0.0001;
		absoluteBeamWidth = 200;
		configure();
	}
	
	private void configure() {
		decoder = getDecoder();
		List<Monitor> monitors = getMonitors();
		recognizer = new Recognizer(decoder, monitors);
	}

	private List<Monitor> getMonitors() {
		List<Monitor> monitors = new LinkedList<Monitor>();
		
		// Add accuracy tracker for now 
		BestPathAccuracyTracker accuracytracker = new BestPathAccuracyTracker(recognizer, false, false, false, false, false, false);
		monitors.add(accuracytracker);
		return monitors;
	}

	private Decoder getDecoder() {
		SearchManager searchManager = getSearchManager();
		int featureBlockize = 100000;
		Decoder decoder = new Decoder(searchManager, false, false, null, featureBlockize);
		return decoder;
	}

	private SearchManager getSearchManager() {
		
		Linguist linguist = getLinguist();
		Pruner pruner = getPruner();
		AcousticScorer scorer = getScorer();
		ActiveListFactory activeListFactory = getActiveListFactory();
		
		SimpleBreadthFirstSearchManager searchManager = new SimpleBreadthFirstSearchManager(logMath, linguist, pruner, scorer, activeListFactory, false, relativeWordBeamWidth, 0, false);
		return searchManager;
	}

	private ActiveListFactory getActiveListFactory() {
		ActiveListFactory activeList = new PartitionActiveListFactory(absoluteBeamWidth, relativeBeamWidth, logMath);
		return activeList;
	}

	private AcousticScorer getScorer() {
		// TODO Auto-generated method stub
		return null;
	}

	private Pruner getPruner() {
		// TODO Auto-generated method stub
		return null;
	}

	private Linguist getLinguist() {
		// TODO Auto-generated method stub
		return null;
	}

}

