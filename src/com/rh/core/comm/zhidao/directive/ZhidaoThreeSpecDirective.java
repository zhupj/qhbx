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
 * 获取同事分享信息
 * @author zhangjinxi
 * 
 */
public class ZhidaoThreeSpecDirective implements TemplateDirectiveModel {
    /** log */
    private static Log log = LogFactory.getLog(ZhidaoThreeSpecDirective.class);

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void execute(Environment env, Map params,
            TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {
        String debugName = DirectiveUtils.getString("debugName", params);
        
        log.debug(debugName + ":开始");
        
        
        OutBean threeSpec = new ZhidaoServ().getThreeSpec(new ParamBean());
        

        Map<String, TemplateModel> paramWrap = new HashMap<String, TemplateModel>(params);
        paramWrap.put("threeSpec", ObjectWrapper.DEFAULT_WRAPPER.wrap(threeSpec));
        Map<String, TemplateModel> origMap = DirectiveUtils.addParamsToVariable(env, paramWrap);
        if (body != null) {
            body.render(env.getOut());
        }
        DirectiveUtils.removeParamsFromVariable(env, paramWrap, origMap);
        log.debug(debugName + ":结束");

    }
}