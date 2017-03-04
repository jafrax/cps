package com.controllers.master.produk;

import com.pojo.*;
import com.tools.Libs;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;
import org.zkoss.zul.event.PagingEvent;

import java.text.SimpleDateFormat;
import java.util.List;


public class BarangController extends Window {

    private Logger log = LoggerFactory.getLogger(BarangController.class);
    private Listbox lb;
    private Paging pg;
    private ClientPOJO clientPOJO;
    private String where;
    private Object[] ihm;
    private Combobox cbStatus;

    public void onCreate() {
        clientPOJO = (ClientPOJO) getAttribute("clientPOJO");

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
    	((Toolbarbutton) getFellow("tbnDelete")).setDisabled(true);
    	lb.getItems().clear();
        Session s = Libs.sfDB.openSession();
        try {
            String q0 = "select count(*) ";
            String q1 = "select "
                    + "kode, idbarang,namabrg,merkbrg,tipebrg,satuan,harga1,harga2,harga3,harga4,harga5,harga6,"
                    + "kd_subkategori,merk,ket1,ket2 ";
            String q2 = " from "
                    + ""+Libs.getDbName()+".dbo.barang ";
            String q3 = "";
                   
            String q4 = "order by kode desc, namabrg asc ";
            
            if(cbStatus.getSelectedIndex() == 0){
            	q2 += " where tdkpakai = 0 ";
            }else if(cbStatus.getSelectedIndex() == 1){
            	q2 += " where tdkpakai = 1  ";
            }else{
            	q2 += " where tdkpakai in (1,0)  ";
            }
            
            if (where!=null) {
                q3 += " and (" + where + ") ";
            }
            
            Integer rc = (Integer) s.createSQLQuery(q0 + q2 + q3).uniqueResult();
            pg.setTotalSize(rc);
            
            System.out.println(q1 + q2 + q3 + q4);
            List<Object[]> l = s.createSQLQuery(q1 + q2 + q3 + q4).setFirstResult(offset).setMaxResults(limit).list();
            for (Object[] o : l) {
            	Listitem li = new Listitem();
                li.setValue(o);

                li.appendChild(new Listcell(Libs.nn(o[0])));
                li.appendChild(new Listcell(Libs.nn(o[1])));
                li.appendChild(new Listcell(Libs.nn(o[2])));
                li.appendChild(new Listcell(Libs.nn(o[3])));
                li.appendChild(new Listcell(Libs.nn(o[4])));
                
                Object[] obj = Libs.getStok(Libs.nn(Libs.nn(o[0])));
			  	li.appendChild(new Listcell(obj[0].toString()));
                
			  	li.appendChild(new Listcell(Libs.nn(o[5])));
                li.appendChild(new Listcell(Libs.nn(o[6])));
                li.appendChild(new Listcell(Libs.nn(o[7])));
                li.appendChild(new Listcell(Libs.nn(o[8])));
                li.appendChild(new Listcell(Libs.nn(o[9])));
                li.appendChild(new Listcell(Libs.nn(o[10])));
                li.appendChild(new Listcell(Libs.nn(o[11])));
                li.appendChild(new Listcell(Libs.nn(o[12])));
                li.appendChild(new Listcell(Libs.nn(o[13])));
                li.appendChild(new Listcell(Libs.nn(o[14])));
                li.appendChild(new Listcell(Libs.nn(o[15])));
             
                lb.appendChild(li);
            }
        } catch (Exception ex) {
            log.error("populate", ex);
        } finally {
            s.close();
        }
    }

    public void productSelected() {
    	  if (lb.getSelectedCount()==1) {
          	((Toolbarbutton) getFellow("tbnDelete")).setDisabled(false);
          }
    }

    public void openMasterProduk(String viewName) {
        Window w = (Window) Executions.createComponents("views/master/produk/" + viewName + ".zul", this, null);
        w.doOverlapped();
    }
    
    public void refresh() {
        where = null;
        populate(0, pg.getPageSize());
    }

    public void quickSearch() {
        String val = ((Textbox) getFellow("tQuickSearch")).getText();
        if (!val.isEmpty()) {
            where = " kode  like '%" + val + "%' or "
                    + " namabrg like '%" + val + "%' ";

            populate(0, pg.getPageSize());
        } else refresh();
    }

    public void Selected() {
        quickSearch();
    }
    
    public void delete(){
    	if (Messagebox.show("Do you want to remove this Product ?", "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, Messagebox.CANCEL)==Messagebox.OK) {
           	ihm = (lb.getSelectedItem().getValue());
           	
           	Session s = Libs.sfDB.openSession();
           	try{
           		s.beginTransaction();
           		
           		String sql = " delete "+Libs.getDbName()+".dbo.stokperlok where  "
                        +" kd_barang ='"+ Libs.nn(ihm[0]) + "' ";
            		
           		String qry = " delete "+Libs.getDbName()+".dbo.barang where  "
                       +" kode ='"+ Libs.nn(ihm[0]) + "' ";
           		
           		s.createSQLQuery(qry+sql).executeUpdate();
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
