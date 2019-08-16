package com.lh.hermes.bean;

import com.library.hermes.annotation.ClassId;
import com.library.hermes.annotation.GetInstance;

/**
 * user：LuHao
 * time：2019/8/15 17:19
 * describe：测试跨进程hermes的实体类。对应方法：Hermes.getInstance(Class , Object...)
 * doc:该方法可以获得单例，调用的对象必须是static且带有@GetInstance。另外，可以忽略@MethodId注解
 */
@ClassId("HermesInstanceBean")
public class HermesInstanceBean {
    private int id;
    private String name;
    private static HermesInstanceBean instance;

    private HermesInstanceBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @GetInstance
    public synchronized static HermesInstanceBean getInstance() {
        if (instance == null) {
            instance = new HermesInstanceBean(1, "admin");
        }
        return instance;
    }

    @GetInstance
    public synchronized static HermesInstanceBean getInstance(int id) {
        if (instance == null) {
            instance = new HermesInstanceBean(id, "只有id");
        }
        return instance;
    }

    @GetInstance
    public synchronized static HermesInstanceBean getInstance(String name) {
        if (instance == null) {
            instance = new HermesInstanceBean(100, name);
        }
        return instance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
