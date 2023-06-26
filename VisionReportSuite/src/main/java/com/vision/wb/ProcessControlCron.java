package com.vision.wb;

import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractCommonDao;
import com.vision.dao.CronServiceDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.ValidationUtil;
/*
 * Detail : RSS Scheduler Cron - Move data from RS_Schedule Table to RSS_Process_Control
 * Author : Pavithra 
 * Date   : 23-Feb-2022
 * */
@Component
public class ProcessControlCron extends AbstractCommonDao {
	String serverName = "";
	String nodeName = "";
	int SUCCESS_OPERATION = 1;
	int ERRONEOUS_OPERATION = 0;
	int NO_RECORDS_FOUND = 4;
	
	@Value("${app.databaseType}")
	private String databaseType;
	
	@Autowired
	CronServiceDao cronServiceDao;
	
	public static Logger logger = LoggerFactory.getLogger(ProcessControlCron.class);
	
	@Scheduled(fixedRate = 5000)
	public ExceptionCode callProcforSchedulerCron()  {
		HashMap<String, String> cronControlMap = new HashMap<String, String>();
		CallableStatement cs =  null;
		Connection con = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			serverName = System.getenv("VISION_SERVER_NAME");
			nodeName = System.getenv("VISION_NODE_NAME");
			/*if(!ValidationUtil.isValid(serverName)) {
				serverName = "APP_SERVER";	
			}if(!ValidationUtil.isValid(nodeName)) {
				nodeName = "U1";	
			}*/
			if(!ValidationUtil.isValid(serverName) || !ValidationUtil.isValid(nodeName)) {
				logger.info(" Environment variables not maintained for VISION_SERVER_NAME and VISION_NODE_NAME");
				return exceptionCode;
			}
			String query = " SELECT MAX_THREAD,RUN_THREAD,CRON_SCHEDULE_TIME,CRON_RUN_STATUS,AUTO_SCHEDULE_CRON_STATUS"
					+ " ,NODE_NAME FROM PRD_CRON_CONTROL WHERE CRON_TYPE='PRD_PROCESS_SCHEDULE_CRON' AND NODE_NAME = '"+nodeName+"' ";
			exceptionCode = getCommonResultDataQuery(query);
			if(exceptionCode.getErrorCode() == SUCCESS_OPERATION) {
				List cronControllst = (List) exceptionCode.getResponse();
				if(cronControllst == null) {
					logger.info(" No maintainence in PRD_CRON_CONTROL for cron Type [PRD_PROCESS_SCHEDULE_CRON] and Node ["+nodeName+"]");
				}else {
					cronControlMap = (HashMap<String, String>) cronControllst.get(0);
					String runningNode = cronControlMap.get("NODE_NAME");
					String cronRunStatus = cronControlMap.get("CRON_RUN_STATUS");
					if("ORACLE".equalsIgnoreCase(databaseType)) {
						if ("Y".equalsIgnoreCase(cronRunStatus) && runningNode.equalsIgnoreCase(nodeName)) {
							logger.info("RSS Process Cron is running in Node[" + nodeName + "]");
							updatePingCronTime();
							String serverEnvironment = getServerEnv();
							String procedure = "PRD_RSS_PROCESSOR_STUDIO (?, ? ,?)";
							if (!ValidationUtil.isValid(procedure)) {
								exceptionCode.setErrorMsg("Invalid Procedure ");
								return exceptionCode;
							}
							con = getConnection();
							cs = con.prepareCall("{call " + procedure + "}");
							cs.setString(1, serverEnvironment);
							cs.registerOutParameter(2, java.sql.Types.NUMERIC); // Status
							cs.registerOutParameter(3, java.sql.Types.VARCHAR); // Error Message
							cs.execute();
							exceptionCode.setErrorCode(cs.getInt(2));
							exceptionCode.setErrorMsg(cs.getString(3));
							if (exceptionCode.getErrorCode() != 0) {
								logger.error(exceptionCode.getErrorMsg());
							}
							cs.close();
						}
					}else if("MSSQL".equalsIgnoreCase(databaseType)) {
						if ("Y".equalsIgnoreCase(cronRunStatus)) {
							System.out.println("RSS Process Cron is running");
							updatePingCronTime();
							String execsPath = cronServiceDao.findVisionVariableValue("PRD_SCHEDULER_EXECS_PATH");
							String jarName = cronServiceDao.findVisionVariableValue("PRD_PROCESS_CRON_JAR_NAME");
							jarName = ValidationUtil.isValid(jarName) ? jarName : "RSSProcessor.jar";
							
							String logPath = cronServiceDao.findVisionVariableValue("PRD_SCHEDULER_LOG_FILE_PATH");
							String fileName = logPath+"RSSProcessor_Logs.txt";
							String execs = "java -jar " + execsPath + jarName + " N Y "+" "+fileName;
							//System.out.println("execs : "+execs);
							Process proc;
							proc = Runtime.getRuntime().exec("java -jar " + execsPath + jarName + " N  Y "+" "+fileName);
							proc.waitFor();
							InputStream in = proc.getInputStream();
							InputStream err = proc.getErrorStream();
							int exitVal = proc.exitValue();
							//System.out.println("exit Val : "+exitVal);
							byte b[] = new byte[in.available()];
							in.read(b, 0, b.length);
							/*if (ValidationUtil.isValid(new String(b))) {
								String status = new String(b);
								status = status.substring(status.indexOf(":") + 1);
								if ("0".equalsIgnoreCase(status.trim())) {
									returnStatus = "S";
								} else {
									returnStatus = "E";
								}
							} else {
								returnStatus = "E";
							}*/
						}
					}
				}
				
				
			}
		}catch(Exception e) {
			exceptionCode.setErrorMsg(e.getMessage());
			e.printStackTrace();
		}finally {
			if(con != null)
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return exceptionCode;
	}
	
