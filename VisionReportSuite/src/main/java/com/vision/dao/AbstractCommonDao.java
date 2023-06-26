package com.vision.dao;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class AbstractCommonDao {
	@Value("${app.databaseType}")
	public String databaseType;
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	private int batchLimit = 5000;
	
	public Connection getConnection() {
		try {
			return getJdbcTemplate().getDataSource().getConnection();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	protected String strErrorDesc = "";
	protected String strCurrentOperation = "";
	protected String strApproveOperation = "";// Current operation for Audit purpose
	protected long intCurrentUserId = 0;// Current user Id of incoming request
	protected int retVal = 0;
	protected String serviceName = "";
	protected String serviceDesc = "";// Display Name of the service.
	protected String tableName = "";
	protected String childTableName = "";
	protected String userGroup = "";
	protected String userProfile = "";
	protected String userGrpProfile = "";
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getChildTableName() {
		return childTableName;
	}

	public void setChildTableName(String childTableName) {
		this.childTableName = childTableName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceDesc() {
		return serviceDesc;
	}

	public void setServiceDesc(String serviceDesc) {
		this.serviceDesc = serviceDesc;
	}

	public String getStrErrorDesc() {
		return strErrorDesc;
	}

	public void setStrErrorDesc(String strErrorDesc) {
		this.strErrorDesc = strErrorDesc;
	}

	public String getStrCurrentOperation() {
		return strCurrentOperation;
	}

	public void setStrCurrentOperation(String strCurrentOperation) {
		this.strCurrentOperation = strCurrentOperation;
	}

	public String getStrApproveOperation() {
		return strApproveOperation;
	}

	public void setStrApproveOperation(String strApproveOperation) {
		this.strApproveOperation = strApproveOperation;
	}

	public long getIntCurrentUserId() {
		return intCurrentUserId;
	}

	public void setIntCurrentUserId(long intCurrentUserId) {
		this.intCurrentUserId = intCurrentUserId;
	}

	public int getRetVal() {
		return retVal;
	}

	public void setRetVal(int retVal) {
		this.retVal = retVal;
	}
}