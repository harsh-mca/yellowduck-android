/* $Id: d_mos4.model,v 25.95 2006/08/26 01:23:57 al Exp $ -*- C++ -*-
 * Copyright (C) 2001 Albert Davis
 * Author: Albert Davis <aldavis@ieee.org>
 *
 * This file is part of "Gnucap", the Gnu Circuit Analysis Package
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *------------------------------------------------------------------
 * MOS BSIM1 model.
 * derived from Spice3f4,Copyright 1990 Regents of the University of California
 * 1985 Hong J. Park, Thomas L. Quarles
 * Recoded for Gnucap model compiler, Al Davis, 2000
 */
/* This file is automatically generated. DO NOT EDIT */
#include "ap.h"
#include "d_mos4.h"
/*--------------------------------------------------------------------------*/
const double NA(NOT_INPUT);
const double INF(BIGBIG);
/*--------------------------------------------------------------------------*/
int MODEL_MOS4::_count = 0;
/*--------------------------------------------------------------------------*/
SDP_MOS4::SDP_MOS4(const COMMON_COMPONENT* cc)
  :SDP_MOS_BASE(cc)
{
  assert(cc);
  const COMMON_MOS* c = prechecked_cast<const COMMON_MOS*>(cc);
  assert(c);
  const MODEL_MOS4* m = prechecked_cast<const MODEL_MOS4*>(c->model());
  assert(m);
  const CARD_LIST* par_scope = m->scope();
  assert(par_scope);

      l_eff -= m->dl;
      w_eff -= m->dw;
      double L = l_eff/MICRON2METER;
      double W = w_eff/MICRON2METER;
      double CoxWoverL = 1e-4 * m->cox * w_eff / l_eff;
  // adjust: override
    this->cgate = m->cox * w_eff * l_eff;
  // adjust: raw
  phi = m->phi(L, W, 0., par_scope);
    //phi = std::max(phi, .1);
  vfb = m->vfb(L, W, 0., par_scope);
  k1 = m->k1(L, W, 0., par_scope);
  k2 = m->k2(L, W, 0., par_scope);
  eta = m->eta(L, W, 0., par_scope);
  etaB = m->etaB(L, W, 0., par_scope);
  etaD = m->etaD(L, W, 0., par_scope);
  mobZero = m->mobZero(L, W, 0., par_scope);
  mobZeroB = m->mobZeroB(L, W, 0., par_scope);
  mobVdd = m->mobVdd(L, W, 0., par_scope);
  mobVddB = m->mobVddB(L, W, 0., par_scope);
  mobVddD = m->mobVddD(L, W, 0., par_scope);
  ugs = m->ugs(L, W, 0., par_scope);
  ugsB = m->ugsB(L, W, 0., par_scope);
  uds = m->uds(L, W, 0., par_scope);
  udsB = m->udsB(L, W, 0., par_scope);
  udsD = m->udsD(L, W, 0., par_scope);
  n0 = m->n0(L, W, 0., par_scope);
  nB = m->nB(L, W, 0., par_scope);
  nD = m->nD(L, W, 0., par_scope);
  // adjust: calculated
    betaZero = mobZero  * CoxWoverL;
    betaZeroB = mobZeroB * CoxWoverL;
    betaVdd = mobVdd   * CoxWoverL;
    betaVddB = mobVddB  * CoxWoverL;
    betaVddD = mobVddD  * CoxWoverL;
    vt0 = vfb + phi + k1 * sqrt(phi) - k2 * phi;
  // code_post
}
/*--------------------------------------------------------------------------*/
TDP_MOS4::TDP_MOS4(const DEV_MOS* d)
  :TDP_MOS_BASE(d)
{
}
/*--------------------------------------------------------------------------*/
MODEL_MOS4::MODEL_MOS4()
  :MODEL_MOS_BASE(),
   phi(0.),
   vfb(0.),
   k1(0.),
   k2(0.),
   eta(0.),
   etaB(0.),
   etaD(0.),
   mobZero(0.),
   mobZeroB(0.),
   mobVdd(0.),
   mobVddB(0.),
   mobVddD(0.),
   ugs(0.),
   ugsB(0.),
   uds(0.),
   udsB(0.),
   udsD(0.),
   n0(0.),
   nB(0.),
   nD(0.),
   dl_u(0.),
   dw_u(0.),
   tox_u(0.),
   vdd(0.),
   wdf(0.),
   dell(0.),
   temp(300.15),
   xpart(0.),
   dl(NA),
   dw(NA),
   tox(NA),
   cox(NA)
{
  ++_count;
  mjsw = NA;
  pb = NA;
  pbsw = NA;
  cjo = 0.0;
  mos_level = LEVEL;
}
/*--------------------------------------------------------------------------*/
bool MODEL_MOS4::parse_front(CS& cmd)
{
  return MODEL_MOS_BASE::parse_front(cmd);
}
/*--------------------------------------------------------------------------*/
bool MODEL_MOS4::parse_params(CS& cmd)
{
  return ONE_OF
    || get(cmd, "DIODElevel", &mos_level)
    || get(cmd, "PHI", &phi)
    || get(cmd, "VFB", &vfb)
    || get(cmd, "K1", &k1)
    || get(cmd, "K2", &k2)
    || get(cmd, "ETA", &eta)
    || get(cmd, "X2E", &etaB)
    || get(cmd, "X3E", &etaD)
    || get(cmd, "MUZ", &mobZero)
    || get(cmd, "X2MZ", &mobZeroB)
    || get(cmd, "MUS", &mobVdd)
    || get(cmd, "X2MS", &mobVddB)
    || get(cmd, "X3MS", &mobVddD)
    || get(cmd, "U0", &ugs)
    || get(cmd, "X2U0", &ugsB)
    || get(cmd, "U1", &uds)
    || get(cmd, "X2U1", &udsB)
    || get(cmd, "X3U1", &udsD)
    || get(cmd, "N0", &n0)
    || get(cmd, "NB", &nB)
    || get(cmd, "ND", &nD)
    || get(cmd, "DL", &dl_u)
    || get(cmd, "DW", &dw_u)
    || get(cmd, "TOX", &tox_u)
    || get(cmd, "VDD", &vdd)
    || get(cmd, "WDF", &wdf)
    || get(cmd, "DELL", &dell)
    || get(cmd, "TEMP", &temp)
    || get(cmd, "XPART", &xpart)
    || MODEL_MOS_BASE::parse_params(cmd)
    ;
}
/*--------------------------------------------------------------------------*/
void MODEL_MOS4::elabo1()
{
  if (1 || !evaluated()) {
    const CARD_LIST* par_scope = scope();
    assert(par_scope);
    MODEL_MOS_BASE::elabo1();
    // final adjust: code_pre
    // final adjust: override
    if (mjsw == NA) {
      mjsw = .33;
    }
    if (pb == NA) {
      pb = 0.1;
    }
    //pb = std::max(pb, 0.1);
    if (pbsw == NA) {
      pbsw = pb;
    }
    //pbsw = std::max(pbsw, 0.1);
    cmodel = ((!cmodel)?1:cmodel);
    // final adjust: raw
    this->dl_u.e_val(0., par_scope);
    this->dw_u.e_val(0., par_scope);
    this->tox_u.e_val(0., par_scope);
    //this->tox_u = std::max(tox_u, 1e-20);
    this->vdd.e_val(0., par_scope);
    this->wdf.e_val(0., par_scope);
    this->dell.e_val(0., par_scope);
    this->temp.e_val(300.15, par_scope);
    this->xpart.e_val(0., par_scope);
    // final adjust: mid
    // final adjust: calculated
    dl = dl_u*MICRON2METER;
    dw = dw_u*MICRON2METER;
    tox = tox_u*MICRON2METER;
    cox = 3.453e-11 /*E_OX*/ / tox;
    // final adjust: post
    // final adjust: done
  }else{
    untested();
  }
}
/*--------------------------------------------------------------------------*/
SDP_CARD* MODEL_MOS4::new_sdp(const COMMON_COMPONENT* c)const
{
  assert(c);
  {if (dynamic_cast<const COMMON_MOS*>(c)) {
    return new SDP_MOS4(c);
  }else{
    return MODEL_MOS_BASE::new_sdp(c);
  }}
}
/*--------------------------------------------------------------------------*/
void MODEL_MOS4::print_front(OMSTREAM& o)const
{
  MODEL_MOS_BASE::print_front(o);
}
/*--------------------------------------------------------------------------*/
void MODEL_MOS4::print_params(OMSTREAM& o)const
{
  o << "level=4";
  MODEL_MOS_BASE::print_params(o);
  if (mos_level != LEVEL)
    o << "  diodelevel=" << mos_level;
  phi.print(o, "phi");
  vfb.print(o, "vfb");
  k1.print(o, "k1");
  k2.print(o, "k2");
  eta.print(o, "eta");
  etaB.print(o, "x2e");
  etaD.print(o, "x3e");
  mobZero.print(o, "muz");
  mobZeroB.print(o, "x2mz");
  mobVdd.print(o, "mus");
  mobVddB.print(o, "x2ms");
  mobVddD.print(o, "x3ms");
  ugs.print(o, "u0");
  ugsB.print(o, "x2u0");
  uds.print(o, "u1");
  udsB.print(o, "x2u1");
  udsD.print(o, "x3u1");
  n0.print(o, "n0");
  nB.print(o, "nb");
  nD.print(o, "nd");
  o << "  dl=" << dl_u;
  o << "  dw=" << dw_u;
  o << "  tox=" << tox_u;
  o << "  vdd=" << vdd;
  o << "  wdf=" << wdf;
  o << "  dell=" << dell;
  o << "  temp=" << temp;
  o << "  xpart=" << xpart;
}
/*--------------------------------------------------------------------------*/
void MODEL_MOS4::print_calculated(OMSTREAM& o)const
{
  MODEL_MOS_BASE::print_calculated(o);
}
/*--------------------------------------------------------------------------*/
bool MODEL_MOS4::is_valid(const COMMON_COMPONENT* cc)const
{
  return MODEL_MOS_BASE::is_valid(cc);
}
/*--------------------------------------------------------------------------*/
void MODEL_MOS4::tr_eval(COMPONENT* brh)const
{
  DEV_MOS* d = prechecked_cast<DEV_MOS*>(brh);
  assert(d);
  const COMMON_MOS* c = prechecked_cast<const COMMON_MOS*>(d->common());
  assert(c);
  const SDP_MOS4* s = prechecked_cast<const SDP_MOS4*>(c->sdp());
  assert(s);
  const MODEL_MOS4* m = this;

    trace3("", d->vds, d->vgs, d->vbs);
    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */ 
    d->reverse_if_needed();
    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
    trace4("", c->lo, m->dl, c->wo, m->dw);
    trace3("", s->ugs, s->ugsB, d->vbs);
    double Ugs = s->ugs + s->ugsB * d->vbs;
    double dUgsdVbs;
    if(Ugs <= 0) {
      untested();
      Ugs = 0;
      dUgsdVbs = 0.0;
    }else{
      dUgsdVbs = s->ugsB;
    }
    trace2("", Ugs, dUgsdVbs);
    
    double Uds = s->uds + s->udsB * d->vbs + s->udsD * (d->vds - m->vdd);
    double dUdsdVbs;
    double dUdsdVds;
    if(Uds <= 0) {    
      untested();
      Uds = 0.0;
      dUdsdVbs = dUdsdVds = 0.0;
    }else{
      double Leff = s->l_eff * 1e6; /* Leff in um */
      Uds  =  Uds / Leff;
      dUdsdVbs = s->udsB / Leff;
      dUdsdVds = s->udsD / Leff;
    }
    trace3("", Uds, dUdsdVbs, dUdsdVds);
    
    double Vpb;
    if(d->vbs <= 0) {
      Vpb = s->phi - d->vbs;
      d->sbfwd = false;
    }else{
      Vpb = s->phi;
      d->sbfwd = true;
    }
    double SqrtVpb = sqrt(Vpb);
    trace2("", Vpb, SqrtVpb);
    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
    /* threshold voltage */
    double dVthdVbs;
    double dVthdVds;
    {
      double Eta = s->eta + s->etaB * d->vbs + s->etaD * (d->vds - m->vdd);
      double dEtadVds;
      double dEtadVbs;
      if(Eta <= 0) {   
	Eta  = 0; 
	dEtadVds = dEtadVbs = 0.0 ;
      }else if (Eta > 1) {
	untested();
	Eta = 1;
	dEtadVds = dEtadVbs = 0;
      }else{ 
	untested();
	dEtadVds = s->etaD;
	dEtadVbs = s->etaB;
      }
      trace3("", Eta, dEtadVds, dEtadVbs);
      d->von = s->vfb + s->phi + s->k1 * SqrtVpb - s->k2 * Vpb - Eta * d->vds;
      dVthdVds = -Eta - dEtadVds * d->vds;
      dVthdVbs = s->k2 - 0.5 * s->k1 / SqrtVpb - dEtadVbs * d->vds;
      d->vgst  = d->vgs - d->von;
      trace4("", d->von, dVthdVds, dVthdVbs, d->vgst);
    }
    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
    double G = 1. - 1./(1.744+0.8364 * Vpb);
    double A = 1. + 0.5*G*s->k1/SqrtVpb;
    A = std::max(A, 1.0);   /* Modified */
    double Arg = std::max((1 + Ugs * d->vgst), 1.0);
    double dGdVbs = -0.8364 * (1-G) * (1-G);
    double dAdVbs = 0.25 * s->k1 / SqrtVpb *(2*dGdVbs + G/Vpb);
    trace3("", G, A, Arg);
    trace2("", dGdVbs, dAdVbs);
    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
    /* ids and derivatives calculation */
    if (d->vgst < 0) {
      d->cutoff = true;
      /* cutoff */
      d->ids = 0;
      d->gmf = 0;
      d->gds = 0;
      d->gmbf = 0;
      d->vdsat = 0;
      trace4("cutoff", d->ids, d->gmf, d->gds, d->gmbf);
    }else{
      d->cutoff = false;
      /* Quadratic Interpolation for Beta0 (Beta at d->vgs  =  0, vds=Vds) */
      double Beta0;
      double dBeta0dVds;
      double dBeta0dVbs;
      {
	trace2("", m->tox, m->cox*1e-4);
	trace3("", s->betaVdd, s->betaVddB, d->vbs);
	double BetaVdd = (s->betaVdd + s->betaVddB * d->vbs);
	double dBetaVdd_dVds = std::max(s->betaVddD, 0.0); /* Modified */
	trace2("", BetaVdd, dBetaVdd_dVds);
	if(d->vds > m->vdd) {
	  Beta0      = BetaVdd + dBetaVdd_dVds * (d->vds - m->vdd);
	  dBeta0dVds = dBetaVdd_dVds;
	  dBeta0dVbs = s->betaVddB;
	  trace3("vds>vdd", Beta0, dBeta0dVds, dBeta0dVbs);
	}else{
	  double Beta_Vds_0 = (s->betaZero + s->betaZeroB * d->vbs);
	  double VddSquare = m->vdd * m->vdd;
	  double C1 = (-BetaVdd + Beta_Vds_0+dBetaVdd_dVds*m->vdd) / VddSquare;
	  double C2 = 2 * (BetaVdd - Beta_Vds_0) / m->vdd - dBetaVdd_dVds;
	  trace4("", Beta_Vds_0, VddSquare, C1, C2);
	  double dBeta_Vds_0_dVbs = s->betaZeroB;
	  double dBetaVdd_dVbs = s->betaVddB;
	  double dC1dVbs = (dBeta_Vds_0_dVbs - dBetaVdd_dVbs) / VddSquare;
	  double dC2dVbs = dC1dVbs * (-2) * m->vdd;
	  trace4("", dBeta_Vds_0_dVbs, dBetaVdd_dVbs, dC1dVbs, dC2dVbs);
	  Beta0	   = (C1 * d->vds + C2) * d->vds + Beta_Vds_0;
	  dBeta0dVds = 2*C1*d->vds + C2;
	  dBeta0dVbs = dC1dVbs * d->vds * d->vds 
	    + dC2dVbs * d->vds + dBeta_Vds_0_dVbs;
	  trace3("vds<vdd", Beta0, dBeta0dVds, dBeta0dVbs);
	}
      }
      
      /*Beta  =  Beta0 / ( 1 + Ugs * d->vgst );*/
      double Beta	     = Beta0 / Arg ;
      double dBetadVgs = -Beta * Ugs / Arg;
      double dBetadVds = dBeta0dVds / Arg - dBetadVgs * dVthdVds ;
      double dBetadVbs = dBeta0dVbs / Arg 
	+ Beta * Ugs * dVthdVbs / Arg - Beta * d->vgst * dUgsdVbs / Arg;
      trace4("", Beta, dBetadVgs, dBetadVds, dBetadVbs);
      
      /*d->vdsat  = std::max(d->vgst / ( A + Uds * d->vgst ),  0.0);*/
      double Vc = Uds * d->vgst / A;
      if(Vc < 0.0) {
	untested();
	Vc=0.0;
      }
      
      double Term1 = sqrt(1 + 2 * Vc);
      double K = 0.5 * (1 + Vc + Term1);
      d->vdsat = std::max(d->vgst / (A * sqrt(K)) , 0.0);
      trace4("", Vc, Term1, K, d->vdsat);
      
      if(d->vds < d->vdsat) {		/* Triode Region */
	d->saturated = false;
	/*Argl1  =  1 + Uds * d->vds;*/
	double Argl1 = std::max((1 + Uds * d->vds), 1.);
	double Argl2 = d->vgst - 0.5 * A * d->vds;
	trace2("", Argl1, Argl2);
	d->ids = Beta * Argl2 * d->vds / Argl1;
	d->gmf   = (dBetadVgs * Argl2 * d->vds + Beta * d->vds) / Argl1;
	d->gds  = (dBetadVds * Argl2 * d->vds 
		   + Beta * (d->vgst - d->vds * dVthdVds - A * d->vds)
		   - d->ids * (d->vds * dUdsdVds + Uds)) /  Argl1;
	d->gmbf = (dBetadVbs * Argl2 * d->vds 
		  + Beta * d->vds * (-dVthdVbs - 0.5 * d->vds * dAdVbs)
		  - d->ids * d->vds * dUdsdVbs) / Argl1;
	trace4("triode", d->ids, d->gmf, d->gds, d->gmbf);
      }else{			/* Pinchoff (Saturation) Region */
	d->saturated = true;
	double Args1   =  1. + 1. / Term1;
	double dVcdVgs =  Uds / A;
	double dVcdVds =  d->vgst * dUdsdVds / A - dVcdVgs * dVthdVds;
	double dVcdVbs =  (d->vgst * dUdsdVbs 
			   - Uds * (dVthdVbs + d->vgst * dAdVbs / A )) / A;
	double dKdVc   =  0.5 * Args1;
	double dKdVgs  =  dKdVc * dVcdVgs;
	double dKdVds  =  dKdVc * dVcdVds;
	double dKdVbs  =  dKdVc * dVcdVbs;
	double Args2   =  d->vgst / A / K;
	double Args3   =  Args2 * d->vgst;
	trace3("", Args1, Args2, Args3);
	trace3("", dVcdVgs, dVcdVds, dVcdVbs);
	trace4("", dKdVc, dKdVgs, dKdVds, dKdVbs);
	d->ids =  0.5 * Beta * Args3;
	d->gmf = 0.5 * Args3 * dBetadVgs + Beta * Args2 - d->ids * dKdVgs / K;
	d->gds = 0.5*Args3*dBetadVds - Beta*Args2*dVthdVds - d->ids*dKdVds/K;
	d->gmbf = 0.5 * dBetadVbs * Args3 - Beta * Args2 *dVthdVbs
	  - d->ids * (dAdVbs / A + dKdVbs / K);
	trace4("sat", d->ids, d->gmf, d->gds, d->gmbf);
      }
    }
    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
    /* SubthresholdComputation */
    
    /* The following 'if' statement has been modified so that subthreshold  *
     * current computation is always executed unless N0 >= 200. This should *
     * get rid of the Ids kink seen on Ids-Vgs plots at low Vds.            *
     *                                                Peter M. Lee          *
     *                                                6/8/90                *
     *  Old 'if' statement:  (reversed)                                     *
     *  if( (N0 >=  200) || (d->vgst < Vcut ) || (d->vgst > (-0.5*Vcut)))   */
    
    //double Vcut  =  - 40. * s->n0 * t->vt ;
    if (s->n0 < 200) {
      double N = s->n0 + s->nB*d->vbs + s->nD*d->vds; /* subthreshold slope */
      trace4("", s->n0, s->nB, s->nD, N);
      if (N < 0.5) {
	untested();
	N = 0.5;
      }
      const double temp = 300.15;
      const double vt0 = temp * P_K_Q;
      const double Vtsquare = vt0 * vt0 ;
      const double nvt0 = N * vt0;
      double Warg1 = exp(-d->vds / vt0);
      double Wds   = 1 - Warg1;
      double Wgs   = exp( d->vgst / nvt0);
      double Warg2  = 6.04965 * Vtsquare * s->betaZero;
      double Ilimit = 4.5 * Vtsquare * s->betaZero;
      double Iexp   = Warg2 * Wgs * Wds;
      d->ids += Ilimit * Iexp / (Ilimit + Iexp);
      double Temp1  = Ilimit / (Ilimit + Iexp);
      Temp1  =  Temp1 * Temp1;
      double Temp3  = Ilimit / (Ilimit + Wgs * Warg2);
      Temp3 = Temp3 * Temp3 * Warg2 * Wgs;
      /*    if ( Temp3 > Ilimit ) Temp3=Ilimit;*/
      d->gmf  += Temp1 * Iexp / nvt0;
      /* gds term has been modified to prevent blow up at Vds=0 */
      d->gds += Temp3
	* (Wds / nvt0 * (dVthdVds + d->vgst * s->nD / N)
	   + Warg1 / vt0);
      d->gmbf -= Temp1 * Iexp * (dVthdVbs + d->vgst * s->nB / N) / nvt0;
      trace3("", vt0, Vtsquare, nvt0);
      trace4("", Warg1, Wds, Wgs, Warg2);
      trace4("", Ilimit, Iexp, Temp1, Temp3);
      trace4("sub", d->ids, d->gmf, d->gds, d->gmbf);
    }else{
      untested();
    }
    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
    /* Some Limiting of DC Parameters */
    if(d->ids < 0.0) d->ids = 0.0;
    if(d->gmf  < 0.0) d->gmf  = 0.0;
    if(d->gds < 0.0) d->gds = 0.0;
    if(d->gmbf < 0.0) d->gmbf = 0.0;
    trace4("final", d->ids, d->gmf, d->gds, d->gmbf);

    trace3("", G, A, s->phi);
    trace1("", m->xpart);
    
    double Vth0 = s->vfb + s->phi + s->k1 * SqrtVpb; // almost same as d->von
    double Vgs_Vth = d->vgs - Vth0; // almost same as d->vgst
    trace2("", Vth0, Vgs_Vth);
    double Arg1 = A * d->vds;
    double Arg2 = Vgs_Vth - 0.5 * Arg1;
    double Arg3 = d->vds - Arg1;
    trace3("", Arg1, Arg2, Arg3);
    /*double*/ dVthdVbs = -0.5 * s->k1 / SqrtVpb;
    /*dbl*/ dAdVbs = 0.5 * s->k1 * (0.5*G/Vpb - 0.8364*(1-G)*(1-G)) / SqrtVpb;
    trace2("", dVthdVbs, dAdVbs);
    double Ent = std::max(Arg2,1.0e-8);
    double dEntdVds = -0.5 * A;
    double dEntdVbs = -dVthdVbs - 0.5 * d->vds * dAdVbs;
    trace3("", Ent, dEntdVds, dEntdVbs);
    double VdsPinchoff = std::max(Vgs_Vth / A, 0.0);
    double Vgb  = d->vgs - d->vbs ;
    double Vgb_Vfb  = Vgb - s->vfb;
    trace3("", VdsPinchoff, Vgb, Vgb_Vfb);
    
    if(Vgb_Vfb < 0) {			/* Accumulation Region */
      untested();
      d->qgate = s->cgate * Vgb_Vfb;
      d->qbulk = -d->qgate;
      d->qdrn  = 0. ;
      d->cggb = s->cgate;
      d->cgdb = 0.;
      d->cgsb = 0.;
      d->cbgb = -s->cgate;
      d->cbdb = 0.;
      d->cbsb = 0.;
      d->cdgb = 0.;
      d->cddb = 0.;
      d->cdsb = 0.;
      trace4("", d->qgate, d->cggb, d->cgdb, d->cgsb);
      trace4("", d->qbulk, d->cbgb, d->cbdb, d->cbsb);
      trace4("acc", d->qdrn, d->cdgb, d->cddb, d->cdsb);
    }else if (d->vgs < Vth0) {		/* Subthreshold Region */
      d->qgate = 0.5*s->cgate*s->k1*s->k1*(-1+sqrt(1+4*Vgb_Vfb/(s->k1*s->k1)));
      d->cggb = s->cgate / sqrt(1 + 4 * Vgb_Vfb / (s->k1 * s->k1));
      d->cgdb = d->cgsb = 0.;
      d->qbulk = -d->qgate;
      d->cbgb = -d->cggb;
      d->cbdb = d->cbsb = 0.0;
      d->qdrn = 0.;
      d->cdgb = d->cddb = d->cdsb = 0.0;
      trace4("", d->qgate, d->cggb, d->cgdb, d->cgsb);
      trace4("", d->qbulk, d->cbgb, d->cbdb, d->cbsb);
      trace4("sub", d->qdrn, d->cdgb, d->cddb, d->cdsb);
    }else if (d->vds < VdsPinchoff) {	/* triode region */
      double EntSquare = Ent * Ent;
      trace1("tri", EntSquare);
      double Argl1 = 1.2e1 * EntSquare;
      double Argl2 = 1.0 - A;
      double Argl3 = Arg1 * d->vds;
      trace3("", Argl1, Argl2, Argl3);
      double Argl5;
      if (Ent > 1.0e-8) {
	Argl5 = Arg1 / Ent;
      }else{   
	untested();
	Argl5 = 2.0;
      }
      double Argl7 = Argl5 / 1.2e1;
      double Argl8 = 6.0 * Ent;
      trace3("", Argl5, Argl7, Argl8);
      
      d->qgate = s->cgate 
	* (d->vgs - s->vfb - s->phi - 0.5 * d->vds + d->vds * Argl7);
      d->cggb = s->cgate * (1.0 - Argl3 / Argl1);
      d->cgdb = s->cgate * (-0.5 + Arg1 / Argl8 - Argl3 * dEntdVds / Argl1);
      double cgbb = s->cgate * (d->vds*d->vds*dAdVbs*Ent-Argl3*dEntdVbs)/Argl1;
      d->cgsb = -(d->cggb + d->cgdb + cgbb);
      trace4("", d->qgate, d->cggb, d->cgdb, d->cgsb);
      
      d->qbulk = s->cgate * (-Vth0 + s->vfb + s->phi + 0.5*Arg3 - Arg3*Argl7);
      d->cbgb = s->cgate * Argl3 * Argl2 / Argl1;
      d->cbdb = s->cgate * Argl2 * (0.5 - Arg1/Argl8 + Argl3 * dEntdVds/Argl1);
      double cbbb = -s->cgate * (dVthdVbs + 0.5 * d->vds * dAdVbs
	       +d->vds*d->vds*((1.0-2.0*A)*dAdVbs*Ent-Argl2*A*dEntdVbs)/Argl1);
      d->cbsb = -(d->cbgb + d->cbdb + cbbb);
      trace4("", d->qbulk, d->cbgb, d->cbdb, d->cbsb);
      
      if (m->xpart >= 1) {
	/*0/100 partitioning for drain/source chArges at saturation region*/
	double Argl9 = 0.125 * Argl5 * Argl5; //t
	d->qdrn = -s->cgate * (0.5*Vgs_Vth - 0.75*Arg1 + 0.125*Arg1*Argl5);
	d->cdgb = -s->cgate * (0.5 - Argl9);
	d->cddb = s->cgate * (0.75*A - 0.25*A*Arg1/Ent + Argl9*dEntdVds);
	double cdbb = s->cgate * (0.5 * dVthdVbs + d->vds * dAdVbs * 
				  (0.75 - 0.25 * Argl5 ) + Argl9 * dEntdVbs);
	d->cdsb = -(d->cdgb + d->cddb + cdbb);
	trace2("", Argl9, cdbb);
	trace4("tri 0/100", d->qdrn, d->cdgb, d->cddb, d->cdsb);
      }else{
	/*40/60 partitioning for drain/source chArges at saturation region*/
	double Vgs_VthSquare = Vgs_Vth*Vgs_Vth;
	trace2("", Vgs_Vth, Vgs_VthSquare);
	double Arg5 = Arg1*Arg1;
	double Vcom = Vgs_Vth*Vgs_Vth/6.0-1.25e-1*Arg1*Vgs_Vth+2.5e-2*Arg5;
	double Argl4 = Vcom/Ent/EntSquare;
	double Argl6;
	if (Ent > 1.0e-8) {
	  Argl6 = Vcom / EntSquare;
	}else{   
	  untested();
	  Argl6 = 4.0 / 1.5e1;
	}
	d->qdrn = -s->cgate * (0.5 * (Vgs_Vth-Arg1) + Arg1 * Argl6);
	d->cdgb = -s->cgate
	  * (0.5 + Arg1*(4.0*Vgs_Vth-1.5*Arg1)/Argl1 - 2.0*Arg1*Argl4);
	d->cddb = s->cgate*(0.5*A+2.0*Arg1*dEntdVds*Argl4-A*(2.0*Vgs_VthSquare
					-3.0*Arg1*Vgs_Vth+0.9*Arg5)/Argl1);
	double cdbb =s->cgate*(0.5*dVthdVbs+0.5*d->vds*dAdVbs+2.0*Arg1*dEntdVbs
	     *Argl4-d->vds*(2.0*Vgs_VthSquare*dAdVbs-4.0*A*Vgs_Vth*dVthdVbs-3.0
	     *Arg1*Vgs_Vth*dAdVbs+1.5*A*Arg1*dVthdVbs+0.9*Arg5*dAdVbs)
			       /Argl1);
	d->cdsb = -(d->cdgb + d->cddb + cdbb);
	trace4("", Vcom, Argl4, Argl6, cdbb);
	trace4("lin 40/60", d->qdrn, d->cdgb, d->cddb, d->cdsb);
      }
    }else{				/* saturation region */
      assert(d->vds >= VdsPinchoff);
      double Args1 = 1.0 / (3.0 * A);
      trace2("sat", s->cgate, Args1);
      
      d->qgate = s->cgate * (d->vgs - s->vfb - s->phi - Vgs_Vth * Args1);
      d->cggb = s->cgate * (1.0 - Args1);
      d->cgdb = 0.0;
      double cgbb = s->cgate * Args1 * (dVthdVbs + Vgs_Vth * dAdVbs / A);
      d->cgsb = -(d->cggb + d->cgdb + cgbb);
      trace4("", d->qgate, d->cggb, d->cgdb, d->cgsb);
      
      d->qbulk = s->cgate * (s->vfb + s->phi - Vth0 + (1.0-A)*Vgs_Vth*Args1);
      d->cbgb = s->cgate * (Args1 - 1.0 / 3.0);
      d->cbdb = 0.0;
      double cbbb = -s->cgate * ((2.0 / 3.0 + Args1) * dVthdVbs
				 + Vgs_Vth * Args1 * dAdVbs / A);
      d->cbsb = -(d->cbgb + d->cbdb + cbbb);
      trace4("", d->qbulk, d->cbgb, d->cbdb, d->cbsb);
      
      if (m->xpart >= 1) {
	/*0/100 partitioning for drain/source chArges at saturation region*/
	d->qdrn = 0.0;
	d->cdgb = 0.0;
	d->cddb = 0.0;
	d->cdsb = 0.0;
	trace4("sat 0/100", d->qdrn, d->cdgb, d->cddb, d->cdsb);
      }else{
	/*40/60 partitioning for drain/source chArges at saturation region*/
	const double co4v15 = 4./15.;
	d->qdrn = -co4v15 * s->cgate * Vgs_Vth;
	d->cdgb = -co4v15 * s->cgate;
	d->cddb =  0.0;
	double cdbb = co4v15 * s->cgate * dVthdVbs;
	d->cdsb = -(d->cdgb + d->cddb + cdbb);
	trace4("sat 40/60", d->qdrn, d->cdgb, d->cddb, d->cdsb);
      }
    }
    if (d->reversed) {
      d->ids *= -1;
      d->gmr = d->gmf;
      d->gmbr = d->gmbf;
      d->gmf = d->gmbf = 0;
    }else{
      d->gmr = d->gmbr = 0.;
    }
}
/*--------------------------------------------------------------------------*/
/*--------------------------------------------------------------------------*/
/*--------------------------------------------------------------------------*/
/*--------------------------------------------------------------------------*/
