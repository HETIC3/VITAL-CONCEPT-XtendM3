/**
 * README
 * This extension is an API transaction
 * 
 * Name: HoldGrpInvoice
 * Description: Changes a record of the table FSLGP2
 * Date       Changed By            Description
 * 20230127   Ludovic TRAVERS       Creation of transaction EXT195MI-HoldGrpInvoice
 */
public class HoldGrpInvoice extends ExtendM3Transaction {
  private final MIAPI mi
  private final ProgramAPI program
  private final DatabaseAPI database
  private final LoggerAPI logger
  private final UtilityAPI utility
  
  public HoldGrpInvoice(MIAPI mi, DatabaseAPI database, LoggerAPI logger, ProgramAPI program, UtilityAPI utility) {
    this.mi = mi
    this.program = program
    this.database = database
    this.logger = logger
    this.utility = utility
  }
  
  int company
  String chid
  int chno
  
  public void main() {
    // Parse Transaction arguments
    company = (Integer) program.getLDAZD().CONO
    String divi = mi.inData.get("DIVI").trim()
    String grpa = mi.inData.get("GRPA").trim()
    
    // Select fields to handle from table FSLGP2
    DBAction query = database.table("FSLGP2")
      .index("00")
    .selection("F2CONO", "F2DIVI", "F2JBNO", "F2JBDT", "F2JBTM", "F2GRPA", "F2GPST")
      .build()
      
    DBContainer container = query.getContainer();
    // Set the key fields
    container.set("F2CONO", company)
    container.set("F2DIVI", mi.inData.get("DIVI").trim())
    container.set("F2JBNO", utility.call("NumberUtil","parseStringToInteger", mi.inData.get("JBNO")))
    container.set("F2JBDT", utility.call("NumberUtil","parseStringToInteger", mi.inData.get("JBDT")))
    container.set("F2JBTM", utility.call("NumberUtil","parseStringToInteger", mi.inData.get("JBTM")))
    container.set("F2GRPA", mi.inData.get("GRPA").trim())
    
    Closure<?> updateCallBack = { LockedResult lockedResult ->
      lockedResult.set("F2GPST", 3)
      lockedResult.update()
    }
    
    // If there is an existing record of this key, the fields are set and the record is updated in the table
    if (query.read(container)) {
      query.readAllLock(container, 6, updateCallBack)
    } else {
      // If the record doesn't already exist in the table, an error is thrown
      mi.error("L'enregistrement n'existe pas.")
    }
  }
  
  /**
   * Check if the input field is empty. If it is not empty, the value is set to the record
   * @param fieldToSet: the name of the field to set in the table 
   * @param fieldToCheck: the input field of the API to control
   * @param recordToModify: the record that will be changed in the table
   */
  String checkEmptyField(String fieldToSet, String fieldToCheck, LockedResult recordToModify) {
    String wField = fieldToCheck.trim()
    if (!wField.isEmpty()) {
      recordToModify.set(fieldToSet, wField)
    }
  }
}