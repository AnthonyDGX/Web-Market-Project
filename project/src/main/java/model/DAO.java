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



public class DAO {

	
        private final DataSource myDataSource;

	/**
	 * Construit le AO avec sa source de données
	 */
	public DAO() {
                this.myDataSource = DataSourceFactory.getDataSource();
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
            String sql = "SELECT ORDER_NUM, CUSTOMER_ID, PRODUCT_ID, QUANTITY, SHIPPING_COST, SHIPPING_DATE, DESCRIPTION FROM PURCHASE_ORDER"
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
                        double cost = rs.getDouble("SHIPPING_COST");
                        Date date = rs.getDate("SHIPPING_DATE");
                        String descritpion = rs.getString("DESCRIPTION");
                        PurchaseOrder po = new PurchaseOrder(code, idCust, quantity);
                        po.setDESCRIPTION(descritpion);
                        System.out.println("ici le prix : "+cost);
                        po.setSHIPPING_COST(cost);
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
		String sql = "INSERT INTO PURCHASE_ORDER (ORDER_NUM, CUSTOMER_ID, PRODUCT_ID, QUANTITY, SHIPPING_COST, SHIPPING_DATE) VALUES (?, ?, ?, ?, ?, ?)";
		try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(2, customerID);
			stmt.setInt(1, numeroCommande());
                        stmt.setInt(3, product_id);
                        stmt.setInt(4, quantity);
                        stmt.setDouble(5, setPrix(quantity, product_id, customerID));
                        stmt.setDate(6, java.sql.Date.valueOf(java.time.LocalDate.now()));
			result = stmt.executeUpdate();
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
				
                                result = (rs.getInt("PURCHASE_COST")*quantite)*((100-valueOfDiscountCode(customer_id))/100);             
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
		String sql = "SELECT DESCRIPTION FROM PRODUCT";
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
		String sql = "UPDATE PURCHASE_ORDER SET QUANTITY = ?, SHIPPING_COST = ? WHERE ORDER_NUM = ?";
		try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setInt(1, quantity);
                        stmt.setDouble(2, setPrix(quantity, getProductIdByOrderNum(order_num), customerID));
			stmt.setInt(3, order_num);
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
        
        // Partie Admin
        
        public double chiffreAffaireByProduct(int product, Date deb, Date fin) throws SQLException {
            double ret = 0;
            String sql ="SELECT * FROM PURCHASE_ORDER WHERE PRODUCT_ID = ? AND SHIPPING_DATE BETWEEN ? AND ? ";
            
            
            try (Connection connection = myDataSource.getConnection(); 
		     PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setInt(1, product);
                        stmt.setDate(2, (java.sql.Date) deb);
                        stmt.setDate(3, (java.sql.Date) fin);
                        
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = rs.getInt("PRODUCT_ID");								                               
			}
		}
            System.out.println("Le Ca est de ------------------------------------------------------------------ "+ ret);
            return ret;
        }
       
        
        
        
       

}
