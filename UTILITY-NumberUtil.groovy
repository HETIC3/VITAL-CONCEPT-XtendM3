/**
 * README
 * This extension is an Utility
 * 
 * Name: NumberUtil
 * Description: Methods to cast Alpha to Number 
 * Date       Changed By            Description
 * 20230127   Ludovic TRAVERS       Creation of NumberUtil Utility
 */
 
public class NumberUtil extends ExtendM3Utility {
  
  /**
   *  Transform a string parameter into an integer
   * @return integer
   */
  Integer parseStringToInteger(String parameter) {
    String wParameter = parameter.trim()
    if (wParameter.isInteger()) return Integer.valueOf(wParameter)
    else return 0
  }

  /**
   *  Transform a string parameter into an double
   * @return double
   */
  Double parseStringToDouble(String parameter) {
    String wParameter = parameter.trim()
    if (wParameter.isDouble()) return Double.valueOf(wParameter)
    else return 0d
  }
  
}