package com.yang.wrench.starter.dynamic.config.center.listener;

import com.yang.wrench.starter.dynamic.config.center.domain.model.valobj.AttributeVO;
import com.yang.wrench.starter.dynamic.config.center.domain.service.IDynamicConfigCenterService;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicConfigCenterAdjustListener implements MessageListener<AttributeVO> {

    private final Logger log = LoggerFactory.getLogger(DynamicConfigCenterAdjustListener.class);

    private final IDynamicConfigCenterService dynamicConfigCenterService;

    public DynamicConfigCenterAdjustListener(IDynamicConfigCenterService dynamicConfigCenterService) {
        this.dynamicConfigCenterService = dynamicConfigCenterService;
    }

    @Override
    public void onMessage(CharSequence charSequence, AttributeVO attributeVO) {
        try {
            log.info("wrench dcc config attribute:{} value:{}", attributeVO.getAttribute(), attributeVO.getValue());
            dynamicConfigCenterService.adjustAttributeValue(attributeVO);
        } catch (Exception e) {
            log.error("wrench dcc config attribute:{} value:{}", attributeVO.getAttribute(), attributeVO.getValue(), e);
        }
    }

}
