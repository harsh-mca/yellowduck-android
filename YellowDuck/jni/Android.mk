LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_LDLIBS := -llog
LOCAL_MODULE := w_yd_gnucap

LOCAL_SRC_FILES := interface.cpp gnucap/u_prblst.cc gnucap/ap_construct.cc	gnucap/bm_pwl.cc	gnucap/c_nodset.cc	gnucap/d_dot.cc	gnucap/d_switch.cc	gnucap/globals.cc	gnucap/md.cc		gnucap/s_fo_out.cc \
			gnucap/ap_convert.cc	gnucap/bm_sffm.cc	gnucap/c_param.cc	gnucap/d_logic.cc	gnucap/d_trln.cc	gnucap/io.cc		gnucap/plot.cc		gnucap/s_fo_set.cc \
			gnucap/ap_error.cc	gnucap/bm_sin.cc	gnucap/c_prbcmd.cc	gnucap/d_logicmod.cc	gnucap/d_vcr.cc	gnucap/io_contr.cc	gnucap/s__.cc		gnucap/s_tr.cc \
			gnucap/ap_get.cc	gnucap/bm_tanh.cc	gnucap/c_sim.cc	gnucap/d_mos.cc	gnucap/d_vcvs.cc	gnucap/io_error.cc	gnucap/s__aux.cc	gnucap/s_tr_rev.cc \
			gnucap/ap_match.cc	gnucap/bm_value.cc	gnucap/c_status.cc	gnucap/d_mos1.cc	gnucap/d_vs.cc		gnucap/io_findf.cc	gnucap/s__init.cc	gnucap/s_tr_set.cc \
			gnucap/ap_skip.cc	gnucap/bmm_semi.cc	gnucap/c_sweep.cc	gnucap/d_mos123.cc	gnucap/e_base.cc	gnucap/io_getln.cc	gnucap/s__map.cc	gnucap/s_tr_swp.cc \
			gnucap/bm.cc		gnucap/bmm_table.cc	gnucap/c_system.cc	gnucap/d_mos2.cc	gnucap/e_card.cc	gnucap/io_out.cc	gnucap/s__out.cc	gnucap/u_nodemap.cc \
			gnucap/bm_complex.cc	gnucap/c__cmd.cc	gnucap/d_admit.cc	gnucap/d_mos3.cc	gnucap/e_cardlist.cc	gnucap/io_xopen.cc	gnucap/s__solve.cc	gnucap/u_opt1.cc \
			gnucap/bm_cond.cc	gnucap/c_comand.cc	gnucap/d_bjt.cc	gnucap/d_mos4.cc	gnucap/e_ccsrc.cc	gnucap/l_ftos.cc	gnucap/s_ac.cc		gnucap/u_opt2.cc \
			gnucap/bm_exp.cc	gnucap/c_delete.cc	gnucap/d_cap.cc	gnucap/d_mos5.cc	gnucap/e_compon.cc	gnucap/l_pmatch.cc	gnucap/s_ac_set.cc	gnucap/u_parameter.cc \
			gnucap/bm_fit.cc	gnucap/c_fanout.cc	gnucap/d_cccs.cc	gnucap/d_mos6.cc	gnucap/e_elemnt.cc	gnucap/l_timer.cc	gnucap/s_ac_slv.cc	 \
			gnucap/bm_generator.cc	gnucap/c_file.cc	gnucap/d_ccvs.cc	gnucap/d_mos7.cc	gnucap/e_model.cc	gnucap/l_trim.cc	gnucap/s_ac_swp.cc	gnucap/u_probe.cc \
			gnucap/bm_model.cc	gnucap/c_genrat.cc	gnucap/d_coil.cc	gnucap/d_mos8.cc	gnucap/e_node.cc	gnucap/l_wmatch.cc	gnucap/s_dc.cc		gnucap/u_sdp.cc \
			gnucap/bm_poly.cc	gnucap/c_getckt.cc	gnucap/d_coment.cc	gnucap/d_mos_base.cc	gnucap/e_storag.cc	gnucap/m_fft.cc	gnucap/s_dc_set.cc	gnucap/u_xprobe.cc \
			gnucap/bm_posy.cc	gnucap/c_list.cc	gnucap/d_cs.cc		gnucap/d_res.cc	gnucap/e_subckt.cc	gnucap/m_spline.cc	gnucap/s_dc_swp.cc \
			gnucap/bm_pulse.cc	gnucap/c_modify.cc	gnucap/d_diode.cc	gnucap/d_subckt.cc	gnucap/findbr.cc	gnucap/main.cc		gnucap/s_fo.cc 
    
include $(BUILD_SHARED_LIBRARY) 
