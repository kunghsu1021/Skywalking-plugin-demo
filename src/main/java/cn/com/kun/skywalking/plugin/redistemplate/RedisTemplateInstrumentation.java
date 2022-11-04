package cn.com.kun.skywalking.plugin.redistemplate;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.StaticMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.NameMatch;

/**
 * 拦截普通实例方法例子
 *
 * author:xuyaokun_kzx
 * date:2022/11/4
 * desc:
*/
public class RedisTemplateInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    @Override
    protected ClassMatch enhanceClass() {
        //指定想要监控的类
        return NameMatch.byName("org.springframework.data.redis.core.DefaultValueOperations");
    }

    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[0];
    }

    /**
     * 拦截实例方法（非静态方法）
     *
     * @return
     */
    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        //指定想要监控的实例方法，每个实例方法对应一个InstanceMethodsInterceptPoint
        return new InstanceMethodsInterceptPoint[]{
                new InstanceMethodsInterceptPoint() {
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {

                        return ElementMatchers.named("get");
                    }

                    @Override
                    public String getMethodsInterceptor() {
                        return "cn.com.kun.skywalking.plugin.redistemplate.RedisTemplateInterceptor";
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return false;
                    }
                }
        };
    }

    /**
     * 注意这个只能拦截静态方法
     *
     * @return
     */
    @Override
    public StaticMethodsInterceptPoint[] getStaticMethodsInterceptPoints() {
        // 指定想要监控的静态方法，每一个方法对应一个StaticMethodsInterceptPoint
        return new StaticMethodsInterceptPoint[0];
    }
}
