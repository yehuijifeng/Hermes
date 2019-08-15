// IHermesService.aidl
package com.lh.hermes.bean;

// Declare any non-default types here with import statements
import com.lh.hermes.bean.HermesBean;

interface IHermesService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     HermesBean getHermesBean();

     void setHermesBean(int id,String name,double money);
}
