package com.yang.wrench.starter.design.framework.link.model1;

/**
 * @author yang
 * @description 责任链装配
 */
public interface ILogicChainArmory<T, D, R> {

    ILogicLink<T, D, R> next();

    ILogicLink<T, D, R> appendNext(ILogicLink<T, D, R> next);

}
