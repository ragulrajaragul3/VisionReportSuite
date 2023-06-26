package com.vision.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringBeanFinder implements ApplicationContextAware {

	// Static properties
    public static ApplicationContext m_applicationContext	= null;
    
    // Constant
    private final static Logger LOG  = LoggerFactory.getLogger(SpringBeanFinder.class);
      
    /**
     * @param pApplicationContext Setter set by the Spring
     */
	public void setApplicationContext(ApplicationContext pApplicationContext) throws BeansException {
		m_applicationContext = pApplicationContext;
	}
	
    
    /**
     * Looks up the bean from the Spring BeanFactory using the bean id
     * 
     * @param pBeanKey
     * @return
     */
    public static Object lookUp(String pBeanKey) {
        
    	if(LOG.isDebugEnabled()){
    		LOG.debug("Entering lookUp method on SpringBeanFinder");
    	}
        
        // Defensive validation
        if(pBeanKey==null) {
            LOG.warn("No bean key to find bean from the SpringBeanFactory");
            throw new IllegalArgumentException("No bean key to find bean from the " +
            											"SpringBeanFactory");
        }
        
        // Look up the bean using the bean key
        Object lObject = m_applicationContext.getBean(pBeanKey);

        // Object not found
        if(lObject==null) {
        	LOG.warn("lookUp() returned no bean for the bean key : " + pBeanKey);
        }

        if(LOG.isDebugEnabled()) {
            LOG.debug("lookUp() lookUp returned object :" + lObject);
        }

        // return the bean object
        return lObject;
    }

}
