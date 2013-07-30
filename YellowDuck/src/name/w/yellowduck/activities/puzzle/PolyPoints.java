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
//Tangram outline element
public class PolyPoints extends Object {
	private float xpoints[], ypoints[];
    private int npoints;
    
    
	public float[] getXpoints() {
		return xpoints;
	}
	public float[] getYpoints() {
		return ypoints;
	}
	public int getNpoints() {
		return npoints;
	}
    
	public PolyPoints(int nbr, int pmax) {
		super();
        npoints=nbr;
        if (pmax < nbr)
            pmax=nbr;
        xpoints=new float[pmax];
        ypoints=new float[pmax];
	}

	/**
	 * Concatène un CalcPolyPoints à la fin de celui-ci.
	 * Il doit rester suffisament de points disponibles.
	 *
	 * @return l'index du premier point ajouté.
	 */

	public int addPolyPoints(PolyPoints cp) {
	    int save=npoints;
	    
	    for (int i = 0; i < cp.getNpoints(); ++i) {
	        xpoints[npoints]=cp.getXpoints()[i];
	        ypoints[npoints]=cp.getYpoints()[i];
	        ++npoints;
	    }
	    
		return save;
	}

	/**
	 * Ajoute un points.
	 * il doit rester de la place.
	 *
	 * @return l'index du point ajouté.
	 */
	public int addPoint(float x, float y) {
		xpoints[npoints] = x;
		ypoints[npoints] = y;
		
		return npoints++;
	}

	public void clear() {
		npoints = 0;
	}


	/**
	 * Calcule la direction d'un segment.
	 * Compris entre -PI et PI.
	 */
	public float angle(int pnt1, int pnt2) {
		return (float)Math.atan2(xpoints[pnt1] - xpoints[pnt2], ypoints[pnt1] - ypoints[pnt2]);
	}


	/**
	 * Calcule le carré de la distance entre deux points.
	 *
	 * @param pnt1 position du premier point.
	 * @param pnt1 idem deuxième point.
	 */
	public float distSqr(int pnt1, int pnt2) {
	    
		float dx = xpoints[pnt1] - xpoints[pnt2];
		float dy = ypoints[pnt1] - ypoints[pnt2];
	    
		return dx * dx + dy * dy;
	}


	/**
	 * Calcule le carre de la distance et le deplacement entre un point et un segment.
	 *
	 * @param seg0 index du point de debut du segment.
	 * @param seg1 index du point de fi du segment.
	 * @param pnt  index du point.
	 * @param res  CalcPolyPoints contenant (au moins) un point dans lequel sera place
	 *             le deplacement du vecteur au point (normal au vecteur).
	 *             N'est pas modifié si la projection n'est pas sur le segment.
	 *             Peut être null.
	 *
	 * @return la distance entre le point et le segment ou 1E5 si la projection n'est pas
	 *         sur le segment.
	 */
	public float distSegSqr(int seg0, int seg1, int pnt, PolyPoints res) {
	    
		float scal, dum;
	    
		float segDx = xpoints[seg1] - xpoints[seg0];
		float segDy = ypoints[seg1] - ypoints[seg0];
		float resDx = xpoints[pnt] - xpoints[seg0];
		float resDy = ypoints[pnt] - ypoints[seg0];
	    
		float segLenSqr = segDx * segDx + segDy * segDy;
	    
		if ( (scal = (resDx * segDx) + (resDy * segDy)) < 0 ||
	        (dum = scal / segLenSqr) > 1 ) {
		    return (float)1e5;
		}
	    
		resDx -= segDx * dum;
		resDy -= segDy * dum;
		
		if ( res!=null) {
		    res.getXpoints()[0] = resDx;
		    res.getYpoints()[0] = resDy;
		}
	    
		return resDx * resDx + resDy * resDy;
	}
	
}
