package com.controllers.penjualan;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DataMutasiController extends Window {

    private Logger log = LoggerFactory.getLogger(DataMutasiController.class);
    private Listbox lb;
    private Listbox lbdt;
    private Paging pg;
    private String where;
    private Datebox startDate;
    private Datebox endDate;
    private Object[] ihm;
    

    public void onCreate() {
        initComponents();
        populate(0, pg.getPageSize());
    }

    private void initComponents() {
        lb = (Listbox) getFellow("lb");
        lbdt = (Listbox) getFellow("lbdt");
        pg = (Paging) getFellow("pg");
        startDate = (Datebox)getFellow("startDate");
        endDate = (Datebox)getFellow("endDate");
        endDate.setValue(new Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -30);
        startDate.setValue(cal.getTime());

        
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String startDate = sdf.format(this.startDate.getValue());
		String endDate = sdf.format(this.endDate.getValue());
              
        Session s = Libs.sfDB.openSession();
        try {
            String q0 = "select count(*) ";
            String q1 = "select tgl_mutasi,no_mutasi,keterangan  ";
            String q2 = "from "+Libs.getDbName()+".dbo.mutasi ";
            String q3 = "where  tgl_mutasi <= '"+endDate+"' and tgl_mutasi >= '"+startDate+"' ";
            String q4 = "order by no_mutasi asc ";
           
//            System.out.println(q1 + q2 + q3 + q4);
            Integer rc = (Integer) s.createSQLQuery(q0 + q2 + q3).uniqueResult();
            pg.setTotalSize(rc);
            
            List<Object[]> l = s.createSQLQuery(q1 + q2 + q3 + q4).setFirstResult(offset).setMaxResults(limit).list();
            for (Object[] o : l) {
                Listitem li = new Listitem();
                li.setValue(o);
                li.appendChild(new Listcell(Libs.nn(o[0]).trim()));
                
		                String newstring = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		                if (newstring.equals(Libs.nn(o[0]))){
		                
		                		A mutasi = new A(Libs.nn(o[1]));
			                    mutasi.setStyle("color:#00bbee;text-decoration:none");
			                    Listcell cell = new Listcell();
			                    cell.appendChild(mutasi);
			        			li.appendChild(cell);
			        			
			                    final String no = (String)o[1];
			                    mutasi.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			
			    					@Override
			    					public void onEvent(Event arg0) throws Exception {
			    						showMutasi(no);
			    					}
			    				});
		                	
		                }else {
	                	li.appendChild(new Listcell(Libs.nn(o[1]).trim()));
		                } 
	                	
                 li.appendChild(new Listcell(Libs.nn(o[2]).trim()));
                 lb.appendChild(li);
            }
        } catch (Exception ex) {
            log.error("populate", ex);
        } finally {
            s.close();
        }
    }
    
    
    public void showMutasi(String no){
    	Window w = (Window) Executions.createComponents("views/penjualan/mutasi.zul", Libs.getRootWindow(), null);
    	w.setAttribute("no", no);
    	w.doModal();
    }

    public void DetailSelected() {
    	ihm = (lb.getSelectedItem().getValue());
    	lbdt.getItems().clear();
        Session s = Libs.sfDB.openSession();
        try {
            String sql = "select b.lok_dr,b.lok_ke ,b.kode_brg,namabrg,b.jumlah,a.satuan,a.merkbrg,a.tipebrg "
            		+ "from "+Libs.getDbName()+".dbo.barang a inner join "+Libs.getDbName()+".dbo.mutasi_rin b "
            		+ "ON a.kode=b.kode_brg   "
            		+ "where b.no_mutasi = '"+ Libs.nn(ihm[1]) + "' ";
           
            
            List<Object[]> l = s.createSQLQuery(sql ).list();
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

    
   public void viewMutasiHistory(){
	   refresh();
   }

   
    
}
