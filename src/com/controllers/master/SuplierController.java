package com.controllers.master;

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

import java.util.List;

public class SuplierController extends Window {

    private Logger log = LoggerFactory.getLogger(SuplierController.class);
    private Listbox lb;
    private Paging pg;
    private String cari;
    private Object[] ihm;
    private Combobox cbStatus;

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
        Session s = Libs.sfDB.openSession();
        try {
            String q0 = "select count(*) ";
            String q1 = "select "
                    + "kode,nama,perusahaan,alamat1,alamat2,kota,kodepos,telp,fax,tempobyr ";
            String q2 = "from "+Libs.getDbName()+".dbo.suplier ";
            String q3 = "";
            String q4 = "order by kode asc ";

            
            if(cbStatus.getSelectedIndex() == 0){
            	q2 += " where tdkpakai = 0 ";
            }else if(cbStatus.getSelectedIndex() == 1){
            	q2 += " where tdkpakai = 1 ";
            }else
            	q2 += " where tdkpakai in (1,0) ";
        
            if (cari!=null) q3 = " and " + cari;

            Integer rc = (Integer) s.createSQLQuery(q0 + q2 + q3).uniqueResult();
            pg.setTotalSize(rc);
            
            List<Object[]> l = s.createSQLQuery(q1 + q2 + q3 + q4).setFirstResult(offset).setMaxResults(limit).list();
            for (Object[] o : l) {
                
                Listitem li = new Listitem();
                li.setValue(o);
                
                li.appendChild(new Listcell(Libs.nn(o[0]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[1]).trim()));
                
                A SuplierName = new A(Libs.nn(o[2]));
                SuplierName.setStyle("color:#00bbee;text-decoration:none");
                Listcell cell = new Listcell();
                cell.appendChild(SuplierName);
    			li.appendChild(cell);
    			
                li.appendChild(new Listcell(Libs.nn(o[3]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[4]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[5]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[6]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[7]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[8]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[9]).trim()));  
                
                final String SuplierId = (String)o[0];
                SuplierName.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event arg0) throws Exception {
						showSuplierDetail(SuplierId);
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

    public void clientSelected() {
        if (lb.getSelectedCount()>0) {
        	((Toolbarbutton) getFellow("tbnDelete")).setDisabled(false);   
        }
    } 
    
    public void showSuplierDetail(String SuplierId){
    	Window w = (Window) Executions.createComponents("views/master/EditSuplier.zul", Libs.getRootWindow(), null);
    	w.setAttribute("Id", SuplierId);
    	w.doModal();
    }
    
    
    public void openMaster(String viewName) {
        Window w = (Window) Executions.createComponents("views/master/" + viewName + ".zul", this, null);
        w.doOverlapped();
    }
    
    
    public void Selected() {
        quickSearch();
    }
    
   

    public void refresh() {
        cari = null;
        ((Toolbarbutton) getFellow("tbnDelete")).setDisabled(true);   
        populate(0, pg.getPageSize());
    }

    public void quickSearch() {
        String val = ((Textbox) getFellow("tQuickSearch")).getText();
        if (!val.isEmpty()) {
            cari = " perusahaan like '%" + val + "%' or "
            		+ " nama like '%" + val + "%' or "
                    + " kode like '%" + val + "%' ";

            populate(0, pg.getPageSize());
        } else refresh();
    }

    public void delete() {
        if (Messagebox.show("Do you want to remove this Client ?", "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, Messagebox.CANCEL)==Messagebox.OK) {
        	ihm = (lb.getSelectedItem().getValue());
        	
        	Session s = Libs.sfDB.openSession();
        	try{
        	s.beginTransaction();
        	String qry = " Update "+Libs.getDbName()+".dbo.suplier  set tdkpakai = 1  where  "
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
    
    public void print() {
    	Window w = (Window) Executions.createComponents("views/reporting/PrintSuplier.zul", this, null);
        w.doModal();
    }
   
    
}
