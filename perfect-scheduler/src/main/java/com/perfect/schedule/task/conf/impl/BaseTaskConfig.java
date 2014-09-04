package com.perfect.schedule.task.conf.impl;

import com.perfect.schedule.core.strategy.ScheduleStrategy;
import com.perfect.schedule.core.strategy.TBScheduleManagerFactory;
import com.perfect.schedule.core.taskmanager.ScheduleTaskType;
import com.perfect.schedule.task.conf.StrategyConfig;
import com.perfect.schedule.task.conf.TaskConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * Created by yousheng on 2014/8/1.
 *
 * @author yousheng
 */
public class BaseTaskConfig implements TaskConfig {

    private final String baseTaskName;
    private final String ownSig;
    private final String dealBean;
    private final String cronExp;
    private final String[] taskDef;

    private StrategyConfig strategyConfig;

    public BaseTaskConfig(String baseTaskName, String ownSig, String dealBean, String cronExp, String[] taskDef) {
        this.baseTaskName = baseTaskName;
        this.ownSig = ownSig;
        this.dealBean = dealBean;
        this.cronExp = cronExp;
        this.taskDef = taskDef;
        log = LoggerFactory.getLogger(getTaskName());
    }

    public BaseTaskConfig(String baseTaskName, String ownSig, String dealBean, String cronExp, String[] taskDef, StrategyConfig strategyConfig) {
        this.baseTaskName = baseTaskName;
        this.ownSig = ownSig;
        this.dealBean = dealBean;
        this.cronExp = cronExp;
        this.taskDef = taskDef;

        this.strategyConfig = strategyConfig;
        log = LoggerFactory.getLogger(getTaskName());
    }


    protected transient Logger log = null;


    @Resource
    TBScheduleManagerFactory scheduleManagerFactory;


    public void setScheduleManagerFactory(
            TBScheduleManagerFactory tbScheduleManagerFactory) {
        this.scheduleManagerFactory = tbScheduleManagerFactory;
    }

    @Override
    public void createTask() {
        try {
            while (!this.scheduleManagerFactory.isZookeeperInitialSucess()) {
                Thread.sleep(1000);
            }
            scheduleManagerFactory.stopServer(null);
            Thread.sleep(20000);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            this.scheduleManagerFactory.getScheduleDataManager()
                    .deleteTaskType(baseTaskName);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        ScheduleTaskType baseTaskType = createTaskType();

        try {
            this.scheduleManagerFactory.getScheduleDataManager()
                    .createBaseTaskType(baseTaskType);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        log.info("创建调度任务成功:" + baseTaskType.toString());

        String taskName = getTaskName();

        String strategyName = getStrategyName();

        try {
            this.scheduleManagerFactory.getScheduleStrategyManager()
                    .deleteMachineStrategy(strategyName, true);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        ScheduleStrategy strategy = new ScheduleStrategy();
        strategy.setStrategyName(strategyName);
        strategy.setKind(ScheduleStrategy.Kind.Schedule);
        strategy.setTaskName(taskName);

        if (strategyConfig == null) {
            strategy.setNumOfSingleServer(1);
            strategy.setAssignNum(10);
            strategy.setIPList("127.0.0.1".split(","));
        } else {
            strategy.setNumOfSingleServer(strategyConfig.getNumOfSingleServer());
            strategy.setAssignNum(strategyConfig.getAssignNum());
            strategy.setIPList(strategyConfig.getIpList());
        }
        try {
            this.scheduleManagerFactory.getScheduleStrategyManager()
                    .createScheduleStrategy(strategy);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        log.info("创建调度策略成功:" + strategy.toString());
    }

    private String getStrategyName() {
        return this.baseTaskName + SUFFIX_STRATEGY;
    }

    /**
     * 获取任务名，包含基本名称与域名
     *
     * @return
     */
    private String getTaskName() {
        return baseTaskName + "$" + ownSig;
    }

    /**
     * 创建调度任务类型
     * 子类可以重写该方法完成加入自定义的参数
     *
     * @return
     */
    protected ScheduleTaskType createTaskType() {
        ScheduleTaskType baseTaskType = new ScheduleTaskType();
        baseTaskType.setBaseTaskType(baseTaskName);
        baseTaskType.setDealBeanName(dealBean);
        baseTaskType.setHeartBeatRate(20000);
        baseTaskType.setJudgeDeadInterval(100000);
        baseTaskType.setPermitRunStartTime(cronExp);
        baseTaskType.setTaskItems(taskDef);
        return baseTaskType;
    }
}
