package net.lab1024.sa.base.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Mybatis Plus 插入或者更新时指定字段设置值
 *
 * @author zhoumingfa
 */
@Component
@Slf4j
public class MybatisPlusFillHandler implements MetaObjectHandler {
    public static final String CREATE_TIME = "createTime";
    public static final String UPDATE_TIME = "updateTime";

    @Override
    public void insertFill(MetaObject metaObject) {
        fillTimeField(metaObject, CREATE_TIME);
        fillTimeField(metaObject, UPDATE_TIME);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        fillTimeField(metaObject, UPDATE_TIME);
    }

    /**
     * 动态处理时间字段类型（兼容 LocalDateTime 和 Date）
     */
    private void fillTimeField(MetaObject metaObject, String fieldName) {
        if (metaObject.hasSetter(fieldName)) {
            Class<?> fieldType = metaObject.getGetterType(fieldName);
            Object value = null;

            if (fieldType == LocalDateTime.class) {
                value = LocalDateTime.now();
            } else if (fieldType == Date.class) {
                // 将 LocalDateTime 转换为 Date
                value = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
            }

            if (value != null) {
                this.fillStrategy(metaObject, fieldName, value);
            }
        }
    }
}