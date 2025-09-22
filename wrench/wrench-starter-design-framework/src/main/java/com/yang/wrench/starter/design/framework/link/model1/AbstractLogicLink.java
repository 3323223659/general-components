package com.yang.wrench.starter.design.framework.link.model1;

/**
 * @author yang
 * @description 抽象类
 */
public abstract class AbstractLogicLink<T, D, R> implements ILogicLink<T, D, R> {

    /**
     * 下一个处理逻辑
     */
    private ILogicLink<T, D, R> next;

    /**
     * 获取下一个处理逻辑
     *
     * @return 下一个处理逻辑
     */
    @Override
    public ILogicLink<T, D, R> next() {
        return next;
    }

    /**
     * 添加下一个处理逻辑
     *
     * @param next 下一个处理逻辑
     * @return 下一个处理逻辑
     */
    @Override
    public ILogicLink<T, D, R> appendNext(ILogicLink<T, D, R> next) {
        this.next = next;
        return next;
    }

    /**
     * 获取下一个处理逻辑结果
     *
     * @param requestParameter 请求参数
     * @param dynamicContext   动态上下文
     * @return 下一个处理逻辑结果
     * @throws Exception 异常
     */
    protected R next(T requestParameter, D dynamicContext) throws Exception {
        return next.apply(requestParameter, dynamicContext);
    }

}
