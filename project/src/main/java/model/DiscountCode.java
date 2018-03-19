package model;

/**
 * Un enregistrement de la table DISCOUNT_CODE
 * @author 
 */
public class DiscountCode {

	private String discountCode;

	private float rate;

	public DiscountCode(String code, float rate) {
		this.discountCode = code;
		this.rate = rate;
	}


	public String getDiscountCode() {
		return discountCode;
	}

	public float getRate() {
		return rate;
	}
        
        @Override
        public String toString(){
            return ""+this.discountCode;
        }
}