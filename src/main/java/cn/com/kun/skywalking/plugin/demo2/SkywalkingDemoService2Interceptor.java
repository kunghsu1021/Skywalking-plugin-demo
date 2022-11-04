package cn.com.kun.skywalking.plugin.demo2;

import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.tag.StringTag;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.context.trace.SpanLayer;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.StaticMethodsAroundInterceptor;
import org.apache.skywalking.apm.network.trace.component.ComponentsDefine;
import org.apache.skywalking.apm.network.trace.component.OfficialComponent;

import java.lang.reflect.Method;

/**
 * 8.8.0版本拦截器开发
 *
 * author:xuyaokun_kzx
 * date:2022/11/4
 * desc:
*/
public class SkywalkingDemoService2Interceptor implements InstanceMethodsAroundInterceptor {

    public static final OfficialComponent CUSTOM_OFFICIAL_COMPONENT2 = new OfficialComponent(8100, "SkywalkingDemoService22");

    @Override
    public void beforeMethod(EnhancedInstance enhancedInstance, Method method, Object[] objects, Class<?>[] classes, MethodInterceptResult methodInterceptResult) throws Throwable {

        //创建span(监控的开始)，本质上是往ThreadLocal对象里面设值
        //这里的operationName就是展示到UI上的端点名称
        AbstractSpan span = ContextManager.createLocalSpan("SkywalkingDemoService22.method4");
        /*
         * 可用ComponentsDefine工具类指定Skywalking官方支持的组件
         * 也可自己new OfficialComponent或者Component
         * 不过在Skywalking的控制台上不会被识别，只会显示Unknown
         */
        span.setComponent(CUSTOM_OFFICIAL_COMPONENT2);
//        span.setComponent(ComponentsDefine.TOMCAT);

        span.tag(new StringTag(1000, "params"), objects[0].toString());
        //指定该调用的layer，layer是个枚举（框架内的枚举值）
        span.setLayer(SpanLayer.CACHE);
    }

    @Override
    public Object afterMethod(EnhancedInstance enhancedInstance, Method method, Object[] objects, Class<?>[] classes, Object o) throws Throwable {

        String retString = (String) o;
        // 激活span，本质上是读取ThreadLocal对象
        AbstractSpan span = ContextManager.activeSpan();

        // 状态码，任意写，Tags也是个Skywalking的工具类，用来比较方便地操作tag
//        Tags.STATUS_CODE.set(span, "20000");//(6.6.0版本写法)

        // 停止span(监控的结束)，本质上是清理ThreadLocal对象
        ContextManager.stopSpan();
        return retString;
    }

    @Override
    public void handleMethodException(EnhancedInstance enhancedInstance, Method method, Object[] objects, Class<?>[] classes, Throwable throwable) {
        AbstractSpan activeSpan = ContextManager.activeSpan();

        // 记录日志
        activeSpan.log(throwable);
        activeSpan.errorOccurred();
    }
}
