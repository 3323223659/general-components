package com.yang.wrench.test.design.framework.biz.tree;

import com.yang.wrench.starter.design.framework.tree.AbstractMultiThreadStrategyRouter;
import com.yang.wrench.test.design.framework.biz.tree.factory.DefaultStrategyFactory;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public abstract class AbstractXxxSupport extends AbstractMultiThreadStrategyRouter<String, DefaultStrategyFactory.DynamicContext, String> {

    @Override
    protected void multiThread(String requestParameter, DefaultStrategyFactory.DynamicContext dynamicContext) throws ExecutionException, InterruptedException, TimeoutException {
        // 缺省的方法
    }

}
