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

public class ClientsController extends Window {

    private Logger log = LoggerFactory.getLogger(ClientsController.class);
    private Listbox lb;
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
        pg = (Paging) getFellow("pg");
        cbStatus = (Combobox) getFellow("cbStatus");
        
        cbStatus.appendItem("ACTIVE");
        cbStatus.appendItem("INACTIVE");
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
        ((Toolbarbutton) getFellow("tbnDelete")).setDisabled(true);
        
        Session s = Libs.sfDB.openSession();
        try {
            String q0 = "select count(*) ";
            String q1 = "select "
                    + "kode,nama,perusahaan,alamat1,alamat2,kota,kodepos,telp,hp,fax,kd_sales,ketharga,plafon ";
            String q2 = "from "+Libs.getDbName()+".dbo.customer ";
            String q3 = "";
            String q4 = "order by kode asc ";
           
            if(cbStatus.getSelectedIndex() == 0){
            	q2 += " where flg = 0 ";
            }else if(cbStatus.getSelectedIndex() == 1){
            	q2 += " where flg = 1 ";
            }else
            	q2 += " where flg in (1,0) ";
            
            if (where!=null) q3 = "and  " + where;
         
            Integer rc = (Integer) s.createSQLQuery(q0 + q2 + q3).uniqueResult();
            pg.setTotalSize(rc);
            
            //Messagebox.show("query :" + q1 + q2 + q3 + q4);
            List<Object[]> l = s.createSQLQuery(q1 + q2 + q3 + q4).setFirstResult(offset).setMaxResults(limit).list();
            for (Object[] o : l) {
                ClientPOJO e = new ClientPOJO();
                e.setClientId(Libs.nn(o[0]).trim());
                e.setClientName(Libs.nn(o[1]).trim());

                Listitem li = new Listitem();
                li.setValue(o);
                li.appendChild(new Listcell(e.getClientId()));
                li.appendChild(new Listcell(e.getClientName()));
                li.appendChild(new Listcell(Libs.nn(o[2]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[3]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[4]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[5]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[6]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[7]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[8]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[9]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[10]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[11]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[12]).trim()));
                lb.appendChild(li);
            }
        } catch (Exception ex) {
            log.error("populate", ex);
        } finally {
            s.close();
        }
    }

    public void clientSelected() {
        if (lb.getSelectedCount()==1) {
        	((Toolbarbutton) getFellow("tbnDelete")).setDisabled(false);
        }
    } 

    public void refresh() {
        where = null;
        populate(0, pg.getPageSize());
    }

    public void Input() {
    	Window w = (Window) Executions.createComponents("views/InputClient.zul", this, null);
        w.doModal();
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
    
    
   
    public void delete() {
       if (Messagebox.show("Do you want to remove this Client ?", "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, Messagebox.CANCEL)==Messagebox.OK) {
       	ihm = (lb.getSelectedItem().getValue());
       	
       	Session s = Libs.sfDB.openSession();
       	try{
       	s.beginTransaction();
       	String qry = " delete "+Libs.getDbName()+".dbo.customer where  "
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
