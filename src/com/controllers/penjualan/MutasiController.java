package com.controllers.penjualan;

import com.pojo.ProductPOJO;
import com.tools.Libs;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.*;
import org.zkoss.zul.event.PagingEvent;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;


@SuppressWarnings("serial")
public class MutasiController extends Window {

    private Logger log = LoggerFactory.getLogger(MutasiController.class);
    private Object[] obj;
    private Bandbox bbProduct;
    private Listbox lb;
    private Listbox lbProducts;
    private Paging pgProducts;
    private String CariProducts;
    private Paging pg;
    private Datebox tgl;
    private Textbox nomor;
    private Textbox keterangan;
    private Textbox jumlah;
    private Combobox dari ;
    private Combobox ke ;
    private String mutasiId ;
    private int stok;

 
    public void onCreate() {
        initComponents();
        mutasiId = (String) getAttribute("no");
//        System.out.println(mutasiId);
        if (mutasiId != null && !mutasiId.isEmpty()){
//        	System.out.println("edit ya");
        	populateEdit(mutasiId);
        }

    }

    private void initComponents() {
    	tgl = (Datebox) getFellow("tgl");
        nomor = (Textbox) getFellow("nomor");
        keterangan = (Textbox) getFellow("keterangan");
        lbProducts = (Listbox) getFellow("lbProducts");
        lb = (Listbox) getFellow("lb");
        pg = (Paging) getFellow("pg");
        pgProducts = (Paging) getFellow("pgProducts");
        dari = (Combobox) getFellow("dari");
        ke = (Combobox) getFellow("ke");
        jumlah = (Textbox) getFellow("jumlah");
        bbProduct = (Bandbox) getFellow("bbProduct");
        
        pg.addEventListener("onPaging", new EventListener() {
     	   @Override
     	   public void onEvent(Event event) throws Exception {
     	       PagingEvent evt = (PagingEvent) event;
     	       populate(evt.getActivePage() * pg.getPageSize(), pg.getPageSize());
     	   }
     	   });

        
        pgProducts.addEventListener("onPaging", new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
                PagingEvent evt = (PagingEvent) event;
                populateProducts(evt.getActivePage()*pgProducts.getPageSize(), pgProducts.getPageSize());
            }
        });
        
    }

    private void populateEdit(String no){
    	 Session s = Libs.sfDB.openSession();
    	 
    	 ((Datebox) getFellow("tgl")).setDisabled(true);
    	 ((Textbox) getFellow("nomor")).setDisabled(true);
         ((Textbox) getFellow("keterangan")).setDisabled(true);
    	 
 	 	try{
 	 		
 	 		String query = "select  tgl_mutasi,	keterangan from  " +Libs.getDbName()+".dbo.mutasi where no_mutasi= '"+no+"' ";
 	 		List<Object[]> l = s.createSQLQuery(query).list();
   	    	for(Object[] o : l){
   	    		tgl.setText(Libs.nn(o[0]));
   	    		keterangan.setText(Libs.nn(o[1]));
   	    		nomor.setText(no);
   	    	}
   	    	
	 		
	 	} catch (Exception ex) {
          log.error("tgl", ex);
      } finally {
          s.close();
      }
 	 	
 	   ((Toolbarbutton) getFellow("clear")).setDisabled(true);
 	   populateProducts(0, pgProducts.getPageSize());
 	  populate(0, pg.getPageSize());
    	
    }
 
 public void tanggalSelected(){
	 ((Toolbarbutton) getFellow("save")).setDisabled(false);
	 Calendar cal = Calendar.getInstance();
	 SimpleDateFormat fm = new SimpleDateFormat("yy");
	 String tahun = fm.format(cal.getTime());
//	 int year = cal.get(Calendar.YEAR);
	
    
	 Session s = Libs.sfDB.openSession();
	 	try{
	 		
	 		String query = "select isnull(max(right(no_mutasi,5)),0) as nomor , 0 from  " +Libs.getDbName()+".dbo.mutasi  ";
//	 		System.out.println(query);
	 		List<Object[]> l = s.createSQLQuery(query).list();
  	    	for(Object[] o : l){
  	    		String rs = Libs.nn(o[0]).trim();
  	                String AN = "" + (Integer.parseInt(rs) + 1);
  	                String Nol = "";

  	                if(AN.length()==1)
  	                {Nol = "0000";}
  	                else if(AN.length()==2)
  	                {Nol = "000";}
  	                else if(AN.length()==3)
  	                {Nol = "00";}
  	                else if(AN.length()==4)
	                {Nol = "0";}
	                else if(AN.length()==5)
	                {Nol = "";}
	               
  	                
  	              nomor.setText("MB-"+tahun+ Nol + AN );
  	    	}
	 		
	 	} catch (Exception ex) {
          log.error("tgl", ex);
      } finally {
          s.close();
      }
	 
 }

 
 public void save() {
 	((Toolbarbutton) getFellow("save")).setDisabled(true);
     
 	 Session s = Libs.sfDB.openSession();
   try {
       s.beginTransaction();

       String qry = "insert into  " +Libs.getDbName()+".dbo.mutasi "
       		+ "(no_mutasi,tgl_mutasi,keterangan) values  "
            + "( '"+ nomor.getText() + "' , '"+tgl.getText()+"' , '"+keterangan.getText()+"') ";
//       System.out.println(qry);
       s.createSQLQuery(qry).executeUpdate();
       s.flush();
       s.clear();
       s.getTransaction().commit();
       
       Messagebox.show("Mutasi Save", "Information", Messagebox.OK, Messagebox.INFORMATION);
   } catch (Exception ex) {
       log.error("saveProcess", ex);
   } finally {
       if (s!=null && s.isOpen()) s.close();
   }

   ((Toolbarbutton) getFellow("clear")).setDisabled(false);
   populateProducts(0, pgProducts.getPageSize());
   

 }
 
 public void clear() {
	if (Messagebox.show("Do you want to clear the list?", "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION)==Messagebox.OK) {
	  
		Session s = Libs.sfDB.openSession();
		   try {
		       s.beginTransaction();

		       String qry1 = "delete " +Libs.getDbName()+".dbo.mutasi  where no_mutasi = '"+nomor.getText()+"' ";
		       String qry2 = "delete " +Libs.getDbName()+".dbo.mutasi_rin  where no_mutasi = '"+nomor.getText()+"' ";
		       
		       s.createSQLQuery(qry1 + qry2).executeUpdate();
		       s.flush();
		       s.clear();
		       s.getTransaction().commit();
		       
		       Messagebox.show("Mutasi Clear", "Information", Messagebox.OK, Messagebox.INFORMATION);
	}catch (Exception ex) {
	       log.error("clear", ex);
	   } finally {
	       if (s!=null && s.isOpen()) s.close();
	   }
	}
	
	 ((Toolbarbutton) getFellow("clear")).setDisabled(true);
	 tgl.setText("");
	 nomor.setText("");
	 keterangan.setText("");
	 lbProducts.getItems().clear();
	 lb.getItems().clear();
	 dari.setText("");
     ke.setText("");
     jumlah.setText("");
     bbProduct.setText("");
	 
 }
 

    private void populateProducts(int offset, int limit) {
        lbProducts.getItems().clear();
        Session s = Libs.sfDB.openSession();
        try {
        	String q0 = "select count(*) ";
            String q1 = "select kode,namabrg,merkbrg,tipebrg ";
            String q2 = "from "+Libs.getDbName()+".dbo.barang ";
            String q3 = "";
            String q4 = "order by kode  asc ";

 
            if (CariProducts!=null) {
                q3 += " where (" + CariProducts + ") ";
            }

            Integer rc = (Integer) s.createSQLQuery(q0 + q2 + q3).uniqueResult();
            pgProducts.setTotalSize(rc);
            
//            System.out.println(q1 + q2 + q3 + q4);
            List<Object[]> l = s.createSQLQuery(q1 + q2 + q3 + q4).setFirstResult(offset).setMaxResults(limit).list();
            for (Object[] o : l) {

            	Listitem li = new Listitem();
                li.setValue(o);
                li.appendChild(new Listcell(Libs.nn(o[0])));
                li.appendChild(new Listcell(Libs.nn(o[1])));
                li.appendChild(new Listcell(Libs.nn(o[2])));
                li.appendChild(new Listcell(Libs.nn(o[3])));
                

                lbProducts.appendChild(li);
            }
        } catch (Exception ex) {
            log.error("populateProducts", ex);
        } finally {
            s.close();
        }
    }



    public void productSelected() {
        if (lbProducts.getSelectedCount()>0) {
             obj = lbProducts.getSelectedItem().getValue();
             String kode = Libs.nn(obj[0]) ;
            bbProduct.setText(kode);
            bbProduct.close();
            ((Combobox) getFellow("dari")).setDisabled(false);
            ((Combobox) getFellow("ke")).setDisabled(false);
            Libs.getGudang(dari);
            Libs.getGudang(ke);
           
            ((Toolbarbutton) getFellow("btnadd")).setDisabled(false);
            populate (0, pg.getPageSize());
        }
    }


    public void quickSearchProducts() {
        Textbox tQuickSearchProducts = (Textbox) getFellow("tQuickSearchProducts");

        if (!tQuickSearchProducts.getText().isEmpty()) {
        	CariProducts = "namabrg like '%" + tQuickSearchProducts.getText() + "%' or "
                    + "kode like '%" + tQuickSearchProducts.getText() + "%' ";
            populateProducts(0, pgProducts.getPageSize());
        } else {
            refreshProducts();
        }
    }

    public void refreshProducts() {
    	CariProducts = null;
        populateProducts(0, pgProducts.getPageSize());
    }

    
    public void  add(){
    	
    	stok = Libs.cekstoklok(bbProduct.getText(),dari.getText());
    	int jml = Integer.parseInt(jumlah.getText());
    	if (stok < jml){
    		Messagebox.show("stok = "+stok);
    	}else{
    		
   	 Session s = Libs.sfDB.openSession();
     try {
         s.beginTransaction();

         String qry = "insert into  " +Libs.getDbName()+".dbo.mutasi_rin "
         		+ "(no_mutasi,kode_brg,jumlah, lok_dr,lok_ke) values  "
              + "( '"+ nomor.getText() + "' , '"+bbProduct.getText()+"', '"+jumlah.getText()+"' , '"+dari.getText()+"', '"+ke.getText()+"') ";
         String updateA = "update " +Libs.getDbName()+".dbo.stokperlok set jumlah = (jumlah - '"+jumlah.getText()+"' ) "
         		+ " where kd_barang = '"+bbProduct.getText()+"' and kd_lokasi = '"+dari.getText()+"' ";
         String updateB = "update " +Libs.getDbName()+".dbo.stokperlok set jumlah = (jumlah + '"+jumlah.getText()+"' ) "
          		+ " where kd_barang = '"+bbProduct.getText()+"' and kd_lokasi = '"+ke.getText()+"' ";
          
         s.createSQLQuery(qry+ updateA + updateB).executeUpdate();
         s.flush();
         s.clear();
         s.getTransaction().commit();
         populate(0, pgProducts.getPageSize());
         dari.setText("");
         ke.setText("");
         jumlah.setText("");
         bbProduct.setText("");
         
     } catch (Exception ex) {
         log.error("saveProcess", ex);
     } finally {
         if (s!=null && s.isOpen()) s.close();
     }
     
    }
    }


    
    private void populate(int offset, int limit) {
        lb.getItems().clear();
        Session s = Libs.sfDB.openSession();
        try {
        	String q0 = "select count(*) ";
            String q1 = "select b.lok_dr,b.kode_brg,namabrg,b.jumlah,a.satuan,b.lok_ke ";
            String q2 = "from "+Libs.getDbName()+".dbo.barang a inner join "+Libs.getDbName()+".dbo.mutasi_rin b ON a.kode=b.kode_brg  ";
            String q3 = " where b.no_mutasi = '"+nomor.getText()+"'";
            String q4 = "order by kode  asc ";

//            System.out.println(q1 + q2 + q3 + q4);
            Integer rc = (Integer) s.createSQLQuery(q0 + q2 + q3).uniqueResult();
            pg.setTotalSize(rc);
            
            List<Object[]> l = s.createSQLQuery(q1 + q2 + q3 + q4).setFirstResult(offset).setMaxResults(limit).list();
            for (Object[] o : l) {

            	Listitem li = new Listitem();
                li.setValue(o);
                li.appendChild(new Listcell(Libs.nn(o[0])));
                li.appendChild(new Listcell(Libs.nn(o[1])));
                li.appendChild(new Listcell(Libs.nn(o[2])));
                li.appendChild(new Listcell(Libs.nn(o[3])));
                li.appendChild(new Listcell(Libs.nn(o[4])));
                li.appendChild(new Listcell(Libs.nn(o[5])));
                lb.appendChild(li);
            }
        } catch (Exception ex) {
            log.error("populateProducts", ex);
        } finally {
            s.close();
        }
    }

    public void MutasiSelected(){
    	((Toolbarbutton) getFellow("tbnDelete")).setDisabled(false);
    }

    
    
    public void openPenjualan(String viewName) {
        Window w = (Window) Executions.createComponents("views/penjualan/" + viewName + ".zul", this, null);
        w.doOverlapped();
    }
    
    
public void  delete(){
    	
	 if (Messagebox.show("Do you want to remove this Client ?", "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, Messagebox.CANCEL)==Messagebox.OK) {
	       	obj = (lb.getSelectedItem().getValue());
//	       	b.lok_dr,b.kode_brg,namabrg,b.jumlah,a.satuan,b.lok_ke 
	       	String kodebarang = Libs.nn(obj[1]);
	       	String dari = Libs.nn(obj[0]);
	       	String ke = Libs.nn(obj[5]);
	       	String jumlah = Libs.nn(obj[3]);
	       	String nomutasi = mutasiId;
	       	

	    	stok = Libs.cekstoklok(kodebarang,ke);
	    	int jml = Integer.parseInt( Libs.nn(obj[3]));
	    	if (stok < jml){
	    		Messagebox.show("stok kurang !!!");
	    	}else{
	       	
		   	 Session s = Libs.sfDB.openSession();
		     try {
		         s.beginTransaction();
		
		         String qry = "delete  " +Libs.getDbName()+".dbo.mutasi_rin  "
		         		+ "where no_mutasi= '"+ nomutasi +"' and kode_brg = '"+ kodebarang +"' "
		         		+ "and lok_dr = '"+dari+"'  and lok_ke = '"+ke+"' ";
		         
		         String updateA = "update " +Libs.getDbName()+".dbo.stokperlok set jumlah = (jumlah + '"+jumlah+"' ) "
		         		+ " where kd_barang = '"+kodebarang+"' and kd_lokasi = '"+dari+"' ";
		         String updateB = "update " +Libs.getDbName()+".dbo.stokperlok set jumlah = (jumlah - '"+jumlah+"' ) "
		          		+ " where kd_barang = '"+kodebarang+"' and kd_lokasi = '"+ke+"' ";
		          
//		         System.out.println(qry+ updateA + updateB);
		         s.createSQLQuery(qry+ updateA + updateB).executeUpdate();
		         s.flush();
		         s.clear();
		         s.getTransaction().commit();
		        
		         populate (0, pg.getPageSize());
         
		     } catch (Exception ex) {
		         log.error("delete", ex);
		     } finally {
		         if (s!=null && s.isOpen()) s.close();
		     }
     
	    	}
	 }
    }
    
    
}
