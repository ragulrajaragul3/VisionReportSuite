package com.vision.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.util.ValidationUtil;
import com.vision.vb.CommonVb;

@Component
public class CronServiceDao extends AbstractCommonDao{
	@Value("${app.databaseType}")
	private String databaseType;
	@Autowired
	CommonApiDao commonApiDao;
	int ERRONEOUS_EXIT = 1;
	int SUCCESSFUL_EXIT = 0;
	//String sysdate = ValidationUtil.isValid(databaseType.equalsIgnoreCase("ORACLE"))? "SYSDATE" : "GETDATE()";
	
	public String findVisionVariableValue(String pVariableName) throws DataAccessException {
		if(!ValidationUtil.isValid(pVariableName)){
			return null;
		}
		String sql = "select VALUE FROM VISION_VARIABLES where UPPER(VARIABLE) = UPPER(?)";
		Object[] lParams = new Object[1];
		lParams[0] = pVariableName;
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CommonVb commonVb = new CommonVb();
				commonVb.setMakerName(rs.getString("VALUE"));
				return commonVb;
			}
		};
		List<CommonVb> commonVbs = getJdbcTemplate().query(sql, lParams, mapper);
		if(commonVbs != null && !commonVbs.isEmpty()){
			return commonVbs.get(0).getMakerName();
		}
		return null;
	}
	public int updateLogFilePath(String logFilePath) {
		try {
			String sql = "UPDATE VISION_VARIABLES SET VALUE = ?" + " WHERE VARIABLE = 'PRD_SCHEDULER_LOG_FILE_PATH'";
			Object[] args = { logFilePath };
			getJdbcTemplate().update(sql, args);
			return SUCCESSFUL_EXIT;
		}catch(Exception e) {
			e.printStackTrace();
			return ERRONEOUS_EXIT;
		}
	}
	public int getCntFromVisionVar(String nodeServerName,String nodeName) {
		String variable = "PRD_PROCESS_SCHEDULE_CRON_"+nodeServerName+nodeName;
		try {
			String sql = "SELECT COUNT(*) CNT FROM VISION_VARIABLES WHERE "
				+ " VARIABLE IN ('"+variable+"','SPAWN_RS_SCHEDULER')";
			return getJdbcTemplate().queryForObject(sql, Integer.class);
		}catch(Exception e) {
			e.printStackTrace();
			return ERRONEOUS_EXIT;
		}
	}
	public int updateVisionVarCronStatus(String cronStatus,String nodeServerName,String nodeName) {
		String variable = ""; 
		variable = "PRD_PROCESS_SCHEDULE_CRON_"+nodeServerName+nodeName;
		try {
			String sql = "UPDATE VISION_VARIABLES SET VALUE  = '"+cronStatus+"' WHERE VARIABLE = '"+variable+"'";
			getJdbcTemplate().update(sql);
			return SUCCESSFUL_EXIT;
		}catch(Exception e) {
			e.printStackTrace();
			return ERRONEOUS_EXIT;
		}
	}

	public int updateRssProcessStatus(String nodeName) {
		try {
			String sql = "Update PRD_RSS_PROCESS_CONTROL Set RSS_Process_Status = 'P' WHERE RSS_Process_Status in ('I','M') "
					+ " AND NVL(NODE_OVERRIDE,NODE_REQUEST) = ? ";
			Object[] args = { nodeName };
			getJdbcTemplate().update(sql, args);
			return SUCCESSFUL_EXIT;
		} catch (Exception e) {
			e.printStackTrace();
			return ERRONEOUS_EXIT;
		}
	}
	public List<HashMap<String,String>> getScheduleProcessLst(String nodeName){
		String sql = "";
		try {
			if("ORACLE".equalsIgnoreCase(databaseType)) {
				sql = " Select 																	"+
               " T1.RSS_PROCESS_ID, T1.REPORT_ID,                                                        "+
               " TO_CHAR(T1.SCHEDULE_DATE,'DD-MON-YYYY HH24:MI:SS') SCHEDULE_DATE,          "+
               " TO_CHAR(T1.PREV_SCHEDULE_DATE,'DD-MON-YYYY HH24:MI:SS') PREV_SCHEDULE_DATE,"+
               " T1.RSS_PROCESS_STATUS,                                                     "+
               " T1.SCH_ITERATION_COUNT,                                                    "+
               " T1.SCH_MAX_ITERATION_COUNT,                                                "+
               " 'Y' Debug_Mode,                                                            "+
               " T1.Process_Version_No Process_Version_No,                                "+
               " T1.priority                                                                "+
			   " From PRD_RSS_PROCESS_CONTROL T1                                                "+
               " Where sysDate >= T1.NEXT_PROCESS_TIME                                      "+
               " And T1.RSS_PROCESS_STATUS in ('P')                                         "+
               " AND NVL(T1.NODE_OVERRIDE,T1.NODE_REQUEST) = '"+nodeName+"'                 "+
               " AND T1.NEXT_PROCESS_TIME =                                                 "+
               " (Select min(T2.NEXT_PROCESS_TIME)                                          "+
               " From PRD_RSS_PROCESS_CONTROL T2                                                "+
               " Where T2.REPORT_ID = T1.REPORT_ID                                          "+
               " And T2.VISION_ID = T1.VISION_ID                                            "+
               " And T2.RS_SCHEDULE_SEQ = T1.RS_SCHEDULE_SEQ                                "+
               " And T2.BURST_ID_SEQ = T1.BURST_ID_SEQ                                      "+
            //"/* And sysDate >= T2.NEXT_PROCESS_TIME */                                      "+
               " And T2.RSS_PROCESS_STATUS in ('P','R','I','M')                             "+
               " )                                                                          "+
               " Order by T1.priority, T1.SCHEDULE_DATE                                     ";
			}else if("MSSQL".equalsIgnoreCase(databaseType)) {
				sql =  " Select 																		"	
					+ " T1.RSS_PROCESS_ID, T1.REPORT_ID,                                            "           
					+ " FORMAT(T1.SCHEDULE_DATE,'dd-MMM-yyyy HH:mm:ss') SCHEDULE_DATE,              "
					+ " FORMAT(T1.PREV_SCHEDULE_DATE,'dd-MMM-yyyy HH:mm:ss') PREV_SCHEDULE_DATE,    "
					+ " T1.RSS_PROCESS_STATUS,                                                      "
					+ " T1.SCH_ITERATION_COUNT,                                                     "
					+ " T1.SCH_MAX_ITERATION_COUNT,                                                 "
					+ " 'Y' Debug_Mode,                                                             "
					+ " T1.Process_Version_No Process_Version_No,                                   "
					+ " T1.priority                                                                 "
					+ " From PRD_RSS_PROCESS_CONTROL T1                                             "
					+ " Where getdate() >= T1.NEXT_PROCESS_TIME                                     " 
					+ " And T1.RSS_PROCESS_STATUS in ('P')                                          "
					+ " AND isnull(T1.NODE_OVERRIDE,T1.NODE_REQUEST) = '"+nodeName+"'                         "
					+ " AND T1.NEXT_PROCESS_TIME =                                                  "
					+ " (Select min(T2.NEXT_PROCESS_TIME)                                           "
					+ " From PRD_RSS_PROCESS_CONTROL T2                                             "
					+ " Where T2.REPORT_ID = T1.REPORT_ID                                           "
					+ " And T2.VISION_ID = T1.VISION_ID                                             "
					+ " And T2.RS_SCHEDULE_SEQ = T1.RS_SCHEDULE_SEQ                                 "
					+ " And T2.BURST_ID_SEQ = T1.BURST_ID_SEQ                                      "
					+ " And T2.RSS_PROCESS_STATUS in ('P','R','I','M')                             "
					+ " )                                                                          "
					+ " Order by T1.priority, T1.SCHEDULE_DATE                                     ";
			}
			
			return getJdbcTemplate().query(sql,new ResultSetExtractor< List<HashMap<String,String>>>() {
				@Override
				public List<HashMap<String, String>> extractData(ResultSet rs)throws SQLException, DataAccessException {
					List<HashMap<String,String>> processControlLst = new ArrayList<>();
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
						processControlLst.add(resultData);
					}
					return processControlLst;
				}
			});
		}catch(Exception e) {
			return new ArrayList<>();
		}
	}
	public int updateCronControl(String cronType, int runThread) {
		String query = "";
		int retVal = 0;
		try {
			if (runThread == -1)
				runThread = 0;
			query = "UPDATE PRD_CRON_CONTROL SET RUN_THREAD = ? WHERE CRON_TYPE = ? ";
			Object[] args = { runThread, cronType };
			retVal = getJdbcTemplate().update(query, args);
			return retVal;
		} catch (Exception e) {
			return retVal;
		}
	}
	public int availThreadCnt(String nodeName) {
		try {
			String sql = "SELECT (MAX_THREAD - RUN_THREAD) AVAILABLE FROM PRD_CRON_CONTROL WHERE CRON_TYPE='PRD_PROCESS_SCHEDULE_CRON' AND NODE_NAME = ? ";
			Object[] args = {nodeName};
			getJdbcTemplate().queryForObject(sql, Integer.class);
			return SUCCESSFUL_EXIT;
		}catch(Exception e) {
			e.printStackTrace();
			return ERRONEOUS_EXIT;
		}
	}
	public int updateRssProcessStatus(String processId,String versionNo,String emailStatus) {
		retVal = 0;
		String processEndTime = "";
		String processStartTime = "";
		try {
			if("I".equalsIgnoreCase(emailStatus)) {
				processStartTime  = ",PROCESS_START_TIME = "+commonApiDao.getDbFunction("SYSDATE")+"";
				processEndTime = ",PROCESS_END_TIME = null ";
			}else {
				processEndTime = ",PROCESS_END_TIME = "+commonApiDao.getDbFunction("SYSDATE")+" ";
			}
			String query  = "UPDATE  PRD_RSS_PROCESS_CONTROL SET RSS_PROCESS_STATUS = ? "+processStartTime+" "
					+ " "+processEndTime+" where RSS_PROCESS_ID = ? AND PROCESS_VERSION_NO = ? ";
			logger.info("update query : "+query +" emailStatus : "+emailStatus+" processId : "+processId+" versionNo : "+versionNo);
			Object[] args = {emailStatus,processId,versionNo};
			int retVal = getJdbcTemplate().update(query,args);
			return retVal;
		} catch (Exception e) {
			return 0;
		}
	}
}
