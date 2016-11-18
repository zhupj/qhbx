package com.rh.core.comm.wenku.directive;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rh.core.comm.cms.directive.DirectiveUtils;
import com.rh.core.comm.wenku.WenkuServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;

import freemarker.core.Environment;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * @author liwei 公告
 */
public class WenkuNoticeDirective implements TemplateDirectiveModel {
    /** log */
    private static Log log = LogFactory.getLog(WenkuNoticeDirective.class);

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void execute(Environment env, Map params,
            TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {
        String debugName = DirectiveUtils.getString("debugName", params);
        String id = DirectiveUtils.getString("id", params);

        log.debug(debugName + ":开始");
        ParamBean param = new ParamBean();
        param.setId(id);
        OutBean outBean = new WenkuServ().getWenkuNotice(param);
        
        Map<String, TemplateModel> paramWrap = new HashMap<String, TemplateModel>(params);
        paramWrap.put("notice", ObjectWrapper.DEFAULT_WRAPPER.wrap(outBean));
        Map<String, TemplateModel> origMap = DirectiveUtils.addParamsToVariable(env, paramWrap);
        if (body != null) {
            body.render(env.getOut());
        }
        DirectiveUtils.removeParamsFromVariable(env, paramWrap, origMap);
        log.debug(debugName + ":结束");

    }
}
