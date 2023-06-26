package com.vision.vb;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CommonVb implements Serializable{
	
	private static final long serialVersionUID = -2724404220637666443L;
	private int recordIndicatorNt = 7;
	private int recordIndicator = -1;
	private long maker;
	private String makerName = "";
	private long verifier;
	private String verifierName = "";
	private int internalStatus = 0;
	private boolean checked = false;
	private long totalRows = 0l;
	private int currentPage = 1;
	private int minRecords = 1;
	protected int maxRecords = 20;//Constants.PAGE_DISPLAY_SIZE;
	private boolean verificationRequired = true;
	private boolean staticDelete = true;
	private boolean newRecord = false;
	private String dateLastModified = "";
	private String dateCreation = "";
	private int dbStatus = 0;
	private String locale;
	private String previousActionType = "";
	private String screenName = "";
	private String updateRestrictionCountry = "";
	private String updateRestrictionLeBook = "";
	private String updateRestrictionLegalVehicle = "";
	private String remoteAddress = "";
	private String auditDelimiter = "|!#";
	private String auditDelimiterColVal = "^!#";
	private Integer startIndex = 0;
	private Integer lastIndex = 3000;
	
	private Object makerNameFltrCond;
	private Object verifierNameFltrCond;
	private Object dateCreationFltrCond;
	private Object dateLastModifiedFltrCond;
	
	private String actionType = "";
	private String statusDesc="";
	private String recordIndicatorDesc = "";
	private String moduleAccessXml = "";
	private String applicationId="";
	
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	private Map<String ,String> sortHeaderColumnMap = new HashMap<String, String>();
	
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public String getPreviousActionType() {
		return previousActionType;
	}
	public void setPreviousActionType(String previousActionType) {
		this.previousActionType = previousActionType;
	}
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public int getRecordIndicatorNt() {
		return recordIndicatorNt;
	}
	public void setRecordIndicatorNt(int recordIndicatorNt) {
		this.recordIndicatorNt = recordIndicatorNt;
	}
	public int getRecordIndicator() {
		return recordIndicator;
	}
	public void setRecordIndicator(int recordIndicator) {
		this.recordIndicator = recordIndicator;
	}
	public long getMaker() {
		return maker;
	}
	public void setMaker(long maker) {
		this.maker = maker;
	}
	public long getVerifier() {
		return verifier;
	}
	public void setVerifier(long verifier) {
		this.verifier = verifier;
	}
	public int getInternalStatus() {
		return internalStatus;
	}
	public void setInternalStatus(int internalStatus) {
		this.internalStatus = internalStatus;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean isChecked) {
		this.checked = isChecked;
	}
	public long getTotalRows() {
		return totalRows;
	}
	public void setTotalRows(long totalRows) {
		this.totalRows = totalRows;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getMinRecords() {
		return minRecords;
	}
	public void setMinRecords(int minRecords) {
		this.minRecords = minRecords;
	}
	public int getMaxRecords() {
		return maxRecords;
	}
	public void setMaxRecords(int maxRecords) {
		this.maxRecords = maxRecords;
	}
	public boolean isVerificationRequired() {
		return verificationRequired;
	}
	public void setVerificationRequired(boolean verificationRequired) {
		this.verificationRequired = verificationRequired;
	}
	public boolean isStaticDelete() {
		return staticDelete;
	}
	public void setStaticDelete(boolean staticDelete) {
		this.staticDelete = staticDelete;
	}
	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean isNewRecord) {
		this.newRecord = isNewRecord;
	}
	public String getDateLastModified() {
		return dateLastModified;
	}
	public void setDateLastModified(String dateLastModified) {
		this.dateLastModified = dateLastModified;
	}
	public String getDateCreation() {
		return dateCreation;
	}
	public void setDateCreation(String dateCreation) {
		this.dateCreation = dateCreation;
	}
	public String getMakerName() {
		return makerName;
	}
	public void setMakerName(String makerName) {
		this.makerName = makerName;
	}
	public String getVerifierName() {
		return verifierName;
	}
	public void setVerifierName(String verifierName) {
		this.verifierName = verifierName;
	}
	public int getDbStatus() {
		return dbStatus;
	}
	public void setDbStatus(int dbStatus) {
		this.dbStatus = dbStatus;
	}
	public String getAuditDelimiter() {
		return auditDelimiter;
	}
	public void setAuditDelimiter(String auditDelimiter) {
		this.auditDelimiter = auditDelimiter;
	}
	public String getAuditDelimiterColVal() {
		return auditDelimiterColVal;
	}
	public void setAuditDelimiterColVal(String auditDelimiterColVal) {
		this.auditDelimiterColVal = auditDelimiterColVal;
	}
	public String getUpdateRestrictionCountry() {
		return updateRestrictionCountry;
	}
	public void setUpdateRestrictionCountry(String updateRestrictionCountry) {
		this.updateRestrictionCountry = updateRestrictionCountry;
	}
	public String getUpdateRestrictionLeBook() {
		return updateRestrictionLeBook;
	}
	public void setUpdateRestrictionLeBook(String updateRestrictionLeBook) {
		this.updateRestrictionLeBook = updateRestrictionLeBook;
	}
	public String getUpdateRestrictionLegalVehicle() {
		return updateRestrictionLegalVehicle;
	}
	public void setUpdateRestrictionLegalVehicle(
			String updateRestrictionLegalVehicle) {
		this.updateRestrictionLegalVehicle = updateRestrictionLegalVehicle;
	}
	public String getRemoteAddress() {
		return remoteAddress;
	}
	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
	public Integer getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}
	public Integer getLastIndex() {
		return lastIndex;
	}
	public void setLastIndex(Integer lastIndex) {
		this.lastIndex = lastIndex;
	}
	public Object getMakerNameFltrCond() {
		return makerNameFltrCond;
	}
	public void setMakerNameFltrCond(Object makerNameFltrCond) {
		this.makerNameFltrCond = makerNameFltrCond;
	}
	public Object getVerifierNameFltrCond() {
		return verifierNameFltrCond;
	}
	public void setVerifierNameFltrCond(Object verifierNameFltrCond) {
		this.verifierNameFltrCond = verifierNameFltrCond;
	}
	public Object getDateCreationFltrCond() {
		return dateCreationFltrCond;
	}
	public void setDateCreationFltrCond(Object dateCreationFltrCond) {
		this.dateCreationFltrCond = dateCreationFltrCond;
	}
	public Object getDateLastModifiedFltrCond() {
		return dateLastModifiedFltrCond;
	}
	public void setDateLastModifiedFltrCond(Object dateLastModifiedFltrCond) {
		this.dateLastModifiedFltrCond = dateLastModifiedFltrCond;
	}
	public Map<String, String> getSortHeaderColumnMap() {
		return sortHeaderColumnMap;
	}
	public void setSortHeaderColumnMap(Map<String, String> sortHeaderColumnMap) {
		this.sortHeaderColumnMap = sortHeaderColumnMap;
	}
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	public String getRecordIndicatorDesc() {
		return recordIndicatorDesc;
	}
	public void setRecordIndicatorDesc(String recordIndicatorDesc) {
		this.recordIndicatorDesc = recordIndicatorDesc;
	}
	public String getModuleAccessXml() {
		return moduleAccessXml;
	}
	public void setModuleAccessXml(String moduleAccessXml) {
		this.moduleAccessXml = moduleAccessXml;
	}
	public String getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
}