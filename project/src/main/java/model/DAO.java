package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
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
     * Construit le DAO avec sa source de données
     */
    public DAO() {
        this.myDataSource = DataSourceFactory.getDataSource();
    }
    
    /**
     * 
     * @param id du client
     * @param montant de la somme à virer sue le compte
     * @return 1 si l'opération s'est bien déroulée
     * @throws SQLException 
     */

    // entrer l'id d'un client est la somme que vous voulez verser sur son compte
    public int virement(int id, double montant) throws SQLException {
        int ret = 0;
        String sql = "UPDATE CUSTOMER SET CREDIT_LIMIT = ? WHERE CUSTOMER_ID = ?";
        try (
            Connection connection = myDataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, (int) (soldeClient(id) + montant));
            stmt.setInt(2, id);
            ret = stmt.executeUpdate();
            }
        return ret;
    }
    /**
     * 
     * @param id du client
     * @return le solde du client (DREDIT_LIMIT)
     * @throws SQLException 
     */

    //metode qui recupere la solde presente sur le compte d'un client en renseignant son id
    public double soldeClient(int id) throws SQLException {
        double ret = 0;
        String sql = "SELECT CREDIT_LIMIT FROM CUSTOMER WHERE CUSTOMER_ID = ?";
        try (
                Connection connection = myDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ret = rs.getDouble("CREDIT_LIMIT");
            }
        }
        return ret;
    }
