package com.vision.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.vision.exception.ExceptionCode;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.CommonApiModel;

@Component
public class CommonApiDao extends AbstractCommonDao{
	
	@Value("${app.productName}")
	private String productName;
	
	public ExceptionCode getCommonResultDataFetch(CommonApiModel vObject, String runQuery) {
		ExceptionCode exceptionCode = new ExceptionCode();
		ArrayList result = new ArrayList(); 
		try
		{	
			String orginalQuery = "";
			if (!ValidationUtil.isValid(runQuery)) {
				orginalQuery = getPrdQueryConfig(vObject.getQueryId());
			} else {
				orginalQuery = runQuery;
			}
			if(!ValidationUtil.isValid(orginalQuery)) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("No Queries maintained for the Query Id["+vObject.getQueryId()+"]");
				return exceptionCode;
			}
			orginalQuery = replacePromptVariables(orginalQuery, vObject);
			ResultSetExtractor mapper = new ResultSetExtractor() {
				@Override
				public Object extractData(ResultSet rs)  throws SQLException, DataAccessException {
					ResultSetMetaData metaData = rs.getMetaData();
					int colCount = metaData.getColumnCount();
					Boolean dataPresent = false;
					while(rs.next()){
						HashMap<String,String> resultData = new HashMap<String,String>();
						dataPresent = true;
						for(int cn = 1;cn <= colCount;cn++) {
							String columnName = metaData.getColumnName(cn);
							resultData.put(columnName.toUpperCase(), rs.getString(columnName));
						}
						result.add(resultData);
					}
					if(dataPresent) {
						exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
						exceptionCode.setResponse(result);
					}else {
						exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
					}
					return exceptionCode;
				}
			};
			return (ExceptionCode)getJdbcTemplate().query(orginalQuery, mapper);
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
	}

	public String replacePromptVariables(String query, CommonApiModel vObj) {
		query = query.replaceAll("#PROMPT_VALUE_1#",
				ValidationUtil.isValid(vObj.getParam1()) ? vObj.getParam1() : "''");
		query = query.replaceAll("#PROMPT_VALUE_2#",
				ValidationUtil.isValid(vObj.getParam2()) ? vObj.getParam2() : "''");
		query = query.replaceAll("#PROMPT_VALUE_3#",
				ValidationUtil.isValid(vObj.getParam3()) ? vObj.getParam3() : "''");
		query = query.replaceAll("#PROMPT_VALUE_4#",
				ValidationUtil.isValid(vObj.getParam4()) ? vObj.getParam4() : "''");
		query = query.replaceAll("#PROMPT_VALUE_5#",
				ValidationUtil.isValid(vObj.getParam5()) ? vObj.getParam5() : "''");
		query = query.replaceAll("#PROMPT_VALUE_6#",
				ValidationUtil.isValid(vObj.getParam6()) ? vObj.getParam6() : "''");
		query = query.replaceAll("#PROMPT_VALUE_7#",
				ValidationUtil.isValid(vObj.getParam7()) ? vObj.getParam7() : "''");
		query = query.replaceAll("#PROMPT_VALUE_8#",
				ValidationUtil.isValid(vObj.getParam8()) ? vObj.getParam8() : "''");
		query = query.replaceAll("#PROMPT_VALUE_9#",
				ValidationUtil.isValid(vObj.getParam9()) ? vObj.getParam9() : "''");
		query = query.replaceAll("#PROMPT_VALUE_10#",
				ValidationUtil.isValid(vObj.getParam10()) ? vObj.getParam10() : "''");
		// query = query.replaceAll("#VISION_ID#", "" +
		// SessionContextHolder.getContext().getVisionId());
		return query;
	}
	public String getPrdQueryConfig(String queryId){
		String resultQuery = "";
		try
		{			
			String sql = "SELECT QUERY from PRD_QUERY_CONFIG WHERE DATA_REF_ID = '"+queryId+"' AND STATUS = 0 AND APPLICATION_ID = '"+productName+"' ";
			resultQuery = getJdbcTemplate().queryForObject(sql,String.class);
			return resultQuery;
		}catch(Exception ex){
			logger.error("Exception while getting the Query for the Query ID["+queryId+"]");
			return null;
		}
	}
	public ExceptionCode getCommonResultDataQuery(String query){
		ExceptionCode exceptionCode = new ExceptionCode();
		ArrayList result = new ArrayList(); 
		try
		{	
			if(!ValidationUtil.isValid(query)) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Query Invalid");
				return exceptionCode;
			}
			//orginalQuery = replacePromptVariables(query, vObject);
			ResultSetExtractor mapper = new ResultSetExtractor() {
				@Override
				public Object extractData(ResultSet rs)  throws SQLException, DataAccessException {
					ResultSetMetaData metaData = rs.getMetaData();
					int colCount = metaData.getColumnCount();
					Boolean dataPresent = false;
					while(rs.next()){
						HashMap<String,String> resultData = new HashMap<String,String>();
						dataPresent = true;
						for(int cn = 1;cn <= colCount;cn++) {
							String columnName = metaData.getColumnName(cn);
							resultData.put(columnName.toUpperCase(), rs.getString(columnName));
						}
						result.add(resultData);
					}
					if(dataPresent) {
						exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
						exceptionCode.setResponse(result);
					}else {
						exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
					}
					return exceptionCode;
				}
			};
			return (ExceptionCode)getJdbcTemplate().query(query, mapper);
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
}
	public String getDbFunction(String reqFunction) {
		String functionName = "";
		if("MSSQL".equalsIgnoreCase(databaseType)) {
			switch(reqFunction) {
			case "DATEFUNC":
				functionName = "FORMAT";
				break;
			case "SYSDATE":
				functionName = "GetDate()";
				break;
			case "NVL":
				functionName = "ISNULL";
				break;
			case "TIME":
				functionName = "HH:mm:ss";
				break;
			case "DATEFORMAT":
				functionName = "dd-MMM-yyyy";
				break;
			case "CONVERT":
				functionName = "CONVERT";
				break;
			case "TYPE":
				functionName = "varchar,";
				break;
			case "TIMEFORMAT":
				functionName = "108";
				break;
			case "PIPELINE":
				functionName = "+";	
				break;
			}
		}else if("ORACLE".equalsIgnoreCase(databaseType)) {
			switch(reqFunction) {
			case "DATEFUNC":
				functionName = "TO_CHAR";
				break;
			case "SYSDATE":
				functionName = "SYSDATE";
				break;
			case "NVL":
				functionName = "NVL";
				break;
			case "TIME":
				functionName = "HH24:MI:SS";
				break;
			case "DATEFORMAT":
				functionName = "DD-Mon-RRRR";
				break;
			case "CONVERT":
				functionName = "TO_CHAR";
				break;
			case "TYPE":
				functionName = "";
				break;
			case "TIMEFORMAT":
				functionName = "'HH:MM:SS'";
				break;
			case "PIPELINE":
				functionName = "||";		
			}
		}
		
		return functionName;
	}
	
}