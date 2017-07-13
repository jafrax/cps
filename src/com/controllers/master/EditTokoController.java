package com.controllers.master;

import com.pojo.ClientPOJO;
import com.pojo.MemberPOJO;
import com.pojo.ProductPOJO;
import com.tools.Libs;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;
import org.zkoss.zul.event.PagingEvent;

import java.text.SimpleDateFormat;
import java.util.List;

@SuppressWarnings("serial")
public class EditTokoController extends Window {

    private Logger log = LoggerFactory.getLogger(InputTokoController.class);
    private String kdtoko;
    private String kdkota;
    private Listbox lb;
    private Object[] obj;
    private Textbox kode_toko;
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
      
    private String Id ;
    private Combobox kode_sales;
    private Textbox plafon;
    
    public void onCreate() {
        initComponents();
        Id = (String) getAttribute("Id");
        System.out.println(Id);
		        String[] pecah1 = Id.split("-");
				for(int counter = 0; counter < pecah1.length; counter++){
					if (counter == 0){kdkota = pecah1[counter]; }
					if (counter == 1){kdtoko = pecah1[counter]; }
				}
		populate(kdkota,kdtoko);
		populateplafon(kdkota,kdtoko);
    }

    private void initComponents() {
    	 lb = (Listbox) getFellow("lb");
    	
         kode_toko = (Textbox) getFellow("kode_toko");
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
       	 
       	 kode_sales = (Combobox) getFellow("kode_sales");
       	 plafon = (Textbox) getFellow("plafon");
       
       	 kel_harga.appendItem("");
       	 kel_harga.appendItem("E");
       	 kel_harga.appendItem("A");
       	 kel_harga.appendItem("B");
       	 kel_harga.setSelectedIndex(0); 
       	 
       	 
       	Libs.getSales(kode_sales);
        
     }
    
    private void populate(String kdkota,String kdtoko){
        Session s = Libs.sfDB.openSession();
        try {
        	 String query = "select kode_kota,kode_toko,nama,toko,alamat1,alamat2,kota,kode_pos,telp1,telp2,hp1,hp2,kel_harga,status "
        	 		+ "from "+Libs.getDbName()+".dbo.toko where kode_kota = '"+kdkota+"' and kode_toko='"+kdtoko+"' ";
             System.out.println(query);
             List<Object[]> l = s.createSQLQuery(query).list();
             for (Object[] o : l) {
             	
            	 kode_toko.setText(kdkota +"-"+kdtoko);
            	 String propinsi = Libs.propinsi(kdkota);
            	 kota_region.setText(propinsi);
            	
                
                 nama.setText(Libs.nn(o[2]).trim());
                 toko.setText(Libs.nn(o[3]).trim());
                 alamat1.setText(Libs.nn(o[4]).trim());
                 alamat2.setText(Libs.nn(o[5]).trim());
                 kota.setText(Libs.nn(o[6]).trim());
               	 pos.setText(Libs.nn(o[7]).trim());
               	 telp1.setText(Libs.nn(o[8]).trim());
               	 telp2.setText(Libs.nn(o[9]).trim());
               	 hp1.setText(Libs.nn(o[10]).trim());
               	 hp2.setText(Libs.nn(o[11]).trim());
               	 kel_harga.setText(Libs.nn(o[12]).trim());
               	 status.setText(Libs.nn(o[13]).trim());
             	
            }
        } catch (Exception ex) {
            log.error("populate", ex);
        } finally {
            s.close();
        }
    
    }
    
