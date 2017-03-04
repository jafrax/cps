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

public class DataBeliController extends Window {

    private Logger log = LoggerFactory.getLogger(DataBeliController.class);
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
              
        Session s = Libs.sfDB.openSession();
        try {
            String q0 = "select count(*) ";
            String q1 = "select no_fak, tgl_fak,tgl_tempo, kd_suplier, transaksi, total ";
            String q2 = "from "+Libs.getDbName()+".dbo.beli ";
            String q3 = "";
            String q4 = "order by no_fak asc ";
           
            if(cbStatus.getSelectedIndex() == 0){
            	q2 += " where flg = 1 ";
            }else if(cbStatus.getSelectedIndex() == 1){
            	q2 += " where flg = null ";
            }
            
            if (where!=null) q3 = "and  " + where;
         
            Integer rc = (Integer) s.createSQLQuery(q0 + q2 + q3).uniqueResult();
            pg.setTotalSize(rc);
            
            List<Object[]> l = s.createSQLQuery(q1 + q2 + q3 + q4).setFirstResult(offset).setMaxResults(limit).list();
            for (Object[] o : l) {
                Listitem li = new Listitem();
                li.setValue(o);
                li.appendChild(new Listcell(Libs.nn(o[0]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[1]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[2]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[3]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[4]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[5]).trim()));
                lb.appendChild(li);
            }
        } catch (Exception ex) {
            log.error("populate", ex);
        } finally {
            s.close();
        }
    }

    public void DetailSelected() {
    	ihm = (lb.getSelectedItem().getValue());
    	lbdt.getItems().clear();
        Session s = Libs.sfDB.openSession();
        try {
            String q1 = "select no_fak, kd_barang, banyak, hrgperbrg, disc_rp, disc_pc, kodelok ";
            String q2 = "from "+Libs.getDbName()+".dbo.beli_rin ";
            String q3 = " where no_fak = '"+ Libs.nn(ihm[0]) + "'  ";
          
            System.out.println("detail = " + q1 + q2 + q3 );
            List<Object[]> l = s.createSQLQuery(q1 + q2 + q3 ).list();
            for (Object[] o : l) {
                Listitem li = new Listitem();
                li.setValue(o);
                li.appendChild(new Listcell(Libs.nn(o[0]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[1]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[2]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[3]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[4]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[5]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[6]).trim()));
                
                lbdt.appendChild(li);
            }
        } catch (Exception ex) {
            log.error("detail", ex);
        } finally {
            s.close();
        }
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
            where = "no_fak like '%" + val + "%' or "
                    + "kd_suplier like '%" + val + "%' ";
            populate(0, pg.getPageSize());
        } else refresh();
    }
    
    
   

   
    
}
