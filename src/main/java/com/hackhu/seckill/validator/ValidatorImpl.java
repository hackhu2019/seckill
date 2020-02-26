package com.hackhu.seckill.validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;


/**
 * @author hackhu
 * @date 2020/2/26
 */
@Component
public class ValidatorImpl implements InitializingBean {
    private Validator validator;

    /**
     * 实现校验方法并返回校验结果
     */
    public ValidatorResult validate(Object bean) {
        ValidatorResult validatorResult = new ValidatorResult();
        Set<ConstraintViolation<Object>> validateSet = validator.validate(bean);
        // 判断是否通过校验
        if (validateSet.size() > 0) {
            validatorResult.setHasErrors(true);
            validateSet.forEach(validate->{
                String errMsg = validate.getMessage();
                String propertyName = validate.getPropertyPath().toString();
                validatorResult.getErrorMsgMap().put(propertyName, errMsg);
            });
        }
        return validatorResult;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        // 以工厂的初始化方式初始化 hibernate validator
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
}