	public int updatePingCronTime() {
		String sql = "";
		if("ORACLE".equalsIgnoreCase(databaseType)) {
			sql = "UPDATE VISION_RAC_FETCH_DET SET LAST_PING_TIME = SYSDATE "
				+ " WHERE SERVER_NAME = ? AND NODE_NAME  = ? AND CRON_NAME  = 'PRD_PROCESS_SCHEDULE_CRON'";
		}else if("MSSQL".equalsIgnoreCase(databaseType)) {
			sql = "UPDATE VISION_RAC_FETCH_DET SET LAST_PING_TIME = GETDATE() "
				+ " WHERE SERVER_NAME = ? AND NODE_NAME  = ? AND CRON_NAME  = 'PRD_PROCESS_SCHEDULE_CRON'";
		}
		try {
			Object args[] = {serverName,nodeName};
			int retVal = getJdbcTemplate().update(sql,args);
			return retVal;
		}catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	public String getServerEnv() {
		try {
			String sql = "SELECT SERVER_ENVIRONMENT FROM VISION_NODE_CREDENTIALS WHERE SERVER_NAME = ? AND NODE_NAME  = ?";
			Object[] args = new Object[2];
			args[0] = serverName;
			args[1] = nodeName;
			return getJdbcTemplate().queryForObject(sql, args, String.class);
		}catch(Exception e) {
			e.printStackTrace();
			return "";
		}
		
		
	}
	public ExceptionCode getCommonResultDataQuery(String query){
		ExceptionCode exceptionCode = new ExceptionCode();
		ArrayList result = new ArrayList(); 
		try
		{	
			if(!ValidationUtil.isValid(query)) {
				exceptionCode.setErrorCode(ERRONEOUS_OPERATION);
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
						exceptionCode.setErrorCode(SUCCESS_OPERATION);
						exceptionCode.setResponse(result);
					}else {
						exceptionCode.setErrorCode(ERRONEOUS_OPERATION);
					}
					return exceptionCode;
				}
			};
			return (ExceptionCode)getJdbcTemplate().query(query, mapper);
		}catch(Exception e) {
			exceptionCode.setErrorCode(ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
	}
}
