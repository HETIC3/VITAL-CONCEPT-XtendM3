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
  
  int company
  String divi
  String grpa
  
  public HoldGrpInvoice(MIAPI mi, DatabaseAPI database, LoggerAPI logger, ProgramAPI program, UtilityAPI utility) {
    this.mi = mi
    this.program = program
    this.database = database
    this.logger = logger
    this.utility = utility
  }
  
  public void main() {
    // Parse Transaction arguments
    company = (Integer) program.getLDAZD().CONO
    divi = mi.inData.get("DIVI").trim()
    grpa = mi.inData.get("GRPA").trim()
    
    // Select fields to handle from table FSLGP2
    DBAction query = database.table("FSLGP2")
      .index("00")
    .selection("F2CONO", "F2DIVI", "F2JBNO", "F2JBDT", "F2JBTM", "F2GRPA", "F2GPST")
      .build()
      
    DBContainer container = query.getContainer()
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
    
    // If there is an existing record of this key, and Status GPST = 0, then the fields are set and the record is updated in the table
    if (query.read(container)) {
      if (container.get("F2GPST") == 0) {
        query.readAllLock(container, 6, updateCallBack)
      }
    } else {
      // If the record doesn't already exist in the table, an error is thrown
      mi.error("L'enregistrement n'existe pas.")
    }
  }
}