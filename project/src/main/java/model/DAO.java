package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.sql.DataSource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class DAO {

	
        private final DataSource myDataSource;

	/**
	 * Construit le AO avec sa source de données
	 */
	public DAO() {
                this.myDataSource = DataSourceFactory.getDataSource();
	}
        
        public int virement(int id, int montant) throws SQLException{
            int ret = 0;
            String sql = "UPDATE CUSTOMER SET CREDIT_LIMIT = ? WHERE CUSTOMER_ID = ?";
            try (
                Connection connection = myDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1,(int)(soldeClient(id)+montant));
                    stmt.setInt(2, id);
                   ret = stmt.executeUpdate();                   
            }
            return ret;
        }
        
        public double soldeClient(int id) throws SQLException {
            double ret = 0;
            String sql = "SELECT CREDIT_LIMIT FROM CUSTOMER WHERE CUSTOMER_ID = ?";
              try (
                Connection connection = myDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    ResultSet rs = stmt.executeQuery();
                    while(rs.next()){
                       ret = rs.getDouble("CREDIT_LIMIT");                      
                    }
            }
            return ret;
        }
        
        public int updateSolde(int id, double price) throws SQLException{
            int ret = 0;
            String sql = "UPDATE CUSTOMER SET CREDIT_LIMIT = ? WHERE CUSTOMER_ID = ?";
		try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setInt(2, id);
                        stmt.setInt(1, (int)(this.soldeClient(id)- price));                      
			ret = stmt.executeUpdate();			
		}
                
                return ret;           
        }
        
        public int remboursement (int id, double price) throws SQLException{
            int ret = 0;
            
            return ret;
        }
        
        public boolean checkAchatSolde(int id, int product_id, int quantite) throws SQLException{
            boolean ret = false;
            double solde = this.soldeClient(id);
            if (solde >= setPrix(quantite, product_id, id)){
                ret = true;
                System.out.println("Le prix est de tadadadadadadadada" + setPrix(quantite, product_id, id));
            }
            return ret;
        }
        
        public List<DiscountCode> customerCodes(Customer c) throws SQLException{
            List<DiscountCode> result = new LinkedList<>();
            int id = Integer.parseInt(c.getPassword());
            String sql = "SELECT * FROM DISCOUNT_CODE"
                    + " INNER JOIN CUSTOMER"
                    + " ON DISCOUNT_CODE.DISCOUNT_CODE = CUSTOMER.DISCOUNT_CODE"
                    + " WHERE CUSTOMER_ID = ? ";
            try (
                Connection connection = myDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    ResultSet rs = stmt.executeQuery();
                    while(rs.next()){
                        String code = rs.getString("DISCOUNT_CODE");
                        Float rate = rs.getFloat("RATE");
                        DiscountCode dc = new DiscountCode(code, rate);
                        result.add(dc);                      
                        
                    }
            }
            
            return result;
        }
        
        
        public List<PurchaseOrder> customerCommandes(Customer c) throws SQLException{
            List<PurchaseOrder> result = new LinkedList<>();
            int id = Integer.parseInt(c.getPassword());
            String sql = "SELECT ORDER_NUM, CUSTOMER_ID, PRODUCT_ID, QUANTITY, SHIPPING_DATE, DESCRIPTION FROM PURCHASE_ORDER"
                    + " INNER JOIN CUSTOMER"
                    + " USING(CUSTOMER_ID)"
                    + " INNER JOIN PRODUCT"
                    + " USING (PRODUCT_ID)"
                    + " WHERE CUSTOMER_ID = ? ";
            try (
                Connection connection = myDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    ResultSet rs = stmt.executeQuery();
                    while(rs.next()){
                        int code = rs.getInt("ORDER_NUM");
                        int idCust = rs.getInt("CUSTOMER_ID");
                        int quantity = rs.getInt("QUANTITY");
                        int product = rs.getInt("PRODUCT_ID");
                        double cost = setPrix(quantity, product, idCust);
                        Date date = rs.getDate("SHIPPING_DATE");
                        String descritpion = rs.getString("DESCRIPTION");
                        PurchaseOrder po = new PurchaseOrder(code, idCust, quantity);
                        po.setDESCRIPTION(descritpion);
                        System.out.println("ici le prix : "+cost);
                        po.setCOST(cost);
                        po.setSHIPPING_DATE(date);
                        result.add(po);
                                                                    
                    }
            }
            
            return result;
        }
        
        

	/**
	 * Contenu de la table DISCOUNT_CODE
	 * @return Liste des discount codes
	 * @throws SQLException renvoyées par JDBC
	 */
	public List<DiscountCode> allCodes() throws SQLException {

		List<DiscountCode> result = new LinkedList<>();

		String sql = "SELECT * FROM DISCOUNT_CODE ORDER BY DISCOUNT_CODE";
		try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
                    
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String id = rs.getString("DISCOUNT_CODE");
				float rate = rs.getFloat("RATE");
				DiscountCode c = new DiscountCode(id, rate);
				result.add(c);
                                
			}
		}
		return result;
	}
        
        public double valueOfDiscountCode(int customer_id) throws SQLException {
            double ret = 0;
            String sql = "SELECT RATE FROM DISCOUNT_CODE"
                    +" INNER JOIN CUSTOMER"
                    +" USING (DISCOUNT_CODE)"
                    +" WHERE CUSTOMER_ID = ?";
            
            		try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setInt(1, customer_id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				
				double rate = rs.getDouble("RATE");
				ret = rate;				
			}
		}     
            System.out.println("ma valeur est de  -----------------------"+ ret);
            return ret;
        }

	/**
	 * Ajout d'un enregistrement dans la table DISCOUNT_CODE
	 * @param code le code (non null)
	 * @param rate le taux (positive or 0)
	 * @return 1 si succès, 0 sinon
	 * @throws SQLException renvoyées par JDBC
	 */
	public int addDiscountCode(String code, float rate) throws SQLException {
		int result = 0;
		String sql = "INSERT INTO DISCOUNT_CODE VALUES (?, ?)";
		try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, code);
			stmt.setFloat(2, rate);
			result = stmt.executeUpdate();
		}
		return result;
	}
        
        
        public int addCommande(int customerID, int quantity, int product_id) throws SQLException {
		int result = 0;
		String sql = "INSERT INTO PURCHASE_ORDER (ORDER_NUM, CUSTOMER_ID, PRODUCT_ID, QUANTITY, SHIPPING_DATE) VALUES (?, ?, ?, ?, ?)";
                if (checkAchatSolde(customerID, product_id, quantity) == true){
                    updateSolde(customerID, setPrix(quantity, product_id,  customerID));
                    try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(2, customerID);
			stmt.setInt(1, numeroCommande());
                        stmt.setInt(3, product_id);
                        stmt.setInt(4, quantity);
                       
                        stmt.setDate(5, java.sql.Date.valueOf(java.time.LocalDate.now()));
			result = stmt.executeUpdate();
                    }
                }
                
                else{
                    throw new SQLException("Vous n'avez pas assez d'argent pour acheter ce produit. Votre solde est de "+ soldeClient(customerID)+"€ et votre achat coûte : "+ setPrix(quantity, product_id, customerID)+"€");
                }
                
               
		
		return result;
	}
        
        public double setPrix(int quantite, int product_id, int customer_id) throws SQLException{
            double result = 0;
		String sql = "SELECT PURCHASE_COST FROM PRODUCT WHERE PRODUCT_ID = ?";
		  try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setInt(1, product_id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				
                                result = (rs.getDouble("PURCHASE_COST")*quantite)*((100-valueOfDiscountCode(customer_id))/100);             
			}
		}
		return result;
        }
        
        public int numeroCommande() throws SQLException{          
            List<Integer> result = new ArrayList<>();

		String sql = "SELECT ORDER_NUM FROM PURCHASE_ORDER";
		try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {                 
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("ORDER_NUM");
                                result.add(id);				                               
			}                 
		}
                int numAlea = (int) (Math.random()*10000);
                while(result.contains(numAlea)){
                    numAlea = (int) (Math.random()*10000);
                }
                return numAlea;		
        }
        
        public ArrayList<String> allProduct() throws SQLException {
		ArrayList<String> result = new ArrayList<>();
		String sql = "SELECT DESCRIPTION FROM PRODUCT WHERE AVAILABLE = TRUE";
		try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
                   
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String des = rs.getString("DESCRIPTION");				
				result.add(des);                               
			}
		}
		return result;
	}
        
        public int numProduct(String des) throws SQLException {
            int result = 0;
            
            String sql = "SELECT * FROM PRODUCT WHERE DESCRIPTION LIKE ?";
              try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setString(1, des);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				
                                result = rs.getInt("PRODUCT_ID");             
			}
		}
              System.out.println("Le produit est : "+ result+"----------------------");
            return result;
        }
        
        
        
        public int deleteCommande(int order_num) throws SQLException {
		int result = 0;
		String sql = "DELETE FROM PURCHASE_ORDER WHERE ORDER_NUM = ?";
		try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, order_num);
			result = stmt.executeUpdate();
		}
		return result;
	}
        
        public int editCommande(int order_num, int quantity, int customerID) throws SQLException{
            int result = 0;
		String sql = "UPDATE PURCHASE_ORDER SET QUANTITY = ? WHERE ORDER_NUM = ?";
		try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setInt(1, quantity);
                        
			stmt.setInt(2, order_num);
			result = stmt.executeUpdate();
		}
		return result;
        }
        
        public int getProductIdByOrderNum(int order_num)throws SQLException {
            int result = 0;
            String sql = "SELECT PRODUCT_ID FROM PRODUCT "
                    +"INNER JOIN PURCHASE_ORDER "
                    +"USING (PRODUCT_ID)"
                    +"WHERE ORDER_NUM = ?";
            try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setInt(1, order_num);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				result = rs.getInt("PRODUCT_ID");				
				                               
			}
		}
            return result;
        }
        
        
        /* public List<PurchaseOrder> selectCustomerCommande(int customerID)throws SQLException {
            List<PurchaseOrder> result = new LinkedList<>();
            String sql = "SELECT * FROM PURCAHSE_ORDER WHERE CUSTOMER_ID = ?";
            try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
                      stmt.setInt(1, customerID);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("ORDER_NUM");
				int quantite = rs.getInt("QUANTITE");
				PurchaseOrder c = new PurchaseOrder(id, customerID, quantite);
				result.add(c);                               
			}
		}
            return result;
        }

		
	/**
	 * Supprime un enregistrement dans la table DISCOUNT_CODE
	 * @param code la clé de l'enregistrement à supprimer
	 * @return le nombre d'enregistrements supprimés (1 ou 0)
	 * @throws java.sql.SQLException renvoyées par JDBC
	 **/
	public int deleteDiscountCode(String code) throws SQLException {
		int result = 0;
		String sql = "DELETE FROM DISCOUNT_CODE WHERE DISCOUNT_CODE = ?";
		try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, code);
			result = stmt.executeUpdate();
		}
		return result;
	}
        
        public Customer findCustomerID(String email) throws SQLException {
            
            // On va créer un nouveau Customer pour pouvoir récuperer ses informations
            Customer c = new Customer();
            // on récupère dans la bdd toutes les infos du customer qui possède le mail qui nous intéresse
            String sql = "SELECT * FROM CUSTOMER WHERE EMAIL = ?";
            // on va maintenant se connecter à la BDD
            try (Connection connection = myDataSource.getConnection(); 
                    // on fait notre requete
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
                // on précise que l'attribut à remplacer par le ? est email
                        stmt.setString(1, email);
                        // on prépare notre resultat
			ResultSet rs = stmt.executeQuery();
                        System.out.println(rs);
                        if (!rs.next() ) {
                            System.out.println("no data");
                            c.setEmail("nodata");
                            c.setPassword("nodata");
                            c.setName("nodata");
                            
                            return c;
                            } 
                        else {
                            do {
                             System.out.println(String.valueOf(rs.getInt("CUSTOMER_ID")));
                            // maintenant on va ajouter les propriétés trouvées dans la bdd à notre customer
				c.setEmail(email); 
				c.setPassword(String.valueOf(rs.getInt("CUSTOMER_ID")));
                                c.setName(rs.getString("NAME"));
                                c.setCity(rs.getString("CITY"));
                                c.setAddressline1(rs.getString("ADDRESSLINE1"));
                                c.setPhone(rs.getString("PHONE"));
                                c.setCredit(rs.getInt("CREDIT_LIMIT"));
                            } while (rs.next());
                            }
			
		}
            
            
            // et pour finir on return le customer pour pouvoir l'utiliser dans checkLogin par exemple
            return c;
        }
        
        public String infoCustomer() throws SQLException{
            String ret = "";
            String sql = "SELECT EMAIL, CUSTOMER_ID FROM CUSTOMER";
            try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String mail = rs.getString("EMAIL");
                                int id = rs.getInt("CUSTOMER_ID");
                                ret += " "+mail+" id : "+ id;
				
                                
			}
		}
            
            return ret;
        }
        
            public String descProduct(int product_id) throws SQLException{
            String ret = "";
            String sql = "SELECT DESCRIPTION FROM PRODUCT WHERE PRODUCT_ID = ?";
            try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setInt(1, product_id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = rs.getString("DESCRIPTION");
                            
			}
		}
            
            return ret;
        }
        
        // Partie Admin
            
        public double getCost(int product_id) throws SQLException{
            double result = 0;
		String sql = "SELECT PURCHASE_COST FROM PRODUCT WHERE PRODUCT_ID = ?";
		  try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setInt(1, product_id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				
                                result = rs.getDouble("PURCHASE_COST");             
			}
		}
		return result;
        }
        
        public String nameCustomer(int id) throws SQLException{
            String ret = "";
            String sql = "SELECT NAME FROM CUSTOMER WHERE CUSTOMER_ID = ?";
            try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = rs.getString("NAME");
                                 
			}
		}
            
            return ret;
        }
        
        public Map<String,Double> chiffreAffaireByProduct(String deb, String fin) throws SQLException {
            Map<String,Double> ret = new HashMap<>();
            String sql ="SELECT PRODUCT_ID, SUM(QUANTITY) AS SALES FROM PURCHASE_ORDER"                  
                    + " WHERE SHIPPING_DATE BETWEEN ? AND ?"
                    + " GROUP BY PRODUCT_ID";                        
            try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
                       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                       Date parsed1 = null;
                       Date parsed2 = null;
                    try {
                        parsed1 = sdf.parse(deb);
                    } catch (ParseException e1) {
                           // TODO Auto-generated catch block

                    }
                    try {
                        parsed2 = sdf.parse(fin);
                    } catch (ParseException e2) {
                        // TODO Auto-generated catch block
                        e2.printStackTrace();
                    }
                    java.sql.Date data1 = new java.sql.Date(parsed1.getTime());
                    java.sql.Date data2 = new java.sql.Date(parsed2.getTime());
                    System.out.println("Le Ca est de ------------------------------------------------------------------ "+ data1);
                    System.out.println("Le Ca est de ------------------------------------------------------------------ "+ data2);
                        stmt.setDate(1,  data1);
                        stmt.setDate(2,  data2);
                        
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
                            String product = descProduct(rs.getInt("PRODUCT_ID"));
                            double price = rs.getDouble("SALES")*getCost(rs.getInt("PRODUCT_ID"));
                            ret.put(product, price);
                            System.out.println("Le Ca est de ------------------------------------------------------------------ "+ price);
			}
		}
            
            return ret;
        }
        
        
         public Map<String,Double> chiffreAffaireByCustomer(String deb, String fin) throws SQLException {
            Map<String,Double> ret = new HashMap<>();
            String sql ="SELECT CUSTOMER_ID, PRODUCT_ID, QUANTITY FROM PURCHASE_ORDER"                  
                    + " WHERE SHIPPING_DATE BETWEEN ? AND ?";
                                           
            try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
                       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                       Date parsed1 = null;
                       Date parsed2 = null;
                    try {
                        parsed1 = sdf.parse(deb);
                    } catch (ParseException e1) {
                           // TODO Auto-generated catch block

                    }
                    try {
                        parsed2 = sdf.parse(fin);
                    } catch (ParseException e2) {
                        // TODO Auto-generated catch block
                        e2.printStackTrace();
                    }
                    java.sql.Date data1 = new java.sql.Date(parsed1.getTime());
                    java.sql.Date data2 = new java.sql.Date(parsed2.getTime());
                    
                        stmt.setDate(1,  data1);
                        stmt.setDate(2,  data2);
                        
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
                            String customer = nameCustomer(rs.getInt("CUSTOMER_ID"));
                            double price = rs.getDouble("QUANTITY")*getCost(rs.getInt("PRODUCT_ID"));
                            
                             if (ret.containsKey(customer)){
                                ret.put(customer, ret.get(customer) + price);
                                System.out.println("nouveau ca  "+ customer +" est de "+ret.get(customer));
                            }
                            else{
                                ret.put(customer, price);
                                System.out.println("ca = "+ customer +" est de " + price);
                            }
			}
		}
            
            return ret;
        }
        public Map<String,Double> chiffreAffaireByState(String deb, String fin) throws SQLException {
            Map<String,Double> ret = new HashMap<>();
            String sql ="SELECT PRODUCT_ID, CUSTOMER_ID, QUANTITY, STATE FROM PURCHASE_ORDER"
                    + " INNER JOIN CUSTOMER"
                    + " USING (CUSTOMER_ID)"                  
                    + " WHERE SHIPPING_DATE BETWEEN ? AND ?";
                                          
            try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
                       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                       Date parsed1 = null;
                       Date parsed2 = null;
                    try {
                        parsed1 = sdf.parse(deb);
                    } catch (ParseException e1) {
                           // TODO Auto-generated catch block

                    }
                    try {
                        parsed2 = sdf.parse(fin);
                    } catch (ParseException e2) {
                        // TODO Auto-generated catch block
                        e2.printStackTrace();
                    }
                    java.sql.Date data1 = new java.sql.Date(parsed1.getTime());
                    java.sql.Date data2 = new java.sql.Date(parsed2.getTime());
                    
                        stmt.setDate(1,  data1);
                        stmt.setDate(2,  data2);
                        
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
                            String state = rs.getString("STATE");
                            double price = rs.getDouble("QUANTITY")*getCost(rs.getInt("PRODUCT_ID"));
                            if (ret.containsKey(state)){
                                ret.put(state, ret.get(state) + price);
                                System.out.println("nouveau ca  "+ state +" est de "+ret.get(state));
                            }
                            else{
                                ret.put(state, price);
                                System.out.println("ca = "+ state +" est de " + price);
                            }
                            
                            
			}
		}
            
            return ret;
        }
        
        
        
       

}
