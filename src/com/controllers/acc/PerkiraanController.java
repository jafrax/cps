package com.controllers.acc;

import com.pojo.ClientPOJO;
import com.tools.Libs;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;
import org.zkoss.zul.event.PagingEvent;

import java.math.BigInteger;
import java.util.List;

public class PerkiraanController extends Window {

    private Logger log = LoggerFactory.getLogger(PerkiraanController.class);
    private Listbox lb;
    private Paging pg;
    private String where;
//    private Combobox cbStatus;
    private Object[] ihm;

    public void onCreate() {
        initComponents();
        populate(0, pg.getPageSize());
    }

    private void initComponents() {
        lb = (Listbox) getFellow("lb");
        pg = (Paging) getFellow("pg");
//        cbStatus = (Combobox) getFellow("cbStatus");
//        
//        cbStatus.appendItem("ACTIVE");
//        cbStatus.appendItem("INACTIVE");
//        cbStatus.appendItem("ALL");
//        cbStatus.setSelectedIndex(0);
//        
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
            String q1 = "select kdprk,[group],nama,posnorm,jenis,posslaw,sbgprk,kdpmodal ";
            String q2 = "from "+Libs.getDbName()+".dbo.msprk ";
            String q3 = " where tdkpakai = 0 ";
            String q4 = "order by kdprk asc ";
            if (where!=null) q3 = " and  " + where;
         
            Integer rc = (Integer) s.createSQLQuery(q0 + q2 + q3).uniqueResult();
            pg.setTotalSize(rc);
            
            List<Object[]> l = s.createSQLQuery(q1 + q2 + q3 + q4).setFirstResult(offset).setMaxResults(limit).list();
            for (Object[] o : l) {
               
            	 Listitem li = new Listitem();
                 li.setValue(o);
     			
                 A perkiraan = new A(Libs.nn(o[0]));
                 perkiraan.setStyle("color:#00bbee;text-decoration:none");
                Listcell cell = new Listcell();
                cell.appendChild(perkiraan);
    			li.appendChild(cell);

                li.appendChild(new Listcell(Libs.nn(o[1]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[2]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[3]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[4]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[5]).trim()));
                
                String ket = Libs.nn(o[6]).trim();
                if ( ket.length() >= 1){
                	String setprk = Libs.setperkiraan(Libs.nn(o[6]));
	                li.appendChild(new Listcell(setprk));
  	    		}else{
  	    			li.appendChild(new Listcell("Normal"));
  	    		}
                li.appendChild(new Listcell(Libs.nn(o[7]).trim()));
                

                final String Id = (String)o[0];
                perkiraan.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event arg0) throws Exception {
						showPerkiraan(Id);
					}
				});
        
                lb.appendChild(li);
               
            }
        } catch (Exception ex) {
            log.error("populate", ex);
        } finally {
            s.close();
        }
    }

    public void SalesSelected() {
        if (lb.getSelectedCount()>0) {
        	((Toolbarbutton) getFellow("tbnDelete")).setDisabled(false);
        	}
    } 

    
    public void showPerkiraan(String Id){
    	Window w = (Window) Executions.createComponents("views/acc/EditPerkiraan.zul", Libs.getRootWindow(), null);
    	w.setAttribute("Id", Id);
    	w.doModal();
    }
    
    public void refresh() {
        where = null;
        populate(0, pg.getPageSize());
        ((Toolbarbutton) getFellow("tbnDelete")).setDisabled(true);
    }

    
    public void openMaster(String viewName) {
        Window w = (Window) Executions.createComponents("views/master/" + viewName + ".zul", this, null);
        w.doOverlapped();
    }
    

    public void Selected() {
        quickSearch();
    }
    
    public void quickSearch() {
        String val = ((Textbox) getFellow("tQuickSearch")).getText();
        if (!val.isEmpty()) {
            where = "nama like '%" + val + "%' or "
                    + "kdprk like '%" + val + "%' ";

            populate(0, pg.getPageSize());
        } else refresh();
    }
    
    public void delete(){
    	
    	if (Messagebox.show("Do you want to remove this Sales ?", "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, Messagebox.CANCEL)==Messagebox.OK) {
        	ihm = (lb.getSelectedItem().getValue());
        	
        	Session s = Libs.sfDB.openSession();
        	try{
        	s.beginTransaction();
        	String qry ="update "+Libs.getDbName()+".dbo.msprk set tdkpakai=1 where "
        				+" kdprk ='"+ Libs.nn(ihm[0]) + "' ";
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
