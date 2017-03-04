package com.controllers.master.produk;

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

public class SubKategoriController extends Window {

    private Logger log = LoggerFactory.getLogger(SubKategoriController.class);
    private Listbox lb;
    private Paging pg;
    private String where;
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
        ((Toolbarbutton) getFellow("tbnDelete")).setDisabled(true);
        
        Session s = Libs.sfDB.openSession();
        try {
            String q0 = "select count(*) ";
            String q1 = "select "
                    + " a.kode,a.nama as sub_name,a.kd_kategori,b.nama as ketegori_name ,a.ket1,a.ket2 ";
            String q2 = "from "+Libs.getDbName()+".dbo.subkategori a inner join "+Libs.getDbName()+".dbo.kategori b on b.kode=a.kd_kategori ";
            String q3 = "";
            String q4 = "order by a.kode asc ";
            
            if(cbStatus.getSelectedIndex() == 0){
            	q2 += " where a.tdkpakai = 0 and b.tdkpakai = 0 ";
            }else if(cbStatus.getSelectedIndex() == 1){
            	q2 += " where a.tdkpakai = 1  and b.tdkpakai = 0 ";
            }else{
            	q2 += " where a.tdkpakai in (1,0)  and b.tdkpakai = 0 ";
            }
            
            if (where!=null) q3 = " and  " + where;
            
            Integer rc = (Integer) s.createSQLQuery(q0 + q2 + q3).uniqueResult();
            pg.setTotalSize(rc);
         
            List<Object[]> l = s.createSQLQuery(q1 + q2 + q3 + q4).setFirstResult(offset).setMaxResults(limit).list();
            for (Object[] o : l) {
                Listitem li = new Listitem();
                li.setValue(o);

                A kode = new A(Libs.nn(o[0]).trim());
                kode.setStyle("color:#00bbee;text-decoration:none");
                Listcell cell = new Listcell();
                cell.appendChild(kode);
    			li.appendChild(cell);
    			
                li.appendChild(new Listcell(Libs.nn(o[1])));
                li.appendChild(new Listcell(Libs.nn(o[2])));
                li.appendChild(new Listcell(Libs.nn(o[3])));
                li.appendChild(new Listcell(Libs.nn(o[4])));
                li.appendChild(new Listcell(Libs.nn(o[5])));
                
                final String Id = (String)Libs.nn(o[0]);
                kode.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event arg0) throws Exception {
						showSubKategoriDetail(Id);
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
    
    public void showSubKategoriDetail(String Id){
    	Window w = (Window) Executions.createComponents("views/master/produk/EditSubKategori.zul", Libs.getRootWindow(), null);
    	w.setAttribute("Id", Id);
    	w.doModal();
    }


    public void SubKategoriSelected() {
        if (lb.getSelectedCount()==1) {
        	((Toolbarbutton) getFellow("tbnDelete")).setDisabled(false);
        }
    } 

    public void refresh() {
        where = null;
        populate(0, pg.getPageSize());
    }

    public void openMasterProduk(String viewName) {
        Window w = (Window) Executions.createComponents("views/master/produk/" + viewName + ".zul", this, null);
        w.doOverlapped();
    }
    
    public void Selected() {
        quickSearch();
    }
    
    public void quickSearch() {
        String val = ((Textbox) getFellow("tQuickSearch")).getText();
        if (!val.isEmpty()) {
            where = "a.kode like '%" + val + "%' or "
            		+ " a.nama like '%" + val + "%' or "
            		+ " b.kode like '%" + val + "%' or "
            		+ " b.nama like '%" + val + "%' ";
            populate(0, pg.getPageSize());
        } else refresh();
    }
    
    
   
    public void delete() {
       if (Messagebox.show("Do you want to remove this Client ?", "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, Messagebox.CANCEL)==Messagebox.OK) {
       	ihm = (lb.getSelectedItem().getValue());
       	
       	Session s = Libs.sfDB.openSession();
       	try{
       	s.beginTransaction();
       	String qry = " update "+Libs.getDbName()+".dbo.subkategori set tdkpakai=1  where  "
                   +" kode ='"+ Libs.nn(ihm[0]) + "'  and  kd_kategori ='"+ Libs.nn(ihm[2]) + "'";
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
