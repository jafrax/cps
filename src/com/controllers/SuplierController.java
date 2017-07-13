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

public class SuplierController extends Window {

    private Logger log = LoggerFactory.getLogger(SuplierController.class);
    private Listbox lb;
    private Paging pg;
    private String where;
    private Object[] ihm;

    public void onCreate() {
        initComponents();
        populate(0, pg.getPageSize());
    }

    private void initComponents() {
        lb = (Listbox) getFellow("lb");
        pg = (Paging) getFellow("pg");

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
            String q1 = "select "
                    + "kode,nama,perusahaan,alamat1,alamat2,kota,kodepos,telp,fax,tempobyr ";
            String q2 = "from "+Libs.getDbName()+".dbo.suplier ";
            String q3 = "";
            String q4 = "order by kode asc ";

            if (where!=null) q3 = "where " + where;

            Integer rc = (Integer) s.createSQLQuery(q0 + q2 + q3).uniqueResult();
            pg.setTotalSize(rc);
            
            //Messagebox.show("query : " +q1 + q2 + q3 + q4 );
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
                li.appendChild(new Listcell(Libs.nn(o[6]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[7]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[8]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[9]).trim()));             
                
                lb.appendChild(li);
            }
        } catch (Exception ex) {
            log.error("populate", ex);
        } finally {
            s.close();
        }
    }

    public void clientSelected() {
        if (lb.getSelectedCount()>0) {
        	((Toolbarbutton) getFellow("tbnDelete")).setDisabled(false);   
        
        }
    } 
    
    public void DetailclientSelected() {
        if (lb.getSelectedCount()>0) {
//        	Messagebox.show("dobel clik ya ");
        	ihm = (lb.getSelectedItem().getValue());
        	Window w = (Window) Executions.createComponents("views/EditSuplier.zul",  Libs.getRootWindow(), null);
        	w.setAttribute("kode", ihm[0]);
            w.setAttribute("toko", ihm[2]);
            w.doModal();
            
        }
    } 
    public void Input() {
    	Window w = (Window) Executions.createComponents("views/InputSuplier.zul", this, null);
        w.doModal();
    }
    
    public void print() {
    	Window w = (Window) Executions.createComponents("views/PrintSuplier.zul", this, null);
        w.doModal();
    }
   

    public void refresh() {
        where = null;
        populate(0, pg.getPageSize());
    }

    public void quickSearch() {
        String val = ((Textbox) getFellow("tQuickSearch")).getText();
        if (!val.isEmpty()) {
            where = "perusahaan like '%" + val + "%' or "
            		+ "nama like '%" + val + "%' or "
                    + "kode like '%" + val + "%' ";

            populate(0, pg.getPageSize());
        } else refresh();
    }

    public void delete() {
        if (Messagebox.show("Do you want to remove this Client ?", "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, Messagebox.CANCEL)==Messagebox.OK) {
        	ihm = (lb.getSelectedItem().getValue());
        	
        	Session s = Libs.sfDB.openSession();
        	try{
        	s.beginTransaction();
        	String qry = " delete "+Libs.getDbName()+".dbo.suplier where  "
                    +" kode ='"+ Libs.nn(ihm[0]) + "' ";
        	   s.createSQLQuery(qry).executeUpdate();
            s.flush();
            s.clear();
            s.getTransaction().commit();
            
            lb.removeChild(lb.getSelectedItem());
            ((Toolbarbutton) getFellow("tbnDelete")).setDisabled(true);
            Messagebox.show(" Clien has been removed", "Information", Messagebox.OK, Messagebox.INFORMATION);
        	} catch (Exception ex) {
             log.error("delete", ex);
         } finally {
             s.close();
         }   
        	  
        }
    }
    
}
