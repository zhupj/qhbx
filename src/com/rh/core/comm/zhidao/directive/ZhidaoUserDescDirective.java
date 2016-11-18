package com.rh.core.comm.zhidao.directive;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.comm.cms.directive.DirectiveUtils;
import com.rh.core.comm.zhidao.ZhidaoServ;
import com.rh.core.serv.ParamBean;

import freemarker.core.Environment;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 获取知道用户的个人简介信息宏
 * zhangjinxi
 */
public class ZhidaoUserDescDirective implements TemplateDirectiveModel {
    /** log */
    private static Log log = LogFactory.getLog(ZhidaoUserDescDirective.class);

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void execute(Environment env, Map params,
            TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {
        String debugName = DirectiveUtils.getString("debugName", params);
        String userId = DirectiveUtils.getString("userId", params);

        log.debug(debugName + ":开始");

        // 获取当前用户关注的人或者是关注者的信息
        ParamBean query = new ParamBean();
        query.set("debugName", debugName);
        query.set("USER_ID", userId);
        String userDesc = new ZhidaoServ().getZhidaoUserDesc(query);

        Map<String, TemplateModel> paramWrap = new HashMap<String, TemplateModel>(params);
        
        paramWrap.put("USER_DESC", ObjectWrapper.DEFAULT_WRAPPER.wrap(userDesc));
        Map<String, TemplateModel> origMap = DirectiveUtils.addParamsToVariable(env, paramWrap);
        if (body != null) {
            body.render(env.getOut());
        }
        DirectiveUtils.removeParamsFromVariable(env, paramWrap, origMap);
        log.debug(debugName + ":结束");
    }
}