/**
 * 
 * @param id du client
 * @param price somme à modifier
 * @return 1 si la modification a bien été faite
 * @throws SQLException 
 */
    // méthode qui va mettre à jour la solde d'un client en prenant en compte son id et le prix de son achat
    public int updateSolde(int id, double price) throws SQLException {
        int ret = 0;
        String sql = "UPDATE CUSTOMER SET CREDIT_LIMIT = ? WHERE CUSTOMER_ID = ?";
        try (Connection connection = myDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(2, id);
            stmt.setInt(1, (int) (this.soldeClient(id) - price));
            ret = stmt.executeUpdate();
        }

        return ret;
    }

    /**
     * 
     * @param id du client
     * @param product_id qui est l'id du produit
     * @param quantite du produit à acheter
     * @return true si le client peut effectuer cet achat
     * @throws SQLException 
     */

    // Methode qui vériffie si le cliant a assez d'argent sur son compte pour poouvoir effectuer son achat
    public boolean checkAchatSolde(int id, int product_id, int quantite) throws SQLException {
        boolean ret = false;
        double solde = this.soldeClient(id);
        if (solde >= setPrix(quantite, product_id, id)) {
            ret = true;
            System.out.println("Le prix est de tadadadadadadadada" + setPrix(quantite, product_id, id));
        }
        return ret;
    }

    /**
     * 
     * @param c qui est un customer
     * @return une liste comprenant les discounts codes d'un customer
     * @throws SQLException 
     */
    // retournne le discount code d'un client en fonction de son id
    public List<DiscountCode> customerCodes(Customer c) throws SQLException {
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
            while (rs.next()) {
                String code = rs.getString("DISCOUNT_CODE");
                Float rate = rs.getFloat("RATE");
                DiscountCode dc = new DiscountCode(code, rate);
                result.add(dc);

            }
        }

        return result;
    }
    
    /**
     * 
     * @param c qui est un customer
     * @return la liste de tous les achats de ce client
     * @throws SQLException 
     */

    // methode qui retourne dans une liste la liste de tous les achats d'un client
    public List<PurchaseOrder> customerCommandes(Customer c) throws SQLException {
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
            while (rs.next()) {
                int code = rs.getInt("ORDER_NUM");
                int idCust = rs.getInt("CUSTOMER_ID");
                int quantity = rs.getInt("QUANTITY");
                int product = rs.getInt("PRODUCT_ID");
                double cost = setPrix(quantity, product, idCust);
                Date date = rs.getDate("SHIPPING_DATE");
                String descritpion = rs.getString("DESCRIPTION");
                PurchaseOrder po = new PurchaseOrder(code, idCust, quantity);
                po.setDESCRIPTION(descritpion);
                System.out.println("ici le prix : " + cost);
                po.setCOST(cost);
                po.setSHIPPING_DATE(date);
                result.add(po);

            }
        }

        return result;
    }
    /**
     * 
     * @return tous les codes de réductions des clients
     * @throws SQLException 
     */

    // Retourne tous les codes de promotions client
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
    
    /**
     * 
     * @return la liste de tous les produits que l'on peut acheter
     * @throws SQLException 
     */

    // Retourne une liste de string affichant les description des produits en stock
    public ArrayList<Product> listProduct() throws SQLException {

        ArrayList<Product> result = new ArrayList<>();

        String sql = "SELECT * FROM PRODUCT WHERE QUANTITY_ON_HAND > 0";

        try (Connection connection = myDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("PRODUCT_ID");
                String des = rs.getString("DESCRIPTION");
                double price = rs.getDouble("PURCHASE_COST");
                Product c = new Product(id);
                c.setDescription(des);
                c.setPurchaseCost(price);
                result.add(c);

            }
        }
        return result;
    }
    
    /**
     * 
     * @param customer_id qui est l'id du customer
     * @return la valeur du discount code de ce client/customer
     * @throws SQLException 
     */

    //retourne la valeur du code de reduction d'un client en fonction de son id
    public double valueOfDiscountCode(int customer_id) throws SQLException {
        double ret = 0;
        String sql = "SELECT RATE FROM DISCOUNT_CODE"
                + " INNER JOIN CUSTOMER"
                + " USING (DISCOUNT_CODE)"
                + " WHERE CUSTOMER_ID = ?";

        try (Connection connection = myDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customer_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

                double rate = rs.getDouble("RATE");
                ret = rate;
            }
        }
        System.out.println("ma valeur est de  -----------------------" + ret);
        return ret;
    }
    
    /**
     * 
     * @param code que l'on veut créer
     * @param rate la valeur de ce code
     * @return 1 si l'opération a bien été réalisée
     * @throws SQLException 
     */

    // créer un dsicount code
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
/**
 * 
 * @param customerID qui est l'id du client
 * @param quantity qui est la quantite que l'on veut acheter d'objets
 * @param product_id qui est l'id du produit que l'on veut acheter
 * @return 1 si l'op"artion s'est bien réalisée, 0 sinon
 * @throws SQLException 
 */
    // methode permettant de passer une commande
    public int addCommande(int customerID, int quantity, int product_id) throws SQLException {
        int result = 0;
        String sql = "INSERT INTO PURCHASE_ORDER (ORDER_NUM, CUSTOMER_ID, PRODUCT_ID, QUANTITY, SHIPPING_DATE) VALUES (?, ?, ?, ?, ?)";
        if (checkAchatSolde(customerID, product_id, quantity) == true) {
            updateSolde(customerID, setPrix(quantity, product_id, customerID));
            try (Connection connection = myDataSource.getConnection();
                    PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(2, customerID);
                stmt.setInt(1, numeroCommande());
                stmt.setInt(3, product_id);
                stmt.setInt(4, quantity);

                stmt.setDate(5, java.sql.Date.valueOf(java.time.LocalDate.now()));
                result = stmt.executeUpdate();
            }
        } else {
            throw new SQLException("Vous n'avez pas assez d'argent pour acheter ce produit. Votre solde est de " + soldeClient(customerID) + "€ et votre achat coûte : " + setPrix(quantity, product_id, customerID) + "€");
        }

        return result;
    }
    
    /**
     * 
     * @param quantite du produit
     * @param product_id l'id du produit
     * @param customer_id l'id du client
     * @return le prix  de la commande
     * @throws SQLException 
     */

    // cette méthode prend en compte la valeur des réductions des clients pour calculer le prix d'une commande
    public double setPrix(int quantite, int product_id, int customer_id) throws SQLException {
        double result = 0;
        String sql = "SELECT PURCHASE_COST FROM PRODUCT WHERE PRODUCT_ID = ?";
        try (Connection connection = myDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, product_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

                result = (rs.getDouble("PURCHASE_COST") * quantite) * ((100 - valueOfDiscountCode(customer_id)) / 100);

            }
        }
        return result;
    }
    /**
     * 
     * @return un numéro de commande aléatoire qui n'est pas dans la liste pour créer une commande
     * @throws SQLException 
     */

    public int numeroCommande() throws SQLException {
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
        int numAlea = (int) (Math.random() * 10000);
        while (result.contains(numAlea)) {
            numAlea = (int) (Math.random() * 10000);
        }
        return numAlea;
    }
    
    /**
     * 
     * @return tous les produits disponnbiles
     * @throws SQLException 
     */

    public ArrayList<String> allProduct() throws SQLException {
        ArrayList<String> result = new ArrayList<>();
        String sql = "SELECT DESCRIPTION FROM PRODUCT WHERE QUANTITY_ON_HAND > 0";
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
    
    /**
     * 
     * @param des la description des produits
     * @return l'id de ces produits
     * @throws SQLException 
     */

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
        System.out.println("Le produit est : " + result + "----------------------");
        return result;
    }
    
    /**
     * 
     * @param order_num qui est le numéro de commande 
     * @return l'id du produit
     * @throws SQLException 
     */

    public int prodId(int order_num) throws SQLException {
        int res = 0;

        String sql = "SELECT PRODUCT_ID FROM PURCHASE_ORDER WHERE ORDER_NUM = ?";
        try (Connection connection = myDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, order_num);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                res = rs.getInt("PRODUCT_ID");
            }
        }
        return res;
    }

    
    /**
     * 
     * @param order_num qui est le numéro de la commande
     * @return le nombre, plus précisement la quentité du prouit commandé
     * @throws SQLException 
     */
    
    public int ancienneQuantite(int order_num) throws SQLException {
        int res = 0;

        String sql = "SELECT QUANTITY FROM PURCHASE_ORDER WHERE ORDER_NUM = ?";
        try (Connection connection = myDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, order_num);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                res = rs.getInt("QUANTITY");
            }
        }
        return res;
    }
    
    
     /**
     * 
     * @param order_num qui est le numéro de la commande
     * @return le nombre, plus précisement la quentité du prouit commandé
     * @throws SQLException 
     */
    
    public int currentQuantite(int order_num) throws SQLException {
        int res = 0;

        String sql = "SELECT QUANTITY FROM PURCHASE_ORDER WHERE ORDER_NUM = ?";
        try (Connection connection = myDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, order_num);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                res = rs.getInt("QUANTITY");
            }
        }
        return res;
    }

    /**
     * 
     * @param order_num qui est le numéro de la commande
     * @return l'id du client qui passe la commande
     * @throws SQLException 
     */
    
    public int CustomerIdByOrderNum(int order_num) throws SQLException {
        int res = 0;

        String sql = "SELECT CUSTOMER_ID FROM PURCHASE_ORDER WHERE ORDER_NUM = ?";
        try (Connection connection = myDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, order_num);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                res = rs.getInt("CUSTOMER_ID");
            }
        }
        return res;
    }

    /**
     * 
     * @param order_num qui est le numéro de la commande
     * @return l'affirmation de la comamande supprimée
     * @throws SQLException 
     */
    
    public int deleteCommande(int order_num) throws SQLException {
        int result = 0;
        this.virement(this.CustomerIdByOrderNum(order_num), this.setPrix(this.ancienneQuantite(order_num), this.prodId(order_num), this.CustomerIdByOrderNum(order_num)));

        String sql = "DELETE FROM PURCHASE_ORDER WHERE ORDER_NUM = ?";
        try (Connection connection = myDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, order_num);
            result = stmt.executeUpdate();
        }
        return result;
    }

    /**
     * 
     * @param order_num numéro de la commande
     * @param quantity quantité du produit demandé
     * @param customerID id du client
     * @return affirmation de la modification de la commande si elle est possible,
     * sinon return une impossibilité de d'augmenter la quantité si le client n'a pas assez d'arent sur son compte
     * @throws SQLException 
     */
    
    public boolean editCommande(int order_num, int quantity, int customerID) throws SQLException {
        Boolean res = false;
        System.out.println("____________________________________________________________"+ quantity);
        int oldQuantity = this.ancienneQuantite(order_num);
        if (oldQuantity >= quantity) {
            this.virement(this.CustomerIdByOrderNum(order_num), this.setPrix(oldQuantity - quantity, this.prodId(order_num), this.CustomerIdByOrderNum(order_num)));
            String sql = "UPDATE PURCHASE_ORDER SET QUANTITY = ? WHERE ORDER_NUM = ?";
            try (Connection connection = myDataSource.getConnection();
                    PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, quantity);

                stmt.setInt(2, order_num);
                int result = stmt.executeUpdate();
                res = true;
                
            }
        } else {
            int diff = quantity - oldQuantity;
            System.out.println(" ouai souais ouais ouais____________________________________________________________"+this.setPrix(oldQuantity - quantity, this.prodId(order_num), this.CustomerIdByOrderNum(order_num)));

            if (this.checkAchatSolde(customerID, this.prodId(order_num), diff)) {
                this.updateSolde(customerID, setPrix(diff, this.prodId(order_num), customerID));
                String sql = "UPDATE PURCHASE_ORDER SET QUANTITY = ? WHERE ORDER_NUM = ?";
                try (Connection connection = myDataSource.getConnection();
                        PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, quantity);

                    stmt.setInt(2, order_num);
                    int result = stmt.executeUpdate();
                    res = true;
                }
            }

        }
        return res;

        
    }

    /**
     * 
     * @param order_num numéro de la commande 
     * @return l'id du produit 
     * @throws SQLException 
     */
    
    public int getProductIdByOrderNum(int order_num) throws SQLException {
        int result = 0;
        String sql = "SELECT PRODUCT_ID FROM PRODUCT "
                + "INNER JOIN PURCHASE_ORDER "
                + "USING (PRODUCT_ID)"
                + "WHERE ORDER_NUM = ?";
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
    
/**
 * 
 * @param email c'est l'email du client
 * @return l'identifiant du client, cela permet de savoir si il a un compte sur le site par exemple
 * @throws SQLException 
 */
    
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
            if (!rs.next()) {
                System.out.println("no data");
                c.setEmail("nodata");
                c.setPassword("nodata");
                c.setName("nodata");

                return c;
            } else {
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

    /**
     * 
     * @return les informations concernants le client : son mail et son Id 
     * @throws SQLException 
     */
    
    public String infoCustomer() throws SQLException {
        String ret = "";
        String sql = "SELECT EMAIL, CUSTOMER_ID FROM CUSTOMER";
        try (Connection connection = myDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String mail = rs.getString("EMAIL");
                int id = rs.getInt("CUSTOMER_ID");
                ret += " " + mail + " id : " + id;

            }
        }

        return ret;
    }

    /**
     * 
     * @param product_id l'identifiant du produit
     * @return la description du produit 
     * @throws SQLException 
     */
    public String descProduct(int product_id) throws SQLException {
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
    
    /**
     * 
     * @param product_id identifiant du produit 
     * @return le prix du produit demandé
     * @throws SQLException 
     */
    
    public double getCost(int product_id) throws SQLException {
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

    /**
     * 
     * @param id l'id du client 
     * @return Nom du client 
     * @throws SQLException 
     */
    public String nameCustomer(int id) throws SQLException {
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

    /**
     * 
     * @param deb date de début d'analyse
     * @param fin date de fin de l'analyse
     * @return le chiffre d'affaires effectué par la vente des produits (ordonné par produit)
     * @throws SQLException 
     */
    public Map<String, Double> chiffreAffaireByProduct(String deb, String fin) throws SQLException {
        Map<String, Double> ret = new HashMap<>();
        String sql = "SELECT PRODUCT_ID, SUM(QUANTITY) AS SALES FROM PURCHASE_ORDER"
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
            System.out.println("Le Ca est de ------------------------------------------------------------------ " + data1);
            System.out.println("Le Ca est de ------------------------------------------------------------------ " + data2);
            stmt.setDate(1, data1);
            stmt.setDate(2, data2);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String product = descProduct(rs.getInt("PRODUCT_ID"));
                double price = rs.getDouble("SALES") * getCost(rs.getInt("PRODUCT_ID"));
                ret.put(product, price);
                System.out.println("Le Ca est de ------------------------------------------------------------------ " + price);
            }
        }

        return ret;
    }

    /**
     * 
     * @param deb date de début de l'analyse
     * @param fin date de fin de l'analyse
     * @return le chiffre d'affaires effectué par client
     * @throws SQLException 
     */
    public Map<String, Double> chiffreAffaireByCustomer(String deb, String fin) throws SQLException {
        Map<String, Double> ret = new HashMap<>();
        String sql = "SELECT CUSTOMER_ID, PRODUCT_ID, QUANTITY FROM PURCHASE_ORDER"
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

            stmt.setDate(1, data1);
            stmt.setDate(2, data2);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String customer = nameCustomer(rs.getInt("CUSTOMER_ID"));
                double price = rs.getDouble("QUANTITY") * getCost(rs.getInt("PRODUCT_ID"));

                if (ret.containsKey(customer)) {
                    ret.put(customer, ret.get(customer) + price);
                    System.out.println("nouveau ca  " + customer + " est de " + ret.get(customer));
                } else {
                    ret.put(customer, price);
                    System.out.println("ca = " + customer + " est de " + price);
                }
            }
        }

        return ret;
    }

    /**
     * 
     * @param deb date de début d'analyse
     * @param fin date de fin d'analyse 
     * @return le chiffre d'affaires représenté par état
     * @throws SQLException 
     */
    public Map<String, Double> chiffreAffaireByState(String deb, String fin) throws SQLException {
        Map<String, Double> ret = new HashMap<>();
        String sql = "SELECT PRODUCT_ID, CUSTOMER_ID, QUANTITY, STATE FROM PURCHASE_ORDER"
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

            stmt.setDate(1, data1);
            stmt.setDate(2, data2);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String state = rs.getString("STATE");
                double price = rs.getDouble("QUANTITY") * getCost(rs.getInt("PRODUCT_ID"));
                if (ret.containsKey(state)) {
                    ret.put(state, ret.get(state) + price);
                    System.out.println("nouveau ca  " + state + " est de " + ret.get(state));
                } else {
                    ret.put(state, price);
                    System.out.println("ca = " + state + " est de " + price);
                }

            }
        }

        return ret;
    }

    /**
     * 
     * @param deb date de début d'analyse
     * @param fin date de fin d'analyse
     * @return le chiffre d'affaires de l'entreprise effectué par rapport au produit (à leur code)
     * @throws SQLException 
     */
    public Map<String, Double> chiffreAffaireByProductCode(String deb, String fin) throws SQLException {
        Map<String, Double> ret = new HashMap<>();
        String sql = "SELECT PURCHASE_ORDER.PRODUCT_ID, QUANTITY, PRODUCT_CODE.DESCRIPTION FROM PURCHASE_ORDER"
                + " INNER JOIN PRODUCT"
                + " USING (PRODUCT_ID)"
                + " INNER JOIN PRODUCT_CODE"
                + " ON PRODUCT.PRODUCT_CODE = PRODUCT_CODE.PROD_CODE"
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

            stmt.setDate(1, data1);
            stmt.setDate(2, data2);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String state = rs.getString("DESCRIPTION");
                double price = rs.getDouble("QUANTITY") * getCost(rs.getInt("PRODUCT_ID"));
                if (ret.containsKey(state)) {
                    ret.put(state, ret.get(state) + price);
                    System.out.println("nouveau ca  " + state + " est de " + ret.get(state));
                } else {
                    ret.put(state, price);
                    System.out.println("ca = " + state + " est de " + price);
                }

            }
        }

        return ret;
    }

    /**
     * 
     * @param deb date de début d'analyse
     * @param fin date de fin d'analyse
     * @return le chiffre d'affaires de l'entreprise en fonction du code postal
     * @throws SQLException 
     */
    public Map<String, Double> chiffreAffaireByZip(String deb, String fin) throws SQLException {
        Map<String, Double> ret = new HashMap<>();
        String sql = "SELECT PRODUCT_ID, CUSTOMER_ID, QUANTITY, ZIP FROM PURCHASE_ORDER"
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

            stmt.setDate(1, data1);
            stmt.setDate(2, data2);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String state = rs.getString("ZIP");
                double price = rs.getDouble("QUANTITY") * getCost(rs.getInt("PRODUCT_ID"));
                if (ret.containsKey(state)) {
                    ret.put(state, ret.get(state) + price);
                    System.out.println("nouveau ca  " + state + " est de " + ret.get(state));
                } else {
                    ret.put(state, price);
                    System.out.println("ca = " + state + " est de " + price);
                }

            }
        }

        return ret;
    }

}
