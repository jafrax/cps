package com.controllers.master;

import com.pojo.TokoPOJO;
import com.tools.Libs;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;
import org.zkoss.zul.event.PagingEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@SuppressWarnings("serial")
public class InputTokoController extends Window {

    private Logger log = LoggerFactory.getLogger(InputTokoController.class);
    private String kdtoko;
    private String kdkota;
    
    private Listbox lbkota;
    private Paging pgKota;
    private String whereKota;
    private Bandbox kode_toko;
    private Textbox kota_region;
    
    private Combobox kel_harga;
    private Textbox nama;
    private Textbox toko;
    private Textbox status;
    private Textbox alamat1;
    private Textbox alamat2;
    private Textbox telp1;
    private Textbox telp2;
    private Textbox hp1;
    private Textbox hp2;
    private Textbox kota;
    private Textbox pos;
    
      

    public void onCreate() {
        initComponents();
        populateKota(0, pgKota.getPageSize());
           }

    private void initComponents() {
    	
         lbkota = (Listbox) getFellow("lbKota");
         pgKota = (Paging) getFellow("pgKota");
         kode_toko = (Bandbox) getFellow("kode_toko");
         kota_region = (Textbox) getFellow("kota_region");
    	
         kel_harga = (Combobox) getFellow("kel_harga");
         nama = (Textbox) getFellow("nama");
         toko = (Textbox) getFellow("toko");
         alamat1 = (Textbox) getFellow("alamat1");
         alamat2 = (Textbox) getFellow("alamat2");
         kota = (Textbox) getFellow("kota");
       	 pos = (Textbox) getFellow("pos");
       	 telp1 = (Textbox) getFellow("telp1");
       	 telp2 = (Textbox) getFellow("telp2");
       	 hp1 = (Textbox) getFellow("hp1");
       	 hp2 = (Textbox) getFellow("hp2");
       	 status = (Textbox) getFellow("status");
       
       	 kel_harga.appendItem("");
       	 kel_harga.appendItem("E");
       	 kel_harga.appendItem("A");
       	 kel_harga.appendItem("B");
       	 kel_harga.setSelectedIndex(0);
         
         pgKota.addEventListener("onPaging", new EventListener() {
             @Override
             public void onEvent(Event event) throws Exception {
                 PagingEvent evt = (PagingEvent) event;
                 populateKota(evt.getActivePage()*pgKota.getPageSize(), pgKota.getPageSize());
             }
         });
        
     }
    
    private void populateKota(int offset, int limit) {
        lbkota.getItems().clear();
        Session s = Libs.sfDB.openSession();
        try {
            String q0 = "select count(*) ";
            String q1 = "select "
                    + " a.kode_kota,a.nama_kota,b.nama_prop  ";
            String q2 = "from "+Libs.getDbName()+".dbo.kota a inner join "+Libs.getDbName()+".dbo.propinsi b ON left(a.kode_kota,2) = b.kode_prop ";
            String q3 = "";
            String q4 = "order by a.kode_kota asc ";

            if (whereKota!=null) q3 = "where " + whereKota;

            Integer rc = (Integer) s.createSQLQuery(q0 + q2 + q3).uniqueResult();
            pgKota.setTotalSize(rc);
            
            System.out.println(q1 + q2 + q3 + q4);
            List<Object[]> l = s.createSQLQuery(q1 + q2 + q3 + q4).setFirstResult(offset).setMaxResults(limit).list();
            for (Object[] o : l) {
                TokoPOJO e = new TokoPOJO();
                e.setkode(Libs.nn(o[0]).trim());
                e.setkota(Libs.nn(o[1]).trim());
                e.setprop(Libs.nn(o[2]).trim());
                
                Listitem li = new Listitem();
                li.setValue(e);
                li.appendChild(new Listcell(e.getkode()));
                li.appendChild(new Listcell(e.getkota()));
                li.appendChild(new Listcell(e.getprop()));
                lbkota.appendChild(li);
            }
        } catch (Exception ex) {
            log.error("populateKota", ex);
        } finally {
            s.close();
        }
    }
    
    
    public void quickSearchKota() {
        Textbox tQuickSearchKota = (Textbox) getFellow("tQuickSearchKota");

        if (!tQuickSearchKota.getText().isEmpty()) {
        	whereKota = "a.nama_kota like '%" + tQuickSearchKota.getText() + "%' or "
        			+ " b.nama_prop like '%" + tQuickSearchKota.getText() + "%' or  "
        			+ " a.kode_kota like '%" + tQuickSearchKota.getText() + "%' ";
            populateKota(0, pgKota.getPageSize());
            pgKota.setActivePage(0);
        } else {
            refreshKota();
        }
    }
    
    
    public void refreshKota() {
        whereKota = null;
        populateKota(0, pgKota.getPageSize());
    }
    
    
    
