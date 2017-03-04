package com.controllers.master.gudang;

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

public class StoklokController extends Window {

    private Logger log = LoggerFactory.getLogger(StoklokController.class);
    private Listbox lb;
    private Paging pg;
    private String where;
    private Combobox cbLokasi;
    private Object[] ihm;
    

    public void onCreate() {
        initComponents();
        populate(0, pg.getPageSize());
    }

    private void initComponents() {
        lb = (Listbox) getFellow("lb");
        pg = (Paging) getFellow("pg");
        cbLokasi = (Combobox) getFellow("cbLokasi");
        
        Libs.getGudang(cbLokasi);
        
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
                    + "a.kode,a.namabrg,b.jumlah,b.v_jumlah,a.merkbrg,a.tipebrg ";
            String q2 = "from "+Libs.getDbName()+".dbo.barang a inner join "+Libs.getDbName()+".dbo.stokperlok b ON a.kode=b.kd_barang "
            		+ " where b.kd_lokasi = '"+cbLokasi.getText()+"'";
            String q3 = "";
            String q4 = "order by a.kode asc ";
            
            if (where!=null) q3 = "and  " + where;
            Integer rc = (Integer) s.createSQLQuery(q0 + q2 + q3).uniqueResult();
            pg.setTotalSize(rc);
            
            List<Object[]> l = s.createSQLQuery(q1 + q2 + q3 + q4).setFirstResult(offset).setMaxResults(limit).list();
            for (Object[] o : l) {
       
                Listitem li = new Listitem();
                li.setValue(o);
                li.appendChild(new Listcell(Libs.nn(o[0]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[1]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[2])));
                li.appendChild(new Listcell(Libs.nn(o[3])));
                li.appendChild(new Listcell(Libs.nn(o[4]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[5])));
               
                lb.appendChild(li);
            }
        } catch (Exception ex) {
            log.error("populate", ex);
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
            where = "a.namabrg like '%" + val + "%' or "
                    + "a.kode like '%" + val + "%' ";
            populate(0, pg.getPageSize());
        } else refresh();
    }
    
    
   
   
    
}
