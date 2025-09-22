package com.yang.wrench.starter.design.framework.link.model1;

/**
 * @author yang
 * @description 略规则责任链接口
 */
public interface ILogicLink<T, D, R> extends ILogicChainArmory<T, D, R> {

    R apply(T requestParameter, D dynamicContext) throws Exception;

}