    private void populateplafon(String kdkota,String kdtoko) {
        lb.getItems().clear();
         Session s = Libs.sfDB.openSession();
        try {
            String q1 = "select  a.kode_kota, a.kode_toko, a.kode_sales, b.nama, a.plafon";
            String q2 = " from "+Libs.getDbName()+".dbo.plafontoko a inner join "+Libs.getDbName()+".dbo.sales b ON a.kode_sales=b.kode "
            		+ "where kode_kota = '"+kdkota+"' and kode_toko='"+kdtoko+"' ";
            
            System.out.println( q1 + q2 );
            List<Object[]> l = s.createSQLQuery(q1 + q2 ).list();
            for (Object[] o : l) {

                Listitem li = new Listitem();
                li.setValue(o);
                li.appendChild(new Listcell(Libs.nn(o[2]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[3]).trim()));
                li.appendChild(new Listcell(Libs.nn(o[4]).trim()));
                lb.appendChild(li);
            }
        } catch (Exception ex) {
            log.error("populate", ex);
        } finally {
            s.close();
        }
    }
    
    
    public void SalesSelected(){
    	 if (lb.getSelectedCount()==1) {
         	((Toolbarbutton) getFellow("tbnDelete")).setDisabled(false);
         }
    }
    

    
    public void save(){
    	 boolean valid = false;
    	 String[] pecah1 = kode_toko.getText().split("-");
			for(int counter = 0; counter < pecah1.length; counter++){
				if (counter == 0){kdkota = pecah1[counter]; }
				if (counter == 1){kdtoko = pecah1[counter]; }
			}
			
    	 Session s = Libs.sfDB.openSession();
         try {
        	 s.beginTransaction();
             String qry = " update "+Libs.getDbName()+".dbo.toko set  "
             		+ " nama ='"+nama.getText()+"', toko = '"+toko.getText()+"',alamat1 = '"+alamat1.getText()+"' ,alamat2 = '"+alamat2.getText()+"', "
             		+ "kota = '"+kota.getText()+"',kode_pos = '"+pos.getText()+"',telp1 = '"+telp1.getText()+"',telp2 = '"+telp2.getText()+"', "
             				+ " hp1 = '"+hp1.getText()+"',hp2 = '"+hp2.getText()+"', kel_harga = '"+kel_harga.getText()+"',status = '"+status.getText()+"' "
             						+ " where kode_kota= '"+kdkota+"' and kode_toko = '"+kdtoko+"'   ";
             		
             System.out.println(qry);
             s.createSQLQuery(qry).executeUpdate();
             s.flush();
             s.clear();
             s.getTransaction().commit();
             
             Messagebox.show("Update status has been saved", "Information", Messagebox.OK, Messagebox.INFORMATION);
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
    
    
    public void refresh(){
    	populate(kdkota,kdtoko);
		populateplafon(kdkota,kdtoko);
    	clear();
    }

    public void delete(){
    	 if (Messagebox.show("Do you want to remove this Client ?", "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, Messagebox.CANCEL)==Messagebox.OK) {
    	       	obj = (lb.getSelectedItem().getValue());
    	       
    	       	Session s = Libs.sfDB.openSession();
    	       	try{
    	       	s.beginTransaction();    	
    	       	String qry = "delete "+Libs.getDbName()+".dbo.plafontoko  where  "
    	                +" kode_kota ='"+kdkota + "' and kode_toko = '"+ kdtoko + "' and kode_sales = '"+Libs.nn(obj[2])+"' ";
    	    	System.out.println(qry);
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
    
    
    public void add(){
    		saveplafon(kdkota,kdtoko);
    		clear();
    		
    }
    
   private void clear(){
	   kode_sales.setText("");
		plafon.setText("");
   }
    
    private void saveplafon(String kdkota,String kdtoko){
    	String val1 = ((Textbox) getFellow("kode_sales")).getText();
    	if (!val1.isEmpty() ) {
	    	String sc = kode_sales.getSelectedItem().getLabel();
	        sc = sc.substring(sc.indexOf("(")+1, sc.indexOf(")"));
	        
			        String nominal ;
			        if (plafon.getValue() == ""){
			        	nominal = "0";
			        }else{
			        	nominal = plafon.getValue();
			        }
	        
			        Session s = Libs.sfDB.openSession();
	         try {
	        	 s.beginTransaction();
	             String qry = " INSERT INTO "+Libs.getDbName()+".dbo.plafontoko (kode_kota,kode_toko,kode_sales,plafon)"
	             		+ "VALUES ('"+kdkota+"', '"+kdtoko+"','"+sc+"', '"+ nominal +"' )";
	             		
	             System.out.println(qry);
	             s.createSQLQuery(qry).executeUpdate();
	             s.flush();
	             s.clear();
	             s.getTransaction().commit();
	             Messagebox.show("Save status has been saved", "Information", Messagebox.OK, Messagebox.INFORMATION);
	             
	             populateplafon(kdkota,kdtoko);
	             
	         } catch (Exception ex) {
	             log.error("save", ex);
	             Messagebox.show("Error status has been saved", "Information", Messagebox.OK, Messagebox.INFORMATION);
	         } finally {
	             if (s!=null && s.isOpen()) s.close();
	         }
    	}else{
  		 Messagebox.show("Isi dulu !!!", "Information", Messagebox.OK, Messagebox.INFORMATION);
  		}
    }
    
    
    
}
