package com.yang.wrench.starter.design.framework.link.model2.chain;

import com.yang.wrench.starter.design.framework.link.model2.DynamicContext;
import com.yang.wrench.starter.design.framework.link.model2.handler.ILogicHandler;

/**
 * @author yang
 * @description 业务链路
 */
public class BusinessLinkedList<T, D extends DynamicContext, R> extends LinkedList<ILogicHandler<T, D, R>> implements ILogicHandler<T, D, R>{

    public BusinessLinkedList(String name) {
        super(name);
    }

    @Override
    public R apply(T requestParameter, D dynamicContext) throws Exception {
        Node<ILogicHandler<T, D, R>> current = this.first;
        do {
            ILogicHandler<T, D, R> item = current.item;
            R apply = item.apply(requestParameter, dynamicContext);
            if (!dynamicContext.isProceed()) return apply;

            current = current.next;
        } while (null != current);

        return null;
    }

}