    public void KotaSelected() {
        if (lbkota.getSelectedCount()==1) {
            TokoPOJO TokoPOJO = lbkota.getSelectedItem().getValue();
            String kode = Libs.nextnumber(TokoPOJO.getkode());
            kode_toko.setText(kode);
            kota_region.setText(TokoPOJO.getkota());
            kode_toko.close();
        }
    }
    

	public void save(){
		if (kode_toko.getText().isEmpty()){
			 Messagebox.show("Gagal Proses ", "Error", Messagebox.OK, Messagebox.ERROR);
		}else{

			boolean valid = false;
			String[] pecah1 = kode_toko.getText().split("-");
			for(int counter = 0; counter < pecah1.length; counter++){
	//			System.out.println(" " + pecah1[counter]);
				if (counter == 0){kdkota = pecah1[counter]; }
				if (counter == 1){kdtoko = pecah1[counter]; }
			}
			
	//		System.out.println("kota"+kdkota);
	//		System.out.println("toko"+kdtoko);
	    	 //System.out.println(Str.toUpperCase() );
	    	 Session s = Libs.sfDB.openSession();
	         try {
	        	 s.beginTransaction();
	             String qry = " insert into "+Libs.getDbName()+".dbo.toko "
	             		+ "(kode_kota,kode_toko,nama,toko,alamat1,alamat2,kota,kode_pos,telp1,telp2,hp1,hp2,kel_harga,status) "
	             		+ " values ("
	             		+ "'" +kdkota+"','" +kdtoko+"','"+nama.getText()+"','"+toko.getText()+"','"+alamat1.getText()+"' ,'"+alamat2.getText()+"', "
	             		+ "'"+kota.getText()+"','"+pos.getText()+"','"+telp1.getText()+"','"+telp2.getText()+"','"+hp1.getText()+"','"+hp2.getText()+"', "
	             		+ "'"+kel_harga.getText()+"','"+status.getText()+"' ); ";
	             		
	             System.out.println(qry);
	             s.createSQLQuery(qry).executeUpdate();
	             s.flush();
	             s.clear();
	             s.getTransaction().commit();
	             Messagebox.show("Insert status has been saved", "Information", Messagebox.OK, Messagebox.INFORMATION);
	             valid = true;
	             
	         } catch (Exception ex) {
	             log.error("save", ex);
	             Messagebox.show("Error status has been saved", "Information", Messagebox.OK, Messagebox.INFORMATION);
	         } finally {
	             if (s!=null && s.isOpen()) s.close();
	         }
	
	         
	
	         if (valid) {
	        	 refresh();
	         } else {
	             Messagebox.show("Gagal menyimpan ", "Error", Messagebox.OK, Messagebox.ERROR);
	         }
         
		}
       }
    
    
    private void clear(){
    	 kode_toko.setText("");
    	 kota_region.setText("");
    	
         kel_harga.setText("");
         nama.setText("");
         toko.setText("");
         alamat1.setText("");
         alamat2.setText("");
         kota.setText("");
       	 pos.setText("");
       	 telp1.setText("");
       	 telp2.setText("");
       	 hp1.setText("");
       	 hp2.setText("");
       	 status.setText("");
    }

    public void refresh() {
       clear();
    	   }
  
}
