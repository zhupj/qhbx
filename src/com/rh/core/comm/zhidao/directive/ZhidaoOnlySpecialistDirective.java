package com.rh.core.comm.zhidao.directive;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.comm.cms.directive.DirectiveUtils;
import com.rh.core.comm.zhidao.ZhidaoServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;

import freemarker.core.Environment;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 根据一个专家的userId来查找这个专家的专家信息
 * 前台调用的方法<@zhidao_only_specialist debugName="单个专家的专家信息" userId="">
 * @author zhangjinxi
 * 
 */
public class ZhidaoOnlySpecialistDirective implements TemplateDirectiveModel {
    /** log */
    private static Log log = LogFactory.getLog(ZhidaoOnlySpecialistDirective.class);

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void execute(Environment env, Map params,
            TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {
        String debugName = DirectiveUtils.getString("debugName", params);
        String userId = DirectiveUtils.getString("userId", params);

        log.debug(debugName + ":开始");

        // 取出当前专家擅长的栏目
        ParamBean query = new ParamBean();
        query.set("userId", userId);
        OutBean specialist = new ZhidaoServ().getSpecialist(query);
        

        Map<String, TemplateModel> paramWrap = new HashMap<String, TemplateModel>(params);
        //这个专家的领域列表
        paramWrap.put("specialist", ObjectWrapper.DEFAULT_WRAPPER.wrap(specialist));
        Map<String, TemplateModel> origMap = DirectiveUtils.addParamsToVariable(env, paramWrap);
        if (body != null) {
            body.render(env.getOut());
        }
        DirectiveUtils.removeParamsFromVariable(env, paramWrap, origMap);
        log.debug(debugName + ":结束");
    }
}