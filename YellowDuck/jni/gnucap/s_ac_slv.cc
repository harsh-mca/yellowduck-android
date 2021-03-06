/*$Id: s_ac_slv.cc,v 25.94 2006/08/08 03:22:25 al Exp $ -*- C++ -*-
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
 * ac solution
 */
//testing=script,complete 2006.07.14
#include "e_cardlist.h"
#include "u_status.h"
#include "s_ac.h"
/*--------------------------------------------------------------------------*/
//	void	AC::solve(void);
//	void	AC::clear(void);
/*--------------------------------------------------------------------------*/
void AC::solve(void)
{
  clear();

  ::status.load.start();
  count_iterations(iTOTAL);
  CARD_LIST::card_list.do_ac();
  ::status.load.stop();

  ::status.lud.start();
  acx.lu_decomp();
  ::status.lud.stop();

  ::status.back.start();
  acx.fbsub(ac);
  ::status.back.stop();
}
/*--------------------------------------------------------------------------*/
void AC::clear(void)
{
  acx.zero();
  for (int ii=0;  ii <= ::status.total_nodes;  ++ii) {
    ac[ii] = 0.;
  }
}
/*--------------------------------------------------------------------------*/
/*--------------------------------------------------------------------------*/
