/* 
 *
 * Copyright (C) 2013 The PlayTractor Team (support@playtractor.com)
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, see <http://www.gnu.org/licenses/>.
 */


package name.w.yellowduck.activities.puzzle;

import org.cocos2d.types.CGPoint;

public class TangramOutlineAlgorithm extends Object {
	private final int kMaxPoints                  =100;
	private final int kMaxPoly                    =10;

	private final float kThresholdDist              =(float)(1e-2);
	private final float kThresholdAngle             =(3.14159265f/128);
	
	private float thresholdDist;
	
	private PolyPoints pointBuffer;
	private float xBuf[], yBuf[];
	private int nextPoint[]=new int[kMaxPoints];
	
	private PolyPoints dumBuffer;
	
	private int polyNbr;
	private Poly polyBuffer[]=new Poly[kMaxPoly];
	
	private PolyPoints delta;
	private float xDelta[], yDelta[];

	public TangramOutlineAlgorithm() {
		super();
        pointBuffer=new PolyPoints(0, kMaxPoints);
        xBuf=pointBuffer.getXpoints();
        yBuf=pointBuffer.getYpoints();
        
        dumBuffer=new PolyPoints(0, kMaxPoints);
        
        delta=new PolyPoints(1,1);
        xDelta=delta.getXpoints();
        yDelta=delta.getYpoints();
        
        polyNbr=0;
    }

