package com.controllers;

import com.pojo.ClientPOJO;
import com.tools.Libs;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;
import org.zkoss.zul.event.PagingEvent;

import java.util.List;

public class DataReturJualController extends Window {

    private Logger log = LoggerFactory.getLogger(DataReturJualController.class);
    private Listbox lb;
    private Listbox lbdt;
    private Paging pg;
    private String where;
    private Combobox cbStatus;
    private Object[] ihm;
    

    public void onCreate() {
        initComponents();
        populate(0, pg.getPageSize());
    }

    private void initComponents() {
        lb = (Listbox) getFellow("lb");
        lbdt = (Listbox) getFellow("lbdt");
        pg = (Paging) getFellow("pg");
        cbStatus = (Combobox) getFellow("cbStatus");
        
        cbStatus.appendItem("ACTIVE");
        cbStatus.appendItem("PROCES");
        cbStatus.appendItem("ALL");
        cbStatus.setSelectedIndex(0);
        
        pg.addEventListener("onPaging", new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
                PagingEvent evt = (PagingEvent) event;
                populate(evt.getActivePage()*pg.getPageSize(), pg.getPageSize());
            }
        });
    }

    private void populate(int offset, int limit) {
        lb.getItems().clear();
       
//        Session s = Libs.sfDB.openSession();
//        try {
//            String q0 = "select count(*) ";
//            String q1 = "select no_retfak, tgl_retfak, kd_suplier, transaksi, total ";
//            String q2 = "from "+Libs.getDbName()+".dbo.returbeli ";
//            String q3 = "";
//            String q4 = "order by no_retfak asc ";
//           
//            if(cbStatus.getSelectedIndex() == 0){
//            	q2 += " where flg = 1 ";
//            }else if(cbStatus.getSelectedIndex() == 1){
//            	q2 += " where flg = null ";
//            }else
//            	q2 += " where flg in (1,null) ";
//            
//            if (where!=null) q3 = "and  " + where;
//         
//            Integer rc = (Integer) s.createSQLQuery(q0 + q2 + q3).uniqueResult();
//            pg.setTotalSize(rc);
//            
//            System.out.println(q1 + q2 + q3 + q4);
//            List<Object[]> l = s.createSQLQuery(q1 + q2 + q3 + q4).setFirstResult(offset).setMaxResults(limit).list();
//            for (Object[] o : l) {
//                Listitem li = new Listitem();
//                li.setValue(o);
//                li.appendChild(new Listcell(Libs.nn(o[0]).trim()));
//                li.appendChild(new Listcell(Libs.nn(o[1]).trim()));
//                li.appendChild(new Listcell(Libs.nn(o[2]).trim()));
//                li.appendChild(new Listcell(Libs.nn(o[3]).trim()));
//                li.appendChild(new Listcell(Libs.nn(o[4]).trim()));
//                
//                lb.appendChild(li);
//            }
//        } catch (Exception ex) {
//            log.error("populate", ex);
//        } finally {
//            s.close();
//        }
    }

    public void DetailSelected() {
    	ihm = (lb.getSelectedItem().getValue());
    	lbdt.getItems().clear();
//        
//        Session s = Libs.sfDB.openSession();
//        try {
//            String q0 = "select count(*) ";
//            String q1 = "select no_retfak, faktur, kd_barang,banyak, hrgperbrg ";
//            String q2 = "from "+Libs.getDbName()+".dbo.returbeli_rin ";
//            String q3 = " where no_retfak = '"+ Libs.nn(ihm[0]) + "'  ";
//            String q4 = "order by no_retfak asc ";
//           
//            System.out.println(q1 + q2 + q3 + q4);
//            List<Object[]> l = s.createSQLQuery(q1 + q2 + q3 + q4).list();
//            for (Object[] o : l) {
//                Listitem li = new Listitem();
//                li.setValue(o);
//                li.appendChild(new Listcell(Libs.nn(o[0]).trim()));
//                li.appendChild(new Listcell(Libs.nn(o[1]).trim()));
//                li.appendChild(new Listcell(Libs.nn(o[2]).trim()));
//                li.appendChild(new Listcell(Libs.nn(o[3]).trim()));
//                li.appendChild(new Listcell(Libs.nn(o[4]).trim()));
//                
//                lbdt.appendChild(li);
//            }
//        } catch (Exception ex) {
//            log.error("detail", ex);
//        } finally {
//            s.close();
//        }
    } 

    public void refresh() {
        where = null;
        populate(0, pg.getPageSize());
    }
  
    public void Selected() {
        quickSearch();
    }
    
    public void quickSearch() {
        String val = ((Textbox) getFellow("tQuickSearch")).getText();
        if (!val.isEmpty()) {
            where = "perusahaan like '%" + val + "%' or "
                    + "kode like '%" + val + "%' ";
            populate(0, pg.getPageSize());
        } else refresh();
    }
    
    
}
