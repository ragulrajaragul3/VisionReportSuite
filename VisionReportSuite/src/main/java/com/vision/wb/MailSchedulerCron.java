package com.vision.wb;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vision.dao.CommonApiDao;
import com.vision.dao.CronServiceDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.ProcessCronVb;
@Component
@EnableAsync
public class MailSchedulerCron extends ProcessCronVb{
	@Autowired
	CommonApiDao commonApiDao;
	@Autowired
	CronServiceDao cronServiceDao;
	
	public static Logger logger = LoggerFactory.getLogger(MailSchedulerCron.class);
	
	@Scheduled(fixedRate = 5000)
	@Async
	public void scheduleCron() {
		String serverName = "";
		String nodeName = "";
		HashMap<String, String> cronControlMap = new HashMap<String, String>();
		ExceptionCode exceptionCode = new ExceptionCode();
		//int runThread = 0;
		try {
			serverName = System.getenv("VISION_SERVER_NAME");
			nodeName = System.getenv("VISION_NODE_NAME");
			if(!ValidationUtil.isValid(serverName) || !ValidationUtil.isValid(nodeName)) {
				logger.info(" Environment variables not maintained for VISION_SERVER_NAME and VISION_NODE_NAME");
				return;
			}
			String query = " SELECT MAX_THREAD,RUN_THREAD,CRON_SCHEDULE_TIME,CRON_RUN_STATUS,AUTO_SCHEDULE_CRON_STATUS "
					+ " ,NODE_NAME FROM PRD_CRON_CONTROL WHERE CRON_TYPE='PRD_EMAIL_SCHEDULE_CRON' AND NODE_NAME = '"+nodeName+"' ";
			exceptionCode = commonApiDao.getCommonResultDataQuery(query);
			
			if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				List cronControllst = (List) exceptionCode.getResponse();
				if(cronControllst == null && !cronControllst.isEmpty()) {
					logger.info(" Maintainence for PRD_EMAIL_SCHEDULE_CRON is missing in the table PRD_CRON_CONTROL ");
				}else {
					cronControlMap = (HashMap<String, String>) cronControllst.get(0);
				
					String runningNodeName = cronControlMap.get("NODE_NAME");
	//				int maxThread = Integer.parseInt(cronControlMap.get("MAX_THREAD"));
	//				runThread = Integer.parseInt(cronControlMap.get("RUN_THREAD"));
					String cronScheduleTime = cronControlMap.get("CRON_SCHEDULE_TIME");
					String cronRunStatus = cronControlMap.get("CRON_RUN_STATUS");
					
					if ("Y".equalsIgnoreCase(cronRunStatus) && runThread < maxThread) {
							List<HashMap<String, String>> processControlLst = cronServiceDao.getScheduleProcessLst(nodeName);
							if (processControlLst != null && processControlLst.size() > 0) {
								int cnt = 0;
								int processCnt = 0;
								int availableThread = maxThread - runThread;
								if(processControlLst.size() < availableThread) 
									processCnt = processControlLst.size();
								else
									processCnt = availableThread;
								
								List<HashMap<String, String>> listToProcess = processControlLst.stream()
																			.limit(processCnt).collect(Collectors.toList());
								
								List<DataFetcher> threads = new ArrayList<DataFetcher>(listToProcess.size());
								
								for(HashMap<String, String> processControlMap : listToProcess){
									runThread = runThread+1;
									if(runThread == maxThread || runThread > maxThread) {
										break;
									}
									String processId = processControlMap.get("RSS_PROCESS_ID");
									String debugMode = processControlMap.get("DEBUG_MODE");
									String versionNo = processControlMap.get("PROCESS_VERSION_NO");
									String reportId = processControlMap.get("REPORT_ID");
									
									cronServiceDao.updateRssProcessStatus(processId,versionNo,"I");
									System.out.println("Status I updated for process id : "+processId+" " +new Date());
									String returnStatus = "";
									DataFetcher fetcher = new DataFetcher(processId,debugMode,versionNo,returnStatus);
									fetcher.setDaemon(true);
									threads.add(fetcher);
									new Thread(fetcher).start();
									//fetcher.start();
									try {
										fetcher.join();
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									
									for(DataFetcher df:threads){
										int count = 0;
										if(!df.dataFetched){
											try {
												Thread.sleep(1000);
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
											count++;
											if(count > 150){
												count = 0;
												continue;
											}
										}else {
											runThread = runThread - 1;
											//cronServiceDao.updateRssProcessStatus(df.processId,df.versionNo,df.returnStatus);
										}
									}
									runThread = runThread - 1;
								}
							}
					}
				}
				
			}
		}catch(Exception e) {
			e.printStackTrace();
			cronServiceDao.updateCronControl("PRD_EMAIL_SCHEDULE_CRON", runThread - 1);
		}
	}
	public String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();
		return dateFormat.format(date);
		
	}
	class DataFetcher extends Thread {
		boolean dataFetched = false;
		boolean errorOccured = false;
		String errorMsg = "";
		ExceptionCode exceptionCode;
		String processId= "";
		String versionNo = "";
		String debugMode = "";
		String returnStatus = "";
		HashMap<String,ExceptionCode> resultMap = new HashMap<String,ExceptionCode>();
		public DataFetcher(String processId,String debugMode,String versionNo,String returnStatus){
			this.processId = processId;
			this.debugMode = debugMode;
			this.versionNo = versionNo;
			this.returnStatus = returnStatus;
		}
		public void run() {
			try{
				String execsPath = cronServiceDao.findVisionVariableValue("PRD_SCHEDULER_EXECS_PATH");
				String jarName = cronServiceDao.findVisionVariableValue("PRD_PROCESS_CRON_JAR_NAME");
				Process proc;
				System.out.println("java -jar " + execsPath + jarName + " " + processId
						+ " " + debugMode + " " + versionNo);
				proc = Runtime.getRuntime().exec("java -jar " + execsPath + jarName + " Y " + processId
						+ " " + debugMode + " " + versionNo);
				proc.waitFor();
				System.out.println("Jar execution End for Process id : "+processId+" "+new Date());
				InputStream in = proc.getInputStream();
				InputStream err = proc.getErrorStream();
				int exitVal = proc.exitValue();
				System.out.println("exit Val : "+exitVal);
				byte b[] = new byte[in.available()];
				in.read(b, 0, b.length);
				if (ValidationUtil.isValid(new String(b))) {
					String status = new String(b);
					status = status.substring(status.indexOf(":") + 1);
					if ("0".equalsIgnoreCase(status.trim())) {
						returnStatus = "S";
					} else {
						returnStatus = "E";
					}
				} else {
					returnStatus = "E";
				}
				cronServiceDao.updateRssProcessStatus(processId,versionNo,returnStatus);
				System.out.println("Status "+returnStatus+" updated for process id : "+processId+" " +new Date());
				dataFetched = true;
			}catch(RuntimeCustomException rex){
				dataFetched = true;
				errorOccured = true;
				exceptionCode = rex.getCode();
			}catch(Exception e){
				dataFetched = true;
				errorOccured = true;
				errorMsg = e.getMessage();
			}
		}
	}
}