	public java.util.ArrayList<TangramOutline> createOutline(java.util.ArrayList<TangramShape> tangramShapes) {
	    this.doInit(tangramShapes);
	
	  	this.merge();
	  	this.removeConsec();
	    
		boolean cont=false;
		do {
		    cont = false;
	        
		    this.addInterPoints();
		    
		    cont |= this.merge();
		    cont |= this.removeConsec();
	        
		    if (this.checkIncluded()) {
	            this.checkIncluded();
	            cont = true;
		    }
	        
		    this.removeAligned();
		    this.compact();
		} while (cont);
	    
		// crÈe la collection de polygones fl.
		java.util.ArrayList<TangramOutline> polygones = new java.util.ArrayList<TangramOutline>();
		for ( int i = 0; i < polyNbr; i++ ) { // croissant !
		    Poly p = polyBuffer[i];
		    TangramOutline cp =new TangramOutline(0, p.getPointNbr(), p.getType());
	        
		    int currentPnt = p.getFirstPoint();
		    for ( int j = p.getPointNbr(); j > 0; j--) {
	            cp.addPoint(xBuf[currentPnt],yBuf[currentPnt]);
	            currentPnt = nextPoint[currentPnt];
		    }
		    
		    polygones.add(cp);
		}
		return polygones;
	}
	
	
	/**
	 * Initialise les structures ‡ partir d'une CalcFigure.
	 */
	private void doInit(java.util.ArrayList<TangramShape> tangramShapes) {
	    pointBuffer.clear();
		polyNbr = 0;
	    
	    thresholdDist=0;
		// init des enchainement
		for ( int i = kMaxPoints - 1; i >= 0; i--) {
		    nextPoint[i] = i + 1;
		}
	    int seq[]={2,6,3,1,4,5,0};
	    for (int j = 0; j < 7; ++j) {
	        TangramShape one=tangramShapes.get(seq[j]);
	//    for (TangramShape *one in tangramShapes) {
	        if (thresholdDist <= 0)
	            thresholdDist = kThresholdDist * one.getShapeScale();
	        
	        PolyPoints cp=new PolyPoints(one.getNumOfVertices(), one.getNumOfVertices());
	        for (int i = 0; i < one.getNumOfVertices(); ++i) {
	            CGPoint pt=one.getVertix(i);
	            cp.getXpoints()[i]=pt.x;
	            cp.getYpoints()[i]=pt.y;
	        }
		    int first = pointBuffer.addPolyPoints(cp);
	        //  	    pointBuffer.addPoint(cp.xpoints[0], cp.ypoints[0]); // fermeture du polygone
	        
		    // fermeture dans l'enchainement
		    nextPoint[first + cp.getNpoints() - 1] = first;
	
		    polyBuffer[polyNbr++] = new Poly(first, cp.getNpoints());
		}
	}
	
	
	/**
	 * Ajoute des points intermediaires.
	 */
	private boolean addInterPoints() {
	    
		int i1, i2, k1, k2;
		int currentPnt1, currentPnt2;
		int nextPnt1, nextPnt2;
	    
		//float dx, dy;
		int newPnt;
	    
		boolean ret = false;
		boolean trouve = true;
		while ( trouve ){
		    trouve = false;
	        
		    for ( i1 = polyNbr - 1; i1 >= 0 && !trouve; i1-- ) {
	            
	            for ( i2 = polyNbr - 1; i2 >= 0 && !trouve; i2-- ) {
	                
	                if ( i1 != i2 || i1 == i2 ){
	                    
	                    currentPnt1 = polyBuffer[i1].getFirstPoint();
	                    
	                    for (k1 = polyBuffer[i1].getPointNbr(); k1 > 0  && !trouve; k1--) {
	                        
	                        nextPnt1 = nextPoint[currentPnt1];
	                        currentPnt2 = polyBuffer[i2].getFirstPoint();
	                        
	                        for (k2 = polyBuffer[i2].getPointNbr(); k2 > 0 && !trouve; k2--) {
	                            
	                            nextPnt2 = nextPoint[currentPnt2];
	                            
	                            if (pointBuffer.distSqr(currentPnt1,currentPnt2) > thresholdDist &&
	                                pointBuffer.distSqr(nextPnt1,currentPnt2) > thresholdDist &&
	                                pointBuffer.distSegSqr(currentPnt1,nextPnt1,currentPnt2,delta) < thresholdDist/4) {
	                                    
	                                    // insËre un point sur le point du segment le plus proche
	                                    newPnt = pointBuffer.addPoint(xBuf[currentPnt2] - xDelta[0],yBuf[currentPnt2] - yDelta[0]);
	                                    nextPoint[newPnt] = nextPoint[currentPnt1];
	                                    nextPoint[currentPnt1] = newPnt;
	                                    
	                                    polyBuffer[i1].setPointNbr(polyBuffer[i1].getPointNbr()+1);
	                                    polyBuffer[i1].setFirstPoint(currentPnt1);
	                                    trouve = ret = true;
	                                }
	                            
	                            currentPnt2 = nextPnt2;
	                        }
	                        
	                        currentPnt1 = nextPnt1;
	                    }
	                }
	            }
		    }
		}
	    
		return ret;
	}
	
	
	/**
	 * Compacte et rÈordonne pointBuffer dans dumBuffer puis swap les deux.
	 */
	private void compact() {
		
		dumBuffer.clear();
	    
		Poly currentPoly;
		int currentPoint;
	    
		// recopie les points dans l'ordre
		for ( int i = polyNbr - 1; i >= 0; i--) {
		    currentPoly = polyBuffer[i];
		    currentPoint = currentPoly.getFirstPoint();
		    currentPoly.setFirstPoint(dumBuffer.addPoint(xBuf[currentPoint],yBuf[currentPoint]));
		    for ( int j = currentPoly.getPointNbr() - 1; j > 0; j--) {  // 1 point de moins
	            currentPoint = nextPoint[currentPoint];
	            dumBuffer.addPoint(xBuf[currentPoint], yBuf[currentPoint]);
		    }
		}
	    
		// recrÈe les enchainements
		for ( int i = polyNbr - 1; i >= 0; i--) {
		    currentPoly = polyBuffer[i];
		    currentPoint = currentPoly.getFirstPoint();
		    for ( int j = currentPoly.getPointNbr() - 1; j > 0; j--) {
	            nextPoint[currentPoint] = currentPoint + 1;
	            currentPoint++;
		    }
		    nextPoint[currentPoint] = currentPoly.getFirstPoint();
		}
	    
		// swap les buffers
		PolyPoints cp = dumBuffer;
		dumBuffer = pointBuffer;
		pointBuffer = cp;
		xBuf = pointBuffer.getXpoints();
		yBuf = pointBuffer.getYpoints();
	}
	
	
	/**
	 * Parcours les polygones et supprime les points intermediaires
	 * correspondant ‡ des segments successifs alignÈs.
	 */
	private boolean removeAligned() {
	    
		int i,k;
	    
		int currentPnt, nextPnt, nextNextPnt;
		float currentDir, nextDir;
	    
		float dirDiff;
	    
		boolean ret = false;
		boolean trouve = true;
		while (trouve){
		    trouve = false;
		    
		    for (i = polyNbr - 1; i >= 0 && !trouve; i--) {
	            
	            currentPnt = polyBuffer[i].getFirstPoint();
	            nextPnt = nextPoint[currentPnt];
	            
	            currentDir = pointBuffer.angle(nextPnt,currentPnt);
	            
	            //  		currentDir = (int)((dumi + rotstepnbr / 2) / rotstepnbr);
	            
	            for (k = polyBuffer[i].getPointNbr(); k > 0 && !trouve; k--) {
	                
	                nextPnt = nextPoint[currentPnt];
	                nextNextPnt = nextPoint[nextPnt];
	                
	                nextDir = pointBuffer.angle(nextNextPnt,nextPnt);
	                
	                //  		    nextDir = (int)((dumi + rotstepnbr / 2) / rotstepnbr);
	                
	                dirDiff = nextDir - currentDir;
	                if ( ( dirDiff < kThresholdAngle && dirDiff > 0-kThresholdAngle ) ||
	                    dirDiff > 3.14f * 2 - kThresholdAngle ||
	                    dirDiff < -(3.14f * 2 - kThresholdAngle) ) {
	                    
	                    nextPoint[currentPnt] = nextNextPnt;
	                    polyBuffer[i].setPointNbr(polyBuffer[i].getPointNbr()-1);
	                    polyBuffer[i].setFirstPoint( currentPnt);
	                    trouve = ret = true;
	                }
	                
	                currentPnt = nextPnt;
	                currentDir = nextDir;
	            }
		    }
		}
		
		return ret;
	}
	
	
	/**
	 * Supprime les segments consÈcutifs superposÈs.
	 */
	private boolean removeConsec() {
	    
		int i,k;
		int currentPnt, nextPnt, nextNextPnt;
		
		boolean ret = false;
		boolean trouve = true;
	    
		while (trouve){
	        
		    trouve = false;
	        
		    for (i = polyNbr - 1; i >= 0 && !trouve; i--) {
	            
	            currentPnt = polyBuffer[i].getFirstPoint();
	            
	            for (k = polyBuffer[i].getPointNbr(); k > 0 && !trouve; k--) {
	                
	                nextPnt = nextPoint[currentPnt];
	                nextNextPnt = nextPoint[nextPnt];
	                
	                if ( pointBuffer.distSqr(currentPnt,nextNextPnt) < thresholdDist) {
	                    
	                    nextPoint[currentPnt] = nextPoint[nextNextPnt];
	                    polyBuffer[i].setPointNbr(polyBuffer[i].getPointNbr()-2);
	                    polyBuffer[i].setFirstPoint(currentPnt);
	                    trouve = ret = true;
	                }
	                
	                currentPnt = nextPnt;
	            }
		    }
		}
		
		return ret;
	}
	
	
	/**
	 * ConcatËne les polys ayant 1 segment commun.
	 */
	private boolean merge() {
	    
		int i1, i2, k1, k2, m;
		int currentPnt1, currentPnt2;
		int nextPnt1, nextPnt2;
	    
		boolean ret = false;
		boolean trouve = true;
	    
		while ( trouve ){
	        
		    trouve = false;
	        
		    for ( i1 = polyNbr - 1; i1 >= 0 && !trouve; i1-- ) {
	            
	            for ( i2 = i1 + 1; i2 < polyNbr && !trouve; i2++ ) {
	                
	                currentPnt1 = polyBuffer[i1].getFirstPoint();
	                
	                for ( k1 = polyBuffer[i1].getPointNbr(); k1 > 0 && !trouve; k1-- ) {
	                    
	                    nextPnt1 = nextPoint[currentPnt1];
	                    
	                    currentPnt2 = polyBuffer[i2].getFirstPoint();
	                    
	                    for ( k2 = polyBuffer[i2].getPointNbr(); k2 > 0 && !trouve; k2-- ) {
	                        
	                        nextPnt2 = nextPoint[currentPnt2];
	                        float dist1=pointBuffer.distSqr(currentPnt1,nextPnt2);
	                        float dist2=pointBuffer.distSqr(nextPnt1,currentPnt2);
	                        if (dist1  < thresholdDist && dist2  < thresholdDist ) {
	                            
	                            nextPoint[currentPnt1] = nextPoint[nextPnt2];
	                            nextPoint[currentPnt2] = nextPoint[nextPnt1];
	                            polyBuffer[i1].setPointNbr(polyBuffer[i1].getPointNbr() + polyBuffer[i2].getPointNbr() - 2);
	                            polyBuffer[i1].setFirstPoint ( currentPnt1);
	                            
	                            for (m = i2; m < polyNbr - 1; m++) {
	                                polyBuffer[m] = polyBuffer[m + 1];
	                            }
	                            
	                            //  				System.arraycopy( polyBuffer, i2 + 1, polyBuffer, i2, polyNbr - i2 - 1 );
	                            
	                            polyNbr--;
	                            trouve = ret = true;
	                        }
	                        
	                        currentPnt2 = nextPnt2;
	                    }
	                    
	                    currentPnt1 = nextPnt1;
	                }
	            }
		    }
		}
	    
		return ret;
	}
	
	
	/**
	 * GËre les poly 'inclus'.
	 * En se basant sur des segment superposÈs mais non consÈcutifs..
	 * ProblËme potentiel : pourrait ne pas dÈtecter une inclusion
	 * car on n'ajoute pas de points pour les 'auto-corespondance'
	 * Remarque : commentaire de gTans, je ne sais plus ‡ quoi
	 * Áa correspond.
	 */
	private boolean checkIncluded() {
	    
		int i, k, l, m, n;
		int currentPnt1, currentPnt2;
		int nextPnt1, nextPnt2;
		int pntNbr;
	    
		Poly removedPoly;
		float xMinVal;
		int xMinIndex = 0;
		
		boolean trouve = false;
		boolean ret = false;
	    
		for (i = polyNbr - 1; i >= 0 && !trouve; i--){
	        
		    pntNbr = polyBuffer[i].getPointNbr();
		    
		    // pour Ítre sur de partir de l'exterieur 
		    currentPnt1 = polyBuffer[i].getFirstPoint();
		    xMinVal = xBuf[currentPnt1];
		    xMinIndex = currentPnt1;
		    for (m = pntNbr; m > 0; m--){
	            if ( xBuf[currentPnt1] < xMinVal ){
	                xMinVal = xBuf[currentPnt1];
	                xMinIndex = currentPnt1;
	            }
	            currentPnt1 = nextPoint[currentPnt1];
		    }
		    currentPnt1 = xMinIndex;
	        
		    for (k = 0; k < pntNbr - 2 && !trouve; k++) {
	            
	            nextPnt1 = nextPoint[currentPnt1];
	            currentPnt2 = nextPoint[nextPnt1];
	            
	            for (l = k + 2; l < pntNbr && !trouve; l++) {
	                
	                nextPnt2 = nextPoint[currentPnt2];
	                
	                if ( pointBuffer.distSqr(currentPnt1,nextPnt2) < thresholdDist &&
	                    pointBuffer.distSqr(nextPnt1,currentPnt2) < thresholdDist ) {
	                    
	                    // sÈpare l'enchainement en 2 (2 points sont dÈtachÈs)
	                    nextPoint[currentPnt1] = nextPoint[nextPnt2];
	                    nextPoint[currentPnt2] = nextPoint[nextPnt1];
	                    
	                    // enlËve le poly 
	                    removedPoly = polyBuffer[i];
	                    for (n = i; n < polyNbr - 1; n++) {
	                        polyBuffer[n] = polyBuffer[n + 1];
	                    }			
	                    polyNbr--;
	                    
	                    // cherche la premiËre place aprËs les poly 'pleins'
	                    for (m = 0; polyBuffer[m].getType() == Poly.kTangramOutlineBack && m < polyNbr; m++);
	                    
	                    //  			printf("inclusion trouvee\n"); 
	                    
	                    // dÈcale les poly suivants pour liberer 2 places
	                    for (n = polyNbr + 1; n > m + 1; n--) {
	                        polyBuffer[n] = polyBuffer[n - 2];
	                    }
	                    
	                    // init du 1er nouveau poly
	                    removedPoly.setPointNbr(removedPoly.getPointNbr() - (l - k + 1));
	                    removedPoly.setFirstPoint(currentPnt1);
	                    if (removedPoly.getType() != Poly.kTangramOutlineOn) {
	                        removedPoly.setType(Poly.kTangramOutlineBack);
	                    } else {
	                        removedPoly.setType(Poly. kTangramOutlineOn);
	                    }
	                    polyBuffer[m] = removedPoly;
	                    
	                    // init du deuxiËme
	                    polyBuffer[m + 1] = new Poly(currentPnt2, l - k - 1, Poly.kTangramOutlineOn);
	                    
	                    polyNbr += 2;
	                    
	                    trouve = ret = true;
	                    
	                }
	                
	                currentPnt2 = nextPnt2;
	            }
	            
	            currentPnt1 = nextPnt1;
		    }
		}
		
		return ret;
	}  
}
